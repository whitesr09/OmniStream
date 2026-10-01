package com.omnistream.core.ui.theme

import android.app.Activity
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val OmniBlack = Color(0xFF050505)
val OmniSurface = Color(0xFF121212)
val OmniPrimary = Color(0xFF8B5CF6)

private val DarkColorScheme = darkColorScheme(
    primary = OmniPrimary,
    background = OmniBlack,
    surface = OmniSurface,
    onSurface = Color.White,
    onPrimary = Color.White
)

@Composable
fun OmniStreamTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = OmniBlack.toArgb()
            window.navigationBarColor = OmniBlack.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }
    MaterialTheme(colorScheme = DarkColorScheme, content = content)
}