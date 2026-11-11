package com.cphan.quicknotemvi.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = Blue40,
    onPrimary = Color.White,
    primaryContainer = Blue90,
    onPrimaryContainer = Blue40,
    inversePrimary = Blue80,
    background = Neutral98,
    onBackground = Neutral10,
    surface = Color.White,
    onSurface = Neutral10,
    onSurfaceVariant = NeutralVariant40,
    outlineVariant = NeutralVariant90,
    inverseSurface = Neutral20,
    inverseOnSurface = Neutral95,
    error = Red40,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = Blue80Dark,
    onPrimary = Blue20Dark,
    primaryContainer = Blue30Dark,
    onPrimaryContainer = Blue90,
    inversePrimary = Blue40,
    background = Neutral6,
    onBackground = Neutral90,
    surface = Neutral12,
    onSurface = Neutral90,
    onSurfaceVariant = NeutralVariant80,
    outlineVariant = NeutralVariant30,
    inverseSurface = Neutral90,
    inverseOnSurface = Neutral20,
    error = Red80,
    onError = Red20
)

@Composable
fun QuickNoteMVITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color (Android 12+) would replace the app's palette with the
    // wallpaper's colors, so it is off to keep the designed look.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}