package panaderiarosario.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import panaderiarosario.ui.*

@Composable
fun AdminHomeScreen(onProductos: () -> Unit, onPedidos: () -> Unit, onReportes: () -> Unit,
                    onDelivery: () -> Unit, onCerrarSesion: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("ROSARIO · ADMINISTRACIÓN", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        ScreenTitle("Todo en su punto", "Un espacio para cuidar cada detalle.")
        Surface(color = MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.large) {
            Row(Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Hecho con cariño.\nGestionado con cuidado.", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimary)
                }
                BreadMark(Modifier.size(76.dp))
            }
        }
        AdminTile("01", "Productos", "Catálogo, precios y existencias", onProductos)
        AdminTile("02", "Pedidos", "Cada pedido, de principio a fin", onPedidos)
        AdminTile("03", "Entregas", "Los buenos momentos van en camino", onDelivery)
        AdminTile("04", "Reportes", "Conoce cómo marcha tu panadería", onReportes)
        TextButton(onClick = onCerrarSesion, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, null); Spacer(Modifier.width(10.dp)); Text("Cerrar sesión")
        }
    }
}

@Composable
private fun AdminTile(number: String, title: String, subtitle: String, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.medium) {
                Text(number, Modifier.padding(14.dp), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        }
    }
}
