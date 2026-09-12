package com.zaba.notez

import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
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

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(ThemePref.styleOf(ThemePref.get(this)))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dao = AppDatabase.get(this).noteDao()
        findViewById<android.widget.ImageButton>(R.id.settings).setOnClickListener {
            showThemeDialog()
        }

        adapter = NoteAdapter(
            onOpen = { note -> openEditor(note.id) },
            onDelete = { note ->
                lifecycleScope.launch(Dispatchers.IO) { dao.delete(note) }
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
                launch(Dispatchers.Main) { openEditor(id) }
            }
        }
        findViewById<SearchView>(R.id.search).setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(q: String?) = false
                override fun onQueryTextChange(q: String?): Boolean {
                    query = q.orEmpty()
                    observe()
                    return true
                }
            }
        )
    }

    override fun onResume() {
        super.onResume()
        observe()
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

    private fun openEditor(id: Long) {
        startActivity(Intent(this, EditorActivity::class.java).putExtra("note_id", id))
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
