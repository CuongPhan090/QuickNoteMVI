package com.cphan.quicknotemvi.ui.noteeditor

import com.cphan.quicknotemvi.data.Note
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class NoteEditorReducerTest {

    @Test
    fun `verify state when body change`() {
        // Given
        val state = NoteEditorState(isLoading = true)
        val change = NoteEditorChange.BodyChanged(body = "some text")

        // When
        val newState = reduceNoteEditor(state, change)

        // Then
        assertThat(newState.body).isEqualTo("some text")
    }

    @Test
    fun `verify body change keeps title and loading flag`() {
        // Given
        val state = NoteEditorState(title = "Groceries", body = "Milk", isLoading = false)
        val change = NoteEditorChange.BodyChanged(body = "Milk, eggs")

        // When
        val newState = reduceNoteEditor(state, change)

        // Then
        assertThat(newState).isEqualTo(state.copy(body = "Milk, eggs"))
    }

    @Test
    fun `verify state when title change`() {
        // Given
        val state = NoteEditorState(title = "Grocer", body = "Milk", isLoading = false)
        val change = NoteEditorChange.TitleChanged(title = "Groceries")

        // When
        val newState = reduceNoteEditor(state, change)

        // Then
        assertThat(newState).isEqualTo(state.copy(title = "Groceries"))
    }

    @Test
    fun `verify note loaded fills the fields and stops loading`() {
        // Given
        val state = NoteEditorState(isLoading = true)
        val note = Note(
            id = "a",
            title = "Trip ideas",
            body = "Big Sur in spring",
            createdAt = 1_000L,
            updatedAt = 2_000L
        )
        val change = NoteEditorChange.NoteLoaded(note = note)

        // When
        val newState = reduceNoteEditor(state, change)

        // Then
        assertThat(newState).isEqualTo(
            NoteEditorState(title = "Trip ideas", body = "Big Sur in spring", isLoading = false)
        )
    }
}
