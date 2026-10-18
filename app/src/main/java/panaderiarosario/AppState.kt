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
