package panaderiarosario.ui.cliente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import panaderiarosario.AppState
import panaderiarosario.ui.BackButton
import panaderiarosario.ui.ErrorText
import panaderiarosario.ui.ScreenTitle

@Composable
fun CarritoScreen(
    appState: AppState,
    onVolver: () -> Unit,
    onPedidoCreado: () -> Unit
) {
    appState.versionDatos
    var error by remember { mutableStateOf<String?>(null) }
    val itemsCarrito = appState.carrito.entries.mapNotNull { (id, cantidad) ->
        appState.productoService.buscarPorId(id)?.let { it to cantidad }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BackButton(onVolver)
        ScreenTitle("Carrito")

        if (itemsCarrito.isEmpty()) {
            Text("El carrito está vacío.")
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(itemsCarrito, key = { it.first.id }) { (producto, cantidad) ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(producto.nombre)
                            Text("Cantidad: $cantidad")
                            Text("Subtotal: $${"%.2f".format(producto.precio * cantidad)}")
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(onClick = { appState.cambiarCantidad(producto.id, -1) }) { Text("-") }
                                Button(onClick = { appState.cambiarCantidad(producto.id, 1) }) { Text("+") }
                            }
                        }
                    }
                }
            }

            Text("Total: $${"%.2f".format(appState.totalCarrito())}")
            Button(
                onClick = {
                    error = try {
                        appState.confirmarPedido()
                        null
                    } catch (e: Exception) {
                        e.message ?: "No se pudo crear el pedido."
                    }
                    if (error == null) onPedidoCreado()
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Confirmar pedido") }
        }

        ErrorText(error)
    }
}
