package com.ferreteria.mobile.data.model

data class Producto(
    val idProducto: Long,
    val sku: String,
    val nombre: String,
    val descripcion: String?,
    val idCategoria: Long?,
    val unidadMedida: String?,
    val precioMinorista: Double,
    val precioContratista: Double,
    val cantidadMinimaDescuento: Int?,
    val porcentajeDescuento: Double?,
    val disponible: Int?,
    val disponiblePorAlmacen: List<DisponibleAlmacen>?
)

data class DisponibleAlmacen(
    val idAlmacen: Long,
    val disponible: Int
)

data class Categoria(
    val idCategoria: Long,
    val nombre: String,
    val activo: Boolean?
)

data class Almacen(
    val idAlmacen: Long,
    val codigo: String,
    val nombre: String
)
