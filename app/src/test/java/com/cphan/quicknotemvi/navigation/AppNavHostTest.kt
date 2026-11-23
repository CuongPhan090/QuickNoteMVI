package com.cphan.quicknotemvi.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.lifecycle.Lifecycle
import com.cphan.quicknotemvi.MainActivity
import com.cphan.quicknotemvi.data.Note
import com.cphan.quicknotemvi.data.NoteRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import javax.inject.Inject

/**
 * Full-app navigation tests: the real MainActivity, AppNavHost, Routes, ViewModels and Room,
 * with Hilt's TestNoteModule swapping in an in-memory database.
 */
@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class) // Hilt's test app instead of NoteApplication
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class AppNavHostTest {

    // Hilt must set up its components before the Activity is created, so it runs first
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    // Launches MainActivity, which shows AppNavHost
    @get:Rule(order = 1)
    val composableRule = createAndroidComposeRule<MainActivity>()

    // The same singleton repository the app uses, to put notes in the database
    @Inject
    lateinit var repository: NoteRepository

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun `the app start a list`() = runTest {
        // Given
        val note1 = note("id")
        val note2 = note("id2")
        repository.saveNote(note1)
        repository.saveNote(note2)

        // When
        composableRule.runOnUiThread {
            composableRule.activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
        }

        // Then
        composableRule.onNodeWithText("Title id").assertIsDisplayed()
        composableRule.onNodeWithText("Title id2").assertIsDisplayed()
    }

    @Test
    fun `fab open an empty editor`() = runTest {
        // Given
        val note1 = note("id")
        val note2 = note("id2")
        repository.saveNote(note1)
        repository.saveNote(note2)

        // When
        composableRule.runOnUiThread {
            composableRule.activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
        }
        composableRule.onNodeWithText("New note", useUnmergedTree = true).performClick()

        // Then
        composableRule.onNodeWithText("Title id").assertIsNotDisplayed()
        composableRule.onNodeWithText("Title").assertIsDisplayed()
        composableRule.onNodeWithText("Start writing...").assertIsDisplayed()
    }



    @Test
    fun `tapping a note opens it in the editor`() = runTest {
        // Given
        val note1 = note("id")
        val note2 = note("id2")
        repository.saveNote(note1)
        repository.saveNote(note2)

        // When
        composableRule.onNodeWithText("Title id").performClick()

        // Then: the editor is showing, with this note's text in its fields
        composableRule.onNodeWithContentDescription("Back").assertIsDisplayed()
        composableRule.onNodeWithText("Title id").assertIsDisplayed()
        composableRule.onNodeWithText("Body id").assertIsDisplayed()
        composableRule.onNodeWithText("Title id2").assertDoesNotExist()
    }

    @Test
    fun `back arrow returns to the list`() = runTest {
        // Given: on the editor, opened from the FAB
        val note1 = note("id")
        repository.saveNote(note1)
        composableRule.onNodeWithText("New note", useUnmergedTree = true).performClick()
        composableRule.onNodeWithText("Start writing...").assertIsDisplayed()

        // When
        composableRule.onNodeWithContentDescription("Back").performClick()
        composableRule.waitForIdle()

        // Then: back on the list, and the empty new note was not saved
        composableRule.onNodeWithText("Title id").assertIsDisplayed()
        composableRule.onNodeWithText("Start writing...").assertDoesNotExist()
    }

    @Test
    fun `system back returns to the list`() = runTest {
        // Given: on the editor, opened from the FAB
        val note1 = note("id")
        repository.saveNote(note1)
        composableRule.onNodeWithText("New note", useUnmergedTree = true).performClick()
        composableRule.onNodeWithText("Start writing...").assertIsDisplayed()

        // When: the back gesture / button goes through the editor's BackHandler
        composableRule.runOnUiThread {
            composableRule.activity.onBackPressedDispatcher.onBackPressed()
        }
        composableRule.waitForIdle()
        // Then
        composableRule.onNodeWithText("Title id").assertIsDisplayed()
        composableRule.onNodeWithText("Start writing...").assertDoesNotExist()
    }

    @Test
    fun `a note typed in the editor shows on the list after back`() {
        // Given: on a new note, opened from the FAB
        composableRule.onNodeWithText("New note", useUnmergedTree = true).performClick()
        composableRule.onNodeWithText("Start writing...").assertIsDisplayed()

        // When
        composableRule.onNodeWithText("Title").performTextInput("Groceries")
        composableRule.onNodeWithContentDescription("Back").performClick()

        // Then: saved to Room, and the list's Flow emitted it. Room writes on its own
        // thread, which the compose rule doesn't track, so wait for the card
        composableRule.waitUntil(timeoutMillis = 5_000) {
            composableRule.onAllNodesWithText("Groceries").fetchSemanticsNodes().isNotEmpty()
        }
        composableRule.onNodeWithText("Start writing...").assertDoesNotExist()
    }

    @Test
    fun `double tap on the FAB opens only one editor`() {
        // When: both taps land on the FAB before the first navigation happens
        composableRule.onNodeWithText("New note", useUnmergedTree = true).performTouchInput {
            click()
            advanceEventTime(50)
            click()
        }
        composableRule.onNodeWithText("Start writing...").assertIsDisplayed()

        // Then: launchSingleTop kept one editor, so a single back reaches the list
        composableRule.onNodeWithContentDescription("Back").performClick()
        composableRule.waitForIdle()
        composableRule.onNodeWithText("No notes yet").assertIsDisplayed()
        composableRule.onNodeWithText("Start writing...").assertDoesNotExist()
    }

    private fun note(id: String, updatedAt: Long = 2_000L) = Note(
        id = id,
        title = "Title $id",
        body = "Body $id",
        createdAt = 1_000L,
        updatedAt = updatedAt
    )
}
