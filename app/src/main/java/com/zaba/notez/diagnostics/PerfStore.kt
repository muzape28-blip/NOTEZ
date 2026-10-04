package com.zaba.notez.diagnostics

object PerfStore {
    @Volatile
    private var latestSession: PerfSession? = null

    fun saveSession(session: PerfSession) {
        latestSession = session
    }

    fun getLatestSession(): PerfSession? = latestSession

    fun clear() {
        latestSession = null
    }
}
