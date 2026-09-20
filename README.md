# Universidad Don Bosco Dirección de Educación a Distancia
## Proyecto de Cátedra Etapa 2: Desarrollo del Proyecto
## Estudiantes:
`Christian Geovanni Centeno, CS241743`   
`José Alexander Montoya, MQ252529`
`Félix Gabriel Quintanilla, QR230082`

# Panadería Rosario - Desarrollo Técnico

## Descripción del proyecto

Este proyecto corresponde a la Etapa 2 del desarrollo del sistema para **Panadería Rosario**.

El sistema fue desarrollado en **Kotlin en modo consola** y representa la lógica principal necesaria para gestionar productos, clientes, pedidos, estados de pedidos y reportes.

El objetivo de esta etapa es implementar el núcleo funcional del sistema utilizando Programación Orientada a Objetos (POO), colecciones, validaciones, manejo de excepciones, generación de logs y actualización dinámica de la información durante la ejecución.

---

## Estructura principal

El código se encuentra organizado principalmente en los siguientes paquetes:

```text
panaderiarosario
│
├── data
│   └── DatosIniciales.kt
│
├── exception
│   └── ProductoNoDisponibleException.kt
│
├── model
│   ├── Calculable.kt
│   ├── Categoria.kt
│   ├── DetallePedido.kt
│   ├── EstadoPedido.kt
│   ├── Pedido.kt
│   ├── Producto.kt
│   └── Usuario.kt
│
├── service
│   ├── ClienteService.kt
│   ├── MantenimientoBase.kt
│   ├── PedidoService.kt
│   ├── ProductoService.kt
│   └── ReporteService.kt
│
├── util
│   ├── Logger.kt
│   └── Validador.kt
│
└── Main.kt
```

---

# Funcionamiento principal

## Main.kt

`Main.kt` es el punto de entrada del programa.

Su función principal es controlar la interacción del usuario mediante la consola y conectar los diferentes servicios del sistema.

Desde este archivo se permite:

- iniciar sesión como administrador;
- ingresar o registrar un cliente;
- acceder al menú administrativo;
- acceder al menú del cliente;
- administrar productos;
- administrar clientes;
- crear pedidos;
- consultar pedidos;
- actualizar estados;
- generar reportes;
- controlar errores durante la ejecución.

El administrador utiliza una ruta de acceso propia antes de ingresar a las funciones administrativas.

Los clientes pueden identificarse mediante su número de teléfono. Si el cliente todavía no existe, el sistema permite registrarlo durante la ejecución.

---

# Gestión de productos

La lógica relacionada con los productos se encuentra principalmente en:

```text
service/ProductoService.kt
```

La clase `ProductoService` administra una colección de objetos `Producto`.

Entre sus funciones principales se encuentran:

### listar()

Devuelve la lista de productos registrados.

```kotlin
fun listar(): List<Producto>
```

### disponibles()

Obtiene únicamente los productos que se encuentran disponibles para venta.

### buscarPorId()

Busca un producto utilizando su identificador.

```kotlin
fun buscarPorId(id: Int): Producto?
```

### buscarPorCategoria()

Permite filtrar los productos según su categoría.

### crear()

Registra un nuevo producto en el sistema.

### actualizar()

Modifica los datos de un producto existente.

### cambiarDisponibilidad()

Permite habilitar o deshabilitar un producto para la venta.

### eliminar()

Elimina un producto según su identificador.

Estas funciones permiten implementar el CRUD de productos requerido por el proyecto.

---

# Gestión de clientes

La gestión de clientes se encuentra en:

```text
service/ClienteService.kt
```

`ClienteService` utiliza una colección mutable para almacenar los clientes registrados durante la ejecución.

Entre sus operaciones principales se encuentran:

### registrarCliente()

Crea y registra un nuevo cliente.

```kotlin
fun registrarCliente(
    nombre: String,
    telefono: String,
    direccion: String
): Cliente
```

Antes de registrar el cliente se verifica que el teléfono no se encuentre previamente registrado.

### listar()

Devuelve todos los clientes registrados.

```kotlin
fun listar(): List<Cliente>
```

### buscarPorTelefono()

Permite localizar a un cliente utilizando su número de teléfono.

```kotlin
fun buscarPorTelefono(telefono: String): Cliente?
```

### eliminar()

Permite eliminar un cliente utilizando su teléfono como identificador.

---

# Gestión y procesamiento de pedidos

La lógica principal de los pedidos se encuentra en:

```text
service/PedidoService.kt
```

Esta clase representa uno de los módulos principales de procesamiento del sistema.

## crear()

Permite registrar un nuevo pedido asociado a un cliente.

Antes de guardar el pedido se realizan validaciones como:

- el pedido debe contener productos;
- las cantidades deben ser mayores que cero;
- los productos seleccionados deben encontrarse disponibles.

El sistema genera automáticamente el identificador del nuevo pedido.

## buscarPorId()

Localiza un pedido utilizando su identificador.

## listar()

Devuelve los pedidos registrados durante la ejecución.

## actualizarEstado()

Controla el avance de un pedido.

El flujo permitido es:

```text
RECIBIDO
   ↓
EN_PREPARACION
   ↓
EN_CAMINO
   ↓
ENTREGADO
```

El sistema impide saltar estados.

Por ejemplo:

```text
RECIBIDO → ENTREGADO
```

es considerado un cambio inválido.

También se impide modificar un pedido que ya se encuentra entregado.

---

# Detalle y cálculo de pedidos

La clase:

```text
model/DetallePedido.kt
```

representa la relación entre un producto y la cantidad solicitada.

Cada pedido puede contener múltiples detalles.

La clase:

```text
model/Pedido.kt
```

representa el pedido completo y contiene información como:

- identificador;
- cliente;
- productos solicitados;
- cantidades;
- estado;
- cálculos relacionados con el pedido.

Los subtotales de los productos permiten obtener el total general del pedido.

---

# Reportes

La lógica de reportes se encuentra en:

```text
service/ReporteService.kt
```

El método principal es:

```kotlin
generarResumen()
```

Este genera un resumen con información como:

- productos registrados;
- productos disponibles;
- pedidos registrados;
- pedidos recibidos;
- pedidos en preparación;
- pedidos en camino;
- pedidos entregados;
- ventas acumuladas;
- producto más vendido.

Ejemplo:

```text
REPORTE PANADERÍA ROSARIO

Productos registrados: 6
Productos disponibles: 5
Pedidos registrados: 1
Recibidos: 0
En preparación: 1
En camino: 0
Entregados: 0
Ventas acumuladas entregadas: $0.00
Producto más vendido: Donas Rellenas (3 unidades)
```

El sistema también permite exportar el reporte mediante:

```kotlin
exportarReporte()
```

generando el archivo:

```text
reporte_ventas.txt
```

---

# Validaciones

Las validaciones generales se encuentran en:

```text
util/Validador.kt
```

Esta clase centraliza comprobaciones utilizadas en diferentes módulos.

Entre sus funciones se encuentran:

```kotlin
textoNoVacio()
enteroPositivo()
decimalPositivo()
```

Estas funciones evitan que el sistema procese información inválida.

Por ejemplo:

- textos vacíos;
- cantidades iguales o menores que cero;
- valores monetarios inválidos.

Cuando una validación falla se genera una excepción que puede ser controlada por el sistema.

---

# Manejo de errores y logs

El sistema cuenta con registro de errores mediante:

```text
util/Logger.kt
```

El logger permite almacenar información sobre los problemas ocurridos durante la ejecución.

Los logs diarios son almacenados dentro de:

```text
logs/
```

Además, los errores son registrados en:

```text
errores.txt
```

Los registros pueden incluir:

- fecha;
- hora;
- nivel del evento;
- mensaje;
- excepción producida;
- información relacionada con el error.

Esto permite mantener evidencia de los errores sin detener completamente la ejecución del programa.

---

# Excepciones

El proyecto utiliza excepciones estándar de Kotlin y excepciones personalizadas.

Un ejemplo es:

```text
exception/ProductoNoDisponibleException.kt
```

Esta excepción representa situaciones en las que se intenta utilizar un producto que no se encuentra disponible.

También se utilizan validaciones mediante:

```kotlin
require(...)
```

para impedir que determinadas operaciones continúen con información inválida.

---

# Programación Orientada a Objetos

El proyecto utiliza diferentes principios de Programación Orientada a Objetos.

## Clases y objetos

El sistema representa las entidades principales mediante clases como:

```text
Producto
Pedido
DetallePedido
Cliente
Administrador
```

Durante la ejecución se crean objetos de estas clases para representar información real del sistema.

---

## Herencia

La herencia se utiliza en:

```text
model/Usuario.kt
```

Existe una clase base:

```kotlin
Usuario
```

de la cual se derivan tipos específicos de usuario:

```text
Usuario
├── Cliente
└── Administrador
```

Esto permite compartir características comunes entre diferentes tipos de usuario y agregar comportamiento específico cuando sea necesario.

---

## Interfaces

El proyecto utiliza interfaces para establecer contratos que deben cumplir determinadas clases.

### Calculable

La interfaz:

```text
model/Calculable.kt
```

define operaciones relacionadas con cálculos del sistema.

`Pedido` implementa esta interfaz para realizar los cálculos relacionados con los importes del pedido.

### MantenimientoBase

La interfaz:

```text
service/MantenimientoBase.kt
```

establece operaciones comunes para servicios de mantenimiento.

Su estructura incluye:

```kotlin
interface MantenimientoBase<T, ID> {
    fun listar(): List<T>
    fun eliminar(id: ID): Boolean
}
```

Esta interfaz utiliza **genéricos**, permitiendo reutilizar el mismo contrato con diferentes tipos de objetos e identificadores.

Por ejemplo:

```kotlin
ClienteService : MantenimientoBase<Cliente, String>
```

y:

```kotlin
ProductoService : MantenimientoBase<Producto, Int>
```

De esta manera ambos servicios comparten operaciones comunes, aunque trabajen con diferentes clases y tipos de identificadores.

---

# Encapsulamiento y separación de responsabilidades

El sistema separa la lógica en diferentes tipos de clases.

Las clases ubicadas en:

```text
model/
```

representan los datos y entidades del negocio.

Las clases ubicadas en:

```text
service/
```

contienen la lógica de negocio y operaciones principales.

Las clases ubicadas en:

```text
util/
```

contienen funciones reutilizables como validaciones y registro de errores.

Finalmente:

```text
Main.kt
```

se encarga principalmente de la interacción mediante consola y de coordinar los servicios.

Esta organización evita concentrar toda la lógica del programa en un único archivo.

---

# Colecciones

El sistema utiliza colecciones de Kotlin para mantener los datos durante la ejecución.

Principalmente se utilizan:

```kotlin
mutableListOf()
```

para almacenar dinámicamente:

- productos;
- clientes;
- pedidos;
- detalles de pedidos.

Esto permite agregar, buscar, modificar y eliminar información mientras el programa se encuentra en ejecución.

---

# Actualización dinámica de información

Los datos cambian dinámicamente durante la ejecución.

Por ejemplo, al:

- registrar un producto;
- eliminar un producto;
- registrar un cliente;
- crear un pedido;
- cambiar el estado de un pedido;

las colecciones internas son modificadas inmediatamente.

Los reportes utilizan la información actual de estas colecciones, por lo que reflejan el estado actual del sistema.

---

# Tecnologías utilizadas

- Kotlin
- JVM
- JDK 17
- Gradle
- IntelliJ IDEA
- Git
- GitHub

---

# Compilación del proyecto

El proyecto puede compilarse utilizando el Gradle Wrapper:

```powershell
.\gradlew.bat clean build
```

Una compilación correcta genera:

```text
BUILD SUCCESSFUL
```

---

# Control de versiones

El proyecto utiliza Git y GitHub para el control de versiones.

Repositorio:

```text
https://github.com/GabrielQuintanilla94/Panaderia
```

El desarrollo fue realizado mediante diferentes ramas de trabajo y posteriormente integrado a la rama principal mediante GitHub.

---

# Conclusión

La Etapa 2 de Panadería Rosario implementa el núcleo funcional del sistema mediante una aplicación de consola desarrollada en Kotlin.

La solución permite administrar productos y clientes, procesar pedidos, controlar el flujo de estados, generar reportes, validar datos y registrar errores.

Además, el proyecto aplica Programación Orientada a Objetos mediante clases, objetos, herencia, interfaces y genéricos, manteniendo separadas las entidades, la lógica de negocio, las validaciones y la interacción con el usuario.
