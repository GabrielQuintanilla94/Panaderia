package panaderiarosario.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import panaderiarosario.AppState
import panaderiarosario.ui.*

@Composable
fun LoginScreen(appState: AppState, onRegistro: () -> Unit, onCliente: () -> Unit, onAdminLogin: () -> Unit) {
    AccesoCorreo(appState, false, onCliente, onRegistro, onAdminLogin)
}

@Composable
fun AccesoCorreo(appState: AppState, admin: Boolean, onIngresado: () -> Unit,
                 onRegistro: () -> Unit, onVolver: () -> Unit) {
    var email by rememberSaveableString()
    var clave by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var ocupado by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        if (!admin) {
            ocupado = true
            try { if (appState.recuperarSesion()) onIngresado() }
            catch (e: CancellationException) { throw e }
            catch (e: Exception) { error = e.localizedMessage }
            finally { ocupado = false }
        }
    }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (admin) BackButton(onVolver)
        if (panaderiarosario.BuildConfig.FIREBASE_EMULATORS) Text("Modo local: Firebase Emulator Suite")
        Surface(color = MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.extraLarge) {
            Row(Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("PANADERÍA ROSARIO", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f))
                    Text("El sabor de\nsentirte en casa.", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onPrimary)
                }
                BreadMark(Modifier.size(88.dp))
            }
        }
        Spacer(Modifier.height(4.dp))
        ScreenTitle(if (admin) "Tu espacio de trabajo" else "Qué gusto verte",
            if (admin) "Ingresa con tu cuenta administrativa." else "Tu próximo antojo comienza aquí.")
        OutlinedTextField(email, { email = it }, label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(), singleLine = true, enabled = !ocupado, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
        OutlinedTextField(clave, { clave = it }, label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(), singleLine = true, enabled = !ocupado, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation())
        Button(enabled = !ocupado, modifier = Modifier.fillMaxWidth().heightIn(min = 54.dp), onClick = {
            scope.launch {
                ocupado = true; error = null
                try {
                    require(email.isNotBlank() && clave.isNotBlank()) { "Ingrese correo y contraseña." }
                    appState.loginCorreo(email, clave, admin)
                    onIngresado()
                } catch (e: CancellationException) { throw e }
                catch (e: Exception) { error = e.localizedMessage ?: "No se pudo iniciar sesión." }
                finally { ocupado = false }
            }
        }) { Text(if (ocupado) "Conectando…" else "Ingresar") }
        if (!admin) {
            OutlinedButton(enabled = !ocupado, modifier = Modifier.fillMaxWidth().heightIn(min = 54.dp), onClick = {
                scope.launch {
                    ocupado = true; error = null
                    try {
                        require(!panaderiarosario.BuildConfig.FIREBASE_EMULATORS) { "Google requiere un proyecto Firebase con OAuth configurado. Use correo en el modo local." }
                        val clientId = context.getString(panaderiarosario.R.string.default_web_client_id)
                        require(clientId.isNotBlank()) { "Configure Google en Firebase y agregue google-services.json." }
                        val option = GetSignInWithGoogleOption.Builder(clientId).build()
                        val result = CredentialManager.create(context).getCredential(context,
                            GetCredentialRequest.Builder().addCredentialOption(option).build())
                        val credential = result.credential
                        if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                            val token = GoogleIdTokenCredential.createFrom(credential.data).idToken
                            appState.loginGoogle(token)
                            onIngresado()
                        } else {
                            throw IllegalStateException("Google devolvió una credencial no compatible.")
                        }
                    } catch (_: GetCredentialCancellationException) { /* User closed the account picker. */ }
                    catch (e: CancellationException) { throw e }
                    catch (e: Exception) { error = e.localizedMessage ?: "No se pudo ingresar con Google." }
                    finally { ocupado = false }
                }
            }) { Text("G", fontWeight = FontWeight.Bold); Spacer(Modifier.width(12.dp)); Text("Continuar con Google") }
            OutlinedButton(enabled = !ocupado, onClick = onRegistro, modifier = Modifier.fillMaxWidth()) { Text("Crear cuenta") }
            TextButton(enabled = !ocupado, onClick = onVolver) { Text("Acceso administrativo") }
        }
        ErrorText(error)
    }
}

@Composable
private fun rememberSaveableString() = androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf("") }
