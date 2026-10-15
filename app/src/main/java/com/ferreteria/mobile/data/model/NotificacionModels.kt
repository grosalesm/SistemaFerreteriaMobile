package com.ferreteria.mobile.data.model

data class Notificacion(
    val idNotificacion: Long,
    val tipo: String?,
    val titulo: String?,
    val mensaje: String?,
    val clienteId: Long?,
    val referenciaId: Long?,
    val leida: Boolean,
    val fecha: String?
)
