# Panadería Rosario - Etapa 2

Proyecto académico en **Kotlin/JVM** y modo consola para la Etapa 2 de Desarrollo de Software para Móviles.

El proyecto usa **Gradle** como sistema de construcción. Kotlin sigue siendo el lenguaje de programación principal; Gradle solamente administra la compilación, ejecución y dependencias del proyecto.

## 1. Tecnologías y programas necesarios

Para trabajar con este proyecto se recomienda utilizar **IntelliJ IDEA** como entorno de desarrollo. El proyecto está configurado para **Kotlin/JVM** y utiliza **Gradle** como sistema de construcción.

### Requisitos

| Componente | Versión / recomendación | Enlace oficial |
|---|---|---|
| IntelliJ IDEA | Versión actual para Windows. La edición gratuita es suficiente para este proyecto. | https://www.jetbrains.com/idea/download/ |
| JDK | **JDK 17** o superior. El proyecto fue probado con JDK 17. | https://adoptium.net/temurin/releases/?version=17 |
| Gradle | **Gradle 8.10.2** para generar inicialmente el Wrapper. | https://gradle.org/releases/ |
| Kotlin | No se instala manualmente. El proyecto usa el plugin Kotlin/JVM **2.0.21** desde Gradle. | https://kotlinlang.org/docs/gradle-configure-project.html |

### Configuración utilizada por el proyecto

- **Lenguaje:** Kotlin/JVM.
- **Plugin de Kotlin para Gradle:** `2.0.21`.
- **Gradle Wrapper objetivo:** `8.10.2`.
- **JDK recomendado:** `17`.
- **Tipo de aplicación:** consola.
- **Clase principal:** `panaderiarosario.MainKt`.
- **Repositorio de dependencias:** Maven Central.
- **IDE recomendado y utilizado:** **IntelliJ IDEA**.

> **Importante:** no es necesario instalar `kotlinc` de forma global. Gradle descarga y utiliza el compilador de Kotlin indicado en `build.gradle.kts`.

> **Nota sobre IntelliJ IDEA:** se recomienda utilizar una versión reciente. Versiones antiguas del IDE pueden mostrar avisos como `unsupported binary format` con librerías de Kotlin nuevas, aunque el proyecto llegue a compilar y ejecutar correctamente.

## 2. Archivos de configuración

### `settings.gradle.kts`

Define el nombre del proyecto Gradle:

```kotlin
rootProject.name = "PanaderiaRosarioEtapa2"
```

### `build.gradle.kts`

Configura Kotlin y la aplicación de consola:

```kotlin
plugins {
    kotlin("jvm") version "2.0.21"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("panaderiarosario.MainKt")
}

tasks.test {
    useJUnitPlatform()
}
```

### `gradle.properties`

Define opciones generales de ejecución de Gradle y el estilo oficial de Kotlin:

```properties
org.gradle.jvmargs=-Xmx1024m -Dfile.encoding=UTF-8
kotlin.code.style=official
```

## 3. Estructura del proyecto

La estructura utiliza el formato estándar de un proyecto Kotlin con Gradle:

```text
PanaderiaRosarioEtapa2/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
├── gradle/
│   └── wrapper/
├── README.md
├── .gitignore
└── src/
    └── main/
        └── kotlin/
            └── panaderiarosario/
                ├── Main.kt
                ├── data/
                │   └── DatosIniciales.kt
                ├── model/
                │   ├── Calculable.kt
                │   ├── Categoria.kt
                │   ├── DetallePedido.kt
                │   ├── EstadoPedido.kt
                │   ├── Pedido.kt
                │   ├── Producto.kt
                │   └── Usuario.kt
                ├── service/
                │   ├── PedidoService.kt
                │   ├── ProductoService.kt
                │   └── ReporteService.kt
                └── util/
                    ├── Logger.kt
                    └── Validador.kt
```

## 4. Instalación y preparación del entorno

### 4.1 Instalar IntelliJ IDEA

Descargar IntelliJ IDEA desde la página oficial:

https://www.jetbrains.com/idea/download/

La edición gratuita es suficiente para abrir, compilar y ejecutar este proyecto Kotlin/JVM.

Durante la instalación pueden dejarse las opciones predeterminadas. Después de instalarlo, abrir IntelliJ IDEA y seleccionar **Open** para cargar la carpeta raíz del proyecto `PanaderiaRosarioEtapa2`.

### 4.2 JDK 17

El proyecto requiere un JDK compatible con Gradle y Kotlin. Se recomienda **JDK 17**.

Descarga recomendada de Eclipse Temurin 17:

https://adoptium.net/temurin/releases/?version=17

Si JDK 17 ya está instalado, no es necesario instalar otro.

Para verificarlo desde PowerShell:

```powershell
java -version
```

En IntelliJ IDEA comprobar también:

**File > Project Structure > Project > SDK > JDK 17**

### 4.3 Instalar Gradle 8.10.2 solamente para generar el Wrapper

Gradle puede descargarse desde:

https://gradle.org/releases/

Buscar **Gradle 8.10.2** y descargar la distribución **binary-only** (`gradle-8.10.2-bin.zip`).

En Windows se recomienda descomprimirlo, por ejemplo, en:

```text
C:\Gradle\gradle-8.10.2
```

Después agregar al `Path` de Windows la carpeta `bin`:

```text
C:\Gradle\gradle-8.10.2\bin
```

Ruta de Windows en inglés:

**Start > Edit the system environment variables > Advanced > Environment Variables > System variables > Path > Edit > New**

Después cerrar y volver a abrir IntelliJ IDEA o PowerShell y comprobar:

```powershell
gradle --version
```

La salida debe mostrar Gradle y una JVM 17.

### 4.4 Generar el Gradle Wrapper

Este paso se realiza una sola vez si el proyecto todavía no contiene `gradlew`, `gradlew.bat` y `gradle/wrapper/`.

Desde la raíz del proyecto ejecutar:

```powershell
gradle wrapper --gradle-version 8.10.2
```

Esto genera:

```text
gradlew
gradlew.bat
gradle/wrapper/gradle-wrapper.jar
gradle/wrapper/gradle-wrapper.properties
```

Una vez generado el Wrapper, los integrantes del grupo **no necesitan instalar Gradle globalmente** para ejecutar el proyecto. Deben utilizar `gradlew` / `gradlew.bat`.

## 5. Abrir el proyecto en IntelliJ IDEA

1. Abrir **IntelliJ IDEA**.
2. Seleccionar **Open**.
3. Elegir la carpeta raíz `PanaderiaRosarioEtapa2`.
4. IntelliJ detectará `build.gradle.kts` y realizará la sincronización de Gradle.
5. Esperar a que termine la descarga inicial de dependencias de Kotlin y Gradle.
6. Verificar que el SDK seleccionado sea **JDK 17**.
7. Abrir:

```text
src/main/kotlin/panaderiarosario/Main.kt
```

8. Ejecutar el programa desde el triángulo verde junto a:

```kotlin
fun main() {
```

También puede hacerse clic derecho sobre `Main.kt` y elegir **Run 'MainKt'**.

## 6. Ejecutar mediante Gradle Wrapper

Aunque IntelliJ IDEA permite ejecutar directamente `Main.kt`, también puede utilizarse la terminal integrada del IDE.

En Windows:

```powershell
.\gradlew.bat run
```

El proyecto debe mostrar:

```text
================================
       PANADERÍA ROSARIO
================================
1. Gestión de productos
2. Registrar pedido
3. Consultar pedidos
4. Actualizar estado de pedido
5. Reportes
6. Salir
```

El Wrapper es la forma recomendada de ejecutar Gradle dentro del proyecto porque mantiene la misma versión para todos los integrantes del grupo.

## 7. Compilar el proyecto

Para compilar sin ejecutar:

```powershell
.\gradlew.bat build
```

Los archivos generados se almacenarán en la carpeta `build/`.

Para limpiar los archivos generados:

```powershell
.\gradlew.bat clean
```

## 8. Funcionalidades implementadas

- Gestión CRUD de productos.
- Categorías: pan, repostería, desayuno y cena.
- Disponibilidad de productos.
- Registro de pedidos con múltiples productos y cantidades.
- Cálculo automático de subtotales y total.
- Datos básicos del cliente y dirección de entrega.
- Estados del pedido: recibido, en preparación, en camino y entregado.
- Reporte general en consola.
- Manejo de colecciones mediante `MutableList`.
- Programación orientada a objetos mediante clases y objetos.
- Herencia mediante `Usuario`, `Cliente` y `Administrador`.
- Interfaz `Calculable` implementada por `Pedido`.
- Validación de entradas.
- Manejo de excepciones con `try/catch`.
- Registro de errores en el archivo `errores.txt`.

## 9. Organización lógica

### `model`

Contiene las entidades principales y enumeraciones del sistema:

- `Producto`
- `Pedido`
- `DetallePedido`
- `Usuario`
- `Cliente`
- `Administrador`
- `Categoria`
- `EstadoPedido`
- `Calculable`

### `service`

Contiene la lógica funcional:

- `ProductoService`: administración de productos.
- `PedidoService`: creación y actualización de pedidos.
- `ReporteService`: generación del resumen del sistema.

### `util`

Contiene funciones auxiliares:

- `Validador`: validación de entradas de consola.
- `Logger`: registro de errores en archivo de texto.

### `data`

Contiene datos iniciales para facilitar las pruebas del programa.

### `Main.kt`

Controla el menú principal y los flujos interactivos de consola.

## 10. Relación con la Etapa 2

La implementación busca cubrir los elementos solicitados para la base funcional del sistema:

- uso de Kotlin;
- programación orientada a objetos;
- clases, objetos, herencia e interfaz;
- módulos funcionales;
- colecciones;
- interfaz de consola;
- validaciones;
- excepciones;
- archivo de log;
- CRUD de productos;
- lógica de pedidos;
- actualización dinámica de estados;
- generación de reportes/resúmenes.

## 11. Nota de alcance

La condición de pedido mínimo de pan permanece como una advertencia preliminar. Todavía no bloquea la creación del pedido porque esa política comercial puede ajustarse durante el levantamiento de requerimientos de Panadería Rosario.

Firebase, Cloud Firestore, Cloudinary, interfaz gráfica Android y otras tecnologías de la aplicación final no forman parte de esta base de consola. En esta etapa se prioriza la lógica funcional y la aplicación de Kotlin y POO.
