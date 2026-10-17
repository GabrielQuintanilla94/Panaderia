package panaderiarosario.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import panaderiarosario.AppState
import panaderiarosario.ui.ErrorText
import panaderiarosario.ui.ScreenTitle

@Composable
fun LoginScreen(
    appState: AppState,
    onRegistro: () -> Unit,
    onCliente: () -> Unit,
    onAdmin: () -> Unit
) {
    var telefono by remember { mutableStateOf("") }
    var usuario by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScreenTitle("Panadería Rosario", "Acceso al sistema")
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = { Text("Teléfono del cliente") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Button(
            onClick = {
                error = when {
                    telefono.isBlank() -> "Ingrese su teléfono."
                    appState.loginCliente(telefono) == null -> "Cliente no encontrado. Puede registrarse."
                    else -> null
                }
                if (error == null) onCliente()
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Ingresar como cliente") }

        OutlinedButton(onClick = onRegistro, modifier = Modifier.fillMaxWidth()) {
            Text("Registrar cliente")
        }

        Spacer(Modifier.height(24.dp))
        Text("Acceso administrativo")

        OutlinedTextField(
            value = usuario,
            onValueChange = { usuario = it },
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = clave,
            onValueChange = { clave = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )
        Button(
            onClick = {
                error = if (appState.loginAdministrador(usuario, clave)) null
                else "Credenciales de administrador incorrectas."
                if (error == null) onAdmin()
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Ingresar como administrador") }

        ErrorText(error)
    }
}