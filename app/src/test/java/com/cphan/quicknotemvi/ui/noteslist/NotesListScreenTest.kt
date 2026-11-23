package com.cphan.quicknotemvi.ui.noteslist

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import com.cphan.quicknotemvi.data.Note
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NotesListScreenTest {

    @get:Rule
    val composableRule = createComposeRule() // Create a blank screen

    @Test
    fun `empty list screen`() {
        // Given
        composableRule.setContent {
            NotesListScreen(
                state = NotesListState(notes = emptyList(), isLoading = false),
                onIntent = {}
            )
        }

        // Verify
        composableRule.onNodeWithText("No notes yet").assertIsDisplayed()
        composableRule.onNodeWithText("Tap New note to write your first one.").assertIsDisplayed()
        composableRule.onNodeWithTag("CircularProgressIndicator").assertDoesNotExist()
    }

    @Test
    fun `list screen with notes`() {
        // Given
        composableRule.setContent {
            NotesListScreen(
                state = NotesListState(
                    notes = listOf(note("1"), note("2")),
                    isLoading = false
                ),
                onIntent = {}
            )
        }

        composableRule.onNodeWithText("Title 1").assertIsDisplayed()
        composableRule.onNodeWithText("Body 1").assertIsDisplayed()
        composableRule.onNodeWithText("Title 2").assertIsDisplayed()
        composableRule.onNodeWithText("Body 2").assertIsDisplayed()
        composableRule.onNodeWithTag("CircularProgressIndicator").assertDoesNotExist()
    }

    @Test
    fun `list screen loading`() {
        // Given
        composableRule.setContent {
            NotesListScreen(
                state = NotesListState(isLoading = true),
                onIntent = {}
            )
        }

        composableRule.onNodeWithTag("CircularProgressIndicator").assertIsDisplayed()
        composableRule.onNodeWithText("No notes yet").assertDoesNotExist()
    }

    @Test
    fun `click on floating button to create a note`() {
        // Given
        val sentIntents = mutableListOf<NotesListIntent>()
        composableRule.setContent {
            NotesListScreen(
                state = NotesListState(isLoading = false),
                onIntent = { sentIntents += it }
            )
        }

        // When
        composableRule.onNodeWithText("New note", useUnmergedTree = true).performClick()

        // Verify
        assertThat(sentIntents).containsExactly(NotesListIntent.CreateClicked)
    }

    @Test
    fun `on note swipe left to delete a note`() {
        // Given
        val sentIntents = mutableListOf<NotesListIntent>()
        composableRule.setContent {
            NotesListScreen(
                state = NotesListState(
                    notes = listOf(note("1"), note("2")), isLoading = false
                ),
                onIntent = { sentIntents += it }
            )
        }

        // When
        composableRule.onNodeWithText("Title 1").performTouchInput { swipeLeft() }
        composableRule.waitForIdle()

        // Verify
        assertThat(sentIntents).containsExactly(NotesListIntent.DeleteSwiped("1"))
    }

    @Test
    fun `on note swipe right does nothing`() {
        // Given
        val sentIntents = mutableListOf<NotesListIntent>()
        composableRule.setContent {
            NotesListScreen(
                state = NotesListState(
                    notes = listOf(note("1"), note("2")), isLoading = false
                ),
                onIntent = { sentIntents += it }
            )
        }

        // When
        composableRule.onNodeWithText("Title 1").performTouchInput { swipeRight() }
        composableRule.waitForIdle()

        // Verify
        assertThat(sentIntents).isEmpty()
    }


    @Test
    fun `on note click`() {
        // Given
        val sentIntents = mutableListOf<NotesListIntent>()
        composableRule.setContent {
            NotesListScreen(
                state = NotesListState(
                    notes = listOf(note("1"), note("2")), isLoading = false
                ),
                onIntent = { sentIntents += it }
            )
        }

        // When
        composableRule.onNodeWithText("Title 1").performClick()

        // Verify
        assertThat(sentIntents).containsExactly(NotesListIntent.NoteClicked("1"))
    }

    private fun note(id: String) = Note(
        id = id,
        title = "Title $id",
        body = "Body $id",
        createdAt = 1_000L,
        updatedAt = 2_000L
    )
}
