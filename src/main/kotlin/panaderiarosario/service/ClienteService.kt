package panaderiarosario.service

import panaderiarosario.model.Cliente

class ClienteService : MantenimientoBase<Cliente, String> {

    // REQUERIMIENTO DE RÚBRICA: Manejo de colecciones para gestionar los datos

    private val clientes = mutableListOf<Cliente>()

    fun registrarCliente(nombre: String, telefono: String, direccion: String): Cliente {
        require(buscarPorTelefono(telefono) == null) { "Ya existe un cliente registrado con el teléfono $telefono." }
        val nuevoCliente = Cliente(nombre, telefono, direccion)

        clientes.add(nuevoCliente)

        return nuevoCliente

    }

    override fun listar(): List<Cliente> {

        return clientes.toList() // Retorna una copia de solo lectura

    }

    fun buscarPorTelefono(telefono: String): Cliente? {

        return clientes.find { it.telefono == telefono }

    }

    override fun eliminar(id: String): Boolean {

        // Elimina al cliente si el teléfono (que usamos como ID) coincide y retorna true si tuvo éxito

        return clientes.removeIf { it.telefono == id }

    }

}
