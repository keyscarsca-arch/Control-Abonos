package com.abonos.control.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * Formato numérico local colombiano: puntos como separador de miles, sin decimales.
 * Ej: 150000 -> "$150.000"
 */
private val simbolos = DecimalFormatSymbols(Locale("es", "CO")).apply {
    groupingSeparator = '.'
    decimalSeparator = ','
}
private val formateador = DecimalFormat("#,###", simbolos)

fun Long.aFormatoCop(): String = "$" + formateador.format(this)

fun Int.aFormatoCop(): String = this.toLong().aFormatoCop()

/** Convierte texto ingresado por el usuario (puede traer puntos o no) a Long en COP. */
fun String.aLongCop(): Long =
    this.replace(".", "").replace(",", "").trim().toLongOrNull() ?: 0L
