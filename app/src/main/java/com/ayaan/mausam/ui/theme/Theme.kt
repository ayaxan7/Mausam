package com.ayaan.mausam.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary          = Blue40,
    onPrimary        = Color.White,
    primaryContainer = Blue20,
    onPrimaryContainer = Blue90,
    secondary        = Cyan40,
    onSecondary      = Color.White,
    secondaryContainer = Cyan80.copy(alpha = 0.2f),
    onSecondaryContainer = Cyan80,
    background       = Grey10,
    onBackground     = Grey90,
    surface          = Grey20,
    onSurface        = Grey90,
    surfaceVariant   = Blue10,
    onSurfaceVariant = Blue90,
    error            = Red40,
    onError          = Color.White,
    errorContainer   = Red80.copy(alpha = 0.2f),
    onErrorContainer = Red80,
    outline          = Blue80.copy(alpha = 0.3f),
    outlineVariant   = Blue80.copy(alpha = 0.15f)
)

@Composable
fun MausamTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Grey10.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography  = MausamTypography,
        content     = content
    )
}