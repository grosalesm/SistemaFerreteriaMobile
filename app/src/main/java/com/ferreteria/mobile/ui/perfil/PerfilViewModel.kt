package com.ferreteria.mobile.ui.perfil

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferreteria.mobile.data.AvisosEnVivo
import com.ferreteria.mobile.data.Carrito
import com.ferreteria.mobile.data.RetrofitClient
import com.ferreteria.mobile.data.model.Cliente
import com.ferreteria.mobile.data.model.ClienteRequest
import com.ferreteria.mobile.util.SessionManager
import com.ferreteria.mobile.util.leerError
import kotlinx.coroutines.launch

class PerfilViewModel : ViewModel() {

    var cliente by mutableStateOf<Cliente?>(null)
    var telefono by mutableStateOf("")
    var email by mutableStateOf("")
    var direccion by mutableStateOf("")
    var cargando by mutableStateOf(false)
    var guardando by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var mensaje by mutableStateOf<String?>(null)

    init {
        cargar()
    }

    fun cargar() {
        cargando = true
        error = null
        viewModelScope.launch {
            try {
                mostrarDatos(RetrofitClient.api.obtenerMiCliente())
            } catch (e: Exception) {
                error = leerError(e)
            }
            cargando = false
        }
    }

    private fun mostrarDatos(datos: Cliente) {
        cliente = datos
        telefono = datos.telefono ?: ""
        email = datos.email ?: ""
        direccion = datos.direccion ?: ""
    }

    fun guardar() {
        val actual = cliente ?: return
        if (email.isNotBlank() && !email.contains("@")) {
            error = "Ingresa un email válido"
            return
        }
        guardando = true
        error = null
        viewModelScope.launch {
            try {
                val request = ClienteRequest(
                    tipoDocumento = actual.tipoDocumento,
                    numeroDocumento = actual.numeroDocumento,
                    nombreRazonSocial = actual.nombreRazonSocial,
                    telefono = telefono.trim().ifBlank { null },
                    email = email.trim().ifBlank { null },
                    direccion = direccion.trim().ifBlank { null }
                )
                mostrarDatos(RetrofitClient.api.actualizarMiCliente(request))
                mensaje = "Tus datos se guardaron correctamente"
            } catch (e: Exception) {
                error = leerError(e)
            }
            guardando = false
        }
    }

    fun cerrarSesion() {
        AvisosEnVivo.desconectar()
        Carrito.vaciar()
        SessionManager.cerrarSesion()
    }
}
