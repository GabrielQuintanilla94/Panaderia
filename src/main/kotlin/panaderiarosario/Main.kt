package panaderiarosario

import panaderiarosario.data.DatosIniciales
import panaderiarosario.model.Categoria
import panaderiarosario.model.Cliente
import panaderiarosario.model.DetallePedido
import panaderiarosario.model.EstadoPedido
import panaderiarosario.service.PedidoService
import panaderiarosario.service.ProductoService
import panaderiarosario.service.ReporteService
import panaderiarosario.util.Logger
import panaderiarosario.util.Validador

fun main() {
    val productoService = ProductoService(DatosIniciales.productos())
    val pedidoService = PedidoService()
    val reporteService = ReporteService(productoService, pedidoService)
    val clienteService = panaderiarosario.service.ClienteService()

    var continuar = true
    while (continuar) {
        println("\n================================")
        println("       PANADERÍA ROSARIO")
        println("================================")
        println("1. Gestión de productos")
        println("2. Registrar pedido")
        println("3. Consultar pedidos")
        println("4. Actualizar estado de pedido")
        println("5. Reportes")
        println("6. Gestión de clientes")
        println("7. Salir")
        print("Seleccione una opción: ")

        when (readlnOrNull()?.trim()) {
            "1" -> menuProductos(productoService)
            "2" -> registrarPedido(productoService, pedidoService)
            "3" -> consultarPedidos(pedidoService)
            "4" -> actualizarEstado(pedidoService)
            "5" -> println(reporteService.generarResumen())
            "6" -> menuClientes(clienteService)
            "7" -> continuar = false
            else -> println("Opción inválida.")
        }
    }

    println("Programa finalizado.")
}

private fun menuProductos(productoService: ProductoService) {
    var volver = false
    while (!volver) {
        println("\n--- GESTIÓN DE PRODUCTOS ---")
        println("1. Listar productos")
        println("2. Registrar producto")
        println("3. Actualizar producto")
        println("4. Cambiar disponibilidad")
        println("5. Eliminar producto")
        println("6. Regresar")
        print("Opción: ")

        when (readlnOrNull()?.trim()) {
            "1" -> mostrarProductos(productoService.listar())
            "2" -> ejecutarSeguro {
                print("Nombre: ")
                val nombre = Validador.textoNoVacio(readln(), "Nombre")
                val categoria = leerCategoria()
                print("Precio: $")
                val precio = Validador.decimalPositivo(readln(), "Precio")
                val producto = productoService.crear(nombre, categoria, precio)
                println("Producto #${producto.id} registrado correctamente.")
            }
            "3" -> ejecutarSeguro {
                mostrarProductos(productoService.listar())
                print("ID a actualizar: ")
                val id = Validador.enteroPositivo(readln(), "ID")
                print("Nuevo nombre: ")
                val nombre = Validador.textoNoVacio(readln(), "Nombre")
                val categoria = leerCategoria()
                print("Nuevo precio: $")
                val precio = Validador.decimalPositivo(readln(), "Precio")
                if (productoService.actualizar(id, nombre, categoria, precio)) {
                    println("Producto actualizado.")
                } else {
                    println("No existe un producto con ese ID.")
                }
            }
            "4" -> ejecutarSeguro {
                mostrarProductos(productoService.listar())
                print("ID del producto: ")
                val id = Validador.enteroPositivo(readln(), "ID")
                if (productoService.cambiarDisponibilidad(id)) println("Disponibilidad actualizada.")
                else println("Producto no encontrado.")
            }
            "5" -> ejecutarSeguro {
                mostrarProductos(productoService.listar())
                print("ID a eliminar: ")
                val id = Validador.enteroPositivo(readln(), "ID")
                if (productoService.eliminar(id)) println("Producto eliminado.")
                else println("Producto no encontrado.")
            }
            "6" -> volver = true
            else -> println("Opción inválida.")
        }
    }
}

private fun registrarPedido(productoService: ProductoService, pedidoService: PedidoService) {
    ejecutarSeguro {
        println("\n--- REGISTRAR PEDIDO ---")
        print("Nombre del cliente: ")
        val nombre = Validador.textoNoVacio(readln(), "Nombre")
        print("Teléfono: ")
        val telefono = Validador.textoNoVacio(readln(), "Teléfono")
        print("Dirección: ")
        val direccion = Validador.textoNoVacio(readln(), "Dirección")
        val cliente = Cliente(nombre, telefono, direccion)

        val detalles = mutableListOf<DetallePedido>()
        var agregar = true
        while (agregar) {
            val disponibles = productoService.disponibles()
            mostrarProductos(disponibles)
            print("ID del producto: ")
            val id = Validador.enteroPositivo(readln(), "ID")
            val producto = productoService.buscarPorId(id)
                ?: throw IllegalArgumentException("Producto no encontrado.")
            if (!producto.disponible) throw IllegalArgumentException("El producto no está disponible.")

            print("Cantidad: ")
            val cantidad = Validador.enteroPositivo(readln(), "Cantidad")
            val existente = detalles.find { it.producto.id == producto.id }
            if (existente != null) existente.cantidad += cantidad
            else detalles.add(DetallePedido(producto, cantidad))

            print("¿Agregar otro producto? (s/n): ")
            agregar = readln().trim().equals("s", ignoreCase = true)
        }

        val pedido = pedidoService.crear(cliente, detalles)
        println("\nPedido #${pedido.id} registrado.")
        pedido.detalles.forEach {
            println("- ${it.producto.nombre} x${it.cantidad}: $${"%.2f".format(it.calcularSubtotal())}")
        }
        println("Total: $${"%.2f".format(pedido.calcularTotal())}")

        val subtotalPan = pedido.totalCategoria(Categoria.PAN)
        if (subtotalPan in 0.01..<2.00) {
            println("Aviso: el subtotal de pan es $${"%.2f".format(subtotalPan)}; la política de mínimo aún puede ajustarse.")
        }
    }
}

private fun consultarPedidos(pedidoService: PedidoService) {
    val pedidos = pedidoService.listar()
    if (pedidos.isEmpty()) {
        println("No hay pedidos registrados.")
        return
    }

    println("\n--- PEDIDOS ---")
    pedidos.forEach { pedido ->
        println("#${pedido.id} | ${pedido.cliente.nombre} | ${pedido.estado} | $${"%.2f".format(pedido.calcularTotal())}")
    }
}

private fun actualizarEstado(pedidoService: PedidoService) {
    ejecutarSeguro {
        consultarPedidos(pedidoService)
        if (pedidoService.listar().isEmpty()) return@ejecutarSeguro

        print("ID del pedido: ")
        val id = Validador.enteroPositivo(readln(), "ID")
        println("1. RECIBIDO")
        println("2. EN_PREPARACION")
        println("3. EN_CAMINO")
        println("4. ENTREGADO")
        print("Nuevo estado: ")
        val estado = when (readln().trim()) {
            "1" -> EstadoPedido.RECIBIDO
            "2" -> EstadoPedido.EN_PREPARACION
            "3" -> EstadoPedido.EN_CAMINO
            "4" -> EstadoPedido.ENTREGADO
            else -> throw IllegalArgumentException("Estado inválido.")
        }

        if (pedidoService.actualizarEstado(id, estado)) println("Estado actualizado.")
        else println("Pedido no encontrado.")
    }
}

private fun leerCategoria(): Categoria {
    println("Categoría:")
    println("1. PAN")
    println("2. REPOSTERIA")
    println("3. DESAYUNO")
    println("4. CENA")
    print("Opción: ")
    return when (readln().trim()) {
        "1" -> Categoria.PAN
        "2" -> Categoria.REPOSTERIA
        "3" -> Categoria.DESAYUNO
        "4" -> Categoria.CENA
        else -> throw IllegalArgumentException("Categoría inválida.")
    }
}

private fun mostrarProductos(productos: List<panaderiarosario.model.Producto>) {
    if (productos.isEmpty()) {
        println("No hay productos para mostrar.")
        return
    }
    println("\nID | Producto | Categoría | Precio | Disponible")
    productos.forEach {
        println("${it.id} | ${it.nombre} | ${it.categoria} | $${"%.2f".format(it.precio)} | ${if (it.disponible) "Sí" else "No"}")
    }
}

private inline fun ejecutarSeguro(bloque: () -> Unit) {
    try {
        bloque()
    } catch (e: Exception) {
        val mensaje = e.message ?: "Error desconocido"
        Logger.registrarError(mensaje, e)
        println("Error: $mensaje")
    }
}

// NUEVO MÓDULO: Gestión de Clientes
private fun menuClientes(clienteService: panaderiarosario.service.ClienteService) {
    var volver = false
    while (!volver) {
        println("\n--- GESTIÓN DE CLIENTES ---")
        println("1. Listar clientes")
        println("2. Registrar cliente")
        println("3. Buscar cliente")
        println("4. Eliminar cliente")
        println("5. Regresar")
        print("Opción: ")

        when (readlnOrNull()?.trim()) {
            "1" -> {
                val clientes = clienteService.listarClientes()
                if (clientes.isEmpty()) println("No hay clientes registrados.")
                else clientes.forEach { println("Nombre: ${it.nombre} | Tel: ${it.telefono} | Dir: ${it.direccion}") }
            }
            "2" -> ejecutarSeguro {
                print("Nombre: ")
                val nombre = panaderiarosario.util.Validador.textoNoVacio(readln(), "Nombre")
                print("Teléfono: ")
                val telefono = panaderiarosario.util.Validador.textoNoVacio(readln(), "Teléfono")
                print("Dirección: ")
                val direccion = panaderiarosario.util.Validador.textoNoVacio(readln(), "Dirección")
                clienteService.registrarCliente(nombre, telefono, direccion)
                println("✅ Cliente registrado exitosamente.")
            }
            "3" -> ejecutarSeguro {
                print("Teléfono a buscar: ")
                val tel = panaderiarosario.util.Validador.textoNoVacio(readln(), "Teléfono")
                val cliente = clienteService.buscarPorTelefono(tel)
                if (cliente != null) println("Encontrado - Nombre: ${cliente.nombre} | Dirección: ${cliente.direccion}")
                else println("❌ Cliente no encontrado.")
            }
            "4" -> ejecutarSeguro {
                print("Teléfono a eliminar: ")
                val tel = panaderiarosario.util.Validador.textoNoVacio(readln(), "Teléfono")
                if (clienteService.eliminarCliente(tel)) println("✅ Cliente eliminado.")
                else println("❌ Cliente no encontrado.")
            }
            "5" -> volver = true
            else -> println("Opción inválida.")
        }
    }
}