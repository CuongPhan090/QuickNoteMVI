package com.cphan.quicknotemvi.ui.noteeditor

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.cphan.quicknotemvi.data.Note
import com.cphan.quicknotemvi.data.NoteRepository
import com.cphan.quicknotemvi.ui.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.time.Duration.Companion.milliseconds

@RunWith(RobolectricTestRunner::class)
class NoteEditorViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repo = mockk<NoteRepository>(relaxed = true)

    @Test
    fun `test init with arg`() = runTest {
        // Given
        val note = note("1")
        coEvery { repo.readNote("id") } returns note
        val viewModel = createViewModel("id")

        // Then
        assertThat(viewModel.noteEditorState.value.title).isEqualTo("Title 1")
        assertThat(viewModel.noteEditorState.value.body).isEqualTo("Body 1")
        assertThat(viewModel.noteEditorState.value.isLoading).isFalse()
    }

    @Test
    fun `test init with null arg`() = runTest {
        // Given
        val note = note("1")
        coEvery { repo.readNote("1") } returns note
        val viewModel = createViewModel("idc")

        // Then
        assertThat(viewModel.noteEditorState.value.title).isEmpty()
        assertThat(viewModel.noteEditorState.value.body).isEmpty()
        assertThat(viewModel.noteEditorState.value.isLoading).isFalse()
    }

    @Test
    fun `test init with arg but empty note`() = runTest {
        // Given
        coEvery { repo.readNote(any()) } returns null
        val viewModel = createViewModel("id")

        // Then
        viewModel.noteEditorEffect.test {
            assertThat(awaitItem()).isEqualTo(NoteEditorEffect.NoteClosed)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `verify state on body type intent`() = runTest {
        // Given
        val note = note("id")
        coEvery { repo.readNote("id") } returns note
        val viewModel = createViewModel("id")

        // When
        viewModel.onIntent(NoteEditorIntent.BodyTyped("body"))

        // Then
        assertThat(viewModel.noteEditorState.value.body).isEqualTo("body")
        advanceTimeBy(501.milliseconds)
        coVerify {
            repo.saveNote(
                match { it.id == "id" && it.body == "body" && it.title == "Title id"}
            )
        }
    }

    @Test
    fun `verify new note never reads the database`() = runTest {
        // Given / When: no navigation argument (a new note)
        createViewModel("idc")

        // Then
        coVerify(exactly = 0) { repo.readNote(any()) }
    }

    @Test
    fun `verify state on title type intent`() = runTest {
        // Given
        val note = note("id")
        coEvery { repo.readNote("id") } returns note
        val viewModel = createViewModel("id")

        // When
        viewModel.onIntent(NoteEditorIntent.TitleTyped("title"))

        // Then
        assertThat(viewModel.noteEditorState.value.title).isEqualTo("title")
        coVerify(exactly = 0) { repo.saveNote(any()) }
        advanceTimeBy(501.milliseconds)
        coVerify(exactly = 1) {
            repo.saveNote(
                match { it.id == "id" && it.title == "title" && it.body == "Body id" }
            )
        }
    }

    @Test
    fun `verify back nav clicked saves then closes`() = runTest {
        // Given
        val note = note("id")
        coEvery { repo.readNote("id") } returns note
        val viewModel = createViewModel("id")
        viewModel.onIntent(NoteEditorIntent.BodyTyped("body"))

        viewModel.noteEditorEffect.test {
            // When
            viewModel.onIntent(NoteEditorIntent.BackNavClicked)

            // Then
            assertThat(awaitItem()).isEqualTo(NoteEditorEffect.NoteClosed)
            cancelAndIgnoreRemainingEvents()
        }
        coVerify(exactly = 1) { repo.saveNote(match { it.id == "id" && it.body == "body" }) }

        // The pending debounced save was cancelled: still exactly one save
        advanceTimeBy(501.milliseconds)
        coVerify(exactly = 1) { repo.saveNote(any()) }
    }

    @Test
    fun `verify screen stopped saves without closing`() = runTest {
        // Given
        val note = note("id")
        coEvery { repo.readNote("id") } returns note
        val viewModel = createViewModel("id")
        viewModel.onIntent(NoteEditorIntent.BodyTyped("body"))

        viewModel.noteEditorEffect.test {
            // When
            viewModel.onIntent(NoteEditorIntent.ScreenStopped)

            // Then
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
        coVerify(exactly = 1) { repo.saveNote(match { it.id == "id" && it.body == "body" }) }
    }

    @Test
    fun `verify back nav clicked with blank fields deletes the note`() = runTest {
        // Given: an existing note whose title and body were cleared
        val note = note("id")
        coEvery { repo.readNote("id") } returns note
        val viewModel = createViewModel("id")
        viewModel.onIntent(NoteEditorIntent.TitleTyped(""))
        viewModel.onIntent(NoteEditorIntent.BodyTyped(""))

        // When
        viewModel.onIntent(NoteEditorIntent.BackNavClicked)

        // Then
        coVerify(exactly = 1) { repo.deleteNote(match { it.id == "id" }) }
        coVerify(exactly = 0) { repo.saveNote(any()) }
    }

    private fun note(id: String) = Note(
        id = id,
        title = "Title $id",
        body = "Body $id",
        createdAt = 1_000L,
        updatedAt = 2_000L
    )

    private fun createViewModel(id: String): NoteEditorViewModel {
        val savedStateHandle = SavedStateHandle(mapOf("id" to id))
        return NoteEditorViewModel(repo, savedStateHandle)
    }
}