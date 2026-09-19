package panaderiarosario.util

import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Logger {
    private val formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val archivoLog = File("errores.txt")

    fun registrarError(mensaje: String, throwable: Throwable? = null) {
        val timestamp = LocalDateTime.now().format(formato)
        val sb = StringBuilder()
        sb.append("[$timestamp] ERROR: $mensaje\n")

        if (throwable != null) {
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            throwable.printStackTrace(pw)
            sb.append(sw.toString())
        }
        sb.append("-".repeat(50)).append("\n")

        try {
            archivoLog.appendText(sb.toString())
        } catch (e: Exception) {
            println("No se pudo escribir en el archivo de log: ${e.message}")
        }
    }
}
