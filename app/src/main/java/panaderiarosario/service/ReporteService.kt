package panaderiarosario.service

import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.provider.MediaStore
import panaderiarosario.model.Categoria
import panaderiarosario.model.EstadoPedido
import panaderiarosario.model.Pedido
import java.util.Calendar

/** Períodos disponibles para filtrar los reportes. */
enum class Periodo(val etiqueta: String) {
    HOY("Hoy"), SEMANA("7 días"), MES("30 días"), TODO("Todo");

    /** Inicio del período en milisegundos; 0 incluye todos los pedidos. */
    fun desde(ahora: Long = System.currentTimeMillis()): Long = when (this) {
        HOY -> Calendar.getInstance().apply {
            timeInMillis = ahora
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        SEMANA -> ahora - 7L * DIA
        MES -> ahora - 30L * DIA
        TODO -> 0L
    }

    private companion object { const val DIA = 24L * 60 * 60 * 1000 }
}

class ReporteService(
    private val productoService: ProductoService,
    private val pedidoService: PedidoService
) {
    /** Pedidos registrados desde [desde] (0 = todos). */
    fun pedidosDesde(desde: Long = 0L): List<Pedido> =
        pedidoService.listar().filter { desde == 0L || it.fecha >= desde }

    /** Importe vendido por categoría, incluidas las categorías sin ventas. */
    fun ventasPorCategoria(desde: Long = 0L): Map<Categoria, Double> {
        val pedidos = pedidosDesde(desde)
        return Categoria.entries.associateWith { categoria -> pedidos.sumOf { it.totalCategoria(categoria) } }
    }

    /** Productos ordenados por unidades vendidas (mayor a menor). */
    fun productosMasVendidos(desde: Long = 0L, limite: Int = 5): List<Pair<String, Int>> =
        pedidosDesde(desde).flatMap { it.detalles }
            .groupBy { it.producto.nombre }
            .map { (nombre, detalles) -> nombre to detalles.sumOf { it.cantidad } }
            .sortedWith(compareByDescending<Pair<String, Int>> { it.second }.thenBy { it.first })
            .take(limite)

    fun generarResumen(): String {
        val productos = productoService.listar()
        val pedidos = pedidoService.listar()
        val ventas = pedidos
            .filter { it.estado == EstadoPedido.ENTREGADO }
            .sumOf { it.calcularTotal() }

        val totalProductosVendidos = pedidos
            .flatMap { it.detalles }
            .sumOf { it.cantidad }

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
            appendLine("Productos vendidos (unidades): $totalProductosVendidos")
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
            appendLine("------------------------------")
            appendLine("Ventas por categoría:")
            ventasPorCategoria().forEach { (categoria, total) ->
                appendLine("  ${nombreCategoria(categoria)}: $${"%.2f".format(total)}")
            }
            appendLine("Productos más vendidos:")
            val top = productosMasVendidos()
            if (top.isEmpty()) appendLine("  Sin datos todavía")
            top.forEachIndexed { i, (nombre, unidades) -> appendLine("  ${i + 1}. $nombre: $unidades unidades") }
            appendLine("==============================")
        }
    }

    fun exportarReporte(context: Context, nombreArchivo: String = "reporte_ventas.txt"): String {
        context.openFileOutput(nombreArchivo, Context.MODE_PRIVATE).bufferedWriter().use {
            it.write(generarResumen())
        }
        return nombreArchivo
    }

    fun exportarReportePdf(context: Context, nombreArchivo: String = "Reporte_Panaderia_Rosario.pdf"): String {
        val contenido = generarResumen()
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paintTitle = Paint().apply {
            textSize = 18f
            isFakeBoldText = true
            color = Color.BLACK
            isAntiAlias = true
        }

        val paint = Paint().apply {
            textSize = 14f
            color = Color.BLACK
            isAntiAlias = true
        }

        val margin = 50f
        var y = 60f
        val lineHeight = 22f

        canvas.drawText("PANADERÍA ROSARIO - REPORTE", margin, y, paintTitle)
        y += lineHeight * 2

        contenido.lines().forEach { line ->
            if (y > 780f) return@forEach
            canvas.drawText(line, margin, y, paint)
            y += lineHeight
        }

        pdfDocument.finishPage(page)

        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, nombreArchivo)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
        if (uri != null) {
            resolver.openOutputStream(uri)?.use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }
        }
        pdfDocument.close()
        return nombreArchivo
    }

    companion object {
        fun nombreCategoria(categoria: Categoria) = when (categoria) {
            Categoria.PAN -> "Pan"
            Categoria.REPOSTERIA -> "Repostería"
            Categoria.DESAYUNO -> "Desayunos"
            Categoria.CENA -> "Cenas"
        }
    }
}
