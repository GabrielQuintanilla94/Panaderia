package panaderiarosario.service

import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.provider.MediaStore
import panaderiarosario.model.EstadoPedido

class ReporteService(
    private val productoService: ProductoService,
    private val pedidoService: PedidoService
) {
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
}
