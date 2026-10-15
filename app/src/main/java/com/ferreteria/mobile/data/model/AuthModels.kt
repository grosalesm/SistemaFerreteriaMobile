package com.ferreteria.mobile.data.model

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val idUsuario: Long,
    val nombreCompleto: String,
    val rol: String
)

data class RegistroRequest(
    val username: String,
    val nombreCompleto: String,
    val email: String,
    val password: String,
    val tipoDocumento: String,
    val numeroDocumento: String,
    val telefono: String?
)

data class UsuarioResponse(
    val idUsuario: Long,
    val username: String,
    val nombreCompleto: String,
    val email: String?,
    val rol: String,
    val clienteId: Long?,
    val activo: Boolean?,
    val fechaCreacion: String?
)
