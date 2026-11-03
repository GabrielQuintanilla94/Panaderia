package panaderiarosario

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import panaderiarosario.data.DatosIniciales
import panaderiarosario.model.Cliente
import panaderiarosario.model.DetallePedido
import panaderiarosario.model.EstadoPedido
import panaderiarosario.model.Pedido
import panaderiarosario.model.Producto
import panaderiarosario.service.PedidoService
import panaderiarosario.service.ProductoService
import panaderiarosario.service.ReporteService

class AppState : androidx.lifecycle.ViewModel() {
    private val firebase = runCatching { com.google.firebase.FirebaseApp.getInstance(); panaderiarosario.data.FirebaseRepository() }.getOrNull()
    private var pedidosListener: com.google.firebase.firestore.ListenerRegistration? = null
    private var productosListener: com.google.firebase.firestore.ListenerRegistration? = null
    var errorNube by mutableStateOf<String?>(null)
        private set
    var cargando by mutableStateOf(false)
        private set
    private fun repo() = requireNotNull(firebase) { "Falta configurar Firebase. Consulte docs/FIREBASE.md." }

    suspend fun loginCorreo(email: String, clave: String, administrador: Boolean = false) {
        repo().login(email, clave)
        iniciarSesion(administrador)
    }

    suspend fun loginGoogle(token: String) {
        repo().google(token)
        iniciarSesion(false)
    }

    suspend fun registrarCorreo(email: String, clave: String, nombre: String, telefono: String, direccion: String) {
        repo().register(email, clave, Cliente(nombre.trim(), telefono.trim(), direccion.trim()))
        iniciarSesion(false)
    }

    private suspend fun iniciarSesion(administrador: Boolean) {
        try {
            val admin = repo().esAdmin()
            require(!administrador || admin) { "Esta cuenta no tiene permisos de administrador." }
            val perfil = repo().perfil()
            pedidosListener?.remove()
            productosListener?.remove()
            carrito.clear()
            pedidoService.reemplazar(emptyList())
            esAdministrador = administrador && admin
            clienteActual = if (esAdministrador) null else perfil
            errorNube = null
            cargando = true
            pedidosListener = repo().observarPedidos(esAdministrador) { pedidos, error ->
                cargando = false
                if (error != null) errorNube = "No se pudo cargar el historial: ${error.localizedMessage}"
                else { pedidoService.reemplazar(pedidos!!); versionDatos++ }
            }
            productosListener = repo().observarProductos { productos, error ->
                if (error != null) errorNube = "No se pudo cargar el catálogo: ${error.localizedMessage}"
                else {
                    catalogoRemoto = productos!!.map { it.copy() }
                    actualizarCatalogoLocal()
                    versionDatos++
                }
            }
        } catch (e: Exception) { cerrarSesion(); throw e }
    }

    suspend fun recuperarSesion(): Boolean {
        if (firebase?.auth?.currentUser == null) return false
        iniciarSesion(false)
        return true
    }

    override fun onCleared() { pedidosListener?.remove(); productosListener?.remove() }

    val productoService = ProductoService(DatosIniciales.productos())
    val pedidoService = PedidoService()
    val reporteService = ReporteService(productoService, pedidoService)

    var clienteActual by mutableStateOf<Cliente?>(null)
        private set

    var esAdministrador by mutableStateOf(false)
        private set

    var versionDatos by mutableStateOf(0)
        private set

    private var catalogoRemoto: List<Producto> = emptyList()
    val carrito = mutableMapOf<Int, Int>()

    fun cerrarSesion() {
        pedidosListener?.remove()
        productosListener?.remove()
        firebase?.auth?.signOut()
        pedidoService.reemplazar(emptyList())
        errorNube = null
        cargando = false
        carrito.forEach { (id, cant) ->
            productoService.buscarPorId(id)?.let { it.stock += cant }
        }
        clienteActual = null
        esAdministrador = false
        carrito.clear()
        versionDatos++
    }

    fun agregarAlCarrito(producto: Producto, cantidad: Int = 1) {
        if (!producto.disponible || cantidad <= 0) return
        val aAgregar = minOf(cantidad, producto.stock)
        if (aAgregar <= 0) return

        val actual = carrito[producto.id] ?: 0
        carrito[producto.id] = actual + aAgregar
        producto.stock -= aAgregar
        versionDatos++
    }

    fun cambiarCantidad(productoId: Int, delta: Int) {
        val producto = productoService.buscarPorId(productoId) ?: return
        val actualEnCarrito = carrito[productoId] ?: 0

        if (delta > 0) {
            val aAgregar = minOf(delta, producto.stock)
            if (aAgregar > 0) {
                carrito[productoId] = actualEnCarrito + aAgregar
                producto.stock -= aAgregar
            }
        } else {
            val aRemover = minOf(-delta, actualEnCarrito)
            if (aRemover > 0) {
                val restante = actualEnCarrito - aRemover
                if (restante <= 0) carrito.remove(productoId) else carrito[productoId] = restante
                producto.stock += aRemover
            }
        }
        versionDatos++
    }

    fun totalCarrito(): Double =
        carrito.entries.sumOf { (id, cantidad) ->
            (productoService.buscarPorId(id)?.precio ?: 0.0) * cantidad
        }

    suspend fun confirmarPedido(nombre: String, telefono: String, direccion: String): Pedido {
        requireNotNull(clienteActual) { "Debe iniciar sesión como cliente." }
        require(nombre.isNotBlank() && telefono.isNotBlank() && direccion.isNotBlank()) { "Complete nombre, teléfono y dirección de entrega." }
        val cliente = Cliente(nombre.trim(), telefono.trim(), direccion.trim())
        repo().guardarPerfil(cliente)
        clienteActual = cliente
        val detalles = carrito.mapNotNull { (id, cantidad) ->
            productoService.buscarPorId(id)?.let { DetallePedido(it, cantidad) }
        }.toMutableList()
        val pedido = repo().crearPedido(cliente, detalles)
        carrito.clear()
        actualizarCatalogoLocal()
        versionDatos++
        return pedido
    }

    fun pedidosCliente(): List<Pedido> {
        val uid = firebase?.auth?.currentUser?.uid ?: return emptyList()
        return pedidoService.listar().filter { it.usuarioId == uid }
    }

    fun avanzarEstado(pedido: Pedido) {
        val siguiente = when (pedido.estado) {
            EstadoPedido.RECIBIDO -> EstadoPedido.EN_PREPARACION
            EstadoPedido.EN_PREPARACION -> EstadoPedido.EN_CAMINO
            EstadoPedido.EN_CAMINO -> EstadoPedido.ENTREGADO
            EstadoPedido.ENTREGADO -> return
        }
        if (!esAdministrador) return
        viewModelScope.launch {
            try { repo().avanzar(pedido, siguiente) }
            catch (e: Exception) { errorNube = "No se pudo actualizar el pedido: ${e.localizedMessage}" }
        }
    }

    private fun actualizarCatalogoLocal() {
        productoService.reemplazar(catalogoRemoto.map { p ->
            p.copy(stock = maxOf(0, p.stock - (carrito[p.id] ?: 0)))
        })
    }

    fun publicarCatalogoInicial() {
        if (esAdministrador && productoService.listar().isEmpty()) {
            productoService.reemplazar(DatosIniciales.productos())
            notificarCambio()
        }
    }

    fun notificarCambio() {
        versionDatos++
        if (esAdministrador) viewModelScope.launch {
            try { repo().guardarCatalogo(productoService.listar()) }
            catch (e: Exception) { errorNube = "No se guardaron los cambios del catálogo: ${e.localizedMessage}" }
        }
    }
}
