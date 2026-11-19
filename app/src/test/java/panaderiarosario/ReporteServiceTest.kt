package panaderiarosario

import org.junit.Assert.assertEquals
import org.junit.Test
import panaderiarosario.data.DatosIniciales
import panaderiarosario.model.Categoria
import panaderiarosario.model.Cliente
import panaderiarosario.model.DetallePedido
import panaderiarosario.model.Pedido
import panaderiarosario.service.PedidoService
import panaderiarosario.service.Periodo
import panaderiarosario.service.ProductoService
import panaderiarosario.service.ReporteService

class ReporteServiceTest {
    private val dia = 24L * 60 * 60 * 1000
    private val ahora = 1_800_000_000_000L
    private val productos = DatosIniciales.productos()
    private val redondo = productos.first { it.nombre == "Redondo" }
    private val cheesecake = productos.first { it.nombre == "Cheesecake de fresa" }
    private val cena = productos.first { it.nombre == "Cena del día" }
    private val cliente = Cliente("Ana", "7000-0000", "San Salvador")

    private fun servicio(): ReporteService {
        val pedidos = PedidoService()
        pedidos.reemplazar(listOf(
            Pedido(1, cliente, mutableListOf(DetallePedido(redondo, 10), DetallePedido(cheesecake, 2)), fecha = ahora - 1000),
            Pedido(2, cliente, mutableListOf(DetallePedido(redondo, 5)), fecha = ahora - 3 * dia),
            Pedido(3, cliente, mutableListOf(DetallePedido(cena, 1)), fecha = ahora - 20 * dia),
        ))
        return ReporteService(ProductoService(productos), pedidos)
    }

    @Test fun ventasPorCategoriaIncluyeTodasLasCategorias() {
        val ventas = servicio().ventasPorCategoria()
        assertEquals(Categoria.entries.toSet(), ventas.keys)
        assertEquals(15 * 0.12, ventas.getValue(Categoria.PAN), 1e-9)
        assertEquals(7.00, ventas.getValue(Categoria.REPOSTERIA), 1e-9)
        assertEquals(5.00, ventas.getValue(Categoria.CENA), 1e-9)
        assertEquals(0.0, ventas.getValue(Categoria.DESAYUNO), 1e-9)
    }

    @Test fun productosMasVendidosOrdenadosPorUnidades() {
        val top = servicio().productosMasVendidos()
        assertEquals(listOf("Redondo" to 15, "Cheesecake de fresa" to 2, "Cena del día" to 1), top)
    }

    @Test fun filtroPorPeriodo() {
        val s = servicio()
        assertEquals(2, s.pedidosDesde(Periodo.SEMANA.desde(ahora)).size)
        assertEquals(3, s.pedidosDesde(Periodo.MES.desde(ahora)).size)
        assertEquals(3, s.pedidosDesde(Periodo.TODO.desde(ahora)).size)
        // Solo el pedido de hoy: 10 redondos y 2 cheesecakes.
        assertEquals(listOf("Redondo" to 10, "Cheesecake de fresa" to 2), s.productosMasVendidos(ahora - 2 * dia))
    }
}
