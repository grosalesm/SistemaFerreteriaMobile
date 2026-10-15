package com.ferreteria.mobile.ui.catalogo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferreteria.mobile.data.RetrofitClient
import com.ferreteria.mobile.data.model.Categoria
import com.ferreteria.mobile.data.model.Producto
import com.ferreteria.mobile.util.leerError
import kotlinx.coroutines.launch

class CatalogoViewModel : ViewModel() {

    var productos by mutableStateOf<List<Producto>>(emptyList())
    var categorias by mutableStateOf<List<Categoria>>(emptyList())
    var tipoCliente by mutableStateOf("MINORISTA")
    var texto by mutableStateOf("")
    var idCategoria by mutableStateOf<Long?>(null)
    var cargando by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    init {
        cargarDatos()
    }

    fun cargarDatos() {
        viewModelScope.launch {
            try {
                categorias = RetrofitClient.api.listarCategorias().filter { it.activo != false }
                tipoCliente = RetrofitClient.api.obtenerMiCliente().tipoCliente ?: "MINORISTA"
            } catch (e: Exception) {
                error = leerError(e)
            }
        }
        buscar()
    }

    fun buscar() {
        cargando = true
        error = null
        viewModelScope.launch {
            try {
                productos = RetrofitClient.api.listarProductos(
                    idCategoria = idCategoria,
                    texto = texto.trim().ifBlank { null },
                    soloConStock = true
                )
            } catch (e: Exception) {
                error = leerError(e)
            }
            cargando = false
        }
    }

    fun elegirCategoria(id: Long?) {
        idCategoria = id
        buscar()
    }

    fun precioDe(producto: Producto): Double {
        return if (tipoCliente == "CONTRATISTA") producto.precioContratista else producto.precioMinorista
    }
}
