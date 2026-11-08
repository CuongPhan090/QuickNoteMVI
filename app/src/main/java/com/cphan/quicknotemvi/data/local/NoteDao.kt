package com.cphan.quicknotemvi.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/**
 * CRUD Operation: Create, Read, Update, Delete
 * Most of these operation does one-off work, execute only one time, must run off main thread to avoid ANR "Application not responding"
 */
@Dao
interface NoteDao {

    // Create/Update <=> Upsert, insert a record if not exist, otherwise update the record with matching ID
    @Upsert
    suspend fun upsert(note: NoteEntity)

    // Delete
    @Delete
    suspend fun delete(note: NoteEntity)

    // Get a note
    @Query(
        """
            SELECT *
            FROM notes
            WHERE id == :id
        """
    )
    suspend fun read(id: String): NoteEntity?


    // Get all notes
    @Query(
        """
            SELECT *
            FROM notes
            ORDER BY updatedAt DESC
        """
    )
    fun readAll(): Flow<List<NoteEntity>>
}
