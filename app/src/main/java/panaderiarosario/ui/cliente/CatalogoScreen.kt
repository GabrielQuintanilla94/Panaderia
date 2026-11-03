package panaderiarosario.ui.cliente

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import panaderiarosario.AppState
import panaderiarosario.model.Categoria
import panaderiarosario.ui.BreadMark
import panaderiarosario.ui.EmptyState

@Composable
fun CatalogoScreen(appState: AppState, onCarrito: () -> Unit, onPedidos: () -> Unit, onCerrarSesion: () -> Unit) {
    appState.versionDatos
    var categoria by remember { mutableStateOf<Categoria?>(null) }
    var busqueda by remember { mutableStateOf("") }
    val cantidades = remember { mutableStateMapOf<Int, Int>() }
    val productos = appState.productoService.listar().filter {
        (categoria == null || it.categoria == categoria) && it.nombre.contains(busqueda.trim(), ignoreCase = true)
    }
    val totalItems = appState.carrito.values.sum()

    Scaffold(containerColor = MaterialTheme.colorScheme.background, bottomBar = {
        NavigationBar(containerColor = MaterialTheme.colorScheme.surface, tonalElevation = 0.dp) {
            NavigationBarItem(selected = true, onClick = {}, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Inicio") })
            NavigationBarItem(selected = false, onClick = onPedidos, icon = { Icon(Icons.AutoMirrored.Filled.List, null) }, label = { Text("Mis pedidos") })
            NavigationBarItem(selected = false, onClick = onCarrito, icon = {
                BadgedBox(badge = { if (totalItems > 0) Badge { Text(totalItems.toString()) } }) {
                    Icon(Icons.Default.ShoppingCart, null)
                }
            }, label = { Text("Carrito") })
        }
    }) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)) {
            item {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("ROSARIO", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, letterSpacing = 3.sp)
                        Text("Hola, ${appState.clienteActual?.nombre?.substringBefore(' ') ?: "cliente"}", style = MaterialTheme.typography.titleLarge)
                    }
                    IconButton(onClick = onCerrarSesion) { Icon(Icons.AutoMirrored.Filled.ExitToApp, "Cerrar sesión") }
                }
            }
            item {
                Surface(shape = RoundedCornerShape(26.dp), color = MaterialTheme.colorScheme.primary) {
                    Row(Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary))).padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("SABOR DE CASA", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f), letterSpacing = 1.5.sp)
                            Text("Recién hecho,\npara ti.", style = MaterialTheme.typography.headlineMedium, color = Color.White)
                            Text("Pequeños antojos.\nGrandes momentos.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f))
                        }
                        BreadMark(Modifier.size(108.dp))
                    }
                }
            }
            item {
                OutlinedTextField(busqueda, { busqueda = it }, placeholder = { Text("¿Qué se te antoja?") }, singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Search, null) }, shape = MaterialTheme.shapes.large,
                    modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = MaterialTheme.colorScheme.surface))
            }
            item {
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = categoria == null, onClick = { categoria = null }, label = { Text("Todo") })
                    Categoria.entries.forEach { cat ->
                        FilterChip(selected = categoria == cat, onClick = { categoria = cat }, label = { Text(etiqueta(cat)) })
                    }
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Elige tu favorito", style = MaterialTheme.typography.titleLarge)
                    Text("${productos.size} opciones", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if (productos.isEmpty()) item {
                EmptyState("Aquí comienza el antojo", if (busqueda.isBlank()) "Pronto encontrarás productos en esta categoría." else "Prueba con otro nombre o categoría.")
            }
            items(productos, key = { it.id }) { producto ->
                val cantidad = (cantidades[producto.id] ?: 1).coerceIn(1, maxOf(1, producto.stock))
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            Surface(shape = MaterialTheme.shapes.medium, color = if (producto.categoria == Categoria.PAN) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.secondaryContainer) {
                                BreadMark(Modifier.size(82.dp).padding(5.dp))
                            }
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(etiqueta(producto.categoria), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(producto.nombre, style = MaterialTheme.typography.titleMedium)
                                Text("$${"%.2f".format(producto.precio)}", fontWeight = FontWeight.Bold, fontSize = 23.sp, color = MaterialTheme.colorScheme.secondary)
                            }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        if (producto.disponible) {
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    FilledTonalIconButton(onClick = { cantidades[producto.id] = cantidad - 1 }, enabled = cantidad > 1) { Text("−", fontSize = 22.sp) }
                                    Text(cantidad.toString(), modifier = Modifier.padding(horizontal = 8.dp), fontWeight = FontWeight.Bold)
                                    FilledTonalIconButton(onClick = { cantidades[producto.id] = cantidad + 1 }, enabled = cantidad < producto.stock) { Text("+", fontSize = 22.sp) }
                                }
                                Button(onClick = { appState.agregarAlCarrito(producto, cantidad); cantidades[producto.id] = 1 }, modifier = Modifier.heightIn(min = 48.dp)) { Text("Agregar") }
                            }
                            Text("${producto.stock} disponibles", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            Text("Agotado por ahora", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}

private fun etiqueta(categoria: Categoria) = when (categoria) {
    Categoria.PAN -> "Pan artesanal"
    Categoria.REPOSTERIA -> "Repostería"
    Categoria.DESAYUNO -> "Desayunos"
    Categoria.CENA -> "Cenas"
}
