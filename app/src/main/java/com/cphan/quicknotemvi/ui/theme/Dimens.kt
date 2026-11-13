package com.cphan.quicknotemvi.ui.theme

import androidx.compose.ui.unit.dp

/** Sizes and spacing from the notes list sketch. */
object Dimens {
    // Top bar
    val TopBarHeight = 64.dp
    val TopBarHorizontalPadding = 20.dp

    // List
    val ListHorizontalPadding = 16.dp
    val ListTopPadding = 8.dp
    val ListItemSpacing = 8.dp
    val ListBottomPadding = 120.dp // last card clears the New note button

    // Note card
    val CardCornerRadius = 14.dp
    val CardBorderWidth = 1.dp
    val CardHorizontalPadding = 16.dp
    val CardVerticalPadding = 14.dp
    val CardTitleBodySpacing = 4.dp
    const val CardBodyMaxLines = 2

    // New note button (extended FAB)
    val FabHeight = 56.dp
    val FabCornerRadius = 16.dp
    val FabIconSpacing = 10.dp
    val FabEdgeMargin = 16.dp
    val FabBottomMargin = 32.dp

    // Snackbar
    val SnackbarMinHeight = 52.dp
    val SnackbarCornerRadius = 10.dp
    val SnackbarHorizontalMargin = 12.dp
    val SnackbarBottomMargin = 24.dp
    val MinTouchTarget = 44.dp

    // Swipe-to-delete background
    val SwipeBackgroundEndPadding = 20.dp
    val SwipeIconSpacing = 8.dp
    val SwipeIconSize = 22.dp

    // Empty state
    val EmptyIconBoxSize = 88.dp
    val EmptyIconBoxCornerRadius = 28.dp
    val EmptyIconSize = 40.dp
    val EmptyItemSpacing = 12.dp
    val EmptyHeadingTopSpacing = 8.dp // extra space above the heading
    val EmptyHorizontalPadding = 48.dp

    // Loading
    val SpinnerSize = 48.dp
    val SpinnerStrokeWidth = 4.dp

    // Editor
    val EditorHorizontalPadding = 20.dp
    val EditorTopPadding = 8.dp
    val EditorFieldSpacing = 12.dp // between title and body

    // Icons
    val IconSize = 24.dp
}
