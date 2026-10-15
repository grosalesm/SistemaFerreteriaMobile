package com.ferreteria.mobile.ui.cotizaciones

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ferreteria.mobile.data.model.Cotizacion
import com.ferreteria.mobile.data.model.ItemCotizacion
import com.ferreteria.mobile.ui.componentes.EstadoEtiqueta
import com.ferreteria.mobile.ui.componentes.TablaTotales
import com.ferreteria.mobile.util.formatearFecha
import com.ferreteria.mobile.util.formatearPorcentaje
import com.ferreteria.mobile.util.formatearSoles

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleCotizacionScreen(
    idCotizacion: Long,
    alVolver: () -> Unit,
    alVerPedidos: () -> Unit,
    viewModel: DetalleCotizacionViewModel = viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var confirmarAprobar by remember { mutableStateOf(false) }
    var mostrarRechazo by remember { mutableStateOf(false) }
    var motivo by remember { mutableStateOf("") }

    LaunchedEffect(idCotizacion) {
        viewModel.cargar(idCotizacion)
    }

    LaunchedEffect(viewModel.mensaje) {
        if (viewModel.mensaje != null) {
            snackbarHostState.showSnackbar(viewModel.mensaje!!)
            viewModel.mensaje = null
        }
    }

    if (confirmarAprobar) {
        AlertDialog(
            onDismissRequest = { confirmarAprobar = false },
            title = { Text("Aprobar cotización") },
            text = { Text("Se reservará el stock y se generará tu pedido. ¿Deseas continuar?") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarAprobar = false
                    viewModel.aprobar()
                }) { Text("Aprobar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarAprobar = false }) { Text("Cancelar") }
            }
        )
    }

    if (mostrarRechazo) {
        AlertDialog(
            onDismissRequest = { mostrarRechazo = false },
            title = { Text("Rechazar cotización") },
            text = {
                OutlinedTextField(
                    value = motivo,
                    onValueChange = { if (it.length <= 300) motivo = it },
                    label = { Text("Motivo del rechazo") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarRechazo = false
                        viewModel.rechazar(motivo.trim())
                        motivo = ""
                    },
                    enabled = motivo.isNotBlank()
                ) { Text("Rechazar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarRechazo = false }) { Text("Cancelar") }
            }
        )
    }

    if (viewModel.pedidoGenerado) {
        AlertDialog(
            onDismissRequest = { viewModel.pedidoGenerado = false },
            title = { Text("Cotización aprobada") },
            text = { Text("¡Listo! Tu pedido fue generado, acércate a caja para pagar") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.pedidoGenerado = false
                    alVerPedidos()
                }) { Text("Ver mis pedidos") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.pedidoGenerado = false }) { Text("Cerrar") }
            }
        )
    }

    if (viewModel.errorAccion != null) {
        AlertDialog(
            onDismissRequest = { viewModel.errorAccion = null },
            title = { Text("No se pudo completar") },
            text = { Text(viewModel.errorAccion!!) },
            confirmButton = {
                TextButton(onClick = { viewModel.errorAccion = null }) { Text("Aceptar") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de cotización") },
                navigationIcon = {
                    IconButton(onClick = alVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        val cotizacion = viewModel.cotizacion
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (viewModel.cargando && cotizacion == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (viewModel.error != null) {
                Text(
                    text = viewModel.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            } else if (cotizacion != null) {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        DatosCotizacion(cotizacion, viewModel.nombreAlmacen())
                    }
                    item {
                        Text("Productos", fontWeight = FontWeight.Bold)
                    }
                    items(cotizacion.items ?: emptyList()) { item ->
                        ItemCotizacionCard(item)
                    }
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                TablaTotales(cotizacion)
                            }
                        }
                    }
                    item {
                        BotonesAcciones(
                            estado = cotizacion.estado,
                            procesando = viewModel.procesando,
                            alAprobar = { confirmarAprobar = true },
                            alRechazar = { mostrarRechazo = true },
                            alReenviar = { viewModel.reenviar() },
                            alEnviar = { viewModel.enviar() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DatosCotizacion(cotizacion: Cotizacion, almacen: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = cotizacion.codigo ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                EstadoEtiqueta(cotizacion.estado)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Creada: ${formatearFecha(cotizacion.fechaCreacion)}")
            if (cotizacion.fechaVencimiento != null) {
                Text("Vence: ${formatearFecha(cotizacion.fechaVencimiento)}")
            }
            if (cotizacion.fechaAprobacion != null) {
                Text("Aprobada: ${formatearFecha(cotizacion.fechaAprobacion)}")
            }
            Text("Almacén: $almacen")
            if (cotizacion.motivoRechazo != null) {
                Text(
                    text = "Motivo del rechazo: ${cotizacion.motivoRechazo}",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun ItemCotizacionCard(item: ItemCotizacion) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = item.nombreProducto ?: "", fontWeight = FontWeight.Bold)
            Text("${item.cantidad} x ${formatearSoles(item.precioUnitario)}")
            if ((item.porcentajeDescuento ?: 0.0) > 0) {
                Text(
                    text = "Descuento por volumen: ${formatearPorcentaje(item.porcentajeDescuento)}",
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Text(text = "Subtotal: ${formatearSoles(item.subtotal)}", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun BotonesAcciones(
    estado: String?,
    procesando: Boolean,
    alAprobar: () -> Unit,
    alRechazar: () -> Unit,
    alReenviar: () -> Unit,
    alEnviar: () -> Unit
) {
    if (procesando) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(modifier = Modifier.size(32.dp))
        }
        return
    }
    when (estado) {
        "ENVIADA" -> {
            Column {
                Button(onClick = alAprobar, modifier = Modifier.fillMaxWidth()) {
                    Text("Aprobar")
                }
                OutlinedButton(
                    onClick = alRechazar,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Rechazar")
                }
            }
        }
        "VENCIDA", "RECHAZADA" -> {
            Button(onClick = alReenviar, modifier = Modifier.fillMaxWidth()) {
                Text("Reenviar")
            }
        }
        "BORRADOR" -> {
            Button(onClick = alEnviar, modifier = Modifier.fillMaxWidth()) {
                Text("Enviar")
            }
        }
    }
}
