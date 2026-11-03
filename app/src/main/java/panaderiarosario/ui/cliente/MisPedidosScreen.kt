package panaderiarosario.ui.cliente

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import panaderiarosario.AppState
import panaderiarosario.model.EstadoPedido
import panaderiarosario.ui.*

@Composable
fun MisPedidosScreen(appState: AppState, onVolver: () -> Unit) {
    appState.versionDatos
    val pedidos = appState.pedidosCliente()
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        item { BackButton(onVolver); ScreenTitle("Tus buenos momentos", "Consulta el avance de tus pedidos.") }
        if (pedidos.isEmpty()) item {
            EmptyState(if (appState.cargando) "Preparando tu historial" else "Tu historia empieza aquí",
                if (appState.cargando) "Estamos recuperando tus pedidos." else "Cuando confirmes un pedido, podrás seguirlo aquí.")
        }
        items(pedidos, key = { it.documentoId }) { pedido ->
            InfoCard {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Pedido #${pedido.id}", style = MaterialTheme.typography.titleMedium)
                    StatusChip(pedido.estado)
                }
                Row(Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    listOf("Recibido", "Preparando", "En camino", "Entregado").forEachIndexed { index, label ->
                        Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(Modifier.size(12.dp).background(if (index <= pedido.estado.ordinal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant, CircleShape))
                            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                pedido.detalles.forEach { detalle ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${detalle.cantidad} × ${detalle.producto.nombre}", Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                        Text("$${"%.2f".format(detalle.calcularSubtotal())}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total", fontWeight = FontWeight.SemiBold)
                    Text("$${"%.2f".format(pedido.calcularTotal())}", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                }
                Text("Entrega: ${pedido.cliente.direccion}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
