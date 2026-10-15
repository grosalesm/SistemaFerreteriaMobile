package com.ferreteria.mobile.ui.cotizaciones

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferreteria.mobile.data.RetrofitClient
import com.ferreteria.mobile.data.model.Almacen
import com.ferreteria.mobile.data.model.Cotizacion
import com.ferreteria.mobile.data.model.RechazoRequest
import com.ferreteria.mobile.util.leerError
import kotlinx.coroutines.launch

class DetalleCotizacionViewModel : ViewModel() {

    var cotizacion by mutableStateOf<Cotizacion?>(null)
    var almacenes by mutableStateOf<List<Almacen>>(emptyList())
    var cargando by mutableStateOf(false)
    var procesando by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var errorAccion by mutableStateOf<String?>(null)
    var pedidoGenerado by mutableStateOf(false)
    var mensaje by mutableStateOf<String?>(null)

    fun cargar(id: Long) {
        cargando = true
        error = null
        viewModelScope.launch {
            try {
                cotizacion = RetrofitClient.api.obtenerCotizacion(id)
                if (almacenes.isEmpty()) {
                    almacenes = RetrofitClient.api.listarAlmacenes()
                }
            } catch (e: Exception) {
                error = leerError(e)
            }
            cargando = false
        }
    }

    fun nombreAlmacen(): String {
        val almacen = almacenes.find { it.idAlmacen == cotizacion?.idAlmacen }
        return almacen?.nombre ?: "-"
    }

    fun aprobar() {
        val id = cotizacion?.idCotizacion ?: return
        procesando = true
        viewModelScope.launch {
            try {
                cotizacion = RetrofitClient.api.aprobarCotizacion(id)
                pedidoGenerado = true
            } catch (e: Exception) {
                errorAccion = leerError(e)
            }
            procesando = false
        }
    }

    fun rechazar(motivo: String) {
        val id = cotizacion?.idCotizacion ?: return
        procesando = true
        viewModelScope.launch {
            try {
                cotizacion = RetrofitClient.api.rechazarCotizacion(id, RechazoRequest(motivo))
                mensaje = "La cotización fue rechazada"
            } catch (e: Exception) {
                errorAccion = leerError(e)
            }
            procesando = false
        }
    }

    fun enviar() {
        val id = cotizacion?.idCotizacion ?: return
        procesando = true
        viewModelScope.launch {
            try {
                cotizacion = RetrofitClient.api.enviarCotizacion(id)
                mensaje = "La cotización fue enviada"
            } catch (e: Exception) {
                errorAccion = leerError(e)
            }
            procesando = false
        }
    }

    fun reenviar() {
        val id = cotizacion?.idCotizacion ?: return
        procesando = true
        viewModelScope.launch {
            try {
                val nueva = RetrofitClient.api.reenviarCotizacion(id)
                cotizacion = RetrofitClient.api.enviarCotizacion(nueva.idCotizacion!!)
                mensaje = "Se generó la cotización ${cotizacion?.codigo} con los precios actuales"
            } catch (e: Exception) {
                errorAccion = leerError(e)
            }
            procesando = false
        }
    }
}
