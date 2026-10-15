package com.ferreteria.mobile.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferreteria.mobile.data.RetrofitClient
import com.ferreteria.mobile.data.model.LoginRequest
import com.ferreteria.mobile.util.SessionManager
import com.ferreteria.mobile.util.leerError
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var cargando by mutableStateOf(false)
    var error by mutableStateOf<String?>(
        if (SessionManager.sesionExpirada) "Tu sesión expiró, vuelve a iniciar sesión" else null
    )
    var ingresoCorrecto by mutableStateOf(false)

    fun iniciarSesion() {
        if (username.isBlank() || password.isBlank()) {
            error = "Ingresa tu usuario y contraseña"
            return
        }
        cargando = true
        error = null
        viewModelScope.launch {
            try {
                val respuesta = RetrofitClient.api.login(LoginRequest(username.trim(), password))
                if (respuesta.rol != "CLIENTE") {
                    error = "Esta app es para clientes"
                } else {
                    SessionManager.guardarToken(respuesta.token)
                    val usuario = RetrofitClient.api.obtenerUsuario()
                    SessionManager.guardarDatos(
                        respuesta.idUsuario,
                        respuesta.nombreCompleto,
                        respuesta.rol,
                        usuario.clienteId ?: 0
                    )
                    SessionManager.sesionExpirada = false
                    ingresoCorrecto = true
                }
            } catch (e: Exception) {
                SessionManager.cerrarSesion()
                error = leerError(e)
            }
            cargando = false
        }
    }
}
