package panaderiarosario.service

import panaderiarosario.model.Cliente
import panaderiarosario.model.DetallePedido
import panaderiarosario.model.EstadoPedido
import panaderiarosario.model.Pedido

class PedidoService {
    private val pedidos = mutableListOf<Pedido>()

    fun listar(): List<Pedido> = pedidos.toList()

    fun buscarPorId(id: Int): Pedido? = pedidos.find { it.id == id }

    fun crear(cliente: Cliente, detalles: MutableList<DetallePedido>): Pedido {
        require(detalles.isNotEmpty()) { "El pedido debe contener al menos un producto." }
        require(detalles.all { it.cantidad > 0 }) { "Todas las cantidades deben ser mayores que cero." }
        require(detalles.all { it.producto.disponible }) { "No se puede pedir un producto no disponible." }

        val nuevoId = (pedidos.maxOfOrNull { it.id } ?: 0) + 1
        val pedido = Pedido(nuevoId, cliente, detalles)
        pedidos.add(pedido)
        return pedido
    }

    fun actualizarEstado(id: Int, estado: EstadoPedido): Boolean {
        val pedido = buscarPorId(id) ?: return false
        pedido.estado = estado
        return true
    }
}
