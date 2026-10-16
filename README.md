# Panadería Rosario

Aplicación móvil Android desarrollada en **Kotlin** con **Jetpack Compose** para digitalizar la gestión de pedidos y parte de la operación diaria de Panadería Rosario.

El proyecto evoluciona el núcleo funcional previamente desarrollado en Kotlin hacia una aplicación Android con interfaz gráfica, navegación entre pantallas y flujos diferenciados para clientes y administración.

---

## Objetivo de la aplicación

La aplicación busca centralizar en un solo sistema las principales operaciones relacionadas con la venta y seguimiento de pedidos de Panadería Rosario.

Desde el punto de vista del cliente, la aplicación pretende permitir consultar los productos disponibles, registrarse, iniciar sesión, seleccionar productos, preparar un carrito, confirmar pedidos y consultar el avance de sus pedidos.

Desde el punto de vista administrativo, permite gestionar productos, controlar la disponibilidad del catálogo, revisar pedidos, avanzar su estado y consultar un resumen general de la operación.

---

## Estado actual

La aplicación ya cuenta con una estructura Android funcional basada en una sola actividad (`MainActivity`) y pantallas construidas con Jetpack Compose.

Actualmente se encuentran implementados los siguientes flujos:

### Cliente

- Inicio de sesión mediante número de teléfono.
- Registro de nuevos clientes.
- Validación de datos requeridos.
- Consulta del catálogo de productos disponibles.
- Filtro de productos por categoría.
- Agregado de productos al carrito.
- Aumento y disminución de cantidades.
- Cálculo automático del total del carrito.
- Carrito y confirmación de pedidos.
- Captura de datos de delivery: receptor, teléfono, dirección y referencias.
- Consulta del historial de pedidos del cliente.
- Seguimiento del estado del pedido y de la entrega.

### Administrador

- Inicio de sesión administrativo.
- Acceso a un panel principal.
- Consulta del catálogo completo.
- Registro de nuevos productos con categoría y precio.
- Edición de nombre, categoría y precio.
- Cambio de disponibilidad de productos.
- Eliminación de productos.
- Consulta de pedidos registrados y sus datos de delivery.
- Actualización progresiva del estado del pedido hasta completar la entrega.
- Consulta de reportes operativos.
- Exportación del resumen de operación a un archivo de texto.

---

## Actualización incorporada en esta versión

La principal actualización del proyecto es la incorporación de una **capa de presentación Android** sobre la lógica de negocio existente.

La interacción que antes dependía de consola fue sustituida por una interfaz móvil con Jetpack Compose.

La aplicación ahora incorpora:

- `MainActivity` como punto de entrada de Android.
- Navegación entre pantallas mediante Navigation Compose.
- Pantallas independientes para autenticación, catálogo, carrito, pedidos y administración.
- Estado compartido de la aplicación mediante `AppState`.
- Actualización reactiva de la interfaz cuando cambian productos, pedidos o cantidades del carrito.
- Adaptación de la generación de reportes al almacenamiento privado de Android.
- Adaptación del registro de errores para trabajar con archivos internos de la aplicación y Logcat.

La lógica central de productos, clientes, pedidos, cálculos, validaciones y estados continúa separada de la interfaz gráfica, evitando concentrar el comportamiento del sistema dentro de las pantallas.

---

## Flujo general

### Flujo del cliente

```text
Inicio de sesión
      │
      ├── Cliente existente ─────────────┐
      │                                  │
      └── Registro de nuevo cliente      │
                                         ▼
                                      Catálogo
                                         │
                                         ▼
                                       Carrito
                                         │
                                         ▼
                                  Confirmar pedido
                                         │
                                         ▼
                                    Mis pedidos
                                         │
                                         ▼
                               Seguimiento del estado
```

### Flujo administrativo

```text
Inicio de sesión
      │
      ▼
Panel administrativo
      │
      ├── Gestión de productos
      │
      ├── Gestión de pedidos
      │
      └── Reportes
```

---

## Gestión de pedidos

Los pedidos constituyen una de las principales reglas de negocio de la aplicación.

Cada pedido:

- pertenece a un cliente;
- contiene uno o más detalles de producto;
- calcula su total a partir de cantidades y precios;
- comienza en estado `RECIBIDO`;
- avanza mediante una secuencia controlada.

El flujo permitido de estados es:

```text
RECIBIDO
   ↓
EN_PREPARACION
   ↓
EN_CAMINO
   ↓
ENTREGADO
```

No se permiten saltos arbitrarios entre estados. Por ejemplo, un pedido recibido no puede pasar directamente a entregado.

Esta validación se encuentra en `PedidoService`, por lo que la regla pertenece a la lógica del negocio y no únicamente a la interfaz.

---

## Gestión de productos

`ProductoService` concentra las operaciones relacionadas con el catálogo.

Entre las funciones disponibles se encuentran:

- listar productos;
- consultar productos disponibles;
- buscar un producto por identificador;
- buscar productos por categoría;
- crear productos;
- actualizar información;
- cambiar disponibilidad;
- eliminar productos.

El catálogo mostrado al cliente utiliza únicamente los productos marcados como disponibles.

---

## Clientes y autenticación

Los clientes son identificados mediante su número de teléfono.

El sistema permite:

- buscar clientes previamente registrados;
- registrar un cliente con nombre, teléfono y dirección;
- mantener al cliente autenticado durante el uso de la aplicación;
- asociar cada pedido con el cliente que lo creó.

La aplicación también mantiene un acceso administrativo independiente para las funciones de gestión.

> Las credenciales administrativas actuales son de prueba para esta etapa del desarrollo y no representan un mecanismo de autenticación definitivo para producción.

---

## Carrito de compras

El carrito es administrado desde `AppState` y mantiene temporalmente la selección del cliente durante la ejecución.

Permite:

- agregar productos;
- modificar cantidades;
- retirar un producto al reducir su cantidad a cero;
- calcular el total;
- convertir el contenido del carrito en un `Pedido`.

Al confirmar, los elementos seleccionados se transforman en objetos `DetallePedido` y son procesados por `PedidoService`.

---

## Reportes

`ReporteService` genera un resumen de la actividad registrada durante la ejecución.

Incluye:

- cantidad de productos registrados;
- productos disponibles;
- total de pedidos;
- cantidad de pedidos por estado;
- ventas acumuladas de pedidos entregados;
- producto con mayor cantidad de unidades solicitadas.

El reporte puede mostrarse dentro de la aplicación y exportarse como:

```text
reporte_ventas.txt
```

En Android, el archivo se guarda utilizando el almacenamiento privado de la aplicación.

---

## Validaciones y manejo de errores

Las validaciones se mantienen separadas de las pantallas siempre que corresponden a reglas reutilizables.

`Validador.kt` incluye comprobaciones para:

- texto obligatorio;
- números enteros positivos;
- valores decimales positivos.

`PedidoService` agrega validaciones propias del negocio, por ejemplo:

- no crear pedidos vacíos;
- no aceptar cantidades menores o iguales a cero;
- no aceptar productos no disponibles;
- impedir cambios inválidos de estado.

`Logger.kt` permite registrar errores y eventos en archivos internos de Android y también enviarlos a Logcat para facilitar la revisión durante el desarrollo.

---

## Programación Orientada a Objetos

El proyecto mantiene una organización orientada a objetos para representar las entidades y reglas del negocio.

### Herencia

`Usuario` funciona como clase base para:

```text
Usuario
├── Cliente
└── Administrador
```

De esta manera, ambos tipos comparten propiedades comunes y pueden especializarse según su función dentro del sistema.

### Interfaces

`Pedido` implementa la interfaz `Calculable`, utilizada para definir operaciones de cálculo.

También se utiliza `MantenimientoBase<T, ID>`, una interfaz genérica aplicada a servicios de mantenimiento, permitiendo reutilizar un contrato común con diferentes tipos de entidad e identificador.

### Encapsulamiento y responsabilidades

La aplicación distribuye responsabilidades en diferentes capas:

```text
model/
    Entidades y estructuras del negocio

service/
    Operaciones, reglas y procesamiento

util/
    Validaciones y registro de eventos

ui/
    Pantallas Jetpack Compose

AppState.kt
    Estado compartido y coordinación entre interfaz y servicios

MainActivity.kt
    Punto de entrada de la aplicación Android
```

Esta separación permite modificar la interfaz sin tener que reescribir las reglas principales del negocio.

---

## Estructura del proyecto

```text
PanaderiaRosarioEtapa3_Android/
│
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/panaderiarosario/
│       │   ├── MainActivity.kt
│       │   ├── AppState.kt
│       │   │
│       │   ├── data/
│       │   │   └── DatosIniciales.kt
│       │   │
│       │   ├── exception/
│       │   │   └── ProductoNoDisponibleException.kt
│       │   │
│       │   ├── model/
│       │   │   ├── Calculable.kt
│       │   │   ├── Categoria.kt
│       │   │   ├── DetallePedido.kt
│       │   │   ├── EstadoPedido.kt
│       │   │   ├── Pedido.kt
│       │   │   ├── Producto.kt
│       │   │   └── Usuario.kt
│       │   │
│       │   ├── service/
│       │   │   ├── ClienteService.kt
│       │   │   ├── MantenimientoBase.kt
│       │   │   ├── PedidoService.kt
│       │   │   ├── ProductoService.kt
│       │   │   └── ReporteService.kt
│       │   │
│       │   ├── util/
│       │   │   ├── Logger.kt
│       │   │   └── Validador.kt
│       │   │
│       │   └── ui/
│       │       ├── auth/
│       │       ├── cliente/
│       │       ├── admin/
│       │       └── theme/
│       │
│       └── res/
│
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew
└── gradlew.bat
```

---

## Tecnologías

- Kotlin
- Android SDK
- Jetpack Compose
- Material 3
- Navigation Compose
- Gradle
- JDK 17
- Android Studio

Configuración actual:

```text
Kotlin: 2.0.21
Gradle: 8.10.2
Android Gradle Plugin: 8.7.3
compileSdk: 35
targetSdk: 35
minSdk: 24
JDK: 17
```

---

## Persistencia actual

En esta versión, productos, clientes, carrito y pedidos se mantienen principalmente **en memoria durante la ejecución de la aplicación**.

Esto significa que el proyecto ya implementa la lógica y los flujos funcionales, pero una futura ampliación puede incorporar persistencia permanente mediante una base de datos local o un servicio remoto.

La exportación del reporte y los archivos de log sí utilizan almacenamiento interno de Android.

---

---

## Ejecución en Android Studio

Para ejecutar esta versión no es necesario modificar manualmente la configuración del código ni crear una nueva configuración de ejecución si el proyecto se abre correctamente como proyecto Android.

### Si ya se tenía abierta una versión anterior del proyecto

Después de reemplazar o actualizar los archivos con esta versión:

1. Cerrar el proyecto anterior en Android Studio.
2. Abrir directamente la carpeta raíz:

```text
PanaderiaRosarioEtapa3_Android
```

No se debe abrir la carpeta padre que contiene el proyecto ni el archivo `.zip`.

Cuando el proyecto se abre correctamente, Android Studio debe mostrar el módulo:

```text
app
```

con una estructura similar a:

```text
app
├── manifests
├── kotlin+java
├── res
└── Gradle Scripts
```

3. Esperar a que Android Studio termine la sincronización de Gradle.
4. Verificar que se esté utilizando **JDK 17**.
5. Verificar que esté instalado el **Android SDK correspondiente a compileSdk 35**.
6. Seleccionar un emulador Android o un dispositivo físico.
7. Ejecutar la configuración `app` con el botón **Run**.

### Importante sobre la configuración `app`

No es necesario crear manualmente una configuración `Android App`.

Si Android Studio muestra:

```text
<no module>
```

en `Run/Debug Configurations`, normalmente significa que el proyecto fue abierto desde una carpeta incorrecta o que el módulo todavía no fue importado por Gradle.

La solución es cerrar el proyecto y volver a abrir directamente:

```text
PanaderiaRosarioEtapa3_Android
```

Una vez importado correctamente, Android Studio reconoce automáticamente el módulo `app`.

### Verificación por terminal

Desde la carpeta raíz del proyecto se puede comprobar que Gradle reconoce el módulo Android con:

```powershell
.\gradlew.bat projects
```

El resultado debe incluir:

```text
Project ':app'
```

También puede comprobarse que la aplicación compila con:

```powershell
.\gradlew.bat :app:assembleDebug
```

Si la compilación es correcta aparecerá:

```text
BUILD SUCCESSFUL
```

El APK de depuración se genera en:

```text
app\build\outputs\apk\debug\app-debug.apk
```

### Emulador

Para probar la aplicación puede utilizarse un dispositivo virtual desde **Device Manager**.

En Windows, el emulador requiere que la virtualización esté habilitada. Dependiendo de la configuración del equipo, puede ser necesario habilitar:

```text
Windows Hypervisor Platform
Virtual Machine Platform
```

Después de habilitarlas puede ser necesario reiniciar Windows.

También puede utilizarse un dispositivo Android físico mediante depuración USB.

### Archivos locales

`local.properties` no debe subirse al repositorio. Android Studio lo genera localmente para indicar la ruta del Android SDK instalada en cada equipo.

Tampoco deben versionarse carpetas generadas como:

```text
.gradle/
.idea/
build/
app/build/
```

## Propósito de esta etapa

Esta versión tiene como objetivo transformar el núcleo funcional de Panadería Rosario en una aplicación Android navegable y utilizable mediante una interfaz gráfica.

El enfoque actual es consolidar:

- autenticación;
- navegación;
- catálogo;
- carrito;
- procesamiento de pedidos;
- seguimiento;
- administración;
- validaciones;
- reportes.

La estructura deja preparado el proyecto para continuar incorporando persistencia de datos, mejoras visuales y servicios adicionales sin reemplazar la lógica principal del sistema.
