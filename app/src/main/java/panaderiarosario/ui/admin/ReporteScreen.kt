package panaderiarosario.ui.admin

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import panaderiarosario.AppState
import panaderiarosario.ui.BackButton
import panaderiarosario.ui.ScreenTitle

@Composable
fun ReporteScreen(appState: AppState, onVolver: () -> Unit) {
    appState.versionDatos
    val context = LocalContext.current
    val resumen = appState.reporteService.generarResumen()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BackButton(onVolver)
        ScreenTitle("Reporte del sistema")
        Card(modifier = Modifier.fillMaxWidth()) {
            Text(resumen, modifier = Modifier.padding(16.dp))
        }
        Button(
            onClick = {
                val nombre = appState.reporteService.exportarReporte(context)
                Toast.makeText(context, "Reporte guardado: $nombre", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Exportar reporte") }
    }
}
