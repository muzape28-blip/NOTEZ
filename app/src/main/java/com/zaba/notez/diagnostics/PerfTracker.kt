package com.zaba.notez.diagnostics

import android.os.SystemClock
import android.util.Log

class PerfTracker(private val noteId: Long?, initialTimeMs: Long? = null) {
    private val startedAt = initialTimeMs ?: SystemClock.uptimeMillis()
    private val events = mutableListOf<PerfEvent>()

    init {
        mark("OPEN_T0")
    }

    fun mark(name: String) {
        val elapsed = SystemClock.uptimeMillis() - startedAt
        synchronized(events) {
            events += PerfEvent(name, elapsed)
        }
        Log.d("NOTEZ_PERF", "$name +${elapsed}ms")
    }

    fun finishSession() {
        val session = PerfSession(
            id = System.currentTimeMillis().toString(),
            startedAt = startedAt,
            noteId = noteId,
            events = synchronized(events) { events.toList() }
        )
        PerfStore.saveSession(session)
    }
}
