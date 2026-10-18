package panaderiarosario.ui.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import panaderiarosario.AppState
import panaderiarosario.model.EstadoPedido
import panaderiarosario.ui.BackButton
import panaderiarosario.ui.ScreenTitle

@Composable
fun ReporteScreen(appState: AppState, onVolver: () -> Unit) {
    appState.versionDatos
    val context = LocalContext.current
    val resumen = appState.reporteService.generarResumen()

    val pedidos = appState.pedidoService.listar()
    val totalPedidos = pedidos.size.coerceAtLeast(1)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BackButton(onVolver)
        ScreenTitle("Reporte del sistema")

        Text("Gráfico de Pedidos por Estado", fontWeight = FontWeight.Bold)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (pedidos.isEmpty()) {
                    Text("No hay pedidos para graficar.")
                } else {
                    EstadoPedido.values().forEach { estado ->
                        val cantidad = pedidos.count { it.estado == estado }
                        val proporcion = cantidad.toFloat() / totalPedidos.toFloat()

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = estado.name,
                                modifier = Modifier.weight(0.4f),
                                style = MaterialTheme.typography.bodySmall
                            )
                            Box(
                                modifier = Modifier
                                    .weight(0.5f)
                                    .height(16.dp)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(proporcion)
                                        .height(16.dp)
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                            }
                            Text(
                                text = "$cantidad",
                                modifier = Modifier.weight(0.1f).padding(start = 8.dp),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }

        Text("Resumen detallado", fontWeight = FontWeight.Bold)
        Card(modifier = Modifier.fillMaxWidth()) {
            Text(resumen, modifier = Modifier.padding(16.dp))
        }

        Button(
            onClick = {
                val nombre = appState.reporteService.exportarReportePdf(context)
                Toast.makeText(context, "PDF guardado en Descargas: $nombre", Toast.LENGTH_LONG).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Exportar reporte PDF") }
    }
}
