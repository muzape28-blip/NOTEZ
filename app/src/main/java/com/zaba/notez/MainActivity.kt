package com.zaba.notez

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
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
    private var notePresenceJob: Job? = null
    private var query = ""
    private var pendingDrawerAction: (() -> Unit)? = null
    private var searchOpen = false
    private var hasAnyNotes = false
    private var appliedTheme = ThemePref.GITHUB_DARK
    private var startupSplashAnimator: AnimatorSet? = null
    private var startupSplashStatusBarColor: Int? = null
    private var startupSplashNavigationBarColor: Int? = null
    private var startupSplashSystemUiVisibility: Int? = null

    private val openMusic = registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        if (::musicDrawer.isInitialized) musicDrawer.onMusicPicked(uris)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        appliedTheme = ThemePref.get(this)
        setTheme(ThemePref.styleOf(appliedTheme))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        applyHomeSystemBars()
        playStartupSplash(savedInstanceState)
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
        findViewById<TextView>(R.id.empty_about_link).setOnClickListener {
            startActivity(Intent(this, AboutNotezActivity::class.java))
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
        observeNotePresence(searchToggle, searchInput)
    }

    private fun playStartupSplash(savedInstanceState: Bundle?) {
        val overlay = findViewById<View>(R.id.splash_overlay)
        if (savedInstanceState != null || animatorDurationScale() == 0f) {
            overlay.visibility = View.GONE
            return
        }

        prepareStartupSplashSystemBars()
        overlay.visibility = View.VISIBLE
        overlay.alpha = 1f
        overlay.isClickable = true
        overlay.bringToFront()

        val glow = findViewById<ImageView>(R.id.splash_glow)
        val horse = findViewById<ImageView>(R.id.splash_horse)
        val accent = findViewById<ImageView>(R.id.splash_accent)

        overlay.post {
            if (isFinishing || isDestroyed) {
                hideStartupSplash(overlay)
                return@post
            }

            glow.alpha = 0f
            glow.scaleX = 1.02f
            glow.scaleY = 1.02f
            horse.alpha = 0f
            horse.scaleX = 0.96f
            horse.scaleY = 0.96f
            horse.translationY = 10f
            accent.alpha = 0f
            accent.scaleX = 0.72f
            accent.scaleY = 0.72f
            accent.translationY = 14f
            accent.pivotX = accent.width * 0.34f
            accent.pivotY = accent.height * 0.78f

            startupSplashAnimator = AnimatorSet().apply {
                playTogether(
                    ObjectAnimator.ofFloat(horse, View.ALPHA, 0f, 1f).timed(260L, 80L),
                    ObjectAnimator.ofFloat(horse, View.SCALE_X, 0.96f, 1f).timed(380L, 80L),
                    ObjectAnimator.ofFloat(horse, View.SCALE_Y, 0.96f, 1f).timed(380L, 80L),
                    ObjectAnimator.ofFloat(horse, View.TRANSLATION_Y, 10f, 0f).timed(380L, 80L),
                    ObjectAnimator.ofFloat(glow, View.ALPHA, 0f, 0.82f, 0.46f).timed(680L, 210L),
                    ObjectAnimator.ofFloat(glow, View.SCALE_X, 1.02f, 1.12f).timed(680L, 210L),
                    ObjectAnimator.ofFloat(glow, View.SCALE_Y, 1.02f, 1.12f).timed(680L, 210L),
                    ObjectAnimator.ofFloat(accent, View.ALPHA, 0f, 1f, 0.9f).timed(430L, 420L),
                    ObjectAnimator.ofFloat(accent, View.SCALE_X, 0.72f, 1f).timed(430L, 420L),
                    ObjectAnimator.ofFloat(accent, View.SCALE_Y, 0.72f, 1f).timed(430L, 420L),
                    ObjectAnimator.ofFloat(accent, View.TRANSLATION_Y, 14f, 0f).timed(430L, 420L),
                    ObjectAnimator.ofFloat(overlay, View.ALPHA, 1f, 0f).timed(180L, 1040L)
                )
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) = hideStartupSplash(overlay)
                    override fun onAnimationCancel(animation: Animator) = hideStartupSplash(overlay)
                })
                start()
            }
        }
    }

    private fun ObjectAnimator.timed(durationMs: Long, delayMs: Long): ObjectAnimator = apply {
        duration = durationMs
        startDelay = delayMs
        interpolator = DecelerateInterpolator()
    }

    private fun applyHomeSystemBars() {
        val background = getColor(ThemePref.optionOf(appliedTheme).backgroundColorRes)
        window.statusBarColor = background
        window.navigationBarColor = background
        val lightBars = isLightColor(background)
        window.decorView.systemUiVisibility = if (lightBars) {
            window.decorView.systemUiVisibility or
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        } else {
            window.decorView.systemUiVisibility and
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv() and
                View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
        }
    }

    private fun isLightColor(color: Int): Boolean {
        val luminance = (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255.0
        return luminance > 0.62
    }

    private fun prepareStartupSplashSystemBars() {
        if (startupSplashStatusBarColor == null) {
            startupSplashStatusBarColor = window.statusBarColor
            startupSplashNavigationBarColor = window.navigationBarColor
            startupSplashSystemUiVisibility = window.decorView.systemUiVisibility
        }
        window.statusBarColor = getColor(R.color.github_bg)
        window.navigationBarColor = getColor(R.color.github_bg)
        window.decorView.systemUiVisibility = window.decorView.systemUiVisibility and
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv() and
            View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
    }

    private fun restoreStartupSplashSystemBars() {
        startupSplashStatusBarColor?.let { window.statusBarColor = it }
        startupSplashNavigationBarColor?.let { window.navigationBarColor = it }
        startupSplashSystemUiVisibility?.let { window.decorView.systemUiVisibility = it }
        startupSplashStatusBarColor = null
        startupSplashNavigationBarColor = null
        startupSplashSystemUiVisibility = null
    }

    private fun hideStartupSplash(overlay: View) {
        overlay.visibility = View.GONE
        overlay.alpha = 0f
        overlay.isClickable = false
        startupSplashAnimator = null
        restoreStartupSplashSystemBars()
    }

    private fun animatorDurationScale(): Float = try {
        Settings.Global.getFloat(contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f)
    } catch (_: Exception) {
        1f
    }

    private fun setupDrawer() {
        findViewById<View>(R.id.menu_markdown_guide).setOnClickListener {
            closeDrawerThen { startActivity(Intent(this, MarkdownGuideActivity::class.java)) }
        }
        findViewById<TextView>(R.id.drawer_settings).setOnClickListener {
            closeDrawerThen { startActivity(Intent(this, SettingsActivity::class.java)) }
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

    private fun observeNotePresence(toggle: ImageButton, input: EditText) {
        notePresenceJob?.cancel()
        notePresenceJob = lifecycleScope.launch {
            dao.observeAll().collectLatest { notes ->
                hasAnyNotes = notes.isNotEmpty()
                updateSearchAvailability(toggle, input)
            }
        }
    }

    private fun updateSearchAvailability(toggle: ImageButton, input: EditText) {
        if (hasAnyNotes) {
            toggle.visibility = View.VISIBLE
            return
        }
        if (searchOpen) closeSearch(toggle, input)
        toggle.visibility = View.GONE
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
        val currentTheme = ThemePref.get(this)
        if (currentTheme != appliedTheme) {
            recreate()
            return
        }
        observe()
        lifecycleScope.launch(Dispatchers.IO) {
            if (BackupHelper.autoBackupIfDue(this@MainActivity, dao)) {
                launch(Dispatchers.Main) { toast("Backup otomatis tersimpan") }
            }
        }
    }

    override fun onDestroy() {
        startupSplashAnimator?.cancel()
        startupSplashAnimator = null
        notePresenceJob?.cancel()
        notePresenceJob = null
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
                val searching = query.isNotBlank()
                findViewById<RecyclerView>(R.id.list).visibility =
                    if (empty) View.GONE else View.VISIBLE
                findViewById<View>(R.id.empty_welcome).visibility =
                    if (empty && !searching) View.VISIBLE else View.GONE
                findViewById<TextView>(R.id.empty_search).visibility =
                    if (empty && searching) View.VISIBLE else View.GONE
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

    private fun toast(msg: String) {
        android.widget.Toast.makeText(this, msg, android.widget.Toast.LENGTH_SHORT).show()
    }
}
