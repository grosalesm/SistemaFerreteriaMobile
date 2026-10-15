package com.ferreteria.mobile.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Naranja = Color(0xFFF57C00)
val NaranjaClaro = Color(0xFFFFE0B2)
val NaranjaOscuro = Color(0xFF5D2E00)

private val colores = lightColorScheme(
    primary = Naranja,
    onPrimary = Color.White,
    primaryContainer = NaranjaClaro,
    onPrimaryContainer = NaranjaOscuro,
    secondary = Color(0xFF6D4C41),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFF3E0),
    onSecondaryContainer = NaranjaOscuro,
    background = Color(0xFFFFFBF7),
    surface = Color(0xFFFFFBF7)
)

@Composable
fun FerreteriaTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = colores, content = content)
}
