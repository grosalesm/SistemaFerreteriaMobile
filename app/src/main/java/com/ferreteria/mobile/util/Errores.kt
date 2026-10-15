package com.ferreteria.mobile.util

import com.ferreteria.mobile.data.model.ErrorResponse
import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException

fun leerError(e: Exception): String {
    if (e is IOException) {
        return "No se pudo conectar con el servidor"
    }
    if (e !is HttpException) {
        return "Ocurrió un error inesperado"
    }
    val error = try {
        Gson().fromJson(e.response()?.errorBody()?.string(), ErrorResponse::class.java)
    } catch (ex: Exception) {
        null
    }
    // Si el backend indica qué servicio falló (gateway o ms-auth), se muestra ese mensaje
    if (e.code() == 503) {
        return error?.mensaje ?: "Servicio no disponible, intenta en unos segundos"
    }
    val mensaje = error?.mensaje ?: "Ocurrió un error (código ${e.code()})"
    val faltantes = error?.faltantes
    if (faltantes.isNullOrEmpty()) {
        return mensaje
    }
    var texto = mensaje
    for (faltante in faltantes) {
        texto += "\n• ${faltante.nombre}: pediste ${faltante.solicitado}, hay ${faltante.disponible}"
    }
    return texto
}
