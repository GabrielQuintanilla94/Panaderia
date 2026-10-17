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

@Composable
fun AdminPedidosScreen(appState: AppState, onVolver: () -> Unit) {
    appState.versionDatos
    val pedidos = appState.pedidoService.listar()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BackButton(onVolver)
        ScreenTitle("Gestión de pedidos")

        if (pedidos.isEmpty()) {
            Text("No hay pedidos registrados.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(pedidos, key = { it.id }) { pedido ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Pedido #${pedido.id} - ${pedido.cliente.nombre}",
                                fontWeight = FontWeight.Bold
                            )
                            Text("Dirección: ${pedido.cliente.direccion}")
                            Text("Teléfono: ${pedido.cliente.telefono}")

                            Text("Productos:", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 4.dp))
                            pedido.detalles.forEach { detalle ->
                                Text("• ${detalle.cantidad}x ${detalle.producto.nombre} ($${"%.2f".format(detalle.calcularSubtotal())})")
                            }

                            Text("Estado: ${pedido.estado}", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
                            Text("Total Final: $${"%.2f".format(pedido.calcularTotal())}")

                            if (pedido.estado != EstadoPedido.ENTREGADO) {
                                Button(
                                    onClick = { appState.avanzarEstado(pedido) },
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Text("Avanzar al siguiente estado")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}