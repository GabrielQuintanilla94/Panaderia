package panaderiarosario

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import panaderiarosario.data.DatosIniciales
import panaderiarosario.model.Cliente
import panaderiarosario.model.DetallePedido
import panaderiarosario.model.EstadoPedido
import panaderiarosario.model.Pedido
import panaderiarosario.model.Producto
import panaderiarosario.service.ClienteService
import panaderiarosario.service.PedidoService
import panaderiarosario.service.ProductoService
import panaderiarosario.service.ReporteService

class AppState {
    val productoService = ProductoService(DatosIniciales.productos())
    val pedidoService = PedidoService()
    val clienteService = ClienteService()
    val reporteService = ReporteService(productoService, pedidoService)

    var clienteActual by mutableStateOf<Cliente?>(null)
        private set

    var esAdministrador by mutableStateOf(false)
        private set

    var versionDatos by mutableStateOf(0)
        private set

    val carrito = mutableMapOf<Int, Int>()

    fun loginAdministrador(usuario: String, clave: String): Boolean {
        val valido = usuario.trim() == "admin" && clave == "admin123"
        esAdministrador = valido
        if (valido) clienteActual = null
        return valido
    }

    fun loginCliente(telefono: String): Cliente? {
        val cliente = clienteService.buscarPorTelefono(telefono.trim())
        if (cliente != null) {
            clienteActual = cliente
            esAdministrador = false
        }
        return cliente
    }

    fun registrarCliente(nombre: String, telefono: String, direccion: String): Cliente {
        val cliente = clienteService.registrarCliente(nombre.trim(), telefono.trim(), direccion.trim())
        clienteActual = cliente
        esAdministrador = false
        versionDatos++
        return cliente
    }

    fun cerrarSesion() {
        clienteActual = null
        esAdministrador = false
        carrito.clear()
        versionDatos++
    }

    fun agregarAlCarrito(producto: Producto) {
        if (!producto.disponible) return
        carrito[producto.id] = (carrito[producto.id] ?: 0) + 1
        versionDatos++
    }

    fun cambiarCantidad(productoId: Int, delta: Int) {
        val nueva = (carrito[productoId] ?: 0) + delta
        if (nueva <= 0) carrito.remove(productoId) else carrito[productoId] = nueva
        versionDatos++
    }

    fun totalCarrito(): Double =
        carrito.entries.sumOf { (id, cantidad) ->
            (productoService.buscarPorId(id)?.precio ?: 0.0) * cantidad
        }

    fun confirmarPedido(): Pedido {
        val cliente = requireNotNull(clienteActual) { "Debe iniciar sesión como cliente." }
        val detalles = carrito.mapNotNull { (id, cantidad) ->
            productoService.buscarPorId(id)?.let { DetallePedido(it, cantidad) }
        }.toMutableList()
        val pedido = pedidoService.crear(cliente, detalles)
        carrito.clear()
        versionDatos++
        return pedido
    }

    fun pedidosCliente(): List<Pedido> {
        val telefono = clienteActual?.telefono ?: return emptyList()
        return pedidoService.listar().filter { it.cliente.telefono == telefono }
    }

    fun avanzarEstado(pedido: Pedido) {
        val siguiente = when (pedido.estado) {
            EstadoPedido.RECIBIDO -> EstadoPedido.EN_PREPARACION
            EstadoPedido.EN_PREPARACION -> EstadoPedido.EN_CAMINO
            EstadoPedido.EN_CAMINO -> EstadoPedido.ENTREGADO
            EstadoPedido.ENTREGADO -> return
        }
        pedidoService.actualizarEstado(pedido.id, siguiente)
        versionDatos++
    }

    fun notificarCambio() {
        versionDatos++
    }
}
