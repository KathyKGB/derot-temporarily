package com.freise.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FreiseScheme = darkColorScheme(
    primary = Color(0xFFB8FFD8),
    onPrimary = Color(0xFF071611),
    secondary = Color(0xFF9DFF63),
    onSecondary = Color(0xFF071611),
    tertiary = Color(0xFFFFC46B),
    background = Color(0xFF071611),
    onBackground = Color(0xFFFFF7E8),
    surface = Color(0xFF10251F),
    onSurface = Color(0xFFFFF7E8),
    error = Color(0xFFFF7058)
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = FreiseScheme,
        content = content
    )
}
