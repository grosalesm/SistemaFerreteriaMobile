package com.ferreteria.mobile.ui.catalogo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ferreteria.mobile.data.Carrito
import com.ferreteria.mobile.data.model.Producto
import com.ferreteria.mobile.util.formatearPorcentaje
import com.ferreteria.mobile.util.formatearSoles
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(viewModel: CatalogoViewModel = viewModel()) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Catálogo") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = viewModel.texto,
                onValueChange = { viewModel.texto = it },
                label = { Text("Buscar producto") },
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { viewModel.buscar() }) {
                        Icon(Icons.Filled.Search, contentDescription = "Buscar")
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { viewModel.buscar() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = viewModel.idCategoria == null,
                        onClick = { viewModel.elegirCategoria(null) },
                        label = { Text("Todas") }
                    )
                }
                items(viewModel.categorias) { categoria ->
                    FilterChip(
                        selected = viewModel.idCategoria == categoria.idCategoria,
                        onClick = { viewModel.elegirCategoria(categoria.idCategoria) },
                        label = { Text(categoria.nombre) }
                    )
                }
            }

            if (viewModel.cargando) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (viewModel.error != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = viewModel.error!!, color = MaterialTheme.colorScheme.error)
                    TextButton(onClick = { viewModel.cargarDatos() }) { Text("Reintentar") }
                }
            } else if (viewModel.productos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se encontraron productos")
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(viewModel.productos) { producto ->
                        ProductoCard(
                            producto = producto,
                            precio = viewModel.precioDe(producto),
                            esContratista = viewModel.tipoCliente == "CONTRATISTA",
                            alAgregar = {
                                Carrito.agregar(producto)
                                scope.launch {
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                    snackbarHostState.showSnackbar("${producto.nombre} agregado al carrito")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductoCard(
    producto: Producto,
    precio: Double,
    esContratista: Boolean,
    alAgregar: () -> Unit
) {
    val porcentaje = producto.porcentajeDescuento ?: 0.0

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = producto.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "SKU: ${producto.sku}",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${formatearSoles(precio)} / ${producto.unidadMedida ?: "unidad"}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            if (esContratista) {
                Text(text = "Precio contratista", style = MaterialTheme.typography.bodySmall)
            }
            if (porcentaje > 0 && producto.cantidadMinimaDescuento != null) {
                Text(
                    text = "Descuento: ${formatearPorcentaje(porcentaje)} desde ${producto.cantidadMinimaDescuento}",
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Text(text = "Disponible: ${producto.disponible ?: 0}")
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = alAgregar,
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(Icons.Filled.AddShoppingCart, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Agregar")
            }
        }
    }
}
