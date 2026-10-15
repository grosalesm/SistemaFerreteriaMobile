package com.ferreteria.mobile.data

import com.ferreteria.mobile.data.model.Almacen
import com.ferreteria.mobile.data.model.Categoria
import com.ferreteria.mobile.data.model.Cliente
import com.ferreteria.mobile.data.model.ClienteRequest
import com.ferreteria.mobile.data.model.Cotizacion
import com.ferreteria.mobile.data.model.CotizacionRequest
import com.ferreteria.mobile.data.model.LoginRequest
import com.ferreteria.mobile.data.model.LoginResponse
import com.ferreteria.mobile.data.model.Notificacion
import com.ferreteria.mobile.data.model.Pedido
import com.ferreteria.mobile.data.model.Producto
import com.ferreteria.mobile.data.model.RechazoRequest
import com.ferreteria.mobile.data.model.RegistroRequest
import com.ferreteria.mobile.data.model.UsuarioResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("api/auth/registro")
    suspend fun registro(@Body request: RegistroRequest): UsuarioResponse

    @GET("api/auth/me")
    suspend fun obtenerUsuario(): UsuarioResponse

    @GET("api/clientes/me")
    suspend fun obtenerMiCliente(): Cliente

    @GET("api/productos")
    suspend fun listarProductos(
        @Query("idCategoria") idCategoria: Long?,
        @Query("texto") texto: String?,
        @Query("soloConStock") soloConStock: Boolean
    ): List<Producto>

    @GET("api/categorias")
    suspend fun listarCategorias(): List<Categoria>

    @GET("api/almacenes")
    suspend fun listarAlmacenes(): List<Almacen>

    @POST("api/cotizaciones/simular")
    suspend fun simularCotizacion(@Body request: CotizacionRequest): Cotizacion

    @POST("api/cotizaciones")
    suspend fun crearCotizacion(@Body request: CotizacionRequest): Cotizacion

    @POST("api/cotizaciones/{id}/enviar")
    suspend fun enviarCotizacion(@Path("id") id: Long): Cotizacion

    @GET("api/cotizaciones")
    suspend fun listarCotizaciones(): List<Cotizacion>

    @GET("api/cotizaciones/{id}")
    suspend fun obtenerCotizacion(@Path("id") id: Long): Cotizacion

    @POST("api/cotizaciones/{id}/aprobar")
    suspend fun aprobarCotizacion(@Path("id") id: Long): Cotizacion

    @POST("api/cotizaciones/{id}/rechazar")
    suspend fun rechazarCotizacion(@Path("id") id: Long, @Body request: RechazoRequest): Cotizacion

    @POST("api/cotizaciones/{id}/reenviar")
    suspend fun reenviarCotizacion(@Path("id") id: Long): Cotizacion

    @GET("api/pedidos")
    suspend fun listarPedidos(): List<Pedido>

    @GET("api/pedidos/{id}")
    suspend fun obtenerPedido(@Path("id") id: Long): Pedido

    @PUT("api/clientes/me")
    suspend fun actualizarMiCliente(@Body request: ClienteRequest): Cliente

    @GET("api/notificaciones")
    suspend fun listarNotificaciones(@Query("soloNoLeidas") soloNoLeidas: Boolean): List<Notificacion>

    @PATCH("api/notificaciones/{id}/leida")
    suspend fun marcarLeida(@Path("id") id: Long): Notificacion
}
