package panaderiarosario.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import panaderiarosario.ui.ScreenTitle

@Composable
fun AdminHomeScreen(
    onProductos: () -> Unit,
    onPedidos: () -> Unit,
    onReportes: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ScreenTitle("Panel administrativo", "Panadería Rosario")
        Button(onClick = onProductos, modifier = Modifier.fillMaxWidth()) { Text("Gestión de productos") }
        Button(onClick = onPedidos, modifier = Modifier.fillMaxWidth()) { Text("Gestión de pedidos") }
        Button(onClick = onReportes, modifier = Modifier.fillMaxWidth()) { Text("Reportes") }
        OutlinedButton(onClick = onCerrarSesion, modifier = Modifier.fillMaxWidth()) { Text("Cerrar sesión") }
    }
}
