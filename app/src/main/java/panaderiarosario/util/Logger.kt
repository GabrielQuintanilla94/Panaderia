package panaderiarosario.util

import android.content.Context
import android.util.Log
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Logger {
    private val formatoTimestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
    private val formatoFecha = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    enum class Nivel { INFO, ADVERTENCIA, ERROR }

    fun registrar(
        context: Context,
        nivel: Nivel,
        mensaje: String,
        throwable: Throwable? = null
    ) {
        val timestamp = LocalDateTime.now().format(formatoTimestamp)
        val fechaActual = LocalDate.now().format(formatoFecha)
        val entrada = buildString {
            append("[$timestamp] [${nivel.name}] $mensaje\n")
            throwable?.let {
                append("Excepción: ${it.javaClass.name}: ${it.message}\n")
                append(it.stackTraceToString())
                append("\n")
            }
            append("-".repeat(65))
            append("\n")
        }

        context.openFileOutput("panaderia_$fechaActual.log", Context.MODE_APPEND).bufferedWriter().use {
            it.write(entrada)
        }
        if (nivel == Nivel.ERROR) {
            context.openFileOutput("errores.txt", Context.MODE_APPEND).bufferedWriter().use {
                it.write(entrada)
            }
        }

        when (nivel) {
            Nivel.INFO -> Log.i("PanaderiaRosario", mensaje, throwable)
            Nivel.ADVERTENCIA -> Log.w("PanaderiaRosario", mensaje, throwable)
            Nivel.ERROR -> Log.e("PanaderiaRosario", mensaje, throwable)
        }
    }

    fun registrarError(context: Context, mensaje: String, throwable: Throwable? = null) =
        registrar(context, Nivel.ERROR, mensaje, throwable)

    fun registrarAdvertencia(context: Context, mensaje: String) =
        registrar(context, Nivel.ADVERTENCIA, mensaje)

    fun registrarInfo(context: Context, mensaje: String) =
        registrar(context, Nivel.INFO, mensaje)
}
