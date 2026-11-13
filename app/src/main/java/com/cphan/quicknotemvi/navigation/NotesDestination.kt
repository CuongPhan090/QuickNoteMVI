package com.cphan.quicknotemvi.navigation

import kotlinx.serialization.Serializable

@Serializable
data object NotesListDestination

@Serializable
data class NoteEditorDestination(
    val id: String? = null
)