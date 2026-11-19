package panaderiarosario.data

import com.google.gson.Gson
import java.io.File
import panaderiarosario.model.*

/** Explicit offline demonstration, independent of Firebase accounts and services. */
class LocalRepository(private val file: File) {
    private val gson = Gson()
    private data class StoredOrder(val id: Int, val nombre: String, val telefono: String,
        val direccion: String, val detalles: List<DetallePedido>, val estado: EstadoPedido,
        val documentoId: String, val usuarioId: String, val fecha: Long)
    private data class Snapshot(val productos: List<Producto>, val pedidos: List<StoredOrder>)
    val productos: MutableList<Producto>
    val pedidos: MutableList<Pedido>

    init {
        val snapshot = if (file.exists()) gson.fromJson(file.readText(), Snapshot::class.java) else null
        productos = snapshot?.productos?.map(::reparar)?.toMutableList() ?: DatosIniciales.productos()
        pedidos = snapshot?.pedidos?.map { p -> Pedido(p.id, Cliente(p.nombre, p.telefono, p.direccion),
            p.detalles.map { DetallePedido(reparar(it.producto), it.cantidad) }.toMutableList(),
            p.estado, p.documentoId, p.usuarioId, p.fecha) }?.toMutableList() ?: mutableListOf()
    }

    fun guardar(catalogo: List<Producto>, historial: List<Pedido>) {
        val snapshot = Snapshot(catalogo, historial.map { p -> StoredOrder(p.id, p.cliente.nombre,
            p.cliente.telefono, p.cliente.direccion, p.detalles, p.estado, p.documentoId, p.usuarioId, p.fecha) })
        file.parentFile?.mkdirs()
        file.writeText(gson.toJson(snapshot))
    }

    /** Gson omite los valores por defecto de Kotlin: completa la descripción de datos antiguos. */
    private fun reparar(p: Producto): Producto {
        @Suppress("USELESS_CAST")
        val descripcion = (p.descripcion as String?).orEmpty().ifBlank { DatosIniciales.descripcion(p.id) }
        return p.copy(descripcion = descripcion)
    }

    companion object { const val CLIENTE_ID = "demostracion-local" }
}
