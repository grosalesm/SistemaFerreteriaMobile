package com.ferreteria.mobile.data.model

data class CotizacionRequest(
    val clienteId: Long,
    val idAlmacen: Long,
    val items: List<ItemRequest>
)

data class ItemRequest(
    val idProducto: Long,
    val cantidad: Int
)

data class RechazoRequest(
    val motivo: String
)

data class Cotizacion(
    val idCotizacion: Long?,
    val codigo: String?,
    val clienteId: Long?,
    val clienteNombre: String?,
    val tipoCliente: String?,
    val idAlmacen: Long?,
    val estado: String?,
    val subtotal: Double?,
    val descuentoTotal: Double?,
    val igv: Double?,
    val total: Double?,
    val fechaCreacion: String?,
    val fechaVencimiento: String?,
    val fechaAprobacion: String?,
    val motivoRechazo: String?,
    val items: List<ItemCotizacion>?
)

data class ItemCotizacion(
    val idItem: Long?,
    val idProducto: Long,
    val sku: String?,
    val nombreProducto: String?,
    val cantidad: Int,
    val precioUnitario: Double?,
    val porcentajeDescuento: Double?,
    val subtotal: Double?,
    val aviso: String?
)
