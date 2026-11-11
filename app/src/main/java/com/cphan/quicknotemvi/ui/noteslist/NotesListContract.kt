package com.cphan.quicknotemvi.ui.noteslist

import com.cphan.quicknotemvi.data.Note

data class NotesListState(
    val notes: List<Note> = emptyList(),
    val pendingUndo: Note? = null,
    val isLoading: Boolean = true
)

/**
 * User interaction on note lists
 */
sealed interface NotesListIntent {
    data class NoteClicked(val id: String): NotesListIntent
    data class DeleteSwiped(val id: String): NotesListIntent
    data class SnackBarDismissed(val id: String): NotesListIntent

    // Navigate to editor screen to create note
    data object CreateClicked: NotesListIntent
    data object UndoClicked: NotesListIntent
}

/**
 * A Change is what happened that should alter State
 */
sealed interface NotesListChange {
    data class AllNotesEmitted(val newList: List<Note>): NotesListChange
    data class NoteDeleted(val note: Note): NotesListChange
    data class UndoCleared(val id: String): NotesListChange
}

sealed interface NotesListEffect {
    data class ShowSnackBar(val id: String): NotesListEffect
    // Pass null id if it is a new note
    data class OpenEditor(val id: String?): NotesListEffect
}
