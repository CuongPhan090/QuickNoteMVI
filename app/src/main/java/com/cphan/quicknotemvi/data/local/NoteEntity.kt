package com.cphan.quicknotemvi.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * An entity is a table, a row is a record and a class property is a column.
 * A row is defined as a data class
 */
@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val body: String,
    val createdAt: Long,
    val updatedAt: Long
)
