package com.ferreteria.mobile.ui.pedidos

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ferreteria.mobile.data.model.Pedido
import com.ferreteria.mobile.ui.componentes.EstadoEtiqueta
import com.ferreteria.mobile.ui.componentes.FilaTotal
import com.ferreteria.mobile.util.formatearFecha
import com.ferreteria.mobile.util.formatearSoles

data class Paso(val estado: String, val titulo: String, val fecha: String?)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallePedidoScreen(
    idPedido: Long,
    alVolver: () -> Unit,
    viewModel: DetallePedidoViewModel = viewModel()
) {
    LaunchedEffect(idPedido) {
        viewModel.cargar(idPedido)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del pedido") },
                navigationIcon = {
                    IconButton(onClick = alVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.cargar(idPedido) }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Actualizar")
                    }
                }
            )
        }
    ) { padding ->
        val pedido = viewModel.pedido
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (viewModel.cargando && pedido == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (viewModel.error != null) {
                Text(
                    text = viewModel.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            } else if (pedido != null) {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item { DatosPedido(pedido) }
                    item { PasosPedido(pedido) }
                    item { Text("Productos", fontWeight = FontWeight.Bold) }
                    items(pedido.items ?: emptyList()) { item ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = item.nombreProducto ?: "", fontWeight = FontWeight.Bold)
                                Text("${item.cantidad} x ${formatearSoles(item.precioUnitario)}")
                                Text("Subtotal: ${formatearSoles(item.subtotal)}")
                            }
                        }
                    }
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                FilaTotal("Total", formatearSoles(pedido.total), negrita = true)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DatosPedido(pedido: Pedido) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = pedido.codigo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                EstadoEtiqueta(pedido.estado)
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (pedido.codigoCotizacion != null) {
                Text("Cotización: ${pedido.codigoCotizacion}")
            }
            Text("Fecha: ${formatearFecha(pedido.fechaCreacion)}")
            if (pedido.estado == "PENDIENTE_PAGO") {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Acércate a caja para pagar antes del ${formatearFecha(pedido.fechaLimitePago)}",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            if (pedido.tipoEntrega != null) {
                Text("Entrega: ${if (pedido.tipoEntrega == "DELIVERY") "Delivery" else "Recojo en tienda"}")
            }
            if (pedido.direccionEntrega != null) {
                Text("Dirección: ${pedido.direccionEntrega}")
            }
            if (pedido.repartidor != null) {
                Text("Repartidor: ${pedido.repartidor}")
            }
            if (pedido.pago != null) {
                Text("Pago: ${formatearSoles(pedido.pago.monto)} el ${formatearFecha(pedido.pago.fecha)}")
            }
            if (pedido.comprobante != null) {
                Text("Comprobante: ${pedido.comprobante.numeroComprobante}")
            }
        }
    }

    if (pedido.estado == "ANULADO" || pedido.estado == "VENCIDO") {
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (pedido.estado == "ANULADO") {
                    Text("Este pedido fue anulado", fontWeight = FontWeight.Bold)
                    if (pedido.motivoAnulacion != null) {
                        Text("Motivo: ${pedido.motivoAnulacion}")
                    }
                } else {
                    Text("Este pedido venció porque no se pagó a tiempo", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PasosPedido(pedido: Pedido) {
    val pasos = listOf(
        Paso("PENDIENTE_PAGO", "Pendiente de pago", pedido.fechaCreacion),
        Paso("PAGADO", "Pagado", pedido.fechaPago),
        Paso("EN_PREPARACION", "En preparación", pedido.fechaPreparacion),
        Paso("DESPACHADO", "Despachado", pedido.fechaDespacho),
        Paso("ENTREGADO", "Entregado", pedido.fechaEntrega)
    )

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Seguimiento", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            for (paso in pasos) {
                val completado = paso.fecha != null
                val actual = paso.estado == pedido.estado
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = if (completado) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (completado) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = paso.titulo,
                            fontWeight = if (actual) FontWeight.Bold else FontWeight.Normal,
                            color = if (completado) MaterialTheme.colorScheme.onSurface else Color.Gray
                        )
                        if (completado) {
                            Text(
                                text = formatearFecha(paso.fecha),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}
