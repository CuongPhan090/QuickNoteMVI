package com.cphan.quicknotemvi.ui.noteslist

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import com.cphan.quicknotemvi.data.Note
import com.cphan.quicknotemvi.ui.theme.QuickNoteMVITheme
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NoteCardTest {

    @get:Rule
    val composableRule = createComposeRule()

    private val note = Note(
        id = "1",
        title = "Title 1",
        body = "Body 1",
        createdAt = 1_000L,
        updatedAt = 2_000L
    )

    // Ids each callback received; null = never called
    private var clickedId: String? = null
    private var swipedId: String? = null

    private fun setCard() {
        composableRule.setContent {
            QuickNoteMVITheme {
                SwipeableNoteCard(
                    note = note,
                    onCardClicked = { clickedId = it },
                    onCardSwiped = { swipedId = it }
                )
            }
        }
    }

    @Test
    fun `shows title and body`() {
        // Given
        setCard()

        // Then
        composableRule.onNodeWithText("Title 1").assertIsDisplayed()
        composableRule.onNodeWithText("Body 1").assertIsDisplayed()
    }

    @Test
    fun `click calls onCardClicked with the note id`() {
        // Given
        setCard()

        // When
        composableRule.onNodeWithText("Title 1").performClick()

        // Then
        assertThat(clickedId).isEqualTo("1")
        assertThat(swipedId).isNull()
    }

    @Test
    fun `full swipe left calls onCardSwiped with the note id`() {
        // Given
        setCard()

        // When: right edge to left edge, well past the positional threshold
        composableRule.onNodeWithText("Title 1").performTouchInput { swipeLeft() }
        composableRule.waitForIdle() // let the dismiss animation settle; onDismiss fires at the end

        // Then
        assertThat(swipedId).isEqualTo("1")
        assertThat(clickedId).isNull()
    }

    @Test
    fun `swipe right calls nothing`() {
        // Given
        setCard()

        // When: StartToEnd is disabled (enableDismissFromStartToEnd = false)
        composableRule.onNodeWithText("Title 1").performTouchInput { swipeRight() }
        composableRule.waitForIdle()

        // Then
        assertThat(swipedId).isNull()
        assertThat(clickedId).isNull()
    }

    @Test
    fun `short slow swipe left springs back without calling onCardSwiped`() {
        // Given
        setCard()

        // When: 20px is short of the positional threshold, and 500ms is slow enough
        // that the release velocity can't dismiss it either
        composableRule.onNodeWithText("Title 1").performTouchInput {
            swipeLeft(startX = right, endX = right - 20f, durationMillis = 500)
        }
        composableRule.waitForIdle()

        // Then
        assertThat(swipedId).isNull()
        composableRule.onNodeWithText("Title 1").assertIsDisplayed()
    }
}
