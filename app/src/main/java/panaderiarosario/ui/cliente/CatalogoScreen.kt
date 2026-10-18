package panaderiarosario.ui.cliente

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
    val cantidades = remember { mutableStateMapOf<Int, Int>() }

    val productos = if (categoria == null) {
        appState.productoService.listar() // Listamos todos para que vean stock y agotados si aplica
    } else {
        appState.productoService.buscarPorCategoria(categoria!!)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Cabecera con Título a la izquierda y Carrito / Salir en la esquina superior derecha
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ScreenTitle(
                "Catálogo",
                "Hola, ${appState.clienteActual?.nombre ?: "cliente"}"
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onCarrito) {
                    BadgedBox(
                        badge = {
                            val totalItems = appState.carrito.values.sum()
                            if (totalItems > 0) {
                                Badge { Text("$totalItems") }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Carrito"
                        )
                    }
                }
                IconButton(onClick = onCerrarSesion) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Salir"
                    )
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onPedidos) { Text("Mis pedidos") }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = categoria == null,
                        onClick = { categoria = null },
                        label = { Text("Todos") }
                    )
                    Categoria.entries.forEach { cat ->
                        FilterChip(
                            selected = categoria == cat,
                            onClick = { categoria = cat },
                            label = { Text(cat.name.lowercase().replaceFirstChar(Char::uppercase)) }
                        )
                    }
                }
            }

            items(productos, key = { it.id }) { producto ->
                val cantidadActual = cantidades[producto.id] ?: 1

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(producto.nombre, style = MaterialTheme.typography.titleMedium)
                        Text(producto.categoria.name, style = MaterialTheme.typography.bodySmall)
                        Text("$${"%.2f".format(producto.precio)}", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "Stock disponible: ${producto.stock}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (producto.disponible) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )

                        if (!producto.disponible) {
                            Text(
                                "🚫 Agotado",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Selector de cantidad (- y +) limitado por el stock disponible
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            if (cantidadActual > 1) {
                                                cantidades[producto.id] = cantidadActual - 1
                                            }
                                        },
                                        modifier = Modifier.size(36.dp),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text("-")
                                    }
                                    Text("$cantidadActual", style = MaterialTheme.typography.bodyLarge)
                                    OutlinedButton(
                                        onClick = {
                                            if (cantidadActual < producto.stock) {
                                                cantidades[producto.id] = cantidadActual + 1
                                            }
                                        },
                                        modifier = Modifier.size(36.dp),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text("+")
                                    }
                                }

                                // Botón de agregar con la cantidad seleccionada
                                Button(onClick = {
                                    appState.agregarAlCarrito(producto, cantidadActual)
                                    cantidades[producto.id] = 1 // Reiniciar a 1 tras agregar
                                }) {
                                    Text("Agregar al carrito")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
