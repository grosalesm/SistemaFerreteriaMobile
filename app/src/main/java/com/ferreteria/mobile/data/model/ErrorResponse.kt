package com.ferreteria.mobile.data.model

data class ErrorResponse(
    val status: Int?,
    val mensaje: String?,
    val faltantes: List<Faltante>?
)

data class Faltante(
    val idProducto: Long,
    val nombre: String,
    val solicitado: Int,
    val disponible: Int
)
