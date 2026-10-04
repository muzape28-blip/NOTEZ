package com.zaba.notez

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.webkit.WebView
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.widget.doAfterTextChanged
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.zaba.notez.markdown.MarkdownPreviewRenderer
import com.zaba.notez.diagnostics.PerfTracker
import android.os.SystemClock
import com.zaba.notez.music.MusicDrawerController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext

/**
 * Editor tanpa batas karakter + autosave 800ms.
 *
 * Dua mode:
 * - VIEW (default untuk catatan lama): judul & isi ditampilkan read-only, isi
 *   di-render sebagai Markdown lokal via WebView Reading View.
 * - EDIT (default untuk catatan baru, atau setelah tombol pensil ditekan):
 *   judul & isi jadi EditText biasa berisi teks Markdown mentah.
 */
class EditorActivity : AppCompatActivity() {

    private lateinit var dao: NoteDao
    private lateinit var drawer: DrawerLayout
    private lateinit var musicDrawer: MusicDrawerController
    private lateinit var markdownPreview: MarkdownPreviewRenderer
    private var perfTracker: PerfTracker? = null

    private lateinit var titleEdit: EditText
    private lateinit var titleView: TextView
    private lateinit var bodyEditContainer: View
    private lateinit var bodyEdit: EditText
    private lateinit var bodyWebView: WebView
    private lateinit var bodyEmptyView: TextView
    private lateinit var counter: TextView
    private lateinit var editToggle: ImageButton

    private var noteId: Long = -1
    private var loaded = false
    private var isEditing = false
    private var suppressAutosave = false

    // Nilai terakhir yang tersimpan/valid, dipakai saat render mode view.
    private var currentTitle = ""
    private var currentContent = ""

    private val handler = Handler(Looper.getMainLooper())
    private var saveTask: Runnable? = null
    private val saveMutex = Mutex()
    @Volatile private var saveGeneration = 0L

    private val openMusic = registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris: List<Uri> ->
        if (::musicDrawer.isInitialized) musicDrawer.onMusicPicked(uris)
    }

    private val saveAsLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("text/markdown")) { uri: Uri? ->
        if (uri == null) return@registerForActivityResult
        runCatching {
            contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
        }
        NoteFileTargetStore.setTargetUri(this, noteId, uri)
        writeToExternalUri(uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val openTime = intent.getLongExtra("open_started_at", SystemClock.uptimeMillis())
        noteId = intent.getLongExtra("note_id", -1)
        perfTracker = PerfTracker(noteId, openTime)
        setTheme(ThemePref.styleOf(ThemePref.get(this)))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)
        perfTracker?.mark("ACTIVITY_CREATED")
        dao = AppDatabase.get(this).noteDao()
        drawer = findViewById(R.id.drawer)
        UiStyle.applyDrawerGlass(findViewById(R.id.drawer_panel))
        drawer.post { perfTracker?.mark("LAYOUT_READY") }
        musicDrawer = MusicDrawerController(
            activity = this,
            root = drawer,
            onAddMusicRequested = { openMusic.launch(arrayOf("audio/*")) }
        )

        findViewById<View>(R.id.menu_file_save)?.setOnClickListener {
            drawer.closeDrawer(GravityCompat.START)
            handleSaveFile()
        }
        findViewById<View>(R.id.menu_file_save_as)?.setOnClickListener {
            drawer.closeDrawer(GravityCompat.START)
            handleSaveAs()
        }
        noteId = intent.getLongExtra("note_id", -1)
        isEditing = intent.getBooleanExtra("is_new", false)

        titleEdit = findViewById(R.id.edit_title)
        titleView = findViewById(R.id.view_title)
        bodyEditContainer = findViewById(R.id.edit_body_scroll)
        bodyEdit = findViewById(R.id.edit_body)
        bodyWebView = findViewById(R.id.view_body_web)
        bodyEmptyView = findViewById(R.id.view_body_empty)
        counter = findViewById(R.id.counter)
        editToggle = findViewById(R.id.edit_toggle)
        markdownPreview = MarkdownPreviewRenderer(this, bodyWebView)
        perfTracker?.mark("WEBVIEW_CREATED")
        markdownPreview.perfTracker = perfTracker

        lifecycleScope.launch {
            dao.getById(noteId)?.let {
                currentTitle = it.title
                currentContent = it.content
                titleEdit.setText(it.title)
                bodyEdit.setText(it.content)
                updateCounter(it.content.length, "")
            }
            perfTracker?.mark("NOTE_LOADED")
            loaded = true
            applyMode(isEditing)
        }

        val schedule = {
            currentTitle = titleEdit.text.toString()
            currentContent = bodyEdit.text.toString()
            updateCounter(bodyEdit.text.length, " • menyimpan...")
            saveTask?.let(handler::removeCallbacks)
            saveTask = Runnable { save(currentTitle, currentContent) }
            handler.postDelayed(saveTask!!, 800)
        }
        titleEdit.doAfterTextChanged { if (loaded && !suppressAutosave) schedule() }
        bodyEdit.doAfterTextChanged { if (loaded && !suppressAutosave) schedule() }

        editToggle.setOnClickListener {
            if (isEditing) switchToView() else switchToEdit()
        }
    }

    private fun switchToView() {
        saveTask?.let(handler::removeCallbacks)
        saveTask = null
        currentTitle = titleEdit.text.toString()
        currentContent = bodyEdit.text.toString()
        save(currentTitle, currentContent)
        isEditing = false
        titleEdit.clearFocus()
        bodyEdit.clearFocus()
        hideKeyboard()
        applyMode(false)
    }

    private fun switchToEdit() {
        isEditing = true
        suppressAutosave = true
        titleEdit.setText(currentTitle)
        bodyEdit.setText(currentContent)
        titleEdit.setSelection(titleEdit.text?.length ?: 0)
        bodyEdit.setSelection(bodyEdit.text?.length ?: 0)
        suppressAutosave = false
        applyMode(true)
        bodyEdit.requestFocus()
        showKeyboard(bodyEdit)
    }

    /** Tampilkan set view yang sesuai mode, dan render Markdown saat masuk mode view. */
    private fun applyMode(editing: Boolean) {
        val hasContent = currentContent.isNotBlank()
        titleEdit.visibility = if (editing) View.VISIBLE else View.GONE
        titleView.visibility = if (editing) View.GONE else View.VISIBLE
        bodyEditContainer.visibility = if (editing) View.VISIBLE else View.GONE
        bodyWebView.visibility = if (!editing && hasContent) View.VISIBLE else View.GONE
        bodyEmptyView.visibility = if (!editing && !hasContent) View.VISIBLE else View.GONE
        counter.visibility = if (editing) View.VISIBLE else View.GONE
        editToggle.setImageResource(if (editing) R.drawable.ic_done_check else R.drawable.ic_edit_pencil)
        editToggle.contentDescription =
            getString(if (editing) R.string.cd_done_editing else R.string.cd_edit_note)

        if (!editing) {
            titleEdit.clearFocus()
            bodyEdit.clearFocus()
            hideKeyboard()
            titleView.text = currentTitle.ifBlank { getString(R.string.untitled_note) }
            if (hasContent) {
                markdownPreview.render(currentContent)
            }
        }
    }

    private fun updateCounter(bodyLength: Int, suffix: String) {
        counter.text = "$bodyLength karakter$suffix"
    }

    @Synchronized
    private fun nextSaveGeneration(): Long {
        saveGeneration += 1
        return saveGeneration
    }

    private fun save(title: String, body: String) {
        val generation = nextSaveGeneration()
        // Tulis DB di IO thread — jangan block UI (penyebab scroll tersendat).
        // Generasi + mutex mencegah save lama menimpa snapshot yang lebih baru.
        lifecycleScope.launch(Dispatchers.IO) {
            saveSnapshot(title, body, generation, updateUi = true)
        }
    }

    private fun saveBlocking(title: String, body: String) {
        val generation = nextSaveGeneration()
        // onPause adalah batas persistence terakhir Activity. Save ini sengaja
        // menunggu DB write selesai agar snapshot terakhir tidak ikut tercancel
        // bersama lifecycleScope saat Activity dihancurkan.
        runBlocking(Dispatchers.IO) {
            saveSnapshot(title, body, generation, updateUi = false)
        }
    }

    private suspend fun saveSnapshot(
        title: String,
        body: String,
        generation: Long,
        updateUi: Boolean
    ) {
        var saved = false
        saveMutex.lock()
        try {
            if (generation == saveGeneration) {
                val existing = dao.getById(noteId)
                if (existing != null && generation == saveGeneration) {
                    dao.update(existing.copy(title = title, content = body, updatedAt = System.currentTimeMillis()))
                    saved = true
                }
            }
        } finally {
            saveMutex.unlock()
        }
        if (saved && updateUi && generation == saveGeneration) {
            withContext(Dispatchers.Main) {
                if (isEditing && generation == saveGeneration) updateCounter(body.length, " • tersimpan")
            }
        }
    }

    private fun showKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val token = currentFocus?.windowToken ?: window.decorView.windowToken
        imm.hideSoftInputFromWindow(token, 0)
    }

    private fun handleSaveFile() {
        val targetUri = NoteFileTargetStore.getTargetUri(this, noteId)
        if (targetUri != null) {
            writeToExternalUri(targetUri)
        } else {
            handleSaveAs()
        }
    }

    private fun handleSaveAs() {
        val title = if (isEditing) titleEdit.text.toString() else currentTitle
        val body = if (isEditing) bodyEdit.text.toString() else currentContent
        val suggestedName = NoteFileTargetStore.suggestFilename(title, body)
        saveAsLauncher.launch(suggestedName)
    }

    private fun writeToExternalUri(uri: Uri) {
        val title = if (isEditing) titleEdit.text.toString() else currentTitle
        val body = if (isEditing) bodyEdit.text.toString() else currentContent
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                contentResolver.openOutputStream(uri, "wt")?.use { stream ->
                    stream.write(body.toByteArray(Charsets.UTF_8))
                }
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditorActivity, "File tersimpan", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditorActivity, "Gagal menyimpan file: " + e.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onBackPressed() {
        if (::drawer.isInitialized && drawer.isDrawerVisible(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onPause() {
        saveTask?.let(handler::removeCallbacks)
        saveTask = null
        val title = if (isEditing) titleEdit.text.toString() else currentTitle
        val body = if (isEditing) bodyEdit.text.toString() else currentContent
        currentTitle = title
        currentContent = body
        saveBlocking(title, body)
        super.onPause()
    }

    override fun onDestroy() {
        if (::musicDrawer.isInitialized) musicDrawer.destroy()
        if (::markdownPreview.isInitialized) markdownPreview.destroy()
        super.onDestroy()
    }
}
