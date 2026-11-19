package panaderiarosario.ui.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import panaderiarosario.AppState
import panaderiarosario.model.EstadoPedido
import panaderiarosario.service.Periodo
import panaderiarosario.service.ReporteService
import panaderiarosario.ui.BackButton
import panaderiarosario.ui.ScreenTitle

@Composable
fun ReporteScreen(appState: AppState, onVolver: () -> Unit) {
    appState.versionDatos
    val context = LocalContext.current
    var periodo by remember { mutableStateOf(Periodo.TODO) }
    val desde = periodo.desde()
    val servicio = appState.reporteService
    val pedidos = servicio.pedidosDesde(desde)
    val porCategoria = servicio.ventasPorCategoria(desde)
    val topProductos = servicio.productosMasVendidos(desde)
    val resumen = servicio.generarResumen()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BackButton(onVolver)
        ScreenTitle("Así va tu panadería", "Cada pedido cuenta una historia.")

        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Periodo.entries.forEach { p ->
                FilterChip(selected = periodo == p, onClick = { periodo = p }, label = { Text(p.etiqueta) })
            }
        }
        Text(
            "${pedidos.size} pedidos · $${"%.2f".format(pedidos.sumOf { it.calcularTotal() })} en ventas registradas",
            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Grafico(
            titulo = "Ventas por categoría",
            vacio = "No hay ventas en este período.",
            datos = porCategoria.map { (c, total) -> ReporteService.nombreCategoria(c) to total },
            formato = { "$${"%.2f".format(it)}" },
            mostrar = porCategoria.values.any { it > 0 }
        )

        Grafico(
            titulo = "Productos más vendidos",
            vacio = "Todavía no hay productos vendidos en este período.",
            datos = topProductos.map { (nombre, unidades) -> nombre to unidades.toDouble() },
            formato = { "${it.toInt()} u." },
            mostrar = topProductos.isNotEmpty()
        )

        Grafico(
            titulo = "El pulso de tus pedidos",
            vacio = "No hay pedidos en este período.",
            datos = EstadoPedido.entries.map { estado ->
                estado.name.lowercase().replace('_', ' ').replaceFirstChar(Char::uppercase) to
                    pedidos.count { it.estado == estado }.toDouble()
            },
            formato = { it.toInt().toString() },
            mostrar = pedidos.isNotEmpty()
        )

        Text("Tu operación, en un vistazo", fontWeight = FontWeight.Bold)
        Card(modifier = Modifier.fillMaxWidth()) {
            Text(resumen, modifier = Modifier.padding(16.dp))
        }

        Button(
            onClick = {
                val nombre = servicio.exportarReportePdf(context)
                Toast.makeText(context, "PDF guardado en Descargas: $nombre", Toast.LENGTH_LONG).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Exportar reporte PDF") }
    }
}

/** Gráfico de barras horizontales: cada barra es proporcional al valor máximo. */
@Composable
private fun Grafico(
    titulo: String,
    vacio: String,
    datos: List<Pair<String, Double>>,
    formato: (Double) -> String,
    mostrar: Boolean
) {
    Text(titulo, fontWeight = FontWeight.Bold)
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (!mostrar) {
                Text(vacio, style = MaterialTheme.typography.bodyMedium)
                return@Column
            }
            val maximo = datos.maxOf { it.second }.coerceAtLeast(1e-9)
            datos.forEach { (etiqueta, valor) ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        etiqueta, modifier = Modifier.weight(0.34f), style = MaterialTheme.typography.bodySmall,
                        maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                    Box(
                        Modifier.weight(0.46f).height(18.dp).clip(RoundedCornerShape(9.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                    ) {
                        if (valor > 0) Box(
                            Modifier.fillMaxWidth((valor / maximo).toFloat().coerceIn(0.02f, 1f)).height(18.dp)
                                .clip(RoundedCornerShape(9.dp)).background(MaterialTheme.colorScheme.primary)
                        )
                    }
                    Text(
                        formato(valor), modifier = Modifier.weight(0.2f).padding(start = 8.dp),
                        style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
