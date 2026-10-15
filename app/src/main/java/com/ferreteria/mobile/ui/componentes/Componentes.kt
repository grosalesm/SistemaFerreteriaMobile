package com.ferreteria.mobile.ui.componentes

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ferreteria.mobile.data.AvisosEnVivo
import com.ferreteria.mobile.data.model.Cotizacion
import com.ferreteria.mobile.util.formatearSoles

fun textoEstado(estado: String?): String {
    return when (estado) {
        "BORRADOR" -> "Borrador"
        "ENVIADA" -> "Enviada"
        "APROBADA" -> "Aprobada"
        "RECHAZADA" -> "Rechazada"
        "VENCIDA" -> "Vencida"
        "CONVERTIDA" -> "Convertida en pedido"
        "PENDIENTE_PAGO" -> "Pendiente de pago"
        "PAGADO" -> "Pagado"
        "EN_PREPARACION" -> "En preparación"
        "DESPACHADO" -> "Despachado"
        "ENTREGADO" -> "Entregado"
        "ANULADO" -> "Anulado"
        "VENCIDO" -> "Vencido"
        else -> estado ?: "-"
    }
}

@Composable
fun EstadoEtiqueta(estado: String?) {
    val color = when (estado) {
        "APROBADA", "CONVERTIDA", "PAGADO", "ENTREGADO" -> Color(0xFF2E7D32)
        "RECHAZADA", "VENCIDA", "ANULADO", "VENCIDO" -> MaterialTheme.colorScheme.error
        "BORRADOR" -> Color.Gray
        else -> MaterialTheme.colorScheme.primary
    }
    Surface(color = color, shape = RoundedCornerShape(8.dp)) {
        Text(
            text = textoEstado(estado),
            color = Color.White,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun CampanaNotificaciones(alAbrir: () -> Unit) {
    IconButton(onClick = alAbrir) {
        if (AvisosEnVivo.noLeidas > 0) {
            BadgedBox(badge = { Badge { Text("${AvisosEnVivo.noLeidas}") } }) {
                Icon(Icons.Filled.Notifications, contentDescription = "Notificaciones")
            }
        } else {
            Icon(Icons.Filled.Notifications, contentDescription = "Notificaciones")
        }
    }
}

@Composable
fun FilaTotal(etiqueta: String, valor: String, negrita: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = etiqueta,
            modifier = Modifier.weight(1f),
            fontWeight = if (negrita) FontWeight.Bold else FontWeight.Normal
        )
        Text(text = valor, fontWeight = if (negrita) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
fun TablaTotales(cotizacion: Cotizacion) {
    FilaTotal("Subtotal", formatearSoles(cotizacion.subtotal))
    FilaTotal("Descuento", "- " + formatearSoles(cotizacion.descuentoTotal))
    FilaTotal("IGV incluido (18 %)", formatearSoles(cotizacion.igv))
    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
    FilaTotal("Total", formatearSoles(cotizacion.total), negrita = true)
}
