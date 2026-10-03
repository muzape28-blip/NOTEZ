package com.zaba.notez

import android.content.Context
import android.net.Uri

object NoteFileTargetStore {
    private const val PREF_NAME = "notez_file_targets"

    fun getTargetUri(context: Context, noteId: Long): Uri? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val uriStr = prefs.getString(noteId.toString(), null) ?: return null
        return runCatching { Uri.parse(uriStr) }.getOrNull()
    }

    fun setTargetUri(context: Context, noteId: Long, uri: Uri) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(noteId.toString(), uri.toString()).apply()
    }

    fun clearTargetUri(context: Context, noteId: Long) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(noteId.toString()).apply()
    }

    fun suggestFilename(title: String, body: String): String {
        val raw = when {
            title.isNotBlank() -> title
            body.isNotBlank() -> body.lines().firstOrNull { it.isNotBlank() }?.take(30) ?: "Catatan NOTEZ"
            else -> "Catatan NOTEZ"
        }
        val sanitized = raw.replace(Regex("""[\\/:*?"<>|]"""), "_").trim()
        val name = if (sanitized.isBlank()) "Catatan NOTEZ" else sanitized
        return if (name.endsWith(".md", ignoreCase = true)) name else "$name.md"
    }
}
