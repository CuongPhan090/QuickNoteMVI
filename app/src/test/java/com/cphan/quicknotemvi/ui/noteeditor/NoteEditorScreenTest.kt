package com.cphan.quicknotemvi.ui.noteeditor

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NoteEditorScreenTest {

    @get:Rule
    val composableRule = createComposeRule()

    @Test
    fun `fresh new note`() {
        // Given
        composableRule.setContent {
            NoteEditorScreen(
                state = NoteEditorState(isLoading = false),
                onIntent = {}
            )
        }

        composableRule.onNodeWithText("Title").assertIsDisplayed()
        composableRule.onNodeWithText("Start writing...").assertIsDisplayed()
        composableRule.onNodeWithTag("CircularProgressIndicator").assertDoesNotExist()
    }

    @Test
    fun `on loading`() {
        // Given
        composableRule.setContent {
            NoteEditorScreen(
                state = NoteEditorState(isLoading = true),
                onIntent = {}
            )
        }

        composableRule.onNodeWithTag("CircularProgressIndicator").assertIsDisplayed()
        composableRule.onNodeWithText("Start writing...").assertDoesNotExist()
    }

    @Test
    fun `on existing note`() {
        // Given
        composableRule.setContent {
            NoteEditorScreen(
                state = NoteEditorState(
                    title = "some title",
                    body = "some body",
                    isLoading = false
                ),
                onIntent = {}
            )
        }

        composableRule.onNodeWithText("some title").assertIsDisplayed()
        composableRule.onNodeWithText("some body").assertIsDisplayed()
        composableRule.onNodeWithTag("CircularProgressIndicator").assertDoesNotExist()
    }

    @Test
    fun `on back button click`() {
        // Given
        val sentIntent = mutableListOf<NoteEditorIntent>()
        composableRule.setContent {
            NoteEditorScreen(
                state = NoteEditorState(isLoading = false),
                onIntent = { sentIntent += it }
            )
        }

        // When
        composableRule.onNodeWithContentDescription("Back").performClick()
        assertThat(sentIntent).containsExactly(NoteEditorIntent.BackNavClicked)
    }

    @Test
    fun `on title typed`() {
        // Given
        val sentIntents = mutableListOf<NoteEditorIntent>()
        composableRule.setContent {
            NoteEditorScreen(
                state = NoteEditorState(isLoading = false),
                onIntent = { sentIntents += it }
            )
        }

        // When
        composableRule.onNodeWithText("Title").performTextInput("some title")

        // Then
        assertThat(sentIntents).containsExactly(NoteEditorIntent.TitleTyped("some title"))
    }

    @Test
    fun `on body typed`() {
        // Given
        val sentIntents = mutableListOf<NoteEditorIntent>()
        composableRule.setContent {
            NoteEditorScreen(
                state = NoteEditorState(isLoading = false),
                onIntent = { sentIntents += it }
            )
        }

        // When
        composableRule.onNodeWithText("Start writing...").performTextInput("some body")

        // Then
        assertThat(sentIntents).containsExactly(NoteEditorIntent.BodyTyped("some body"))
    }
}
