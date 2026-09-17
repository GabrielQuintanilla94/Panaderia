package panaderiarosario.service

import panaderiarosario.model.Categoria
import panaderiarosario.model.Producto

class ProductoService(
    private val productos: MutableList<Producto>
) {
    fun listar(): List<Producto> = productos.toList()

    fun disponibles(): List<Producto> = productos.filter { it.disponible }

    fun buscarPorId(id: Int): Producto? = productos.find { it.id == id }

    fun crear(nombre: String, categoria: Categoria, precio: Double): Producto {
        val nuevoId = (productos.maxOfOrNull { it.id } ?: 0) + 1
        val producto = Producto(nuevoId, nombre, categoria, precio, true)
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

    fun cambiarDisponibilidad(id: Int): Boolean {
        val producto = buscarPorId(id) ?: return false
        producto.disponible = !producto.disponible
        return true
    }

    fun eliminar(id: Int): Boolean = productos.removeIf { it.id == id }
}
