package com.example.moodmusic.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Brand colors
val ColorAzul      = Color(0xFF1DB8D4)
val ColorMorado    = Color(0xFF8B5CF6)

// Global app colors - Made dynamic for Dark Mode
val ColorFondo     @Composable get() = if (LocalIsDarkTheme.current) Color(0xFF121212) else Color(0xFFF0F0F5)
val ColorTexto     @Composable get() = if (LocalIsDarkTheme.current) Color.White else Color(0xFF1A1A2E)
val ColorSubtexto  @Composable get() = if (LocalIsDarkTheme.current) Color(0xFFBBBBBB) else Color(0xFF888888)
val ColorCampo     @Composable get() = if (LocalIsDarkTheme.current) Color(0xFF1E1E1E) else Color(0xFFFFFFFF)
val ColorBorde     @Composable get() = if (LocalIsDarkTheme.current) Color(0xFF333333) else Color(0xFFE0E0E8)
val ColorBotonGris @Composable get() = if (LocalIsDarkTheme.current) Color(0xFF2C2C2C) else Color(0xFFE8E8EE)
