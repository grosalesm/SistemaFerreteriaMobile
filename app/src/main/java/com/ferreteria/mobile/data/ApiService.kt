package com.ferreteria.mobile.data

import com.ferreteria.mobile.data.model.LoginRequest
import com.ferreteria.mobile.data.model.LoginResponse
import com.ferreteria.mobile.data.model.RegistroRequest
import com.ferreteria.mobile.data.model.UsuarioResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("api/auth/registro")
    suspend fun registro(@Body request: RegistroRequest): UsuarioResponse

    @GET("api/auth/me")
    suspend fun obtenerUsuario(): UsuarioResponse
}
