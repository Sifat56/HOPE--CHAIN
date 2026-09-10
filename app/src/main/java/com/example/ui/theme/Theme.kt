package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = HopeCyan,
    onPrimary = Color(0xFF00363F),
    primaryContainer = Color(0xFF004E5B),
    onPrimaryContainer = HopeCyanGlow,
    secondary = HopeEmerald,
    onSecondary = Color(0xFF003924),
    secondaryContainer = Color(0xFF005236),
    onSecondaryContainer = HopeEmeraldGlow,
    tertiary = HopeGold,
    background = HopeDarkBg,
    onBackground = TextPrimary,
    surface = HopeDarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = HopeDarkCard,
    onSurfaceVariant = TextSecondary,
    outline = HopeDarkCardBorder,
    error = HopeCrimson
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = DarkColorScheme,
    typography = Typography,
    content = content
  )
}

