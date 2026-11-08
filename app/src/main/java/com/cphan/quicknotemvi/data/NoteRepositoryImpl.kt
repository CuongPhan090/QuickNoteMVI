package com.cphan.quicknotemvi.data

import com.cphan.quicknotemvi.data.local.NoteDao
import com.cphan.quicknotemvi.data.local.NoteEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteRepositoryImpl(
    private val noteDao: NoteDao,
    private val now: () -> Long
): NoteRepository {
    override suspend fun saveNote(note: Note) {
        noteDao.upsert(note.toNoteEntity().copy(
            updatedAt = now()
        ))
    }

    override suspend fun deleteNote(note: Note) {
        noteDao.delete(note.toNoteEntity())
    }

    override suspend fun readNote(id: String): Note? {
        return noteDao.read(id)?.toNote()
    }

    override suspend fun restoreNote(note: Note) {
        noteDao.upsert(note.toNoteEntity())
    }

    override fun readAllNotes(): Flow<List<Note>> {
        return noteDao.readAll().map { noteEntities ->
            noteEntities.map { noteEntity ->
                noteEntity.toNote()
            }
        }
    }

    private fun Note.toNoteEntity() = NoteEntity(
        id = id,
        title = title,
        body = body,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

    private fun NoteEntity.toNote() = Note(
        id = id,
        title = title,
        body = body,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}
