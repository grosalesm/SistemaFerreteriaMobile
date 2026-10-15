package com.ferreteria.mobile.ui.carrito

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ferreteria.mobile.ui.componentes.CampanaNotificaciones
import com.ferreteria.mobile.data.Carrito
import com.ferreteria.mobile.data.ItemCarrito
import com.ferreteria.mobile.data.model.Cotizacion
import com.ferreteria.mobile.data.model.ItemCotizacion
import com.ferreteria.mobile.ui.componentes.TablaTotales
import com.ferreteria.mobile.util.formatearPorcentaje
import com.ferreteria.mobile.util.formatearSoles
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(
    alVerCotizaciones: () -> Unit,
    alAbrirNotificaciones: () -> Unit,
    viewModel: CarritoViewModel = viewModel()
) {
    LaunchedEffect(Carrito.items.toList(), Carrito.idAlmacen) {
        delay(400)
        viewModel.simular()
    }

    if (viewModel.codigoEnviado != null) {
        AlertDialog(
            onDismissRequest = { viewModel.codigoEnviado = null },
            title = { Text("Cotización enviada") },
            text = { Text("Tu cotización ${viewModel.codigoEnviado} fue enviada. Puedes aprobarla desde Mis cotizaciones.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.codigoEnviado = null
                    alVerCotizaciones()
                }) { Text("Ver mis cotizaciones") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.codigoEnviado = null }) { Text("Cerrar") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carrito") },
                actions = { CampanaNotificaciones(alAbrir = alAbrirNotificaciones) }
            )
        }
    ) { padding ->
        if (Carrito.items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Tu carrito está vacío")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text("Almacén de despacho", fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (almacen in viewModel.almacenes) {
                            FilterChip(
                                selected = Carrito.idAlmacen == almacen.idAlmacen,
                                onClick = { Carrito.idAlmacen = almacen.idAlmacen },
                                label = { Text(almacen.nombre) }
                            )
                        }
                    }
                }

                items(Carrito.items, key = { it.producto.idProducto }) { item ->
                    ItemCarritoCard(item = item, simulado = viewModel.itemSimulado(item.producto.idProducto))
                }

                if (viewModel.hayFaltaDeStock()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Warning, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Algunos productos no tienen stock suficiente en este almacén. Puedes solicitar la cotización, pero al aprobarla se validará el stock.")
                            }
                        }
                    }
                }

                item {
                    ResumenTotales(simulacion = viewModel.simulacion, calculando = viewModel.calculando)
                }

                if (viewModel.error != null) {
                    item {
                        Text(text = viewModel.error!!, color = MaterialTheme.colorScheme.error)
                    }
                }

                item {
                    Button(
                        onClick = { viewModel.solicitarCotizacion() },
                        enabled = !viewModel.enviando,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (viewModel.enviando) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Solicitar cotización")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ItemCarritoCard(item: ItemCarrito, simulado: ItemCotizacion?) {
    var textoCantidad by remember(item.cantidad) { mutableStateOf(item.cantidad.toString()) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.producto.nombre,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { Carrito.quitar(item.producto.idProducto) }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Quitar")
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { Carrito.cambiarCantidad(item.producto.idProducto, item.cantidad - 1) }) {
                    Icon(Icons.Filled.Remove, contentDescription = "Restar")
                }
                OutlinedTextField(
                    value = textoCantidad,
                    onValueChange = { nuevo ->
                        textoCantidad = nuevo.filter { it.isDigit() }
                        val cantidad = textoCantidad.toIntOrNull()
                        if (cantidad != null && cantidad > 0) {
                            Carrito.cambiarCantidad(item.producto.idProducto, cantidad)
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.width(90.dp)
                )
                IconButton(onClick = { Carrito.cambiarCantidad(item.producto.idProducto, item.cantidad + 1) }) {
                    Icon(Icons.Filled.Add, contentDescription = "Sumar")
                }
                Text(text = item.producto.unidadMedida ?: "")
            }

            if (simulado != null) {
                Text("Precio unitario: ${formatearSoles(simulado.precioUnitario)}")
                if ((simulado.porcentajeDescuento ?: 0.0) > 0) {
                    Text(
                        text = "Descuento por volumen: ${formatearPorcentaje(simulado.porcentajeDescuento)}",
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Text(
                    text = "Subtotal: ${formatearSoles(simulado.subtotal)}",
                    fontWeight = FontWeight.Bold
                )
                if (simulado.aviso != null) {
                    Text(text = simulado.aviso, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun ResumenTotales(simulacion: Cotizacion?, calculando: Boolean) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Resumen", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                if (calculando) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (simulacion == null) {
                Text("Calculando totales...")
            } else {
                TablaTotales(simulacion)
            }
        }
    }
}
