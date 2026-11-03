package panaderiarosario

import org.junit.Assert.*
import org.junit.Test
import panaderiarosario.data.FirebaseRepository
import panaderiarosario.model.*
import panaderiarosario.service.PedidoService

class PedidoTest {
    private fun producto() = Producto(1, "Pan francés", Categoria.PAN, 0.25, 70)
    private val cliente = Cliente("Ana", "77777777", "San Salvador")

    @Test fun pedidoCalculaTotalYPreservaPropietario() {
        val pedido = Pedido(1, cliente, mutableListOf(DetallePedido(producto(), 4)), usuarioId = "uid-a")
        assertEquals(1.0, pedido.calcularTotal(), 0.00001)
        assertEquals("uid-a", pedido.usuarioId)
    }

    @Test fun productoSeRecuperaDelFormatoFirestore() {
        val original = producto()
        // Firestore represents integer fields as Longs after reading.
        val datos = FirebaseRepository.productoMap(original).toMutableMap()
        datos["id"] = 1L
        datos["stock"] = 70L
        assertEquals(original, FirebaseRepository.producto(datos))
    }

    @Test fun historialRemotoReemplazaDatosDeLaSesionAnterior() {
        val service = PedidoService()
        service.reemplazar(listOf(Pedido(1, cliente, mutableListOf(), usuarioId = "uid-a")))
        service.reemplazar(listOf(Pedido(2, cliente, mutableListOf(), usuarioId = "uid-b")))
        assertEquals(listOf("uid-b"), service.listar().map { it.usuarioId })
        service.reemplazar(emptyList())
        assertTrue(service.listar().isEmpty())
    }

    @Test fun estadoSoloAvanzaEnOrden() {
        val service = PedidoService()
        val pedido = service.crear(cliente, mutableListOf(DetallePedido(producto(), 2)))
        assertThrows(IllegalArgumentException::class.java) { service.actualizarEstado(pedido.id, EstadoPedido.EN_CAMINO) }
        assertTrue(service.actualizarEstado(pedido.id, EstadoPedido.EN_PREPARACION))
        assertTrue(service.actualizarEstado(pedido.id, EstadoPedido.EN_CAMINO))
        assertTrue(service.actualizarEstado(pedido.id, EstadoPedido.ENTREGADO))
        assertThrows(IllegalArgumentException::class.java) { service.actualizarEstado(pedido.id, EstadoPedido.RECIBIDO) }
    }
}
