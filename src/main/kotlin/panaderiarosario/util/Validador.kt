package panaderiarosario.util

object Validador {
    fun textoNoVacio(valor: String, nombreCampo: String): String {
        if (valor.isBlank()) throw IllegalArgumentException("$nombreCampo no puede quedar vacío.")
        return valor.trim()
    }

    fun enteroPositivo(valor: String, nombreCampo: String): Int {
        val numero = valor.toIntOrNull()
            ?: throw IllegalArgumentException("$nombreCampo debe ser un número entero.")
        if (numero <= 0) throw IllegalArgumentException("$nombreCampo debe ser mayor que cero.")
        return numero
    }

    fun decimalPositivo(valor: String, nombreCampo: String): Double {
        val numero = valor.toDoubleOrNull()
            ?: throw IllegalArgumentException("$nombreCampo debe ser un número válido.")
        if (numero <= 0.0) throw IllegalArgumentException("$nombreCampo debe ser mayor que cero.")
        return numero
    }
}
