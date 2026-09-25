package com.zaba.notez.music

import android.content.ComponentName
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.zaba.notez.R

/** Reusable drawer controller for NOTEZ local music UI in MainActivity and EditorActivity. */
class MusicDrawerController(
    private val activity: AppCompatActivity,
    private val root: View,
    private val onAddMusicRequested: () -> Unit
) {
    private val handler = Handler(Looper.getMainLooper())
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var controller: MediaController? = null
    private var playerListener: Player.Listener? = null
    private var snapshot: MusicLibrarySnapshot = MusicLibraryStore.load(activity)
    private var expanded = false

    private val header: TextView = root.findViewById(R.id.music_header)
    private val subtitle: TextView = root.findViewById(R.id.music_header_subtitle)
    private val submenu: View = root.findViewById(R.id.music_submenu)
    private val nowTitle: TextView = root.findViewById(R.id.music_now_title)
    private val nowArtist: TextView = root.findViewById(R.id.music_now_artist)
    private val previous: TextView = root.findViewById(R.id.music_previous)
    private val playPause: ImageButton = root.findViewById(R.id.music_play_pause)
    private val next: TextView = root.findViewById(R.id.music_next)
    private val mode: ImageButton = root.findViewById(R.id.music_mode)
    private val modeLabel: TextView = root.findViewById(R.id.music_mode_label)
    private val addMusic: TextView = root.findViewById(R.id.music_add)
    private val empty: TextView = root.findViewById(R.id.music_empty)
    private val libraryList: LinearLayout = root.findViewById(R.id.music_library_list)

    private val tick = object : Runnable {
        override fun run() {
            updateUi()
            handler.postDelayed(this, 1000L)
        }
    }

    init {
        header.setOnClickListener {
            expanded = !expanded
            submenu.visibility = if (expanded) View.VISIBLE else View.GONE
            updateUi()
        }
        addMusic.setOnClickListener { onAddMusicRequested() }
        playPause.setOnClickListener { togglePlayPause() }
        previous.setOnClickListener { playPrevious() }
        next.setOnClickListener { playNext() }
        mode.setOnClickListener { cycleMode() }
        modeLabel.setOnClickListener { cycleMode() }
        connectController()
        renderLibrary()
        updateUi()
        handler.post(tick)
    }

    fun onMusicPicked(uris: List<Uri>) {
        if (uris.isEmpty()) return
        snapshot = MusicLibraryStore.addUris(activity, uris)
        renderLibrary()
        syncPlaylist(keepPlaying = controller?.isPlaying == true)
        updateUi()
        Toast.makeText(activity, "${uris.size} musik ditambahkan", Toast.LENGTH_SHORT).show()
    }

    fun destroy() {
        handler.removeCallbacks(tick)
        playerListener?.let { controller?.removeListener(it) }
        controllerFuture?.let { MediaController.releaseFuture(it) }
        controllerFuture = null
        controller = null
    }

    private fun connectController() {
        val token = SessionToken(activity, ComponentName(activity, MusicPlaybackService::class.java))
        val future = MediaController.Builder(activity, token).buildAsync()
        controllerFuture = future
        future.addListener({
            controller = runCatching { future.get() }.getOrNull()
            attachPlayerListener()
            if ((controller?.mediaItemCount ?: 0) == 0) {
                syncPlaylist(keepPlaying = false)
            }
            updateUi()
        }, ContextCompat.getMainExecutor(activity))
    }

    private fun attachPlayerListener() {
        val c = controller ?: return
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) = postUpdate()
            override fun onPlaybackStateChanged(playbackState: Int) = postUpdate()
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                val id = mediaItem?.mediaId
                if (!id.isNullOrBlank()) {
                    snapshot = MusicLibraryStore.savePlaybackState(activity, snapshot.playbackMode, id)
                    MusicLibraryStore.touchLastPlayed(activity, id)
                }
                postUpdate()
            }
            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                activity.runOnUiThread {
                    Toast.makeText(activity, "File musik tidak bisa dibuka", Toast.LENGTH_SHORT).show()
                    updateUi()
                }
            }
        }
        c.addListener(listener)
        playerListener = listener
    }

    private fun togglePlayPause() {
        val c = controller ?: return toastNotReady()
        if (snapshot.tracks.isEmpty()) return Toast.makeText(activity, "Belum ada musik lokal", Toast.LENGTH_SHORT).show()
        if (c.isPlaying) {
            c.pause()
        } else {
            if (c.mediaItemCount == 0) {
                val startIndex = currentTrackIndex().takeIf { it >= 0 } ?: 0
                playTrack(startIndex)
            } else {
                if (c.playbackState == Player.STATE_ENDED) {
                    c.seekTo(0L)
                    c.prepare()
                }
                c.play()
            }
        }
        updateUi()
    }

    private fun playPrevious() {
        val c = controller ?: return toastNotReady()
        if (snapshot.tracks.isEmpty()) return
        if (snapshot.playbackMode == MusicPlaybackMode.ONCE) {
            val prev = wrapIndex(currentTrackIndex() - 1)
            playTrack(prev)
            return
        }
        if (c.hasPreviousMediaItem()) c.seekToPreviousMediaItem() else playTrack(snapshot.tracks.lastIndex)
        c.play()
    }

    private fun playNext() {
        val c = controller ?: return toastNotReady()
        if (snapshot.tracks.isEmpty()) return
        if (snapshot.playbackMode == MusicPlaybackMode.ONCE) {
            val next = wrapIndex(currentTrackIndex() + 1)
            playTrack(next)
            return
        }
        if (c.hasNextMediaItem()) c.seekToNextMediaItem() else playTrack(0)
        c.play()
    }

    private fun cycleMode() {
        val currentId = currentTrackId()
        snapshot = MusicLibraryStore.savePlaybackState(activity, snapshot.playbackMode.next(), currentId)
        syncPlaylist(keepPlaying = controller?.isPlaying == true)
        updateUi()
    }

    private fun playTrack(index: Int) {
        val c = controller ?: return toastNotReady()
        if (snapshot.tracks.isEmpty()) return
        val safe = wrapIndex(index)
        val track = snapshot.tracks[safe]
        snapshot = MusicLibraryStore.savePlaybackState(activity, snapshot.playbackMode, track.id)
        syncPlaylist(keepPlaying = false)
        c.prepare()
        c.play()
        MusicLibraryStore.touchLastPlayed(activity, track.id)
        updateUi()
    }

    private fun syncPlaylist(keepPlaying: Boolean) {
        val c = controller ?: return
        val tracks = snapshot.tracks
        if (tracks.isEmpty()) {
            c.clearMediaItems()
            return
        }
        val currentId = snapshot.currentTrackId ?: tracks.first().id
        val index = tracks.indexOfFirst { it.id == currentId }.takeIf { it >= 0 } ?: 0
        when (snapshot.playbackMode) {
            MusicPlaybackMode.ONCE -> {
                c.shuffleModeEnabled = false
                c.repeatMode = Player.REPEAT_MODE_OFF
                c.setMediaItems(listOf(tracks[index].toMediaItem()), 0, 0L)
            }
            MusicPlaybackMode.LOOP_ALL -> {
                c.shuffleModeEnabled = false
                c.repeatMode = Player.REPEAT_MODE_ALL
                c.setMediaItems(tracks.map { it.toMediaItem() }, index, C.TIME_UNSET)
            }
            MusicPlaybackMode.SHUFFLE -> {
                c.repeatMode = Player.REPEAT_MODE_ALL
                c.shuffleModeEnabled = true
                c.setMediaItems(tracks.map { it.toMediaItem() }, index, C.TIME_UNSET)
            }
        }
        c.prepare()
        if (keepPlaying) c.play()
    }

    private fun renderLibrary() {
        libraryList.removeAllViews()
        for ((index, track) in snapshot.tracks.withIndex()) {
            val item = TextView(activity).apply {
                text = if (track.artist.isBlank()) track.title else "${track.title} - ${track.artist}"
                setTextColor(resolveColor(android.R.attr.textColorPrimary))
                textSize = 13f
                maxLines = 2
                setPadding(dp(20), dp(9), dp(12), dp(9))
                setOnClickListener { playTrack(index) }
            }
            libraryList.addView(
                item,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            )
        }
        empty.isVisible = snapshot.tracks.isEmpty()
    }

    private fun updateUi() {
        val currentTrack = currentTrack()
        subtitle.text = currentTrack?.let {
            if (it.artist.isBlank()) it.title else "${it.title} - ${it.artist}"
        }.orEmpty()
        subtitle.visibility = if (subtitle.text.isNullOrBlank()) View.GONE else View.VISIBLE
        nowTitle.text = currentTrack?.title ?: "Belum ada musik lokal"
        nowArtist.text = currentTrack?.artist?.takeIf { it.isNotBlank() } ?: currentTrack?.displayName.orEmpty()
        val isPlaying = controller?.isPlaying == true
        playPause.setImageResource(if (isPlaying) R.drawable.ic_music_pause else R.drawable.ic_music_play)
        playPause.contentDescription = if (isPlaying) "Pause music" else "Play music"
        mode.setImageResource(
            when (snapshot.playbackMode) {
                MusicPlaybackMode.LOOP_ALL -> R.drawable.ic_music_loop_all
                MusicPlaybackMode.ONCE -> R.drawable.ic_music_once
                MusicPlaybackMode.SHUFFLE -> R.drawable.ic_music_shuffle
            }
        )
        mode.contentDescription = "Mode ${snapshot.playbackMode.label()}"
        modeLabel.text = snapshot.playbackMode.label()
        empty.isVisible = snapshot.tracks.isEmpty()
    }

    private fun postUpdate() {
        activity.runOnUiThread { updateUi() }
    }

    private fun currentTrack(): MusicTrack? {
        val id = currentTrackId()
        return snapshot.tracks.firstOrNull { it.id == id } ?: snapshot.tracks.firstOrNull()
    }

    private fun currentTrackId(): String? = controller?.currentMediaItem?.mediaId
        ?.takeIf { it.isNotBlank() }
        ?: snapshot.currentTrackId

    private fun currentTrackIndex(): Int {
        val id = currentTrackId()
        val idx = snapshot.tracks.indexOfFirst { it.id == id }
        return if (idx >= 0) idx else 0
    }

    private fun wrapIndex(index: Int): Int {
        if (snapshot.tracks.isEmpty()) return 0
        val size = snapshot.tracks.size
        return ((index % size) + size) % size
    }

    private fun MusicTrack.toMediaItem(): MediaItem = MediaItem.Builder()
        .setMediaId(id)
        .setUri(uri)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(title)
                .setArtist(artist.takeIf { it.isNotBlank() })
                .build()
        )
        .build()

    private fun toastNotReady() {
        Toast.makeText(activity, "Music player belum siap", Toast.LENGTH_SHORT).show()
    }

    private fun dp(value: Int): Int = (value * activity.resources.displayMetrics.density).toInt()

    private fun resolveColor(attr: Int): Int {
        val typed = activity.obtainStyledAttributes(intArrayOf(attr))
        return try { typed.getColor(0, 0xFFFFFFFF.toInt()) } finally { typed.recycle() }
    }
}
