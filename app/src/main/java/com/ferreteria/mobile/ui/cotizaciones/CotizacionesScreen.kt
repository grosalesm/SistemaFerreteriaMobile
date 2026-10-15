package com.ferreteria.mobile.ui.cotizaciones

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ferreteria.mobile.ui.componentes.CampanaNotificaciones
import com.ferreteria.mobile.data.model.Cotizacion
import com.ferreteria.mobile.ui.componentes.EstadoEtiqueta
import com.ferreteria.mobile.util.formatearFecha
import com.ferreteria.mobile.util.formatearSoles

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CotizacionesScreen(
    alAbrirCotizacion: (Long) -> Unit,
    alAbrirNotificaciones: () -> Unit,
    viewModel: CotizacionesViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.cargar()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis cotizaciones") },
                actions = {
                    IconButton(onClick = { viewModel.cargar() }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Actualizar")
                    }
                    CampanaNotificaciones(alAbrir = alAbrirNotificaciones)
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (viewModel.cargando && viewModel.cotizaciones.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (viewModel.error != null) {
                Text(
                    text = viewModel.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            } else if (viewModel.cotizaciones.isEmpty()) {
                Text("Aún no tienes cotizaciones", modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(viewModel.cotizaciones) { cotizacion ->
                        CotizacionCard(
                            cotizacion = cotizacion,
                            alTocar = { alAbrirCotizacion(cotizacion.idCotizacion!!) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CotizacionCard(cotizacion: Cotizacion, alTocar: () -> Unit) {
    Card(onClick = alTocar, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = cotizacion.codigo ?: "",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                EstadoEtiqueta(cotizacion.estado)
            }
            Text("Creada: ${formatearFecha(cotizacion.fechaCreacion)}")
            if (cotizacion.estado == "ENVIADA") {
                Text("Vence: ${formatearFecha(cotizacion.fechaVencimiento)}")
            }
            Text(
                text = "Total: ${formatearSoles(cotizacion.total)}",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
