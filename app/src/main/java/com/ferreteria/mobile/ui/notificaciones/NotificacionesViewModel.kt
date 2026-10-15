package com.ferreteria.mobile.ui.notificaciones

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferreteria.mobile.data.AvisosEnVivo
import com.ferreteria.mobile.data.RetrofitClient
import com.ferreteria.mobile.data.model.Notificacion
import com.ferreteria.mobile.util.leerError
import kotlinx.coroutines.launch

class NotificacionesViewModel : ViewModel() {

    var notificaciones by mutableStateOf<List<Notificacion>>(emptyList())
    var cargando by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    fun cargar() {
        cargando = true
        error = null
        viewModelScope.launch {
            try {
                notificaciones = RetrofitClient.api.listarNotificaciones(false)
                    .sortedByDescending { it.fecha }
                AvisosEnVivo.noLeidas = notificaciones.count { !it.leida }
            } catch (e: Exception) {
                error = leerError(e)
            }
            cargando = false
        }
    }

    fun marcarLeida(notificacion: Notificacion) {
        if (notificacion.leida) return
        viewModelScope.launch {
            try {
                val actualizada = RetrofitClient.api.marcarLeida(notificacion.idNotificacion)
                notificaciones = notificaciones.map {
                    if (it.idNotificacion == actualizada.idNotificacion) actualizada else it
                }
                AvisosEnVivo.noLeidas = notificaciones.count { !it.leida }
            } catch (e: Exception) {
                error = leerError(e)
            }
        }
    }
}
