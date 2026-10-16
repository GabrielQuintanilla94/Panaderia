package panaderiarosario.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import panaderiarosario.AppState
import panaderiarosario.ui.BackButton
import panaderiarosario.ui.ErrorText
import panaderiarosario.ui.ScreenTitle
import panaderiarosario.util.Validador

@Composable
fun RegistroScreen(
    appState: AppState,
    onVolver: () -> Unit,
    onRegistrado: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BackButton(onVolver)
        ScreenTitle("Registro de cliente")

        OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(telefono, { telefono = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(direccion, { direccion = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth())

        Button(
            onClick = {
                error = try {
                    val n = Validador.textoNoVacio(nombre, "Nombre")
                    val t = Validador.textoNoVacio(telefono, "Teléfono")
                    val d = Validador.textoNoVacio(direccion, "Dirección")
                    appState.registrarCliente(n, t, d)
                    null
                } catch (e: Exception) {
                    e.message ?: "No fue posible registrar el cliente."
                }
                if (error == null) onRegistrado()
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Crear cuenta") }

        ErrorText(error)
    }
}
