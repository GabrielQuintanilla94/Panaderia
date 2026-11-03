package panaderiarosario.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import panaderiarosario.AppState
import panaderiarosario.model.EstadoPedido
import panaderiarosario.ui.BackButton
import panaderiarosario.ui.ScreenTitle
import panaderiarosario.ui.EmptyState
import panaderiarosario.ui.StatusChip

@Composable
fun DeliveryScreen(appState: AppState, onVolver: () -> Unit) {
    appState.versionDatos
    // Filtramos para que el motorista solo vea los pedidos que están EN_CAMINO
    val pedidosEnDelivery = appState.pedidoService.listar().filter { it.estado == EstadoPedido.EN_CAMINO }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BackButton(onVolver)
        ScreenTitle("En buenas manos", "Pedidos que van en camino.")

        if (pedidosEnDelivery.isEmpty()) {
            EmptyState("Por ahora, todo entregado", "Los pedidos en camino aparecerán en este espacio.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(pedidosEnDelivery, key = { it.id }) { pedido ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Pedido #${pedido.id} - ${pedido.cliente.nombre}",
                                fontWeight = FontWeight.Bold
                            )
                            Text("Dirección: ${pedido.cliente.direccion}", fontWeight = FontWeight.SemiBold)
                            Text("Teléfono a contactar: ${pedido.cliente.telefono}")

                            Button(
                                onClick = { appState.avanzarEstado(pedido) },
                                modifier = Modifier.padding(top = 8.dp).fillMaxWidth()
                            ) {
                                Text("Confirmar entrega")
                            }
                        }
                    }
                }
            }
        }
    }
}