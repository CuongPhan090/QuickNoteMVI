package com.cphan.quicknotemvi.ui.noteslist

import app.cash.turbine.test
import com.cphan.quicknotemvi.data.Note
import com.cphan.quicknotemvi.data.NoteRepository
import com.cphan.quicknotemvi.ui.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NotesListViewModelTest {

    private lateinit var viewModel: NotesListViewModel
    private val repo = mockk<NoteRepository>(relaxed = true)
    private val notesFlow = MutableStateFlow<List<Note>>(emptyList())

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setup() {
        every { repo.readAllNotes() } returns notesFlow
        viewModel = NotesListViewModel(repo)
    }

    @Test
    fun `test init`() = runTest {
        // Given
        val note = note("1")
        notesFlow.emit(listOf(note))

        // When init called, then
        assertThat(viewModel.notesListState.value.notes).containsExactly(note)
        assertThat(viewModel.notesListState.value.isLoading).isFalse()
    }

    @Test
    fun `verify state on create clicked intent`() = runTest {
        viewModel.notesListEffect.test { // Start collecting
            viewModel.onIntent(NotesListIntent.CreateClicked)
            assertThat(awaitItem()).isEqualTo(NotesListEffect.OpenEditor(null))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `verify state on note clicked intent`() = runTest {
        viewModel.notesListEffect.test { // Start collecting
            viewModel.onIntent(NotesListIntent.NoteClicked("id"))
            assertThat(awaitItem()).isEqualTo(NotesListEffect.OpenEditor("id"))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `verify state on note deleted intent`() = runTest {
        val note = note("id")
        coEvery { repo.readNote("id") } returns note

        viewModel.notesListEffect.test {
            viewModel.onIntent(NotesListIntent.DeleteSwiped("id"))
            assertThat(awaitItem()).isEqualTo(NotesListEffect.ShowSnackBar("id"))
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { repo.deleteNote(note) }
        assertThat(viewModel.notesListState.value.pendingUndo).isEqualTo(note)
    }

    @Test
    fun `verify initial state before room emits`() = runTest {
        // Given the ViewModel was just created and Room has not emitted yet

        // Then
        assertThat(viewModel.notesListState.value.isLoading).isTrue()
        assertThat(viewModel.notesListState.value.notes).isEmpty()
    }

    @Test
    fun `verify state on snack bar dismissed intent`() = runTest {
        // Given a note was deleted, so Undo is pending
        val note = note("id")
        coEvery { repo.readNote("id") } returns note
        viewModel.onIntent(NotesListIntent.DeleteSwiped("id"))
        assertThat(viewModel.notesListState.value.pendingUndo).isEqualTo(note)

        // When
        viewModel.onIntent(NotesListIntent.SnackBarDismissed("id"))

        // Then
        assertThat(viewModel.notesListState.value.pendingUndo).isNull()
    }

    @Test
    fun `verify state on undo clicked intent`() = runTest {
        // Given a note was deleted, so Undo is pending
        val note = note("id")
        coEvery { repo.readNote("id") } returns note
        viewModel.onIntent(NotesListIntent.DeleteSwiped("id"))

        // When
        viewModel.onIntent(NotesListIntent.UndoClicked)

        // Then
        coVerify { repo.restoreNote(note) }
        assertThat(viewModel.notesListState.value.pendingUndo).isNull()
    }

    private fun note(id: String) = Note(
        id = id,
        title = "Title $id",
        body = "Body $id",
        createdAt = 1_000L,
        updatedAt = 2_000L
    )
}