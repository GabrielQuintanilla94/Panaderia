package panaderiarosario.util

import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Logger {
    private val formatoTimestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
    private val formatoFecha = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val directorioLogs = File("logs")

    init {
        if (!directorioLogs.exists()) {
            directorioLogs.mkdirs()
        }
    }

    enum class Nivel {
        INFO, ADVERTENCIA, ERROR
    }

    private fun registrar(nivel: Nivel, mensaje: String, throwable: Throwable? = null) {
        val timestamp = LocalDateTime.now().format(formatoTimestamp)
        val fechaActual = LocalDate.now().format(formatoFecha)
        val archivoDiario = File(directorioLogs, "panaderia_$fechaActual.log")
        val archivoErroresGeneral = File("errores.txt")

        val sb = StringBuilder()
        sb.append("[$timestamp] [${nivel.name}] $mensaje\n")

        if (throwable != null) {
            sb.append("  ↳ Excepción: ${throwable.javaClass.name}: ${throwable.message}\n")
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            throwable.printStackTrace(pw)
            sb.append(sw.toString())
        }
        sb.append("-".repeat(65)).append("\n")

        val entrada = sb.toString()

        try {
            archivoDiario.appendText(entrada)
            if (nivel == Nivel.ERROR) {
                archivoErroresGeneral.appendText(entrada)
            }
        } catch (e: Exception) {
            println("❌ Error al escribir en el archivo de log: ${e.message}")
        }
    }

    fun registrarError(mensaje: String, throwable: Throwable? = null) {
        registrar(Nivel.ERROR, mensaje, throwable)
    }

    fun registrarAdvertencia(mensaje: String) {
        registrar(Nivel.ADVERTENCIA, mensaje)
    }

    fun registrarInfo(mensaje: String) {
        registrar(Nivel.INFO, mensaje)
    }
}
