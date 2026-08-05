package com.abonos.control.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val EsquemaClaro = lightColorScheme(
    primary = AzulPrimario,
    onPrimary = BlancoPuro,
    secondary = DoradoPrimario,
    onSecondary = GrisTexto,
    background = AzulFondo,
    onBackground = GrisTexto,
    surface = BlancoPuro,
    onSurface = GrisTexto,
    error = RojoDeuda,
    onError = BlancoPuro
)

@Composable
fun ControlAbonosTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EsquemaClaro,
        typography = AppTypography,
        content = content
    )
}
