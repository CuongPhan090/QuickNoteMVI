package com.cphan.quicknotemvi.ui.noteslist

fun reduceNotesList(state: NotesListState, change: NotesListChange): NotesListState {
    return when (change) {
        is NotesListChange.AllNotesEmitted ->
            state.copy(notes = change.newList, isLoading = false)

        is NotesListChange.NoteDeleted ->
            state.copy(pendingUndo = change.note)

        is NotesListChange.UndoCleared ->
            state.copy(
                pendingUndo = if (state.pendingUndo?.id == change.id) null
                else state.pendingUndo
            )
    }
}
