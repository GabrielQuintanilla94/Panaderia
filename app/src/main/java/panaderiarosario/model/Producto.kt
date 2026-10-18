package panaderiarosario.model

data class Producto(
    val id: Int,
    var nombre: String,
    var categoria: Categoria,
    var precio: Double,
    var stock: Int = if (categoria == Categoria.PAN) 70 else 20
) {
    val disponible: Boolean
        get() = stock > 0
}
