package com.zaba.notez

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Ekspor / impor / backup otomatis NOTEZ.
 * Semua lewat Storage Access Framework — tanpa permission, tetap offline.
 */
object BackupHelper {

    private const val PREF = "notez"
    private const val KEY_TREE = "backup_tree"
    private const val KEY_LAST_AUTO = "backup_last_auto"
    private const val AUTO_INTERVAL_MS = 24L * 60 * 60 * 1000

    fun fileName(ext: String): String {
        val t = SimpleDateFormat("yyyyMMdd-HHmm", Locale.US).format(Date())
        return "notez-backup-$t.$ext"
    }

    fun toJson(notes: List<Note>): String {
        val arr = JSONArray()
        for (n in notes) {
            arr.put(JSONObject()
                .put("id", n.id)
                .put("title", n.title)
                .put("content", n.content)
                .put("updatedAt", n.updatedAt))
        }
        return JSONObject().put("app", "NOTEZ").put("notes", arr).toString()
    }

    fun toTxt(notes: List<Note>): String = buildString {
        for (n in notes) {
            appendLine("=== ${n.title} ===")
            appendLine(n.content)
            appendLine()
        }
    }

    fun fromJson(raw: String): List<Note> {
        val out = mutableListOf<Note>()
        val arr = JSONObject(raw).getJSONArray("notes")
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            out.add(Note(
                id = o.optLong("id"),
                title = o.optString("title"),
                content = o.optString("content"),
                updatedAt = o.optLong("updatedAt", System.currentTimeMillis())
            ))
        }
        return out
    }

    fun getTree(context: Context): Uri? =
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getString(KEY_TREE, null)?.let(Uri::parse)

    fun setTree(context: Context, uri: Uri) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit().putString(KEY_TREE, uri.toString()).apply()
    }

    /** Tulis backup otomatis jika folder dipilih & terakhir >24 jam lalu. Return true jika menulis. */
    suspend fun autoBackupIfDue(context: Context, dao: NoteDao): Boolean {
        val tree = getTree(context) ?: return false
        val prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        if (System.currentTimeMillis() - prefs.getLong(KEY_LAST_AUTO, 0) < AUTO_INTERVAL_MS) return false
        val notes = dao.getAllNow()
        val dir = DocumentFile.fromTreeUri(context, tree) ?: return false
        val f = dir.createFile("application/json", fileName("json")) ?: return false
        context.contentResolver.openOutputStream(f.uri)?.use { os ->
            os.write(toJson(notes).toByteArray())
        } ?: return false
        prefs.edit().putLong(KEY_LAST_AUTO, System.currentTimeMillis()).apply()
        return true
    }
}
