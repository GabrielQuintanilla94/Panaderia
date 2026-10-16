package panaderiarosario.service

// REQUERIMIENTO DE RÚBRICA: Uso de Interfaces en POO
interface MantenimientoBase<T, ID> {
    fun listar(): List<T>
    fun eliminar(id: ID): Boolean
}