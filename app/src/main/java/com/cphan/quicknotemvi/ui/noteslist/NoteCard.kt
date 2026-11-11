package com.cphan.quicknotemvi.ui.noteslist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cphan.quicknotemvi.data.Note
import com.cphan.quicknotemvi.ui.theme.Dimens
import com.cphan.quicknotemvi.ui.theme.QuickNoteMVITheme

@Composable
fun NoteCard(
    note: Note,
    onCardClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.CardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            width = Dimens.CardBorderWidth,
            color = MaterialTheme.colorScheme.outlineVariant
        ),
        onClick = { onCardClicked(note.id) }
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = Dimens.CardHorizontalPadding,
                vertical = Dimens.CardVerticalPadding
            ),
            verticalArrangement = Arrangement.spacedBy(Dimens.CardTitleBodySpacing)
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
            ),
            modifier = Modifier.padding(12.dp),
            onCardClicked = {}
        )
    }
}
