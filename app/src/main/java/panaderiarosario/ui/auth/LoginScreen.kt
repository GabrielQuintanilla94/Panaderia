package panaderiarosario.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import panaderiarosario.AppState
import panaderiarosario.ui.ErrorText
import panaderiarosario.ui.ScreenTitle

@Composable
fun LoginScreen(
    appState: AppState,
    onRegistro: () -> Unit,
    onCliente: () -> Unit,
    onAdminLogin: () -> Unit
) {
    var telefono by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo de pan grande
        Text(
            text = "🍞",
            fontSize = 64.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        ScreenTitle("Panadería Rosario", "Acceso de Clientes")
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = { Text("Teléfono del cliente") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))
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

        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = onRegistro, modifier = Modifier.fillMaxWidth()) {
            Text("Registrar nuevo cliente")
        }

        Spacer(Modifier.height(48.dp))

        // Botón de acceso administrativo con icono de llave/herramienta (Build)
        OutlinedButton(
            onClick = onAdminLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = "Acceso Administrativo",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Acceso Administrativo")
        }

        ErrorText(error)
    }
}
