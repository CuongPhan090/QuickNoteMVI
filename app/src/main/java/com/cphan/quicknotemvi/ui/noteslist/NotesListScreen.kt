package com.cphan.quicknotemvi.ui.noteslist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.cphan.quicknotemvi.R
import com.cphan.quicknotemvi.data.Note
import com.cphan.quicknotemvi.ui.common.ScreenLoading
import com.cphan.quicknotemvi.ui.theme.Dimens
import com.cphan.quicknotemvi.ui.theme.ProgressTrack
import com.cphan.quicknotemvi.ui.theme.QuickNoteMVITheme

/**
 * Stateless list of notes screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesListScreen(
    state: NotesListState,
    onIntent: (NotesListIntent) -> Unit,
    modifier: Modifier = Modifier,
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Notes",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onIntent(NotesListIntent.CreateClicked) },
                text = { Text(text = "New note", style = MaterialTheme.typography.labelLarge) },
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_add),
                        contentDescription = null // Screen reader already read the label
                    )
                },
                containerColor = MaterialTheme.colorScheme.primary
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                state.isLoading -> ScreenLoading()
                state.notes.isEmpty() -> NotesListEmpty()
                else -> NotesList(
                    state = state,
                    onNoteClick = {
                        onIntent(NotesListIntent.NoteClicked(it))
                    }
                )
            }
        }
    }
}

@Composable
private fun NotesListEmpty(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.padding(horizontal = Dimens.EmptyHorizontalPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.EmptyItemSpacing)
        ) {
            Box(
                modifier = Modifier
                    .size(Dimens.EmptyIconBoxSize)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(Dimens.EmptyIconBoxCornerRadius)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_note),
                    contentDescription = null,
                    modifier = Modifier.size(Dimens.EmptyIconSize),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = "No notes yet",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = Dimens.EmptyHeadingTopSpacing)
            )
            Text(
                text = "Tap New note to write your first one.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun NotesList(
    state: NotesListState,
    onNoteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = Dimens.ListHorizontalPadding,
            end = Dimens.ListHorizontalPadding,
            top = Dimens.ListTopPadding,
            bottom = Dimens.ListBottomPadding
        ),
        verticalArrangement = Arrangement.spacedBy(Dimens.ListItemSpacing)
    ) {
        items(items = state.notes, key = { note -> note.id }) { note ->
            NoteCard(
                note = note,
                onCardClicked = onNoteClick
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NotesListScreenLoadingPreview() {
    QuickNoteMVITheme {
        NotesListScreen(
            state = NotesListState(isLoading = true),
            onIntent = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NotesListScreenEmptyPreview() {
    QuickNoteMVITheme {
        NotesListScreen(
            state = NotesListState(isLoading = false, notes = emptyList()),
            onIntent = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NotesListScreenPreview() {
    QuickNoteMVITheme {
        NotesListScreen(
            state = NotesListState(
                isLoading = false, notes = listOf(
                    Note(
                        id = "1",
                        title = "Grocery list",
                        body = "Milk, eggs, spinach, coffee beans, and something for Saturday dinner.",
                        createdAt = 0L,
                        updatedAt = 0L
                    ),
                    Note(
                        id = "2",
                        title = "Trip ideas for the long weekend, with backup plans if it rains",
                        body = "Big Sur in spring. Check campsite availability, the coast road " +
                                "closures, and whether the hot springs need a reservation this year.",
                        createdAt = 0L,
                        updatedAt = 0L
                    ),
                    Note(
                        id = "3",
                        title = "Gift for Mom",
                        body = "Pour-over kettle.",
                        createdAt = 0L,
                        updatedAt = 0L
                    )
                )
            ),
            onIntent = {}
        )
    }
}
