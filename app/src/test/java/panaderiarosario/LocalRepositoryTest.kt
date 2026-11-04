package panaderiarosario

import org.junit.Assert.*
import org.junit.Test
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import panaderiarosario.data.LocalRepository
import panaderiarosario.model.*
import panaderiarosario.service.PedidoService

class LocalRepositoryTest {
    @get:Rule val folder = TemporaryFolder()

    @Test fun pedidoCatalogoYEntregaSobrevivenReinicioSinFirebase() {
        val file = folder.root.resolve("datos.json")
        val local = LocalRepository(file)
        assertTrue(local.productos.isNotEmpty())
        val producto = local.productos.first()
        val stockInicial = producto.stock
        val service = PedidoService()
        val pedido = service.crear(Cliente("Ana", "77777777", "San Salvador"),
            mutableListOf(DetallePedido(producto.copy(), 2))).copy(
            documentoId = "pedido-local", usuarioId = LocalRepository.CLIENTE_ID)
        producto.stock -= 2
        local.guardar(local.productos, listOf(pedido))
        val reiniciado = LocalRepository(file)
        assertEquals(stockInicial - 2, reiniciado.productos.first().stock)
        assertEquals("Ana", reiniciado.pedidos.single().cliente.nombre)
        assertEquals(2, reiniciado.pedidos.single().detalles.single().cantidad)
        assertEquals(producto.precio * 2, reiniciado.pedidos.single().calcularTotal(), 0.00001)
        val admin = PedidoService()
        admin.reemplazar(reiniciado.pedidos)
        admin.actualizarEstado(pedido.id, EstadoPedido.EN_PREPARACION)
        admin.actualizarEstado(pedido.id, EstadoPedido.EN_CAMINO)
        admin.actualizarEstado(pedido.id, EstadoPedido.ENTREGADO)
        reiniciado.guardar(reiniciado.productos, admin.listar())
        assertEquals(EstadoPedido.ENTREGADO, LocalRepository(file).pedidos.single().estado)
        assertEquals(LocalRepository.CLIENTE_ID, LocalRepository(file).pedidos.single().usuarioId)
    }

    @Test fun cambiosDeCatalogoPersistenSinAlterarPrecioDelPedido() {
        val local = LocalRepository(folder.root.resolve("datos.json"))
        val producto = local.productos.first()
        val precioAnterior = producto.precio
        val pedido = Pedido(1, Cliente("Ana", "77777777", "Casa"),
            mutableListOf(DetallePedido(producto.copy(), 1)))
        producto.precio = 9.0
        producto.nombre = "Nuevo nombre"
        local.guardar(local.productos, listOf(pedido))
        val reiniciado = LocalRepository(folder.root.resolve("datos.json"))
        assertEquals(9.0, reiniciado.productos.first().precio, 0.00001)
        assertEquals(precioAnterior, reiniciado.pedidos.single().calcularTotal(), 0.00001)
    }
}
