package panaderiarosario.service

import panaderiarosario.model.EstadoPedido
import java.io.File

class ReporteService(
    private val productoService: ProductoService,
    private val pedidoService: PedidoService
) {
    fun generarResumen(): String {
        val productos = productoService.listar()
        val pedidos = pedidoService.listar()
        val ventas = pedidos.filter { it.estado == EstadoPedido.ENTREGADO }.sumOf { it.calcularTotal() }

        val unidadesPorProducto = pedidos
            .flatMap { it.detalles }
            .groupBy { it.producto.nombre }
            .mapValues { (_, detalles) -> detalles.sumOf { it.cantidad } }

        val masVendido = unidadesPorProducto.maxByOrNull { it.value }

        return buildString {
            appendLine("==============================")
            appendLine("REPORTE PANADERÍA ROSARIO")
            appendLine("==============================")
            appendLine("Productos registrados: ${productos.size}")
            appendLine("Productos disponibles: ${productos.count { it.disponible }}")
            appendLine("Pedidos registrados: ${pedidos.size}")
            appendLine("Recibidos: ${pedidos.count { it.estado == EstadoPedido.RECIBIDO }}")
            appendLine("En preparación: ${pedidos.count { it.estado == EstadoPedido.EN_PREPARACION }}")
            appendLine("En camino: ${pedidos.count { it.estado == EstadoPedido.EN_CAMINO }}")
            appendLine("Entregados: ${pedidos.count { it.estado == EstadoPedido.ENTREGADO }}")
            appendLine("Ventas acumuladas entregadas: $${"%.2f".format(ventas)}")
            if (masVendido != null) {
                appendLine("Producto más vendido: ${masVendido.key} (${masVendido.value} unidades)")
            } else {
                appendLine("Producto más vendido: Sin datos todavía")
            }
            appendLine("==============================")
        }
    }

    fun exportarReporte(nombreArchivo: String = "reporte_ventas.txt"): String {
        val contenido = generarResumen()
        File(nombreArchivo).writeText(contenido)
        return nombreArchivo
    }
}
