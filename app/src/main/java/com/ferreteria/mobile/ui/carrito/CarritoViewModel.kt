package com.ferreteria.mobile.ui.carrito

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferreteria.mobile.data.Carrito
import com.ferreteria.mobile.data.RetrofitClient
import com.ferreteria.mobile.data.model.Almacen
import com.ferreteria.mobile.data.model.Cotizacion
import com.ferreteria.mobile.data.model.CotizacionRequest
import com.ferreteria.mobile.data.model.ItemCotizacion
import com.ferreteria.mobile.data.model.ItemRequest
import com.ferreteria.mobile.util.SessionManager
import com.ferreteria.mobile.util.leerError
import kotlinx.coroutines.launch

class CarritoViewModel : ViewModel() {

    var almacenes by mutableStateOf<List<Almacen>>(emptyList())
    var simulacion by mutableStateOf<Cotizacion?>(null)
    var calculando by mutableStateOf(false)
    var enviando by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var codigoEnviado by mutableStateOf<String?>(null)

    init {
        cargarAlmacenes()
    }

    private fun cargarAlmacenes() {
        viewModelScope.launch {
            try {
                almacenes = RetrofitClient.api.listarAlmacenes()
            } catch (e: Exception) {
                error = leerError(e)
            }
        }
    }

    private fun armarRequest(): CotizacionRequest {
        val items = Carrito.items.map { ItemRequest(it.producto.idProducto, it.cantidad) }
        return CotizacionRequest(SessionManager.obtenerClienteId(), Carrito.idAlmacen, items)
    }

    fun simular() {
        if (Carrito.items.isEmpty()) {
            simulacion = null
            return
        }
        calculando = true
        viewModelScope.launch {
            val request = armarRequest()
            try {
                val respuesta = RetrofitClient.api.simularCotizacion(request)
                if (request == armarRequest()) {
                    simulacion = respuesta
                    error = null
                }
            } catch (e: Exception) {
                error = leerError(e)
            }
            calculando = false
        }
    }

    fun itemSimulado(idProducto: Long): ItemCotizacion? {
        return simulacion?.items?.find { it.idProducto == idProducto }
    }

    fun hayFaltaDeStock(): Boolean {
        return simulacion?.items?.any { it.aviso != null } == true
    }

    fun solicitarCotizacion() {
        if (Carrito.items.isEmpty()) return
        enviando = true
        error = null
        viewModelScope.launch {
            try {
                val creada = RetrofitClient.api.crearCotizacion(armarRequest())
                val enviada = RetrofitClient.api.enviarCotizacion(creada.idCotizacion!!)
                codigoEnviado = enviada.codigo
                Carrito.vaciar()
                simulacion = null
            } catch (e: Exception) {
                error = leerError(e)
            }
            enviando = false
        }
    }
}
