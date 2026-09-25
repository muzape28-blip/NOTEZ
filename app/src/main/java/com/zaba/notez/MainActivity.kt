package com.zaba.notez

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.widget.doAfterTextChanged
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.zaba.notez.music.MusicDrawerController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var dao: NoteDao
    private lateinit var adapter: NoteAdapter
    private lateinit var drawer: DrawerLayout
    private lateinit var musicDrawer: MusicDrawerController
    private var collectJob: Job? = null
    private var query = ""
    private var pendingExport: String? = null
    private var pendingDrawerAction: (() -> Unit)? = null
    private var searchOpen = false
    private var settingsExpanded = false

    private val createDoc = registerForActivityResult(ActivityResultContracts.CreateDocument("*/*")) { uri ->
        val data = pendingExport ?: return@registerForActivityResult
        pendingExport = null
        if (uri == null) return@registerForActivityResult
        lifecycleScope.launch(Dispatchers.IO) {
            contentResolver.openOutputStream(uri)?.use { it.write(data.toByteArray()) }
            launch(Dispatchers.Main) { toast("Diekspor") }
        }
    }

    private val openDoc = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
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

    private val openMusic = registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        if (::musicDrawer.isInitialized) musicDrawer.onMusicPicked(uris)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(ThemePref.styleOf(ThemePref.get(this)))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dao = AppDatabase.get(this).noteDao()
        drawer = findViewById(R.id.drawer)
        setupDrawer()
        musicDrawer = MusicDrawerController(
            activity = this,
            root = drawer,
            onAddMusicRequested = { openMusic.launch(arrayOf("audio/*")) }
        )

        adapter = NoteAdapter(
            onOpen = { note -> openEditor(note.id) },
            onDelete = { note -> deleteWithUndo(note) }
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

    private fun setupDrawer() {
        val submenu = findViewById<View>(R.id.settings_submenu)
        findViewById<TextView>(R.id.drawer_settings).setOnClickListener {
            settingsExpanded = !settingsExpanded
            submenu.visibility = if (settingsExpanded) View.VISIBLE else View.GONE
        }
        findViewById<View>(R.id.menu_theme).setOnClickListener {
            closeDrawerThen { showThemeDialog() }
        }
        findViewById<View>(R.id.menu_export_json).setOnClickListener {
            closeDrawerThen { exportJson() }
        }
        findViewById<View>(R.id.menu_export_txt).setOnClickListener {
            closeDrawerThen { exportTxt() }
        }
        findViewById<View>(R.id.menu_import_json).setOnClickListener {
            closeDrawerThen { openDoc.launch(arrayOf("application/json")) }
        }
        findViewById<View>(R.id.menu_auto_backup_folder).setOnClickListener {
            closeDrawerThen { openTree.launch(null) }
        }
        drawer.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerClosed(drawerView: View) {
                val action = pendingDrawerAction ?: return
                pendingDrawerAction = null
                action()
            }
        })
    }

    private fun closeDrawerThen(action: () -> Unit) {
        if (drawer.isDrawerVisible(GravityCompat.START)) {
            pendingDrawerAction = action
            drawer.closeDrawer(GravityCompat.START)
        } else {
            action()
        }
    }

    private fun deleteWithUndo(note: Note) {
        lifecycleScope.launch(Dispatchers.IO) {
            dao.delete(note)
            launch(Dispatchers.Main) {
                showUndoDeleteSnackbar(note)
            }
        }
    }

    private fun showUndoDeleteSnackbar(note: Note) {
        var undone = false
        Snackbar.make(findViewById(R.id.drawer), "Catatan dihapus", Snackbar.LENGTH_LONG)
            .setAction("URUNGKAN") {
                undone = true
                lifecycleScope.launch(Dispatchers.IO) { dao.upsert(note) }
            }
            .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    if (!undone && event != BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_ACTION) {
                        lifecycleScope.launch(Dispatchers.IO) { vacuumDeletedNotes() }
                    }
                }
            })
            .show()
    }

    private fun vacuumDeletedNotes() {
        try {
            AppDatabase.get(this).openHelper.writableDatabase.execSQL("VACUUM")
        } catch (_: Exception) {
            // VACUUM is best-effort cleanup after the undo window; data correctness
            // already comes from the delete/undo writes above.
        }
    }

    private fun openSearch(toggle: ImageButton, input: EditText) {
        searchOpen = true
        input.visibility = View.VISIBLE
        toggle.setImageResource(R.drawable.ic_eye)
        toggle.contentDescription = getString(R.string.cd_close_search)
        input.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun closeSearch(toggle: ImageButton, input: EditText) {
        searchOpen = false
        input.setText("")
        input.visibility = View.INVISIBLE
        toggle.setImageResource(R.drawable.ic_eye)
        toggle.contentDescription = getString(R.string.cd_open_search)
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(input.windowToken, 0)
    }

    override fun onBackPressed() {
        if (drawer.isDrawerVisible(GravityCompat.START)) {
            pendingDrawerAction = null
            drawer.closeDrawer(GravityCompat.START)
        } else if (searchOpen) {
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

    override fun onDestroy() {
        if (::musicDrawer.isInitialized) musicDrawer.destroy()
        super.onDestroy()
    }

    private fun observe() {
        collectJob?.cancel()
        val flow = if (query.isBlank()) dao.observeAll() else dao.search(query)
        collectJob = lifecycleScope.launch {
            flow.collectLatest {
                adapter.submit(it)
                val empty = it.isEmpty()
                findViewById<RecyclerView>(R.id.list).visibility =
                    if (empty) View.GONE else View.VISIBLE
                findViewById<TextView>(R.id.empty).visibility =
                    if (empty) View.VISIBLE else View.GONE
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

    private fun exportJson() {
        lifecycleScope.launch(Dispatchers.IO) {
            val data = BackupHelper.toJson(dao.getAllNow())
            launch(Dispatchers.Main) {
                pendingExport = data
                createDoc.launch(BackupHelper.fileName("json"))
            }
        }
    }

    private fun exportTxt() {
        lifecycleScope.launch(Dispatchers.IO) {
            val data = BackupHelper.toTxt(dao.getAllNow())
            launch(Dispatchers.Main) {
                pendingExport = data
                createDoc.launch(BackupHelper.fileName("txt"))
            }
        }
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
