package panaderiarosario.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await
import panaderiarosario.model.*

/** Firebase is the source of truth; documents always carry their authenticated owner. */
class FirebaseRepository {
    val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    init {
        if (panaderiarosario.BuildConfig.FIREBASE_EMULATORS) {
            auth.useEmulator("10.0.2.2", 9099)
            db.useEmulator("10.0.2.2", 8080)
        }
    }

    suspend fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email.trim(), password).await()
    }

    suspend fun google(idToken: String) {
        auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null)).await()
    }

    suspend fun register(email: String, password: String, cliente: Cliente) {
        auth.createUserWithEmailAndPassword(email.trim(), password).await()
        // Authentication can succeed even if profile persistence fails. Retrying login repairs it.
        guardarPerfil(cliente)
    }

    suspend fun guardarPerfil(cliente: Cliente) {
        val user = requireNotNull(auth.currentUser)
        db.collection("usuarios").document(user.uid).set(mapOf(
            "nombre" to cliente.nombre, "telefono" to cliente.telefono,
            "direccion" to cliente.direccion, "email" to (user.email ?: "")
        )).await()
    }

    suspend fun perfil(): Cliente {
        val user = requireNotNull(auth.currentUser)
        val doc = db.collection("usuarios").document(user.uid).get().await()
        if (doc.exists()) return Cliente(doc.getString("nombre") ?: "Cliente",
            doc.getString("telefono") ?: "", doc.getString("direccion") ?: "")
        val cliente = Cliente(user.displayName ?: user.email?.substringBefore('@') ?: "Cliente", "", "")
        guardarPerfil(cliente)
        return cliente
    }

    suspend fun esAdmin(): Boolean = auth.currentUser?.getIdToken(true)?.await()?.claims?.get("admin") == true

    fun observarPedidos(admin: Boolean, resultado: (List<Pedido>?, Exception?) -> Unit): ListenerRegistration {
        val uid = requireNotNull(auth.currentUser).uid
        val query = if (admin) db.collection("pedidos") else db.collection("pedidos").whereEqualTo("usuarioId", uid)
        return query.addSnapshotListener { snapshot, error ->
            if (error != null) resultado(null, error)
            else try { resultado(snapshot!!.documents.map(::pedido).sortedByDescending { it.id }, null) }
            catch (e: Exception) { resultado(null, e) }
        }
    }

    fun observarProductos(resultado: (List<Producto>?, Exception?) -> Unit): ListenerRegistration =
        db.collection("productos").addSnapshotListener { snapshot, error ->
            if (error != null) resultado(null, error)
            else try { resultado(snapshot!!.documents.map { producto(it.data!!) }.sortedBy { it.id }, null) }
            catch (e: Exception) { resultado(null, e) }
        }

    suspend fun guardarCatalogo(productos: List<Producto>) {
        val anteriores = db.collection("productos").get().await()
        val batch = db.batch()
        anteriores.documents.filter { old -> productos.none { it.id.toString() == old.id } }
            .forEach { batch.delete(it.reference) }
        productos.forEach { batch.set(db.collection("productos").document(it.id.toString()), productoMap(it)) }
        batch.commit().await()
    }

    suspend fun crearPedido(cliente: Cliente, detalles: List<DetallePedido>): Pedido {
        require(detalles.isNotEmpty()) { "El pedido debe contener productos." }
        require(detalles.all { it.cantidad > 0 }) { "Cantidad inválida." }
        val uid = requireNotNull(auth.currentUser).uid
        val ref = db.collection("pedidos").document()
        val contador = db.collection("contadores").document("pedidos")
        val id = db.runTransaction { tx ->
            val actual = tx.get(contador).getLong("valor") ?: 0L
            val stocks = detalles.map { detalle ->
                val productoRef = db.collection("productos").document(detalle.producto.id.toString())
                val doc = tx.get(productoRef)
                require(doc.exists()) { "El catálogo debe ser publicado por un administrador." }
                val stock = doc.getLong("stock") ?: 0
                require(stock >= detalle.cantidad) { "Stock insuficiente para ${detalle.producto.nombre}." }
                require(doc.getDouble("precio") == detalle.producto.precio) { "El precio cambió. Revise el carrito." }
                productoRef to (stock - detalle.cantidad)
            }
            val nuevo = actual + 1
            tx.set(contador, mapOf("valor" to nuevo, "ultimoPedido" to ref.id))
            stocks.forEach { (productoRef, stock) -> tx.update(productoRef, mapOf("stock" to stock, "ultimoPedido" to ref.id)) }
            tx.set(ref, mapOf(
                "id" to nuevo, "usuarioId" to uid, "estado" to EstadoPedido.RECIBIDO.name,
                "creadoEn" to FieldValue.serverTimestamp(),
                "cliente" to mapOf("nombre" to cliente.nombre, "telefono" to cliente.telefono, "direccion" to cliente.direccion),
                "detalles" to detalles.map { mapOf("producto" to productoMap(it.producto), "cantidad" to it.cantidad) },
                "cantidades" to detalles.associate { it.producto.id.toString() to it.cantidad },
                "total" to detalles.sumOf { it.calcularSubtotal() }
            ))
            nuevo.toInt()
        }.await()
        return Pedido(id, cliente, detalles.toMutableList(), documentoId = ref.id, usuarioId = uid)
    }

    suspend fun avanzar(pedido: Pedido, siguiente: EstadoPedido) {
        val ref = db.collection("pedidos").document(pedido.documentoId)
        db.runTransaction { tx ->
            require(tx.get(ref).getString("estado") == pedido.estado.name) { "El pedido cambió. Vuelva a intentarlo." }
            tx.update(ref, "estado", siguiente.name)
        }.await()
    }

    companion object {
        fun productoMap(p: Producto): Map<String, Any> = mapOf("id" to p.id, "nombre" to p.nombre,
            "categoria" to p.categoria.name, "precio" to p.precio, "stock" to p.stock)
        fun producto(m: Map<String, Any>): Producto = Producto((m["id"] as Number).toInt(),
            m["nombre"] as String, Categoria.valueOf(m["categoria"] as String),
            (m["precio"] as Number).toDouble(), (m["stock"] as Number).toInt())
        private fun pedido(doc: DocumentSnapshot): Pedido {
            val c = doc.get("cliente") as Map<*, *>
            val detalles = (doc.get("detalles") as List<*>).map { item ->
                val d = item as Map<*, *>
                @Suppress("UNCHECKED_CAST")
                DetallePedido(producto(d["producto"] as Map<String, Any>), (d["cantidad"] as Number).toInt())
            }.toMutableList()
            return Pedido(doc.getLong("id")!!.toInt(), Cliente(c["nombre"] as String,
                c["telefono"] as String, c["direccion"] as String), detalles,
                EstadoPedido.valueOf(doc.getString("estado")!!), doc.id, doc.getString("usuarioId")!!)
        }
    }
}
