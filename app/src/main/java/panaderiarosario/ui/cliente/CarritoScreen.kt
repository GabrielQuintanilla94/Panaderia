package panaderiarosario.ui.cliente

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import panaderiarosario.AppState
import panaderiarosario.ui.*

@Composable
fun CarritoScreen(appState: AppState, onVolver: () -> Unit, onPedidoCreado: () -> Unit) {
    appState.versionDatos
    var nombre by remember { mutableStateOf(appState.clienteActual?.nombre ?: "") }
    var telefono by remember { mutableStateOf(appState.clienteActual?.telefono ?: "") }
    var direccion by remember { mutableStateOf(appState.clienteActual?.direccion ?: "") }
    var guardando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val productos = appState.carrito.entries.mapNotNull { (id, cantidad) -> appState.productoService.buscarPorId(id)?.let { it to cantidad } }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { BackButton(onVolver); ScreenTitle("Tu carrito", "Un buen momento está por llegar.") }
        if (productos.isEmpty()) item { EmptyState("Todavía hay espacio para un antojo", "Explora el catálogo y agrega tus favoritos.") }
        items(productos, key = { it.first.id }) { (producto, cantidad) ->
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        BreadMark(Modifier.size(64.dp))
                        Column(Modifier.weight(1f)) {
                            Text(producto.nombre, style = MaterialTheme.typography.titleMedium)
                            Text("$${"%.2f".format(producto.precio)} por unidad", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FilledTonalIconButton(enabled = !guardando, onClick = { appState.cambiarCantidad(producto.id, -1) }) { Text("−") }
                            Text("$cantidad", Modifier.padding(horizontal = 12.dp), fontWeight = FontWeight.Bold)
                            FilledTonalIconButton(enabled = !guardando && producto.stock > 0, onClick = { appState.cambiarCantidad(producto.id, 1) }) { Text("+") }
                        }
                        Text("$${"%.2f".format(producto.precio * cantidad)}", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
        }
        if (productos.isNotEmpty()) {
            item {
                InfoCard {
                    Text("¿Dónde lo disfrutamos?", style = MaterialTheme.typography.titleLarge)
                    Text("Datos para tu entrega", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre de quien recibe") }, modifier = Modifier.fillMaxWidth(), enabled = !guardando, singleLine = true)
                    OutlinedTextField(telefono, { telefono = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth(), enabled = !guardando, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), singleLine = true)
                    OutlinedTextField(direccion, { direccion = it }, label = { Text("Dirección de entrega") }, modifier = Modifier.fillMaxWidth(), enabled = !guardando, minLines = 2)
                }
            }
            item {
                Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.large) {
                    Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Total de tu pedido", style = MaterialTheme.typography.titleMedium)
                            Text("$${"%.2f".format(appState.totalCarrito())}", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                        }
                        Button(enabled = !guardando, modifier = Modifier.fillMaxWidth().heightIn(min = 54.dp), onClick = {
                            scope.launch {
                                guardando = true; error = null
                                try { appState.confirmarPedido(nombre, telefono, direccion); onPedidoCreado() }
                                catch (e: CancellationException) { throw e }
                                catch (e: Exception) { error = e.localizedMessage ?: "No se pudo guardar el pedido." }
                                finally { guardando = false }
                            }
                        }) { Text(if (guardando) "Guardando tu pedido…" else "Confirmar pedido") }
                        ErrorText(error)
                    }
                }
            }
        }
    }
}
