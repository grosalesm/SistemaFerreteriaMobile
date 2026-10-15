package com.ferreteria.mobile.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun formatearSoles(monto: Double?): String {
    return "S/ " + String.format(Locale.US, "%,.2f", monto ?: 0.0)
}

fun formatearPorcentaje(porcentaje: Double?): String {
    return DecimalFormat("0.##", DecimalFormatSymbols(Locale.US)).format(porcentaje ?: 0.0) + " %"
}

fun formatearFecha(fecha: String?): String {
    if (fecha.isNullOrBlank()) return "-"
    return try {
        val fechaHora = LocalDateTime.parse(fecha)
        fechaHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
    } catch (e: Exception) {
        fecha
    }
}
