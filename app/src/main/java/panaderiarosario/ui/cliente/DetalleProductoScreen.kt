package panaderiarosario.ui.cliente

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import panaderiarosario.AppState
import panaderiarosario.model.Categoria
import panaderiarosario.ui.BackButton
import panaderiarosario.ui.BreadMark
import panaderiarosario.ui.EmptyState

@Composable
fun DetalleProductoScreen(appState: AppState, productoId: Int, onVolver: () -> Unit, onAgregado: () -> Unit) {
    appState.versionDatos
    val producto = appState.productoService.buscarPorId(productoId)
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BackButton(onVolver)
        if (producto == null) {
            EmptyState("Producto no disponible", "Este producto ya no forma parte del catálogo.")
            return@Column
        }
        var cantidad by remember(producto.id) { mutableIntStateOf(1) }
        cantidad = cantidad.coerceIn(1, maxOf(1, producto.stock))

        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = if (producto.categoria == Categoria.PAN) MaterialTheme.colorScheme.tertiaryContainer
            else MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(Modifier.fillMaxWidth().padding(28.dp), contentAlignment = Alignment.Center) {
                BreadMark(Modifier.size(180.dp))
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(etiqueta(producto.categoria).uppercase(), style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary, letterSpacing = 1.5.sp)
            Text(producto.nombre, style = MaterialTheme.typography.headlineMedium)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("$${"%.2f".format(producto.precio)}", fontWeight = FontWeight.Bold, fontSize = 28.sp,
                    color = MaterialTheme.colorScheme.secondary)
                Text("por unidad", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            AssistChip(onClick = {}, label = {
                Text(if (producto.disponible) "${producto.stock} disponibles" else "Agotado por ahora")
            }, colors = AssistChipDefaults.assistChipColors(
                labelColor = if (producto.disponible) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error))
        }

        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Descripción", style = MaterialTheme.typography.titleMedium)
                Text(producto.descripcion.ifBlank { "Preparado con cariño por Panadería Rosario." },
                    style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        if (producto.disponible) {
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Cantidad", style = MaterialTheme.typography.titleMedium)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FilledTonalIconButton(onClick = { cantidad-- }, enabled = cantidad > 1) { Text("−", fontSize = 22.sp) }
                            Text(cantidad.toString(), Modifier.padding(horizontal = 12.dp), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            FilledTonalIconButton(onClick = { cantidad++ }, enabled = cantidad < producto.stock) { Text("+", fontSize = 22.sp) }
                        }
                    }
                    Button(onClick = { appState.agregarAlCarrito(producto, cantidad); onAgregado() },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 54.dp)) {
                        Text("Agregar al carrito · $${"%.2f".format(producto.precio * cantidad)}")
                    }
                }
            }
        }
    }
}
