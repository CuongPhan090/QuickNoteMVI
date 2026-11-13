package com.cphan.quicknotemvi.ui.noteeditor

import com.cphan.quicknotemvi.data.Note

data class NoteEditorState(
    val title: String = "",
    val body: String = "",
    val isLoading: Boolean
)

sealed interface NoteEditorIntent {
    data object BackNavClicked: NoteEditorIntent
    data object ScreenStopped: NoteEditorIntent
    data class TitleTyped(val title: String): NoteEditorIntent
    data class BodyTyped(val body: String): NoteEditorIntent
}

sealed interface NoteEditorChange {
    data class TitleChanged(val title: String): NoteEditorChange
    data class BodyChanged(val body: String): NoteEditorChange
    data class NoteLoaded(val note: Note): NoteEditorChange
}

sealed interface NoteEditorEffect {
    data object NoteClosed: NoteEditorEffect
}
