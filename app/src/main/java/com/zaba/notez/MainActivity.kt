package com.zaba.notez

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var dao: NoteDao
    private lateinit var adapter: NoteAdapter
    private var collectJob: Job? = null
    private var query = ""
    private var pendingExport: String? = null
    private var searchOpen = false

    private val createDoc = registerForActivityResult(ActivityResultContracts.CreateDocument("*/*")) { uri ->
        val data = pendingExport ?: return@registerForActivityResult
        pendingExport = null
        if (uri == null) return@registerForActivityResult
        lifecycleScope.launch(Dispatchers.IO) {
            contentResolver.openOutputStream(uri)?.use { it.write(data.toByteArray()) }
            launch(Dispatchers.Main) { toast("Diekspor") }
        }
    }

    private val openDoc = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) return@registerForActivityResult
        lifecycleScope.launch(Dispatchers.IO) {
            val raw = contentResolver.openInputStream(uri)?.use { it.readBytes().toString(Charsets.UTF_8) }
            val notes = try { BackupHelper.fromJson(raw.orEmpty()) } catch (_: Exception) { null }
            if (notes == null) {
                launch(Dispatchers.Main) { toast("File backup tidak valid") }
                return@launch
            }
            for (n in notes) dao.upsert(n)
            launch(Dispatchers.Main) { toast("${notes.size} catatan dipulihkan") }
        }
    }

    private val openTree = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri == null) return@registerForActivityResult
        contentResolver.takePersistableUriPermission(uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        BackupHelper.setTree(this, uri)
        toast("Folder backup otomatis aktif")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(ThemePref.styleOf(ThemePref.get(this)))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dao = AppDatabase.get(this).noteDao()
        findViewById<ImageButton>(R.id.settings).setOnClickListener {
            showSettingsMenu()
        }

        adapter = NoteAdapter(
            onOpen = { note -> openEditor(note.id) },
            onDelete = { note ->
                lifecycleScope.launch(Dispatchers.IO) {
                    dao.delete(note)
                    // Hapus fisik sisa forensik SQLite (freelist) — DB kecil, murah
                    AppDatabase.get(this@MainActivity).openHelper.writableDatabase
                        .execSQL("VACUUM")
                }
                Snackbar.make(findViewById(R.id.list), "Catatan dihapus", Snackbar.LENGTH_LONG)
                    .setAction("URUNGKAN") {
                        lifecycleScope.launch(Dispatchers.IO) { dao.upsert(note) }
                    }
                    .show()
            }
        )
        findViewById<RecyclerView>(R.id.list).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val id = dao.upsert(Note(title = "Catatan baru"))
                launch(Dispatchers.Main) { openEditor(id, isNew = true) }
            }
        }
        val searchToggle = findViewById<ImageButton>(R.id.search_toggle)
        val searchInput = findViewById<EditText>(R.id.search_input)
        searchToggle.setOnClickListener {
            if (searchOpen) closeSearch(searchToggle, searchInput) else openSearch(searchToggle, searchInput)
        }
        searchInput.doAfterTextChanged {
            query = it?.toString().orEmpty()
            observe()
        }
    }

    private fun openSearch(toggle: ImageButton, input: EditText) {
        searchOpen = true
        input.visibility = View.VISIBLE
        toggle.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
        toggle.contentDescription = getString(R.string.cd_close_search)
        input.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun closeSearch(toggle: ImageButton, input: EditText) {
        searchOpen = false
        input.setText("")
        input.visibility = View.INVISIBLE
        toggle.setImageResource(android.R.drawable.ic_menu_search)
        toggle.contentDescription = getString(R.string.cd_open_search)
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(input.windowToken, 0)
    }

    override fun onBackPressed() {
        if (searchOpen) {
            closeSearch(findViewById(R.id.search_toggle), findViewById(R.id.search_input))
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        observe()
        lifecycleScope.launch(Dispatchers.IO) {
            if (BackupHelper.autoBackupIfDue(this@MainActivity, dao)) {
                launch(Dispatchers.Main) { toast("Backup otomatis tersimpan") }
            }
        }
    }

    private fun observe() {
        collectJob?.cancel()
        val flow = if (query.isBlank()) dao.observeAll() else dao.search(query)
        collectJob = lifecycleScope.launch {
            flow.collectLatest {
                adapter.submit(it)
                val empty = it.isEmpty()
                findViewById<RecyclerView>(R.id.list).visibility =
                    if (empty) android.view.View.GONE else android.view.View.VISIBLE
                findViewById<android.widget.TextView>(R.id.empty).visibility =
                    if (empty) android.view.View.VISIBLE else android.view.View.GONE
            }
        }
    }

    private fun openEditor(id: Long, isNew: Boolean = false) {
        startActivity(
            Intent(this, EditorActivity::class.java)
                .putExtra("note_id", id)
                .putExtra("is_new", isNew)
        )
    }

    private fun showSettingsMenu() {
        val items = arrayOf("Tema", "Ekspor JSON", "Ekspor TXT", "Impor JSON", "Folder backup otomatis")
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Pengaturan")
            .setItems(items) { _, which ->
                when (which) {
                    0 -> showThemeDialog()
                    1 -> lifecycleScope.launch(Dispatchers.IO) {
                        pendingExport = BackupHelper.toJson(dao.getAllNow())
                        launch(Dispatchers.Main) { createDoc.launch(BackupHelper.fileName("json")) }
                    }
                    2 -> lifecycleScope.launch(Dispatchers.IO) {
                        pendingExport = BackupHelper.toTxt(dao.getAllNow())
                        launch(Dispatchers.Main) { createDoc.launch(BackupHelper.fileName("txt")) }
                    }
                    3 -> openDoc.launch(arrayOf("application/json"))
                    4 -> openTree.launch(null)
                }
            }
            .show()
    }

    private fun toast(msg: String) {
        android.widget.Toast.makeText(this, msg, android.widget.Toast.LENGTH_SHORT).show()
    }
    private fun showThemeDialog() {
        val current = ThemePref.get(this)
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Tema")
            .setSingleChoiceItems(ThemePref.NAMES, current) { dialog, which ->
                if (which != current) {
                    ThemePref.set(this, which)
                    recreate()
                }
                dialog.dismiss()
            }
            .show()
    }
}
