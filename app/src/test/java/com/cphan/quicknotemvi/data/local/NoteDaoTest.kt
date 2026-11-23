package com.cphan.quicknotemvi.data.local

import androidx.room.Room
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class NoteDaoTest {

    private lateinit var noteDao: NoteDao
    private lateinit var noteDatabase: NoteDatabase

    @Before
    fun setup() {
        noteDatabase = Room.inMemoryDatabaseBuilder(
            RuntimeEnvironment.getApplication(),
            NoteDatabase::class.java
        ).build()
        noteDao = noteDatabase.getNoteDao()
    }

    @Test
    fun `upsert a new note`() = runTest {
        val note = noteEntity("id")
        noteDao.upsert(note)
        assertThat(noteDao.read("id")).isEqualTo(note)
    }

    @Test
    fun `upsert an existing note`() = runTest {
        val note = noteEntity("id")
        noteDao.upsert(note)
        noteDao.upsert(note.copy(title = "new"))
        assertThat(noteDao.read("id")?.title).isEqualTo("new")
    }

    @Test
    fun `read invalid note`() = runTest {
        val note = noteEntity("id")
        noteDao.upsert(note)
        assertThat(noteDao.read("id2")).isNull()
    }

    @Test
    fun `get all notes`() = runTest {
        val note1 = noteEntity("id")
        val note2 = noteEntity("id2", updatedAt = 3_000L)
        noteDao.upsert(note1)
        noteDao.upsert(note2)

        noteDao.readAll().test {
            assertThat(awaitItem()).containsExactly(note2, note1).inOrder()
        }
    }

    @Test
    fun `get all notes again after a write`() = runTest {
        val note1 = noteEntity("id")
        val note2 = noteEntity("id2", updatedAt = 3_000L)
        noteDao.upsert(note1)

        noteDao.readAll().test {
            assertThat(awaitItem()).containsExactly(note1)
            noteDao.upsert(note2)
            assertThat(awaitItem()).containsExactly(note2, note1)
        }
    }

    @Test
    fun `delete matches by id only`() = runTest {
        // Given
        val note = noteEntity("id")
        noteDao.upsert(note)

        // When: the entity passed in is out of date, only the id matches
        noteDao.delete(note.copy(title = "stale"))

        // Then
        assertThat(noteDao.read("id")).isNull()
    }

    @Test
    fun `delete leaves other notes`() = runTest {
        // Given
        val note1 = noteEntity("id")
        val note2 = noteEntity("id2")
        noteDao.upsert(note1)
        noteDao.upsert(note2)

        // When
        noteDao.delete(note1)

        // Then
        assertThat(noteDao.read("id")).isNull()
        assertThat(noteDao.read("id2")).isEqualTo(note2)
    }

    @After
    fun teardown() {
        noteDatabase.close()
    }

    private fun noteEntity(id: String, updatedAt: Long = 2_000L) = NoteEntity(
        id = id,
        title = "Title $id",
        body = "Body $id",
        createdAt = 1_000L,
        updatedAt = updatedAt
    )
}
