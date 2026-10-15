package com.ferreteria.mobile.data.model

data class Pedido(
    val idPedido: Long,
    val codigo: String,
    val cotizacionId: Long?,
    val codigoCotizacion: String?,
    val idAlmacen: Long?,
    val estado: String,
    val total: Double?,
    val tipoEntrega: String?,
    val direccionEntrega: String?,
    val repartidor: String?,
    val fechaCreacion: String?,
    val fechaLimitePago: String?,
    val fechaPago: String?,
    val fechaPreparacion: String?,
    val fechaDespacho: String?,
    val fechaEntrega: String?,
    val motivoAnulacion: String?,
    val items: List<ItemPedido>?,
    val pago: Pago?,
    val comprobante: Comprobante?
)

data class ItemPedido(
    val idItem: Long,
    val idProducto: Long,
    val sku: String?,
    val nombreProducto: String?,
    val cantidad: Int,
    val precioUnitario: Double?,
    val subtotal: Double?
)

data class Pago(
    val metodo: String?,
    val monto: Double?,
    val fecha: String?,
    val devuelto: Boolean?
)

data class Comprobante(
    val tipo: String?,
    val numeroComprobante: String?,
    val fechaEmision: String?
)
