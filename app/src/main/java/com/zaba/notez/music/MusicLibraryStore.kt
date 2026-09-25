package com.zaba.notez.music

import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.OpenableColumns
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID

/** Local-only music library. Disimpan terpisah dari Room notes DB. */
data class MusicTrack(
    val id: String,
    val uri: String,
    val displayName: String,
    val title: String,
    val artist: String,
    val durationMs: Long,
    val mimeType: String,
    val addedAt: Long,
    val lastPlayedAt: Long = 0L
)

enum class MusicPlaybackMode {
    LOOP_ALL,
    ONCE,
    SHUFFLE;

    fun next(): MusicPlaybackMode = when (this) {
        LOOP_ALL -> ONCE
        ONCE -> SHUFFLE
        SHUFFLE -> LOOP_ALL
    }

    fun label(): String = when (this) {
        LOOP_ALL -> "Loop All"
        ONCE -> "Once"
        SHUFFLE -> "Shuffle"
    }
}

data class MusicLibrarySnapshot(
    val tracks: List<MusicTrack>,
    val playbackMode: MusicPlaybackMode,
    val currentTrackId: String?
)

object MusicLibraryStore {
    private const val FILE_NAME = "music_library.json"
    private const val VERSION = 1

    @Synchronized
    fun load(context: Context): MusicLibrarySnapshot = read(context)

    @Synchronized
    fun addUris(context: Context, uris: List<Uri>): MusicLibrarySnapshot {
        if (uris.isEmpty()) return read(context)
        val current = read(context)
        val byUri = current.tracks.associateBy { it.uri }.toMutableMap()
        val now = System.currentTimeMillis()
        for (uri in uris) {
            val key = uri.toString()
            if (byUri.containsKey(key)) continue
            persistReadPermission(context, uri)
            val metadata = readMetadata(context, uri, now)
            byUri[key] = metadata
        }
        val tracks = byUri.values.sortedBy { it.addedAt }
        val currentId = current.currentTrackId ?: tracks.firstOrNull()?.id
        val next = MusicLibrarySnapshot(tracks, current.playbackMode, currentId)
        write(context, next)
        return next
    }

    @Synchronized
    fun savePlaybackState(
        context: Context,
        mode: MusicPlaybackMode,
        currentTrackId: String?
    ): MusicLibrarySnapshot {
        val current = read(context)
        val next = current.copy(playbackMode = mode, currentTrackId = currentTrackId)
        write(context, next)
        return next
    }

    @Synchronized
    fun touchLastPlayed(context: Context, trackId: String) {
        val current = read(context)
        val now = System.currentTimeMillis()
        val tracks = current.tracks.map {
            if (it.id == trackId) it.copy(lastPlayedAt = now) else it
        }
        write(context, current.copy(tracks = tracks, currentTrackId = trackId))
    }

    private fun file(context: Context): File = File(context.filesDir, FILE_NAME)

    private fun read(context: Context): MusicLibrarySnapshot {
        val f = file(context)
        if (!f.exists()) return MusicLibrarySnapshot(emptyList(), MusicPlaybackMode.LOOP_ALL, null)
        return try {
            val root = JSONObject(f.readText())
            val mode = runCatching {
                MusicPlaybackMode.valueOf(root.optString("playbackMode", MusicPlaybackMode.LOOP_ALL.name))
            }.getOrDefault(MusicPlaybackMode.LOOP_ALL)
            val currentTrackId = root.optString("currentTrackId", "").takeIf { it.isNotBlank() }
            val arr = root.optJSONArray("tracks") ?: JSONArray()
            val tracks = buildList {
                for (i in 0 until arr.length()) {
                    val o = arr.optJSONObject(i) ?: continue
                    val id = o.optString("id")
                    val uri = o.optString("uri")
                    if (id.isBlank() || uri.isBlank()) continue
                    add(
                        MusicTrack(
                            id = id,
                            uri = uri,
                            displayName = o.optString("displayName"),
                            title = o.optString("title"),
                            artist = o.optString("artist"),
                            durationMs = o.optLong("durationMs", 0L),
                            mimeType = o.optString("mimeType"),
                            addedAt = o.optLong("addedAt", 0L),
                            lastPlayedAt = o.optLong("lastPlayedAt", 0L)
                        )
                    )
                }
            }
            MusicLibrarySnapshot(tracks, mode, currentTrackId)
        } catch (_: Exception) {
            // Music library corruption must not affect notes.
            MusicLibrarySnapshot(emptyList(), MusicPlaybackMode.LOOP_ALL, null)
        }
    }

    private fun write(context: Context, snapshot: MusicLibrarySnapshot) {
        val arr = JSONArray()
        for (track in snapshot.tracks) {
            arr.put(
                JSONObject()
                    .put("id", track.id)
                    .put("uri", track.uri)
                    .put("displayName", track.displayName)
                    .put("title", track.title)
                    .put("artist", track.artist)
                    .put("durationMs", track.durationMs)
                    .put("mimeType", track.mimeType)
                    .put("addedAt", track.addedAt)
                    .put("lastPlayedAt", track.lastPlayedAt)
            )
        }
        val root = JSONObject()
            .put("version", VERSION)
            .put("playbackMode", snapshot.playbackMode.name)
            .put("currentTrackId", snapshot.currentTrackId.orEmpty())
            .put("tracks", arr)
        file(context).writeText(root.toString())
    }

    private fun persistReadPermission(context: Context, uri: Uri) {
        try {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (_: SecurityException) {
            // Some providers don't offer persistable permissions. Playback will be
            // attempted while the transient grant remains valid; invalid URI paths
            // are handled by the player error path.
        }
    }

    private fun readMetadata(context: Context, uri: Uri, addedAt: Long): MusicTrack {
        val resolver = context.contentResolver
        val mime = resolver.getType(uri).orEmpty()
        val displayName = resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c ->
            if (c.moveToFirst()) c.getString(0) else null
        }.orEmpty().ifBlank { uri.lastPathSegment.orEmpty().ifBlank { "audio" } }

        var title = displayName.substringBeforeLast('.', displayName)
        var artist = ""
        var duration = 0L
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(context, uri)
            title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                ?.takeIf { it.isNotBlank() } ?: title
            artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                ?.takeIf { it.isNotBlank() }.orEmpty()
            duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLongOrNull() ?: 0L
        } catch (_: Exception) {
            // Metadata is optional; URI playback is the real contract.
        } finally {
            try { retriever.release() } catch (_: Exception) { }
        }

        return MusicTrack(
            id = UUID.randomUUID().toString(),
            uri = uri.toString(),
            displayName = displayName,
            title = title,
            artist = artist,
            durationMs = duration,
            mimeType = mime,
            addedAt = addedAt
        )
    }
}
