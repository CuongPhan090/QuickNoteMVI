package com.cphan.quicknotemvi.ui.noteslist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxDefaults
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cphan.quicknotemvi.R
import com.cphan.quicknotemvi.data.Note
import com.cphan.quicknotemvi.ui.theme.Dimens
import com.cphan.quicknotemvi.ui.theme.QuickNoteMVITheme

@Composable
fun SwipeableNoteCard(
    note: Note,
    onCardClicked: (String) -> Unit,
    onCardSwiped: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val positionalThreshold = SwipeToDismissBoxDefaults.positionalThreshold
    val swipeState = remember {
        SwipeToDismissBoxState(
            initialValue = SwipeToDismissBoxValue.Settled,
            positionalThreshold = positionalThreshold
        )
    }

    SwipeToDismissBox(
        state = swipeState,
        modifier = modifier,
        enableDismissFromStartToEnd = false,
        onDismiss = {
            onCardSwiped(note.id)
        },
        backgroundContent = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    Dimens.SwipeIconSpacing,
                    Alignment.End
                ),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = if (swipeState.dismissDirection == SwipeToDismissBoxValue.EndToStart)
                            MaterialTheme.colorScheme.error else Color.Transparent,
                        shape = RoundedCornerShape(Dimens.CardCornerRadius)
                    )
                    .padding(end = Dimens.SwipeBackgroundEndPadding),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_delete),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onError,
                    modifier = Modifier
                        .size(Dimens.SwipeIconSize)
                )
                Text(
                    text = "Delete",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onError
                )
            }
        }) {
        NoteCard(
            note = note, onCardClicked = onCardClicked
        )
    }
}

@Composable
fun NoteCard(
    note: Note, onCardClicked: (String) -> Unit, modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.CardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            width = Dimens.CardBorderWidth, color = MaterialTheme.colorScheme.outlineVariant
        ),
        onClick = { onCardClicked(note.id) }) {
        Column(
            modifier = Modifier.padding(
                horizontal = Dimens.CardHorizontalPadding, vertical = Dimens.CardVerticalPadding
            ), verticalArrangement = Arrangement.spacedBy(Dimens.CardTitleBodySpacing)
        ) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = note.body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = Dimens.CardBodyMaxLines,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NoteCardPreview() {
    QuickNoteMVITheme {
        NoteCard(
            note = Note(
                id = "1",
                title = "Sample Note",
                body = "This is a sample note body",
                createdAt = 123L,
                updatedAt = 123L
            ), modifier = Modifier.padding(12.dp), onCardClicked = {})
    }
}
