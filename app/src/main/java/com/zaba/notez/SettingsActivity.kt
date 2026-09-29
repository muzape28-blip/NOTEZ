package com.zaba.notez

import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    private lateinit var dao: NoteDao
    private var pendingExport: String? = null

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
            for (note in notes) dao.upsert(note)
            launch(Dispatchers.Main) { toast("${notes.size} catatan dipulihkan") }
        }
    }

    private val openTree = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri == null) return@registerForActivityResult
        contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )
        BackupHelper.setTree(this, uri)
        toast("Folder backup otomatis aktif")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(ThemePref.styleOf(ThemePref.get(this)))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        dao = AppDatabase.get(this).noteDao()

        findViewById<ImageButton>(R.id.settings_back).setOnClickListener { finish() }
        findViewById<android.view.View>(R.id.settings_row_theme).setOnClickListener { showThemeDialog() }
        findViewById<TextView>(R.id.settings_row_export_json).setOnClickListener { exportJson() }
        findViewById<TextView>(R.id.settings_row_import_json).setOnClickListener {
            openDoc.launch(arrayOf("application/json"))
        }
        findViewById<TextView>(R.id.settings_row_export_txt).setOnClickListener { exportTxt() }
        findViewById<TextView>(R.id.settings_row_auto_backup_folder).setOnClickListener {
            openTree.launch(null)
        }
        findViewById<TextView>(R.id.settings_row_about_notez).setOnClickListener {
            startActivity(Intent(this, AboutNotezActivity::class.java))
        }
        findViewById<TextView>(R.id.settings_row_privacy).setOnClickListener { showPrivacyDialog() }

        updateSummaries()
    }

    override fun onResume() {
        super.onResume()
        updateSummaries()
    }

    private fun updateSummaries() {
        findViewById<TextView>(R.id.settings_theme_value).text = ThemePref.nameOf(ThemePref.get(this))
        findViewById<TextView>(R.id.settings_version_value).text = versionName()
    }

    private fun showThemeDialog() {
        val current = ThemePref.get(this)
        val list = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, dp(8), 0, dp(8))
        }
        ThemePref.OPTIONS.forEach { option ->
            list.addView(themeOptionRow(option, current))
        }

        AlertDialog.Builder(this)
            .setTitle("Tema")
            .setView(
                ScrollView(this).apply {
                    addView(list)
                }
            )
            .show()
    }

    private fun themeOptionRow(option: ThemePref.ThemeOption, current: Int): View {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            minimumHeight = dp(62)
            setPadding(dp(16), dp(6), dp(16), dp(6))
            isClickable = true
            isFocusable = true
            background = selectableItemBackground()
            setOnClickListener {
                if (option.value != current) {
                    ThemePref.set(this@SettingsActivity, option.value)
                    recreate()
                }
            }

            addView(themePreview(option))
            addView(
                LinearLayout(this@SettingsActivity).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(dp(12), 0, dp(8), 0)
                    layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                    addView(
                        TextView(this@SettingsActivity).apply {
                            text = option.name
                            setTextColor(getColor(option.textColorRes))
                            textSize = 14f
                            typeface = Typeface.DEFAULT_BOLD
                        }
                    )
                    addView(
                        TextView(this@SettingsActivity).apply {
                            text = option.description
                            setTextColor(getColor(option.secondaryColorRes))
                            textSize = 11f
                        }
                    )
                }
            )
            addView(
                TextView(this@SettingsActivity).apply {
                    text = if (option.value == current) "✓" else ""
                    setTextColor(getColor(option.accentColorRes))
                    textSize = 20f
                    typeface = Typeface.DEFAULT_BOLD
                    gravity = Gravity.CENTER
                    layoutParams = LinearLayout.LayoutParams(dp(28), ViewGroup.LayoutParams.WRAP_CONTENT)
                }
            )
        }
    }

    private fun themePreview(option: ThemePref.ThemeOption): View {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(7), dp(6), dp(7), dp(6))
            background = roundedDrawable(
                fillColor = getColor(option.backgroundColorRes),
                strokeColor = getColor(option.outlineColorRes),
                strokeWidth = dp(1),
                radius = dp(12).toFloat()
            )
            layoutParams = LinearLayout.LayoutParams(dp(70), dp(48))

            addView(
                TextView(this@SettingsActivity).apply {
                    text = "Aa"
                    setTextColor(getColor(option.textColorRes))
                    textSize = 12f
                    typeface = Typeface.DEFAULT_BOLD
                    includeFontPadding = false
                }
            )
            addView(
                View(this@SettingsActivity).apply {
                    background = roundedDrawable(getColor(option.surfaceColorRes), getColor(option.outlineColorRes), dp(1), dp(4).toFloat())
                    layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(10)).apply {
                        topMargin = dp(4)
                    }
                }
            )
            addView(
                View(this@SettingsActivity).apply {
                    background = roundedDrawable(getColor(option.accentColorRes), getColor(option.accentColorRes), 0, dp(4).toFloat())
                    layoutParams = LinearLayout.LayoutParams(dp(34), dp(5)).apply {
                        topMargin = dp(5)
                    }
                }
            )
        }
    }

    private fun roundedDrawable(
        fillColor: Int,
        strokeColor: Int,
        strokeWidth: Int,
        radius: Float
    ): GradientDrawable = GradientDrawable().apply {
        setColor(fillColor)
        cornerRadius = radius
        if (strokeWidth > 0) setStroke(strokeWidth, strokeColor)
    }

    private fun selectableItemBackground() = android.util.TypedValue().let { outValue ->
        theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
        getDrawable(outValue.resourceId)
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

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

    private fun showPrivacyDialog() {
        AlertDialog.Builder(this)
            .setTitle("Privacy / Offline")
            .setMessage(
                "NOTEZ tetap lokal dan offline-first.\n\n" +
                    "• Tidak memakai permission INTERNET.\n" +
                    "• Markdown renderer memakai asset lokal dari APK.\n" +
                    "• Remote image tidak auto-load.\n" +
                    "• Link eksternal dibuka lewat aplikasi/browser luar saat user tap."
            )
            .setPositiveButton("OK", null)
            .show()
    }

    @Suppress("DEPRECATION")
    private fun versionName(): String = try {
        packageManager.getPackageInfo(packageName, 0).versionName ?: "unknown"
    } catch (_: Exception) {
        "unknown"
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
