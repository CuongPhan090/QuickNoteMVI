package com.cphan.quicknotemvi.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.cphan.quicknotemvi.ui.theme.Dimens
import com.cphan.quicknotemvi.ui.theme.ProgressTrack

@Composable
internal fun ScreenLoading(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            modifier = Modifier.size(Dimens.SpinnerSize),
            strokeWidth = Dimens.SpinnerStrokeWidth,
            color = MaterialTheme.colorScheme.primary,
            trackColor = ProgressTrack
        )
    }
}
