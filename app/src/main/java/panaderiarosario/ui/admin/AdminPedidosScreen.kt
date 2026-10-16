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
                            Text("Pedido #${pedido.id} - ${pedido.cliente.nombre}")
                            Text("Estado: ${pedido.estado}")
                            Text("Total: $${"%.2f".format(pedido.calcularTotal())}")
                            if (pedido.estado != EstadoPedido.ENTREGADO) {
                                Button(onClick = { appState.avanzarEstado(pedido) }) {
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
