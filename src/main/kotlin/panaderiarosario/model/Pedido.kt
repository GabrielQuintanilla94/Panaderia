package panaderiarosario.model

data class Pedido(
    val id: Int,
    val cliente: Cliente,
    val detalles: MutableList<DetallePedido>,
    var estado: EstadoPedido = EstadoPedido.RECIBIDO
) : Calculable {
    override fun calcularTotal(): Double = detalles.sumOf { it.calcularSubtotal() }

    fun totalCategoria(categoria: Categoria): Double =
        detalles.filter { it.producto.categoria == categoria }
            .sumOf { it.calcularSubtotal() }
}
