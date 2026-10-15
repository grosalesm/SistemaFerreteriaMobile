package com.ferreteria.mobile.ui.cotizaciones

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferreteria.mobile.data.RetrofitClient
import com.ferreteria.mobile.data.model.Cotizacion
import com.ferreteria.mobile.util.leerError
import kotlinx.coroutines.launch

class CotizacionesViewModel : ViewModel() {

    var cotizaciones by mutableStateOf<List<Cotizacion>>(emptyList())
    var cargando by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    fun cargar() {
        cargando = true
        error = null
        viewModelScope.launch {
            try {
                cotizaciones = RetrofitClient.api.listarCotizaciones()
                    .sortedByDescending { it.fechaCreacion }
            } catch (e: Exception) {
                error = leerError(e)
            }
            cargando = false
        }
    }
}
