package com.ferreteria.mobile.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ferreteria.mobile.data.model.Producto

data class ItemCarrito(
    val producto: Producto,
    val cantidad: Int
)

object Carrito {

    val items = mutableStateListOf<ItemCarrito>()
    var idAlmacen by mutableStateOf(1L)

    fun agregar(producto: Producto) {
        val posicion = items.indexOfFirst { it.producto.idProducto == producto.idProducto }
        if (posicion >= 0) {
            items[posicion] = items[posicion].copy(cantidad = items[posicion].cantidad + 1)
        } else {
            items.add(ItemCarrito(producto, 1))
        }
    }

    fun cambiarCantidad(idProducto: Long, cantidad: Int) {
        val posicion = items.indexOfFirst { it.producto.idProducto == idProducto }
        if (posicion < 0) return
        if (cantidad <= 0) {
            items.removeAt(posicion)
        } else {
            items[posicion] = items[posicion].copy(cantidad = cantidad)
        }
    }

    fun quitar(idProducto: Long) {
        items.removeAll { it.producto.idProducto == idProducto }
    }

    fun vaciar() {
        items.clear()
    }
}
