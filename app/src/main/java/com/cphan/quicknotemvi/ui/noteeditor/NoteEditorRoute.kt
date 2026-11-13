package com.cphan.quicknotemvi.ui.noteeditor

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun NoteEditorRoute(
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NoteEditorViewModel = hiltViewModel()
) {
    val state by viewModel.noteEditorState.collectAsStateWithLifecycle()
    val onIntent = viewModel::onIntent
    val currentOnClose by rememberUpdatedState(onClose)

    LaunchedEffect(Unit) {
        viewModel.noteEditorEffect.collect { effect ->
            when (effect) {
                NoteEditorEffect.NoteClosed -> currentOnClose()
            }
        }
    }

    // Intercept back button click / swipe back
    BackHandler {
        onIntent(NoteEditorIntent.BackNavClicked)
    }

    // Switch apps mid-sentence
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        onIntent(NoteEditorIntent.ScreenStopped)
    }

    NoteEditorScreen(state = state, onIntent = onIntent, modifier = modifier)
}
