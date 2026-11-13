package com.cphan.quicknotemvi.ui.noteeditor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.cphan.quicknotemvi.data.Note
import com.cphan.quicknotemvi.data.NoteRepository
import com.cphan.quicknotemvi.navigation.NoteEditorDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class NoteEditorViewModel @Inject constructor(
    private val repository: NoteRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val arg = savedStateHandle.toRoute<NoteEditorDestination>().id
    private val noteId = arg ?: UUID.randomUUID().toString()
    private var createdAt = System.currentTimeMillis()

    private val _noteEditorState = MutableStateFlow(NoteEditorState(isLoading = arg != null))
    val noteEditorState = _noteEditorState.asStateFlow()

    private val _noteEditorEffect = Channel<NoteEditorEffect>(capacity = Channel.BUFFERED)
    val noteEditorEffect = _noteEditorEffect.receiveAsFlow()

    private var saveJob: Job? = null

    private fun scheduleSaveJob() {
        saveJob?.cancel()
        saveJob = viewModelScope.launch {
            delay(500.milliseconds)
            saveIfNotBlank()
        }
    }

    init {
        if (arg != null) {
            viewModelScope.launch {
                repository.readNote(arg)?.let { note ->
                    createdAt = note.createdAt
                    updateState(NoteEditorChange.NoteLoaded(note))
                } ?: run {
                    _noteEditorEffect.send(NoteEditorEffect.NoteClosed)
                }
            }
        }
    }

    private fun updateState(change: NoteEditorChange) {
        _noteEditorState.update { state ->
            reduceNoteEditor(state, change)
        }
    }

    fun onIntent(intent: NoteEditorIntent) {
        when (intent) {
            NoteEditorIntent.BackNavClicked -> {
                viewModelScope.launch {
                    finishNow()
                    _noteEditorEffect.send(NoteEditorEffect.NoteClosed)
                }
            }

            NoteEditorIntent.ScreenStopped -> {
                viewModelScope.launch {
                    finishNow()
                }
            }

            is NoteEditorIntent.BodyTyped -> {
                updateState(NoteEditorChange.BodyChanged(intent.body))
                scheduleSaveJob()
            }

            is NoteEditorIntent.TitleTyped -> {
                updateState(NoteEditorChange.TitleChanged(intent.title))
                scheduleSaveJob()
            }
        }
    }

    private suspend fun saveIfNotBlank() {
        if (_noteEditorState.value.isLoading) return

        val note = currentNote()
        if (note.title.isNotBlank() || note.body.isNotBlank()) {
            repository.saveNote(note)
        }
    }

    private suspend fun finishNow() {
        saveJob?.cancel()

        if (_noteEditorState.value.isLoading) return

        val note = currentNote()
        if (note.title.isBlank() && note.body.isBlank()) {
            repository.deleteNote(note)
        } else {
            repository.saveNote(note)
        }
    }

    private fun currentNote(): Note {
        return Note(
            id = noteId,
            title = _noteEditorState.value.title,
            body = _noteEditorState.value.body,
            createdAt = createdAt,
            updatedAt = System.currentTimeMillis()
        )
    }
}
