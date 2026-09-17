package panaderiarosario.util

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Logger {
    private val formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    fun registrarError(mensaje: String) {
        val linea = "${LocalDateTime.now().format(formato)} - $mensaje\n"
        File("errores.txt").appendText(linea)
    }
}
