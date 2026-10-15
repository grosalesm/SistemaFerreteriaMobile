package com.ferreteria.mobile.data.model

data class Cliente(
    val idCliente: Long,
    val tipoDocumento: String?,
    val numeroDocumento: String?,
    val nombreRazonSocial: String?,
    val tipoCliente: String?,
    val telefono: String?,
    val email: String?,
    val direccion: String?,
    val activo: Boolean?,
    val fechaRegistro: String?
)
