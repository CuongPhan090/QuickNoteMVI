package com.cphan.quicknotemvi.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// The sketch uses Inter; FontFamily.Default (Roboto) until a font is added.
private val AppFont = FontFamily.Default

val Typography = Typography(
    // "Notes" screen title
    headlineSmall = TextStyle(
        fontFamily = AppFont,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.4).sp
    ),
    // Empty-state heading
    titleLarge = TextStyle(
        fontFamily = AppFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp
    ),
    // Note title in a card
    titleMedium = TextStyle(
        fontFamily = AppFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp
    ),
    // Empty-state message
    bodyLarge = TextStyle(
        fontFamily = AppFont,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp
    ),
    // Note body preview, snackbar message
    bodyMedium = TextStyle(
        fontFamily = AppFont,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),
    // "New note" button, snackbar "Undo"
    labelLarge = TextStyle(
        fontFamily = AppFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )
)
