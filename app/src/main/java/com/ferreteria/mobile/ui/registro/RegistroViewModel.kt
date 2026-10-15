package com.ferreteria.mobile.ui.registro

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferreteria.mobile.data.RetrofitClient
import com.ferreteria.mobile.data.model.RegistroRequest
import com.ferreteria.mobile.util.leerError
import kotlinx.coroutines.launch

class RegistroViewModel : ViewModel() {

    var username by mutableStateOf("")
    var nombreCompleto by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var tipoDocumento by mutableStateOf("DNI")
    var numeroDocumento by mutableStateOf("")
    var telefono by mutableStateOf("")
    var cargando by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var registroCorrecto by mutableStateOf(false)

    private fun validar(): String? {
        if (username.isBlank()) return "Ingresa un usuario"
        if (nombreCompleto.isBlank()) return "Ingresa tu nombre completo o razón social"
        if (!email.contains("@")) return "Ingresa un email válido"
        if (password.isBlank()) return "Ingresa una contraseña"
        val digitos = if (tipoDocumento == "DNI") 8 else 11
        if (numeroDocumento.length != digitos || !numeroDocumento.all { it.isDigit() }) {
            return "El $tipoDocumento debe tener $digitos dígitos"
        }
        return null
    }

    fun registrar() {
        error = validar()
        if (error != null) return
        cargando = true
        viewModelScope.launch {
            try {
                val request = RegistroRequest(
                    username = username.trim(),
                    nombreCompleto = nombreCompleto.trim(),
                    email = email.trim(),
                    password = password,
                    tipoDocumento = tipoDocumento,
                    numeroDocumento = numeroDocumento,
                    telefono = telefono.trim().ifBlank { null }
                )
                RetrofitClient.api.registro(request)
                registroCorrecto = true
            } catch (e: Exception) {
                error = leerError(e)
            }
            cargando = false
        }
    }
}
