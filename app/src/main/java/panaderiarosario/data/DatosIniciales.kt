package panaderiarosario.data

import panaderiarosario.model.Categoria
import panaderiarosario.model.Producto

object DatosIniciales {
    fun productos(): MutableList<Producto> = mutableListOf(
        Producto(1, "Redondo", Categoria.PAN, 0.12, true),
        Producto(2, "Largo", Categoria.PAN, 0.12, true),
        Producto(3, "Indio", Categoria.PAN, 0.15, true),
        Producto(4, "Cheesecake de fresa", Categoria.REPOSTERIA, 3.50, true),
        Producto(5, "Desayuno completo", Categoria.DESAYUNO, 4.00, true),
        Producto(6, "Cena del día", Categoria.CENA, 5.00, true)
    )
}
