package com.cphan.quicknotemvi.ui.noteeditor

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.SavedStateHandle
import com.cphan.quicknotemvi.data.Note
import com.cphan.quicknotemvi.data.NoteRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NoteEditorRouteTest {

    @get:Rule
    val composableRule = createAndroidComposeRule<ComponentActivity>()
    private val repo = mockk<NoteRepository>(relaxed = true)

    @Test
    fun `note closed effect`() {
        // Given
        var onClosedTap = 0
        val viewModel = createViewModel("")
        composableRule.setContent {
            NoteEditorRoute(
                onClose = { onClosedTap = 1 },
                viewModel = viewModel
            )
        }

        // When
        viewModel.onIntent(NoteEditorIntent.BackNavClicked)
        composableRule.waitForIdle()

        // Then
        assertThat(onClosedTap).isEqualTo(1)
    }

    @Test
    fun `back button clicked`() {
        // Given
        var onClosedTap = 0
        val viewModel = createViewModel("")
        composableRule.setContent {
            NoteEditorRoute(
                onClose = { onClosedTap = 1 },
                viewModel = viewModel
            )
        }

        // When
        composableRule.runOnUiThread {
            composableRule.activity.onBackPressedDispatcher.onBackPressed()
        }
        composableRule.waitForIdle()

        // Then
        assertThat(onClosedTap).isEqualTo(1)
    }

    @Test
    fun `switch app mid session`() {
        // Given
        var onClosedTap = 0
        coEvery { repo.readNote(any()) } returns note("id")
        val viewModel = createViewModel("id")
        composableRule.setContent {
            NoteEditorRoute(
                onClose = { onClosedTap = 1 },
                viewModel = viewModel
            )
        }

        // When
        composableRule.onNodeWithText("Body id").performTextInput("some body")
        composableRule.runOnUiThread {
            composableRule.activityRule.scenario.moveToState(Lifecycle.State.CREATED)
        }

        // Then
        coVerify { repo.saveNote(match { it.body == "some bodyBody id"}) }
        assertThat(onClosedTap).isEqualTo(0)
    }

    private fun createViewModel(id: String): NoteEditorViewModel {
        val savedStateHandle = SavedStateHandle(mapOf(id to id))
        return NoteEditorViewModel(repo, savedStateHandle)
    }

    private fun note(id: String) = Note(
        id = id,
        title = "Title $id",
        body = "Body $id",
        createdAt = 1_000L,
        updatedAt = 2_000L
    )
}