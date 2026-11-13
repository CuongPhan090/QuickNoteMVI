package com.cphan.quicknotemvi.ui.noteeditor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.cphan.quicknotemvi.R
import com.cphan.quicknotemvi.ui.common.ScreenLoading
import com.cphan.quicknotemvi.ui.theme.Dimens
import com.cphan.quicknotemvi.ui.theme.QuickNoteMVITheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    state: NoteEditorState,
    onIntent: (NoteEditorIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { onIntent(NoteEditorIntent.BackNavClicked) }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                },
                title = {},
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }, containerColor = MaterialTheme.colorScheme.background, modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                state.isLoading -> ScreenLoading()
                else -> NoteEditor(
                    state = state, onIntent = onIntent
                )
            }
        }
    }
}

@Composable
private fun NoteEditor(
    state: NoteEditorState, onIntent: (NoteEditorIntent) -> Unit, modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                start = Dimens.EditorHorizontalPadding,
                end = Dimens.EditorHorizontalPadding,
                top = Dimens.EditorTopPadding
            )
            .imePadding(), verticalArrangement = Arrangement.spacedBy(Dimens.EditorFieldSpacing)
    ) {
        BasicTextField(
            value = state.title,
            onValueChange = { onIntent(NoteEditorIntent.TitleTyped(it)) },
            textStyle = MaterialTheme.typography.headlineSmall.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            singleLine = true,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            decorationBox = { innerTextField ->
                Box {
                    if (state.title.isEmpty()) {
                        Text(
                            text = "Title",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                    innerTextField()
                }

            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        BasicTextField(
            value = state.body,
            onValueChange = { onIntent(NoteEditorIntent.BodyTyped(it)) },
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            decorationBox = { innerTextField ->
                Box {
                    if (state.body.isEmpty()) {
                        Text(
                            text = "Start writing...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    innerTextField()
                }

            },
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview
@Composable
private fun NoteEditorScreenPreview() {
    QuickNoteMVITheme {
        NoteEditorScreen(
            state = NoteEditorState(
                title = "Trip ideas for the long weekend, with backup plans if it rains",
                body = "Big Sur in spring. Check campsite availability, the coast road " + "closures, and whether the hot springs need a reservation this year.",
                isLoading = false
            ), onIntent = {})
    }
}

@Preview
@Composable
private fun NoteEditorScreenEmptyPreview() {
    QuickNoteMVITheme {
        NoteEditorScreen(
            state = NoteEditorState(title = "", body = "", isLoading = false), onIntent = {})
    }
}

@Preview
@Composable
private fun NoteEditorScreenLoadingPreview() {
    QuickNoteMVITheme {
        NoteEditorScreen(
            state = NoteEditorState(isLoading = true), onIntent = {})
    }
}
