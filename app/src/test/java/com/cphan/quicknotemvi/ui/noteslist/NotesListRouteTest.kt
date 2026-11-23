package com.cphan.quicknotemvi.ui.noteslist

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import com.cphan.quicknotemvi.data.Note
import com.cphan.quicknotemvi.data.NoteRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NotesListRouteTest {

    @get:Rule
    val composableRule = createComposeRule()

    private lateinit var viewModel: NotesListViewModel
    val notes = MutableStateFlow<List<Note>>(emptyList())
    private val repo = mockk<NoteRepository>(relaxed = true) {
        every { readAllNotes() } returns notes
    }

    @Before
    fun setup() {
        viewModel = NotesListViewModel(repo)
    }

    @Test
    fun `open editor effect via create fab`() {
        // Given
        var id: String? = "id"
        composableRule.setContent {
            NotesListRoute(
                onOpenEditor = { id = it },
                viewModel = viewModel
            )
        }

        // When
        composableRule.onNodeWithText("New note", useUnmergedTree = true).performClick()
        composableRule.waitForIdle()
        // Then
        assertThat(id).isNull()
    }

    @Test
    fun `open editor effect via note click`() {
        // Given
        val note = note("id")
        var id: String? = null
        notes.value = listOf(note)
        composableRule.setContent {
            NotesListRoute(
                onOpenEditor = { id = it },
                viewModel = viewModel
            )
        }

        // When
        composableRule.onNodeWithText("Title id").performClick()
        composableRule.waitForIdle()

        // Then
        assertThat(id).isEqualTo("id")
    }

    @Test
    fun `dismiss a snack bar`() {
        // Given
        val note = note("id")
        notes.value = listOf(note)
        coEvery { repo.readNote("id") } returns note
        composableRule.setContent {
            NotesListRoute(
                onOpenEditor = {},
                viewModel = viewModel
            )
        }

        // When
        composableRule.onNodeWithText("Title id").performTouchInput {
            swipeLeft()
        }
        composableRule.onNodeWithText("Note deleted").assertIsDisplayed()

        // Then
        composableRule.waitUntil(10_000) {
            composableRule.onAllNodesWithText("Note deleted").fetchSemanticsNodes().isEmpty()
        }
        assertThat(viewModel.notesListState.value.pendingUndo).isNull()
        coVerify(exactly = 0) { repo.restoreNote(note) }
    }

    @Test
    fun `undo action a snack bar`() {
        // Given
        val note = note("id")
        notes.value = listOf(note)
        coEvery { repo.readNote("id") } returns note
        composableRule.setContent {
            NotesListRoute(
                onOpenEditor = {},
                viewModel = viewModel
            )
        }

        // When
        composableRule.onNodeWithText("Title id").performTouchInput { swipeLeft() }
        composableRule.waitUntil(10_000) {
            composableRule.onAllNodesWithText("Note deleted").fetchSemanticsNodes().isNotEmpty()
        }
        composableRule.onNodeWithText("Undo", useUnmergedTree = true).performClick()

        // Then
        composableRule.waitUntil(10_000) {
            composableRule.onAllNodesWithText("Note deleted").fetchSemanticsNodes().isEmpty()
        }
        assertThat(viewModel.notesListState.value.pendingUndo).isNull()
        coVerify(exactly = 1) { repo.restoreNote(note) }
    }

    @Test
    fun `undo action on a snack bar with the latest note`() {
        // Given
        val note1 = note("id1")
        val note2 = note("id2")
        notes.value = listOf(note1, note2)
        coEvery { repo.readNote("id1") } returns note1
        coEvery { repo.readNote("id2") } returns note2
        composableRule.setContent {
            NotesListRoute(
                onOpenEditor = {},
                viewModel = viewModel
            )
        }

        // When
        composableRule.onNodeWithText("Title id1").performTouchInput {
            swipeLeft()
        }
        composableRule.onNodeWithText("Note deleted").assertIsDisplayed()
        composableRule.onNodeWithText("Title id2").performTouchInput {
            swipeLeft()
        }
        composableRule.waitForIdle()
        assertThat(viewModel.notesListState.value.pendingUndo).isEqualTo(note2)
        composableRule.onNodeWithText("Undo", useUnmergedTree = true).performClick()

        // Then
        composableRule.waitUntil(10_000) {
            composableRule.onAllNodesWithText("Note deleted").fetchSemanticsNodes().isEmpty()
        }
        assertThat(viewModel.notesListState.value.pendingUndo).isNull()
        coVerify(exactly = 0) { repo.restoreNote(note1) }
        coVerify(exactly = 1) { repo.restoreNote(note2) }
    }

    private fun note(id: String) = Note(
        id = id,
        title = "Title $id",
        body = "Body $id",
        createdAt = 1_000L,
        updatedAt = 2_000L
    )
}