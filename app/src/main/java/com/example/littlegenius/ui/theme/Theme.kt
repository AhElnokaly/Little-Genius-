package com.example.littlegenius.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = SkyBlue600,
    onPrimary = Color.White,
    primaryContainer = SkyBlue100,
    onPrimaryContainer = SkyBlue900,
    secondary = Amber400,
    onSecondary = Color.Black,
    secondaryContainer = Amber300,
    background = SkyBlue50,
    onBackground = SkyBlue900,
    surface = Color.White,
    onSurface = Color.Black
)

@Composable
fun LittleGeniusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
