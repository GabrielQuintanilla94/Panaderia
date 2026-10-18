package panaderiarosario.ui.admin

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import panaderiarosario.AppState
import panaderiarosario.model.Categoria
import panaderiarosario.ui.BackButton
import panaderiarosario.ui.ErrorText
import panaderiarosario.ui.ScreenTitle
import panaderiarosario.util.Validador

@Composable
fun AdminProductosScreen(appState: AppState, onVolver: () -> Unit) {
    appState.versionDatos
    var nombre by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }

    var categoriaSeleccionada by remember { mutableStateOf(Categoria.PAN) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        BackButton(onVolver)
        ScreenTitle("Gestión de productos", "CRUD principal y Stock")

        OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre nuevo producto") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(precio, { precio = it }, label = { Text("Precio") }, modifier = Modifier.fillMaxWidth())

        Text("Seleccione la categoría:")
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Categoria.values().forEach { categoria ->
                if (categoria == categoriaSeleccionada) {
                    Button(onClick = { categoriaSeleccionada = categoria }) {
                        Text(categoria.name)
                    }
                } else {
                    OutlinedButton(onClick = { categoriaSeleccionada = categoria }) {
                        Text(categoria.name)
                    }
                }
            }
        }

        Button(
            onClick = {
                error = try {
                    val n = Validador.textoNoVacio(nombre, "Nombre")
                    val p = Validador.decimalPositivo(precio, "Precio")
                    appState.productoService.crear(n, categoriaSeleccionada, p)
                    appState.notificarCambio()
                    nombre = ""
                    precio = ""
                    null
                } catch (e: Exception) {
                    e.message
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Agregar producto (${categoriaSeleccionada.name}) - Stock inicial automático") }

        ErrorText(error)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(appState.productoService.listar(), key = { it.id }) { producto ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("#${producto.id} ${producto.nombre}", style = MaterialTheme.typography.titleMedium)
                            Text("${producto.categoria} | $${"%.2f".format(producto.precio)}")
                            Text(
                                if (producto.disponible) "Stock: ${producto.stock}" else "No disponible (Agotado)",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (producto.disponible) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            var stockInput by remember(producto.stock) { mutableStateOf(producto.stock.toString()) }
                            OutlinedTextField(
                                value = stockInput,
                                onValueChange = {
                                    stockInput = it
                                    it.toIntOrNull()?.let { nuevoStock ->
                                        appState.productoService.actualizarStock(producto.id, nuevoStock)
                                        appState.notificarCambio()
                                    }
                                },
                                label = { Text("Stock") },
                                modifier = Modifier.width(110.dp)
                            )

                            IconButton(onClick = {
                                appState.productoService.eliminar(producto.id)
                                appState.notificarCambio()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar producto",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
