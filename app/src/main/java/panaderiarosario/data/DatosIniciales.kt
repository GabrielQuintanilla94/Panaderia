package panaderiarosario.data

import panaderiarosario.model.Categoria
import panaderiarosario.model.Producto

object DatosIniciales {
    fun productos(): MutableList<Producto> = mutableListOf(
        Producto(1, "Redondo", Categoria.PAN, 0.12, 70,
            "Pan francés redondo de corteza dorada y miga suave, horneado cada mañana. Ideal para el desayuno o para acompañar la cena."),
        Producto(2, "Largo", Categoria.PAN, 0.12, 70,
            "Pan francés alargado, crujiente por fuera y tierno por dentro. Perfecto para panes con frijoles, pollo o lo que se te antoje."),
        Producto(3, "Indio", Categoria.PAN, 0.15, 70,
            "Pan dulce tradicional salvadoreño, esponjoso y con un toque de canela. El compañero ideal del café de la tarde."),
        Producto(4, "Cheesecake de fresa", Categoria.REPOSTERIA, 3.50, 20,
            "Porción de cheesecake cremoso sobre base de galleta, cubierto con mermelada de fresas naturales."),
        Producto(5, "Desayuno completo", Categoria.DESAYUNO, 4.00, 20,
            "Huevos al gusto, frijoles, plátano frito, crema, queso fresco y pan recién horneado. Incluye café."),
        Producto(6, "Cena del día", Categoria.CENA, 5.00, 20,
            "Plato fuerte preparado por la casa con acompañamientos del día y pan. Consulta la especialidad al recibir tu pedido.")
    )

    /** Descripción por defecto de un producto inicial (para datos guardados antes de existir el campo). */
    fun descripcion(id: Int): String = productos().find { it.id == id }?.descripcion ?: ""
}
