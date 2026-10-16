package panaderiarosario.model

abstract class Usuario(
    open val nombre: String,
    open val telefono: String
)

class Cliente(
    override val nombre: String,
    override val telefono: String,
    val direccion: String
) : Usuario(nombre, telefono)

class Administrador(
    override val nombre: String,
    override val telefono: String
) : Usuario(nombre, telefono)
