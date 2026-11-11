package com.cphan.quicknotemvi.ui.noteslist

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch

@Composable
fun NotesListRoute(
    onOpenEditor: (String?) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotesListViewModel = hiltViewModel()
) {
    val state by viewModel.notesListState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val onIntent = viewModel::onIntent
    LaunchedEffect(Unit) {
        viewModel.notesListEffect.collect { effect ->
            when (effect) {
                is NotesListEffect.OpenEditor -> {
                    onOpenEditor(effect.id)
                }

                is NotesListEffect.ShowSnackBar -> {
                    // Dismiss the current snack bar to show next snack bar if the overlapping happens
                    snackBarHostState.currentSnackbarData?.dismiss()
                    this@LaunchedEffect.launch {
                        val result = snackBarHostState.showSnackbar(
                            message = "Note deleted",
                            actionLabel = "Undo",
                        )
                        when (result) {
                            SnackbarResult.Dismissed -> {
                                onIntent(NotesListIntent.SnackBarDismissed(effect.id))
                            }

                            SnackbarResult.ActionPerformed -> {
                                onIntent(NotesListIntent.UndoClicked)
                            }
                        }
                    }
                }
            }
        }
    }

    NotesListScreen(
        state = state,
        onIntent = onIntent,
        modifier = modifier,
        snackBarHostState = snackBarHostState
    )
}
