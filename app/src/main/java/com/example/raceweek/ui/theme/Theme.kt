package com.example.raceweek.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val RaceWeekColorScheme = darkColorScheme(
    primary = Accent,
    onPrimary = TextPrimary,
    secondary = AccentDim,
    background = BgDark,
    surface = BgCard,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    surfaceVariant = BgCard2,
    outline = Border,
)

@Composable
fun RaceWeekTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = RaceWeekColorScheme,
        typography = Typography,
        content = content
    )
}
