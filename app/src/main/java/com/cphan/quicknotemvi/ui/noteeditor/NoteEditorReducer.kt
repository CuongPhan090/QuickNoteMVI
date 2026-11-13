package com.cphan.quicknotemvi.ui.noteeditor

fun reduceNoteEditor(state: NoteEditorState, change: NoteEditorChange): NoteEditorState {
    return when (change) {
        is NoteEditorChange.BodyChanged -> state.copy(body = change.body)
        is NoteEditorChange.TitleChanged -> state.copy(title = change.title)
        is NoteEditorChange.NoteLoaded -> state.copy(
            title = change.note.title,
            body = change.note.body,
            isLoading = false
        )
    }
}
