package panaderiarosario.model

data class Producto(
    val id: Int,
    var nombre: String,
    var categoria: Categoria,
    var precio: Double,
    var stock: Int = if (categoria == Categoria.PAN) 70 else 20,
    var descripcion: String = "",
    /** Dirección web de la foto del producto (vacía = ilustración por defecto). */
    var imagenUrl: String = ""
) {
    val disponible: Boolean
        get() = stock > 0
}
