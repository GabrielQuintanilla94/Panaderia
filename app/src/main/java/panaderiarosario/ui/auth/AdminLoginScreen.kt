package panaderiarosario.ui.auth

import androidx.compose.runtime.Composable
import panaderiarosario.AppState

@Composable
fun AdminLoginScreen(appState: AppState, onVolver: () -> Unit, onAdmin: () -> Unit) {
    AccesoCorreo(appState, true, onAdmin, {}, onVolver)
}
