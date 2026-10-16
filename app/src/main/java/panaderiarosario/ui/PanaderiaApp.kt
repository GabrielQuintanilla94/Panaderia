package panaderiarosario.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import panaderiarosario.AppState
import panaderiarosario.ui.admin.AdminHomeScreen
import panaderiarosario.ui.admin.AdminPedidosScreen
import panaderiarosario.ui.admin.AdminProductosScreen
import panaderiarosario.ui.admin.ReporteScreen
import panaderiarosario.ui.auth.LoginScreen
import panaderiarosario.ui.auth.RegistroScreen
import panaderiarosario.ui.cliente.CarritoScreen
import panaderiarosario.ui.cliente.CatalogoScreen
import panaderiarosario.ui.cliente.MisPedidosScreen
import panaderiarosario.ui.theme.PanaderiaRosarioTheme

@Composable
fun PanaderiaApp(appState: AppState) {
    PanaderiaRosarioTheme {
        val nav = rememberNavController()

        NavHost(navController = nav, startDestination = "login") {
            composable("login") {
                LoginScreen(
                    appState = appState,
                    onRegistro = { nav.navigate("registro") },
                    onCliente = {
                        nav.navigate("catalogo") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onAdmin = {
                        nav.navigate("admin") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }

            composable("registro") {
                RegistroScreen(
                    appState = appState,
                    onVolver = { nav.popBackStack() },
                    onRegistrado = {
                        nav.navigate("catalogo") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }

            composable("catalogo") {
                CatalogoScreen(
                    appState = appState,
                    onCarrito = { nav.navigate("carrito") },
                    onPedidos = { nav.navigate("mis_pedidos") },
                    onCerrarSesion = {
                        appState.cerrarSesion()
                        nav.navigate("login") { popUpTo(0) }
                    }
                )
            }

            composable("carrito") {
                CarritoScreen(
                    appState = appState,
                    onVolver = { nav.popBackStack() },
                    onPedidoCreado = { nav.navigate("mis_pedidos") }
                )
            }

            composable("mis_pedidos") {
                MisPedidosScreen(
                    appState = appState,
                    onVolver = { nav.popBackStack() }
                )
            }

            composable("admin") {
                AdminHomeScreen(
                    onProductos = { nav.navigate("admin_productos") },
                    onPedidos = { nav.navigate("admin_pedidos") },
                    onReportes = { nav.navigate("reporte") },
                    onCerrarSesion = {
                        appState.cerrarSesion()
                        nav.navigate("login") { popUpTo(0) }
                    }
                )
            }

            composable("admin_productos") {
                AdminProductosScreen(appState = appState, onVolver = { nav.popBackStack() })
            }

            composable("admin_pedidos") {
                AdminPedidosScreen(appState = appState, onVolver = { nav.popBackStack() })
            }

            composable("reporte") {
                ReporteScreen(appState = appState, onVolver = { nav.popBackStack() })
            }
        }
    }
}
