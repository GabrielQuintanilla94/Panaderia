package panaderiarosario.model

data class Producto(
    val id: Int,
    var nombre: String,
    var categoria: Categoria,
    var precio: Double,
    var disponible: Boolean = true
)
