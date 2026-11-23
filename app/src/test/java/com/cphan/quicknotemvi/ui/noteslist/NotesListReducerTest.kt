package com.cphan.quicknotemvi.ui.noteslist

import com.cphan.quicknotemvi.data.Note
import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import org.junit.Test

class NotesListReducerTest {

    @Test
    fun `verify state when all notes emitted`() {
        // Given
        val state = NotesListState(notes = emptyList())
        val change = NotesListChange.AllNotesEmitted(
            newList = listOf(mockk<Note>())
        )

        // When
        val newState = reduceNotesList(state, change)

        // Then
        assertThat(newState.notes.size).isEqualTo(1)
        assertThat(newState.isLoading).isFalse()
    }

    @Test
    fun `verify all notes emitted keeps pending undo`() {
        // Given
        val deleted = note(id = "a")
        val state = NotesListState(pendingUndo = deleted, isLoading = false)
        val change = NotesListChange.AllNotesEmitted(newList = listOf(note(id = "b")))

        // When
        val newState = reduceNotesList(state, change)

        // Then
        assertThat(newState).isEqualTo(
            state.copy(notes = listOf(note(id = "b")))
        )
    }

    @Test
    fun `verify state when note deleted`() {
        // Given
        val deleted = note(id = "a")
        val state = NotesListState(notes = listOf(deleted), isLoading = false)
        val change = NotesListChange.NoteDeleted(note = deleted)

        // When
        val newState = reduceNotesList(state, change)

        // Then
        assertThat(newState).isEqualTo(state.copy(pendingUndo = deleted))
    }

    @Test
    fun `verify undo cleared with matching id clears pending undo`() {
        // Given
        val state = NotesListState(pendingUndo = note(id = "a"), isLoading = false)
        val change = NotesListChange.UndoCleared(id = "a")

        // When
        val newState = reduceNotesList(state, change)

        // Then
        assertThat(newState.pendingUndo).isNull()
    }

    @Test
    fun `verify undo cleared for an older note keeps the newer pending undo`() {
        // Given: note A's snack bar was replaced by note B's
        val state = NotesListState(pendingUndo = note(id = "b"), isLoading = false)
        val change = NotesListChange.UndoCleared(id = "a")

        // When
        val newState = reduceNotesList(state, change)

        // Then
        assertThat(newState).isEqualTo(state)
    }

    @Test
    fun `verify undo cleared with nothing pending changes nothing`() {
        // Given
        val state = NotesListState(isLoading = false)
        val change = NotesListChange.UndoCleared(id = "a")

        // When
        val newState = reduceNotesList(state, change)

        // Then
        assertThat(newState).isEqualTo(state)
    }

    private fun note(id: String) = Note(
        id = id,
        title = "Title $id",
        body = "Body $id",
        createdAt = 1_000L,
        updatedAt = 2_000L
    )
}
