package com.zaba.notez

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Editor tanpa batas karakter + autosave 800ms. */
class EditorActivity : AppCompatActivity() {

    private lateinit var dao: NoteDao
    private var noteId: Long = -1
    private var loaded = false
    private val handler = Handler(Looper.getMainLooper())
    private var saveTask: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(ThemePref.styleOf(ThemePref.get(this)))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)
        dao = AppDatabase.get(this).noteDao()
        noteId = intent.getLongExtra("note_id", -1)

        val title = findViewById<EditText>(R.id.edit_title)
        val body = findViewById<EditText>(R.id.edit_body)
        val counter = findViewById<TextView>(R.id.counter)

        lifecycleScope.launch {
            dao.getById(noteId)?.let {
                title.setText(it.title)
                body.setText(it.content)
                counter.text = "${it.content.length} karakter (tanpa batas)"
            }
            loaded = true
        }

        val schedule = {
            counter.text = "${body.text.length} karakter (tanpa batas) • menyimpan..."
            saveTask?.let(handler::removeCallbacks)
            saveTask = Runnable { save(title.text.toString(), body.text.toString()) }
            handler.postDelayed(saveTask!!, 800)
        }
        title.doAfterTextChanged { if (loaded) schedule() }
        body.doAfterTextChanged { if (loaded) schedule() }
    }

    private fun save(title: String, body: String) {
        // Tulis DB di IO thread — jangan block UI (penyebab scroll tersendat)
        lifecycleScope.launch(Dispatchers.IO) {
            dao.getById(noteId)?.let {
                dao.update(it.copy(title = title, content = body, updatedAt = System.currentTimeMillis()))
                launch(Dispatchers.Main) {
                    findViewById<TextView>(R.id.counter).text =
                        "${body.length} karakter (tanpa batas) • tersimpan"
                }
            }
        }
    }

    override fun onPause() {
        saveTask?.let(handler::removeCallbacks)
        save(
            findViewById<EditText>(R.id.edit_title).text.toString(),
            findViewById<EditText>(R.id.edit_body).text.toString()
        )
        super.onPause()
    }
}
