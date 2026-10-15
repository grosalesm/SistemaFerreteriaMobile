package com.ferreteria.mobile.ui.notificaciones

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ferreteria.mobile.data.AvisosEnVivo
import com.ferreteria.mobile.data.model.Notificacion
import com.ferreteria.mobile.util.formatearFecha

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificacionesScreen(
    alVolver: () -> Unit,
    alAbrirCotizacion: (Long) -> Unit,
    alAbrirPedido: (Long) -> Unit,
    viewModel: NotificacionesViewModel = viewModel()
) {
    LaunchedEffect(AvisosEnVivo.ultimoAviso) {
        viewModel.cargar()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notificaciones") },
                navigationIcon = {
                    IconButton(onClick = alVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (viewModel.cargando && viewModel.notificaciones.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (viewModel.error != null && viewModel.notificaciones.isEmpty()) {
                Text(
                    text = viewModel.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            } else if (viewModel.notificaciones.isEmpty()) {
                Text("No tienes notificaciones", modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (viewModel.error != null) {
                        item {
                            Text(text = viewModel.error!!, color = MaterialTheme.colorScheme.error)
                        }
                    }
                    items(viewModel.notificaciones) { notificacion ->
                        NotificacionCard(
                            notificacion = notificacion,
                            alMarcarLeida = { viewModel.marcarLeida(notificacion) },
                            alTocar = {
                                viewModel.marcarLeida(notificacion)
                                val id = notificacion.referenciaId
                                val tipo = notificacion.tipo ?: ""
                                if (id != null && tipo.startsWith("cotizacion.")) {
                                    alAbrirCotizacion(id)
                                } else if (id != null && (tipo.startsWith("venta.") || tipo.startsWith("pedido."))) {
                                    alAbrirPedido(id)
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
fun NotificacionCard(
    notificacion: Notificacion,
    alMarcarLeida: () -> Unit,
    alTocar: () -> Unit
) {
    val colorFondo = if (notificacion.leida) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        MaterialTheme.colorScheme.primaryContainer
    }

    Card(
        onClick = alTocar,
        colors = CardDefaults.cardColors(containerColor = colorFondo),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = notificacion.titulo ?: "",
                fontWeight = if (notificacion.leida) FontWeight.Normal else FontWeight.Bold
            )
            Text(text = notificacion.mensaje ?: "")
            Text(
                text = formatearFecha(notificacion.fecha),
                style = MaterialTheme.typography.bodySmall
            )
            if (!notificacion.leida) {
                TextButton(onClick = alMarcarLeida, modifier = Modifier.align(Alignment.End)) {
                    Text("Marcar como leída")
                }
            }
        }
    }
}
