package panaderiarosario.ui.cliente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import panaderiarosario.AppState
import panaderiarosario.ui.BackButton
import panaderiarosario.ui.ScreenTitle

@Composable
fun MisPedidosScreen(appState: AppState, onVolver: () -> Unit) {
    appState.versionDatos
    val pedidos = appState.pedidosCliente()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BackButton(onVolver)
        ScreenTitle("Mis pedidos", "Seguimiento del estado")

        if (pedidos.isEmpty()) {
            Text("Todavía no hay pedidos registrados.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(pedidos, key = { it.id }) { pedido ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Pedido #${pedido.id}")
                            Text("Estado: ${pedido.estado.name.replace('_', ' ')}")
                            pedido.detalles.forEach {
                                Text("• ${it.producto.nombre} x${it.cantidad}")
                            }
                            Text("Total: $${"%.2f".format(pedido.calcularTotal())}")
                        }
                    }
                }
            }
        }
    }
}
