# Panadería Rosario

Aplicación Android en Kotlin y Jetpack Compose para catálogo, carrito, pedidos,
entregas y administración de una panadería.

## Avance 2: autenticación y persistencia

- Registro e inicio de sesión con correo/contraseña mediante Firebase Authentication.
- Acceso con Google mediante Android Credential Manager y Firebase.
- Perfiles vinculados al UID; contraseñas gestionadas por Authentication.
- Catálogo compartido y pedidos persistentes en Cloud Firestore.
- Confirmación transaccional: verificación de stock/precio, numeración y creación del pedido.
- Historial por usuario con actualizaciones en tiempo real.
- Administración mediante custom claim `admin`, sin contraseñas locales de prueba.
- Gestión de productos, existencias, pedidos, entregas y reportes PDF.

## Ejecutar después de hacer pull

Abrir el proyecto existente en Android Studio, sincronizar Gradle y ejecutar **app**.
Sin `google-services.json`, el arranque habilita automáticamente una **demostración
local**: elegir **Probar como cliente** o **Probar administración**. No requiere
cuentas, Firebase CLI ni servidores. Incluye catálogo inicial, carrito, confirmación,
historial, gestión de productos, entrega y reportes. Catálogo y pedidos se guardan
en el almacenamiento privado del dispositivo y sobreviven al reinicio de la app.
Los dos accesos de prueba comparten los datos de demostración del dispositivo.
Este modo no autentica usuarios ni demuestra persistencia en Firestore.

En Windows los resultados de compilación se generan automáticamente en
`%LOCALAPPDATA%/PanaderiaBuild/<identificador>/`, fuera de OneDrive y de la ruta
larga del proyecto. El código permanece en su carpeta original; Android Studio
reconoce las rutas desde Gradle. No es necesario volver a clonar el repositorio.

Para conectar los servicios reales, seguir [docs/FIREBASE.md](docs/FIREBASE.md).
Al agregar `app/google-services.json`, la app usa Firebase en lugar de la demostración.
El proyecto remoto y Google OAuth aún deben configurarse en la cuenta propietaria.
La demostración local mantiene sus datos separados de Firebase y no los sube.

## Diseño móvil

Interfaz Material 3 con paleta original de tonos café y crema, tipografía y tarjetas consistentes,
ilustraciones vectoriales disponibles sin red, buscador y filtros de catálogo,
navegación inferior, carrito con datos de entrega y seguimiento visual del pedido.
Los formularios y listas permiten desplazamiento; la interfaz respeta áreas seguras
y teclado. El estilo se implementa con Compose, no con CSS.

## Compilar y validar

Requisitos: JDK completo 17, Android SDK 35, Build Tools 34.0.0 y conexión para dependencias.
El wrapper usa Gradle 8.10.2, AGP 8.7.3 y Kotlin 2.0.21. Android mínimo: 7.0 / API 24.
Se habilita desugaring para las APIs de fechas en Android 7.

```bash
export ANDROID_HOME=/ruta/al/android-sdk
bash gradlew :app:assembleDebug :app:lintDebug :app:testDebugUnitTest
```

APK en Linux/macOS: `app/build/outputs/apk/debug/app-debug.apk`.
En Windows: `%LOCALAPPDATA%/PanaderiaBuild/<identificador>/app/outputs/apk/debug/app-debug.apk`.

Sin `google-services.json`, la app usa la demostración local identificada en pantalla.

### Modo local

```bash
bash scripts/start_emulators.sh
# Otra terminal, mismo equipo:
bash gradlew -PfirebaseEmulators=true :app:assembleDebug
```

Instalar el APK en un emulador Android del mismo equipo: `10.0.2.2` conecta con
Authentication (9099) y Firestore (8080). La pantalla identifica el modo local.
El backend remoto se selecciona al compilar sin esa propiedad. Release no usa emuladores.

Para una comprobación independiente de Authentication:

```bash
npx --yes firebase-tools@14.22.0 emulators:start --only auth --project demo-panaderia
# Otra terminal:
python3 scripts/smoke_auth_emulator.py
```

El smoke test comprueba registro, acceso, identidad persistente y rechazo de contraseña
incorrecta, y elimina la cuenta temporal. Las pruebas JVM cubren serialización de
productos, totales, reemplazo de historial y secuencia de estados.

## Entrega académica

- Reglas: `firestore.rules`; despliegue detallado en la guía de Firebase.
- La guía incluye un recorrido para el video de demostración de hasta 15 minutos.
  El video debe grabarse con el proyecto configurado y un dispositivo Android.

## Organización

`data/FirebaseRepository.kt` gestiona Authentication y Firestore. `AppState` coordina
sesión, carrito y listeners como ViewModel. `model/` y `service/` conservan las
entidades y reglas; `ui/` contiene pantallas y componentes Compose.

Las reglas aíslan el historial por UID y restringen las operaciones administrativas.
Una operación comercial con pagos requiere validación definitiva de importes desde
un backend confiable. El catálogo se plantea para un administrador editor a la vez.
No se incorporaron pagos electrónicos en este avance.
