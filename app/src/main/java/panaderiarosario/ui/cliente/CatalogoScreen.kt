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
import androidx.compose.material3.FilterChip
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
import panaderiarosario.model.Categoria
import panaderiarosario.ui.ScreenTitle

@Composable
fun CatalogoScreen(
    appState: AppState,
    onCarrito: () -> Unit,
    onPedidos: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    appState.versionDatos
    var categoria by remember { mutableStateOf<Categoria?>(null) }

    val productos = if (categoria == null) {
        appState.productoService.disponibles()
    } else {
        appState.productoService.buscarPorCategoria(categoria!!).filter { it.disponible }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ScreenTitle(
            "Catálogo",
            "Hola, ${appState.clienteActual?.nombre ?: "cliente"}"
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onCarrito) { Text("Carrito (${appState.carrito.values.sum()})") }
            OutlinedButton(onClick = onPedidos) { Text("Mis pedidos") }
            OutlinedButton(onClick = onCerrarSesion) { Text("Salir") }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(selected = categoria == null, onClick = { categoria = null }, label = { Text("Todos") })
                    Categoria.entries.forEach {
                        FilterChip(
                            selected = categoria == it,
                            onClick = { categoria = it },
                            label = { Text(it.name.lowercase().replaceFirstChar(Char::uppercase)) }
                        )
                    }
                }
            }

            items(productos, key = { it.id }) { producto ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(producto.nombre)
                        Text(producto.categoria.name)
                        Text("$${"%.2f".format(producto.precio)}")
                        Button(onClick = { appState.agregarAlCarrito(producto) }) {
                            Text("Agregar al carrito")
                        }
                    }
                }
            }
        }
    }
}
