package panaderiarosario.ui.admin

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
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
import panaderiarosario.model.Categoria
import panaderiarosario.ui.BackButton
import panaderiarosario.ui.ErrorText
import panaderiarosario.ui.ScreenTitle
import panaderiarosario.util.Validador

@Composable
fun AdminProductosScreen(appState: AppState, onVolver: () -> Unit) {
    appState.versionDatos
    var nombre by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }

    // NUEVO: Estado para manejar la categoría seleccionada
    var categoriaSeleccionada by remember { mutableStateOf(Categoria.PAN) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        BackButton(onVolver)
        ScreenTitle("Gestión de productos", "CRUD principal")

        OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre nuevo producto") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(precio, { precio = it }, label = { Text("Precio") }, modifier = Modifier.fillMaxWidth())

        // NUEVO: Selector visual de categorías
        Text("Seleccione la categoría:")
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Categoria.values().forEach { categoria ->
                if (categoria == categoriaSeleccionada) {
                    Button(onClick = { categoriaSeleccionada = categoria }) {
                        Text(categoria.name)
                    }
                } else {
                    OutlinedButton(onClick = { categoriaSeleccionada = categoria }) {
                        Text(categoria.name)
                    }
                }
            }
        }

        Button(
            onClick = {
                error = try {
                    val n = Validador.textoNoVacio(nombre, "Nombre")
                    val p = Validador.decimalPositivo(precio, "Precio")
                    // NUEVO: Usamos la categoría que el usuario seleccionó en lugar de fijar Categoria.PAN
                    appState.productoService.crear(n, categoriaSeleccionada, p)
                    appState.notificarCambio()
                    nombre = ""
                    precio = ""
                    null
                } catch (e: Exception) {
                    e.message
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Agregar producto (${categoriaSeleccionada.name})") }

        ErrorText(error)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(appState.productoService.listar(), key = { it.id }) { producto ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("#${producto.id} ${producto.nombre}")
                        Text("${producto.categoria} | $${"%.2f".format(producto.precio)}")
                        Text(if (producto.disponible) "Disponible" else "No disponible")
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = {
                                appState.productoService.cambiarDisponibilidad(producto.id)
                                appState.notificarCambio()
                            }) {
                                Text("Cambiar disponibilidad")
                            }
                            OutlinedButton(onClick = {
                                appState.productoService.eliminar(producto.id)
                                appState.notificarCambio()
                            }) {
                                Text("Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }
}