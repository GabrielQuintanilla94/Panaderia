package panaderiarosario.ui.auth

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
    val scope = rememberCoroutineScope()
    var ocupado by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BackButton(onVolver)
        ScreenTitle("Bienvenido a la mesa", "Crea tu cuenta y guarda tus pedidos favoritos.")

        OutlinedTextField(email, { email = it }, label = { Text("Correo electrónico") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(clave, { clave = it }, label = { Text("Contraseña (mínimo 6 caracteres)") }, singleLine = true, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(telefono, { telefono = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(direccion, { direccion = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth())

        Button(
            enabled = !ocupado,
            onClick = { scope.launch {
                ocupado = true
                error = try {
                    val n = Validador.textoNoVacio(nombre, "Nombre")
                    val t = Validador.textoNoVacio(telefono, "Teléfono")
                    val d = Validador.textoNoVacio(direccion, "Dirección")
                    require(clave.length >= 6) { "La contraseña debe tener al menos 6 caracteres." }
                    appState.registrarCorreo(email, clave, n, t, d)
                    null
                } catch (e: kotlinx.coroutines.CancellationException) { throw e
                } catch (e: Exception) {
                    e.message ?: "No fue posible registrar el cliente."
                }
                ocupado = false
                if (error == null) onRegistrado()
            } },
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (ocupado) "Creando cuenta…" else "Crear cuenta") }

        ErrorText(error)
    }
}
