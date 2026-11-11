package com.cphan.quicknotemvi.ui.noteslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cphan.quicknotemvi.data.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesListViewModel @Inject constructor(
    private val noteRepo: NoteRepository
) : ViewModel() {

    private val _notesListState = MutableStateFlow(NotesListState())
    val notesListState = _notesListState.asStateFlow()

    private val _notesListEffect = Channel<NotesListEffect>(capacity = Channel.BUFFERED)
    val notesListEffect = _notesListEffect.receiveAsFlow()

    init {
        noteRepo.readAllNotes()
            .onEach { notes ->
                updateState(NotesListChange.AllNotesEmitted(notes))
            }
            .launchIn(viewModelScope)
    }

    fun onIntent(intent: NotesListIntent) {
        when (intent) {
            NotesListIntent.CreateClicked -> {
                viewModelScope.launch {
                    _notesListEffect.send(NotesListEffect.OpenEditor(null))
                }
            }

            is NotesListIntent.DeleteSwiped -> {
                viewModelScope.launch {
                    noteRepo.readNote(intent.id)?.let { note ->
                        noteRepo.deleteNote(note)
                        updateState(NotesListChange.NoteDeleted(note))
                        _notesListEffect.send(NotesListEffect.ShowSnackBar(note.id))
                    }
                }
            }

            is NotesListIntent.NoteClicked -> {
                viewModelScope.launch {
                    _notesListEffect.send(NotesListEffect.OpenEditor(intent.id))
                }
            }

            is NotesListIntent.SnackBarDismissed -> {
                updateState(NotesListChange.UndoCleared(intent.id))
            }

            NotesListIntent.UndoClicked -> {
                viewModelScope.launch {
                    _notesListState.value.pendingUndo?.let {
                        noteRepo.restoreNote(it)
                        updateState(NotesListChange.UndoCleared(it.id))
                    }
                }
            }
        }
    }

    private fun updateState(change: NotesListChange) {
        _notesListState.update { state ->
            reduceNotesList(state, change)
        }
    }
}
