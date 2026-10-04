package com.zaba.notez.diagnostics

data class PerfSession(
    val id: String,
    val startedAt: Long,
    val noteId: Long?,
    val events: List<PerfEvent>
)
