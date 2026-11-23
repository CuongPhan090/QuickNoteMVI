package com.cphan.quicknotemvi.data

import com.cphan.quicknotemvi.data.local.NoteDao
import com.cphan.quicknotemvi.data.local.NoteEntity
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class NoteRepositoryImplTest {

    private val noteDao = mockk<NoteDao>(relaxed = true)

    @Test
    fun `save note`() = runTest {
        // Given
        val repo = createRepo { 100L }
        val note = note("id")

        // When
        repo.saveNote(note)

        // Then
        coVerify { noteDao.upsert(note.toNoteEntity().copy(updatedAt = 100L)) }
    }

    @Test
    fun `delete note`() = runTest {
        // Given
        val repo = createRepo { 100L }
        val note = note("id")

        // When
        repo.deleteNote(note)

        // Then
        coVerify { noteDao.delete(note.toNoteEntity()) }
    }

    @Test
    fun `read note`() = runTest {
        // Given
        val note = note("id")
        val repo = createRepo { 100L }
        coEvery { noteDao.read(any()) } returns note.toNoteEntity()

        // When
        repo.readNote("id")

        // Then
        assertThat(repo.readNote("id")).isEqualTo(note)
    }

    @Test
    fun `restore note`() = runTest {
        // Given: now() differs from the note's updatedAt, so a re-stamp would show
        val repo = createRepo { 100L }
        val note = note("id")

        // When
        repo.restoreNote(note)

        // Then: written back unchanged, updatedAt stays 2_000L
        coVerify { noteDao.upsert(note.toNoteEntity()) }
        coVerify(exactly = 0) { noteDao.upsert(match { it.updatedAt == 100L }) }
    }

    @Test
    fun `read all note`() = runTest {
        // Given
        val repo = createRepo { 100L }
        val note1 = note("1")
        val note2 = note("2")
        every { noteDao.readAll() } returns flowOf(listOf(note1.toNoteEntity(), note2.toNoteEntity()))

        // When
        val notes = repo.readAllNotes().first()

        // Then: every entity mapped, order kept
        assertThat(notes).containsExactly(note1, note2).inOrder()
    }

    private fun createRepo(now: () -> Long): NoteRepository {
        return NoteRepositoryImpl(
            noteDao = noteDao,
            now = now
        )
    }

    private fun note(id: String) = Note(
        id = id,
        title = "Title $id",
        body = "Body $id",
        createdAt = 1_000L,
        updatedAt = 2_000L
    )

    private fun Note.toNoteEntity() = NoteEntity(
        id = id,
        title = title,
        body = body,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}