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

class PedidosViewModel : ViewModel() {

    var pedidos by mutableStateOf<List<Pedido>>(emptyList())
    var cargando by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    fun cargar() {
        cargando = true
        error = null
        viewModelScope.launch {
            try {
                pedidos = RetrofitClient.api.listarPedidos()
                    .sortedByDescending { it.fechaCreacion }
            } catch (e: Exception) {
                error = leerError(e)
            }
            cargando = false
        }
    }
}
