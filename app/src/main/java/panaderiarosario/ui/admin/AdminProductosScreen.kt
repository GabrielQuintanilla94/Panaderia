package panaderiarosario.ui.admin

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import panaderiarosario.AppState
import panaderiarosario.model.Categoria
import panaderiarosario.model.Producto
import panaderiarosario.ui.*
import panaderiarosario.util.Validador

@Composable
fun AdminProductosScreen(appState: AppState, onVolver: () -> Unit) {
    appState.versionDatos
    var nombre by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var imagenUrl by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf(Categoria.PAN) }
    var error by remember { mutableStateOf<String?>(null) }
    var editando by remember { mutableStateOf<Producto?>(null) }
    var eliminando by remember { mutableStateOf<Producto?>(null) }
    var editNombre by remember { mutableStateOf("") }
    var editPrecio by remember { mutableStateOf("") }
    var editDescripcion by remember { mutableStateOf("") }
    var editImagenUrl by remember { mutableStateOf("") }
    var editCategoria by remember { mutableStateOf(Categoria.PAN) }
    var editError by remember { mutableStateOf<String?>(null) }

    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { BackButton(onVolver); ScreenTitle("Nuestro catálogo", "Cuida los sabores que nos hacen únicos.") }
        if (appState.productoService.listar().isEmpty()) item {
            OutlinedButton(onClick = { appState.publicarCatalogoInicial() }, modifier = Modifier.fillMaxWidth()) { Text("Publicar catálogo inicial") }
        }
        item {
            InfoCard {
                Text("Un nuevo favorito", style = MaterialTheme.typography.titleLarge)
                OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre del producto") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(precio, { precio = it }, label = { Text("Precio por unidad ($)") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(descripcion, { descripcion = it.take(MAX_DESCRIPCION) }, label = { Text("Descripción") },
                    minLines = 2, maxLines = 4, modifier = Modifier.fillMaxWidth(),
                    supportingText = { Text("${descripcion.length}/$MAX_DESCRIPCION") })
                OutlinedTextField(imagenUrl, { imagenUrl = it.trim() }, label = { Text("URL de la imagen (opcional)") },
                    placeholder = { Text("https://...") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri))
                Categorias(categoria) { categoria = it }
                Button(onClick = {
                    error = try {
                        val n = Validador.textoNoVacio(nombre, "Nombre")
                        val p = Validador.decimalPositivo(precio, "Precio")
                        appState.productoService.crear(n, categoria, p, descripcion, validarUrl(imagenUrl))
                        appState.notificarCambio(); nombre = ""; precio = ""; descripcion = ""; imagenUrl = ""; null
                    } catch (e: Exception) { e.localizedMessage }
                }, modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp)) { Text("Agregar al catálogo") }
                ErrorText(error)
            }
        }
        item { Text("Productos · ${appState.productoService.listar().size}", style = MaterialTheme.typography.titleLarge) }
        items(appState.productoService.listar(), key = { it.id }) { producto ->
            var stock by remember(producto.stock) { mutableStateOf(producto.stock.toString()) }
            InfoCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.padding(end = 12.dp)) {
                        ProductoImagen(producto, Modifier.size(56.dp), tamanoIlustracion = 48.dp)
                    }
                    Column(Modifier.weight(1f)) {
                        Text(producto.nombre, style = MaterialTheme.typography.titleMedium)
                        Text("${producto.categoria.name.lowercase().replaceFirstChar(Char::uppercase)} · $${"%.2f".format(producto.precio)}",
                            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                    }
                    IconButton(onClick = { editando = producto; editNombre = producto.nombre; editPrecio = producto.precio.toString(); editDescripcion = producto.descripcion; editImagenUrl = producto.imagenUrl; editCategoria = producto.categoria; editError = null }) {
                        Icon(Icons.Default.Edit, "Editar ${producto.nombre}", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = { eliminando = producto }) {
                        Icon(Icons.Default.Delete, "Eliminar ${producto.nombre}", tint = MaterialTheme.colorScheme.error)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(stock, { stock = it }, label = { Text("Existencias") }, singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                    TextButton(enabled = stock.toIntOrNull()?.let { it >= 0 && it != producto.stock } == true, onClick = {
                        stock.toIntOrNull()?.let { appState.productoService.actualizarStock(producto.id, it); appState.notificarCambio() }
                    }) { Text("Guardar") }
                }
            }
        }
    }
    editando?.let { producto ->
        AlertDialog(onDismissRequest = { editando = null }, title = { Text("Editar producto") }, text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(editNombre, { editNombre = it }, label = { Text("Nombre") }, singleLine = true)
                OutlinedTextField(editPrecio, { editPrecio = it }, label = { Text("Precio") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                OutlinedTextField(editDescripcion, { editDescripcion = it.take(MAX_DESCRIPCION) }, label = { Text("Descripción") }, minLines = 2, maxLines = 4)
                OutlinedTextField(editImagenUrl, { editImagenUrl = it.trim() }, label = { Text("URL de la imagen") }, singleLine = true,
                    placeholder = { Text("https://...") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri))
                Categorias(editCategoria) { editCategoria = it }
                ErrorText(editError)
            }
        }, confirmButton = {
            TextButton(onClick = {
                try {
                    appState.productoService.actualizar(producto.id, Validador.textoNoVacio(editNombre, "Nombre"), editCategoria, Validador.decimalPositivo(editPrecio, "Precio"), editDescripcion, validarUrl(editImagenUrl))
                    appState.notificarCambio(); editando = null
                } catch (e: Exception) { editError = e.localizedMessage }
            }) { Text("Guardar cambios") }
        }, dismissButton = { TextButton(onClick = { editando = null }) { Text("Cancelar") } })
    }
    eliminando?.let { producto ->
        AlertDialog(onDismissRequest = { eliminando = null }, title = { Text("¿Eliminar producto?") },
            text = { Text("${producto.nombre} dejará de aparecer en el catálogo. Los pedidos anteriores conservarán sus detalles.") },
            confirmButton = { TextButton(onClick = { appState.productoService.eliminar(producto.id); appState.notificarCambio(); eliminando = null }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) } },
            dismissButton = { TextButton(onClick = { eliminando = null }) { Text("Conservar") } })
    }
}

private const val MAX_DESCRIPCION = 200

/** Acepta vacío o una dirección web http(s). */
private fun validarUrl(url: String): String {
    val limpia = url.trim()
    require(limpia.isEmpty() || limpia.startsWith("https://") || limpia.startsWith("http://")) {
        "La URL de la imagen debe empezar con https://"
    }
    return limpia
}

@Composable
private fun Categorias(selected: Categoria, onSelected: (Categoria) -> Unit) {
    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Categoria.entries.forEach { categoria ->
            FilterChip(selected = selected == categoria, onClick = { onSelected(categoria) }, label = { Text(categoria.name.lowercase().replaceFirstChar(Char::uppercase)) })
        }
    }
}
