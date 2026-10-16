package panaderiarosario.model

data class DetallePedido(
    val producto: Producto,
    var cantidad: Int
) {
    fun calcularSubtotal(): Double = producto.precio * cantidad
}
