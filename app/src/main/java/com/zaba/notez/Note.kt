package com.zaba.notez

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Satu catatan. [content] TEXT tanpa batas panjang (SQLite TEXT ~1GB). */
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String = "",
    val content: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)
