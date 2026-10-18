package panaderiarosario.data

import panaderiarosario.model.Categoria
import panaderiarosario.model.Producto

object DatosIniciales {
    fun productos(): MutableList<Producto> = mutableListOf(
        Producto(1, "Redondo", Categoria.PAN, 0.12, 70),
        Producto(2, "Largo", Categoria.PAN, 0.12, 70),
        Producto(3, "Indio", Categoria.PAN, 0.15, 70),
        Producto(4, "Cheesecake de fresa", Categoria.REPOSTERIA, 3.50, 20),
        Producto(5, "Desayuno completo", Categoria.DESAYUNO, 4.00, 20),
        Producto(6, "Cena del día", Categoria.CENA, 5.00, 20)
    )
}
