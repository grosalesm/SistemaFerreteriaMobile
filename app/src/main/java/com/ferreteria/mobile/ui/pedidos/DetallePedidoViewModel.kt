package com.ferreteria.mobile.ui.pedidos

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferreteria.mobile.data.RetrofitClient
import com.ferreteria.mobile.data.model.Pedido
import com.ferreteria.mobile.util.leerError
import kotlinx.coroutines.launch

class DetallePedidoViewModel : ViewModel() {

    var pedido by mutableStateOf<Pedido?>(null)
    var cargando by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    fun cargar(id: Long) {
        cargando = true
        error = null
        viewModelScope.launch {
            try {
                pedido = RetrofitClient.api.obtenerPedido(id)
            } catch (e: Exception) {
                error = leerError(e)
            }
            cargando = false
        }
    }
}
