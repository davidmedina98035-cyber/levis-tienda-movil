package com.example.levisappadmin.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LevisColorScheme = lightColorScheme(
    primary = LevisRojo,
    onPrimary = LevisBlanco,
    primaryContainer = LevisRojoOscuro,
    onPrimaryContainer = LevisBlanco,
    secondary = LevisGris,
    onSecondary = LevisBlanco,
    background = LevisGrisClaro,
    onBackground = LevisNegro,
    surface = LevisBlanco,
    onSurface = LevisNegro,
    error = LevisRojoOscuro,
    onError = LevisBlanco
)

@Composable
fun LevisAppAdminTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LevisColorScheme,
        typography = Typography,
        content = content
    )
}