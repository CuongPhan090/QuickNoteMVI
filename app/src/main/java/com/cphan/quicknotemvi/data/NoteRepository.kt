package com.cphan.quicknotemvi.data

import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    suspend fun saveNote(note: Note)
    suspend fun deleteNote(note: Note)
    suspend fun readNote(id: String): Note?
    suspend fun restoreNote(note: Note)
    fun readAllNotes(): Flow<List<Note>>
}
