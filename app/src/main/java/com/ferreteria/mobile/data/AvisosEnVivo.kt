package com.ferreteria.mobile.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ferreteria.mobile.data.model.Notificacion
import com.ferreteria.mobile.util.Constantes
import com.ferreteria.mobile.util.SessionManager
import com.google.gson.Gson
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.net.URLEncoder

object AvisosEnVivo {

    private var webSocket: WebSocket? = null

    var noLeidas by mutableIntStateOf(0)
    var ultimoAviso by mutableStateOf<Notificacion?>(null)

    fun conectar() {
        if (webSocket != null) return
        val token = SessionManager.obtenerToken() ?: return
        val url = Constantes.WS_URL + "?token=" + URLEncoder.encode(token, "UTF-8")
        val request = Request.Builder().url(url).build()
        webSocket = RetrofitClient.okHttpClient.newWebSocket(request, escucha)
    }

    fun desconectar() {
        webSocket?.close(1000, "Cierre de sesión")
        webSocket = null
        noLeidas = 0
        ultimoAviso = null
    }

    private val escucha = object : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            webSocket.send("CONNECT\naccept-version:1.2\nhost:10.0.2.2\nheart-beat:0,0\n\n\u0000")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            if (text.startsWith("CONNECTED")) {
                val clienteId = SessionManager.obtenerClienteId()
                webSocket.send("SUBSCRIBE\nid:avisos\ndestination:/topic/cliente/$clienteId\n\n\u0000")
            } else if (text.startsWith("MESSAGE")) {
                val cuerpo = text.substringAfter("\n\n").trimEnd('\u0000')
                try {
                    val aviso = Gson().fromJson(cuerpo, Notificacion::class.java)
                    noLeidas += 1
                    ultimoAviso = aviso
                } catch (e: Exception) {
                }
            } else if (text.startsWith("ERROR")) {
                webSocket.close(1000, null)
            }
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            if (AvisosEnVivo.webSocket == webSocket) {
                AvisosEnVivo.webSocket = null
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            if (AvisosEnVivo.webSocket == webSocket) {
                AvisosEnVivo.webSocket = null
            }
        }
    }
}
