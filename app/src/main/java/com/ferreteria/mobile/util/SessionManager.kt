package com.ferreteria.mobile.util

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object SessionManager {

    private lateinit var prefs: SharedPreferences

    var sesionExpirada by mutableStateOf(false)

    fun iniciar(context: Context) {
        prefs = context.getSharedPreferences("sesion", Context.MODE_PRIVATE)
    }

    fun guardarToken(token: String) {
        prefs.edit().putString("token", token).apply()
    }

    fun guardarDatos(idUsuario: Long, nombre: String, rol: String, clienteId: Long) {
        prefs.edit()
            .putLong("idUsuario", idUsuario)
            .putString("nombre", nombre)
            .putString("rol", rol)
            .putLong("clienteId", clienteId)
            .apply()
    }

    fun obtenerToken(): String? = prefs.getString("token", null)

    fun obtenerNombre(): String = prefs.getString("nombre", "") ?: ""

    fun obtenerClienteId(): Long = prefs.getLong("clienteId", 0)

    fun haySesion(): Boolean = obtenerToken() != null && obtenerClienteId() != 0L

    fun cerrarSesion() {
        prefs.edit().clear().apply()
    }
}
