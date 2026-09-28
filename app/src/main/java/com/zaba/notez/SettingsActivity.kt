package com.zaba.notez

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
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
        findViewById<TextView>(R.id.settings_row_privacy).setOnClickListener { showPrivacyDialog() }

        updateSummaries()
    }

    override fun onResume() {
        super.onResume()
        updateSummaries()
    }

    private fun updateSummaries() {
        findViewById<TextView>(R.id.settings_theme_value).text =
            ThemePref.NAMES.getOrElse(ThemePref.get(this)) { "GitHub Dark" }
        findViewById<TextView>(R.id.settings_version_value).text = versionName()
    }

    private fun showThemeDialog() {
        val current = ThemePref.get(this)
        AlertDialog.Builder(this)
            .setTitle("Tema")
            .setSingleChoiceItems(ThemePref.NAMES, current) { dialog, which ->
                dialog.dismiss()
                if (which != current) {
                    ThemePref.set(this, which)
                    recreate()
                }
            }
            .show()
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
