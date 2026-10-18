package panaderiarosario.service

import panaderiarosario.model.Categoria
import panaderiarosario.model.Producto

class ProductoService(
    private val productos: MutableList<Producto>
) : MantenimientoBase<Producto, Int> {

    override fun listar(): List<Producto> = productos.toList()

    fun disponibles(): List<Producto> = productos.filter { it.disponible }

    fun buscarPorId(id: Int): Producto? = productos.find { it.id == id }

    fun buscarPorCategoria(categoriaBuscada: Categoria): List<Producto> {
        return productos.filter { it.categoria == categoriaBuscada }
    }

    fun crear(nombre: String, categoria: Categoria, precio: Double): Producto {
        val nuevoId = (productos.maxOfOrNull { it.id } ?: 0) + 1
        val stockInicial = if (categoria == Categoria.PAN) 70 else 20
        val producto = Producto(nuevoId, nombre, categoria, precio, stockInicial)
        productos.add(producto)
        return producto
    }

    fun actualizar(id: Int, nombre: String, categoria: Categoria, precio: Double): Boolean {
        val producto = buscarPorId(id) ?: return false
        producto.nombre = nombre
        producto.categoria = categoria
        producto.precio = precio
        return true
    }

    fun actualizarStock(id: Int, nuevoStock: Int): Boolean {
        val producto = buscarPorId(id) ?: return false
        producto.stock = maxOf(0, nuevoStock)
        return true
    }

    override fun eliminar(id: Int): Boolean = productos.removeIf { it.id == id }
}
