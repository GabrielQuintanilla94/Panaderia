# Configuración de Firebase — Panadería Rosario

La app incorpora Firebase Authentication (correo/contraseña y Google), perfiles,
catálogo compartido, pedidos e historial en Firestore. No se ha creado un proyecto
remoto ni se han desplegado reglas desde este repositorio. Sin configuración, el
APK permite probar los módulos en una demostración local identificada en pantalla,
con datos guardados en el dispositivo. No se presenta como autenticación Firebase.

## Crear el proyecto (una sola vez)

1. Entrar a https://console.firebase.google.com/ con la cuenta que será propietaria.
2. Crear un proyecto. Analytics es opcional para este avance.
3. Registrar una aplicación Android con el identificador exacto
   `com.panaderiarosario.app`.
4. Registrar estas huellas en Configuración del proyecto → Tus apps → Android → Agregar huella.
   Corresponden a `app/debug.keystore`, compartido en el repositorio, por lo que son
   iguales en todas las computadoras del equipo:
   - SHA-1: `EB:F8:29:6F:48:75:8A:30:52:44:6A:E7:49:49:78:50:B7:CD:11:54`
   - SHA-256: `BF:A9:10:52:1C:27:5F:FD:FB:FA:C1:CF:CB:E0:EC:AD:C1:2C:A9:75:70:77:B3:CF:7A:9B:6F:2B:38:4A:BB:B3`
   Se pueden comprobar con `bash gradlew :app:signingReport`.
5. En Authentication → Métodos de acceso, activar **Correo electrónico/contraseña**
   y **Google**, seleccionando el correo de soporte.
6. Descargar nuevamente `google-services.json` después de habilitar Google y colocar
   el archivo en `app/google-services.json`. El cliente OAuth de tipo web genera
   `default_web_client_id`, utilizado por Credential Manager. No usar el ID OAuth Android.
   Hacer commit y push del archivo: así el resto del equipo obtiene la configuración con pull.
7. Crear Cloud Firestore en modo producción y elegir la región antes de cargar datos.
8. Copiar `firestore.rules` a la pestaña Reglas y publicar, o usar el CLI autenticado:
   `firebase deploy --only firestore:rules --project ID_DEL_PROYECTO`.
9. Compilar e instalar el APK en Android 7 o superior con servicios de Google Play.
   Para Google, usar un dispositivo con cuenta Google o un emulador con Play Store.

`google-services.json` contiene configuración cliente, no una clave de servicio, y
se versiona para que todo el equipo use el mismo proyecto. La seguridad depende de
Authentication y de las reglas. Nunca agregar claves privadas de cuentas de servicio
(`*firebase-adminsdk*.json`, ignoradas por Git) al repositorio ni al APK, ni crear
reglas de acceso público.

## Administrador y catálogo inicial

Crear primero una cuenta normal con correo/contraseña desde la app. Copiar su UID
desde Authentication → Usuarios. Abrir Google Cloud Shell con la cuenta propietaria
(https://shell.cloud.google.com), subir `scripts/set_admin.py` con el menú
**Subir** de Cloud Shell y ejecutar:

```bash
gcloud auth application-default login
gcloud auth application-default set-quota-project ID_DEL_PROYECTO
pip install --user firebase-admin
python3 set_admin.py ID_DEL_PROYECTO UID_DEL_USUARIO
```

El script conserva otros claims y agrega `admin: true`. No se puede otorgar este
permiso desde la app o escribiendo un campo en el perfil de Firestore.
Cerrar sesión y entrar mediante **Acceso administrativo** usando correo y contraseña.
En Productos, **Publicar catálogo inicial** carga los productos de demostración
cuando el catálogo remoto está vacío. Los productos nuevos y cambios posteriores
se guardan en Firestore. Hasta publicar productos, el catálogo de clientes estará vacío.

## Datos y permisos

| Ruta | Contenido | Acceso |
| --- | --- | --- |
| `usuarios/{uid}` | Nombre, correo, teléfono, dirección | Propietario; lectura administrativa |
| `productos/{id}` | Catálogo y stock compartido | Lectura autenticada; administración del catálogo |
| `pedidos/{documento}` | UID, cliente, detalles, total, estado y fecha de servidor | Propietario y administradores |
| `contadores/pedidos` | Numeración y referencia del último pedido | Transacción de creación autenticada |

El historial consulta `usuarioId == FirebaseAuth.currentUser.uid`, nunca el teléfono.
Los listeners se eliminan al cerrar sesión y el historial local se vacía para evitar
mostrar datos de otra cuenta. Firebase mantiene la sesión entre reinicios; los pedidos
se recuperan desde Firestore. Las contraseñas las gestiona Authentication, no Firestore.

La confirmación utiliza una transacción: asigna número, verifica precio y stock,
descuenta existencias y crea el pedido. Si falla, conserva el carrito para reintentar.
Las reglas impiden leer pedidos de otra cuenta y limitan cambios de estado al
administrador, en orden RECIBIDO → EN_PREPARACION → EN_CAMINO → ENTREGADO.
El perfil puede completarse en el carrito para cuentas de Google.

El avance es un prototipo académico. Una operación comercial con cobros requiere
validación de precios y totales desde un backend confiable: la validación Kotlin del
cliente no basta para impedir solicitudes manipuladas. La edición administrativa
del catálogo completo debe hacerse por un administrador a la vez. No se implementó
un sistema de pagos ni recuperación de contraseña en este avance.

## Verificación y demostración (máximo 15 minutos)

1. Registrar cliente A con correo; cerrar sesión y volver a entrar.
2. Entrar con Google en cliente B y completar sus datos de entrega.
3. Revisar el catálogo publicado y agregar productos al carrito.
4. Confirmar un pedido con conexión; comprobar documento, UID y stock en Firestore.
5. Cerrar completamente la app; abrirla y comprobar que el historial conserva el pedido.
6. Entrar con otra cuenta; comprobar que no aparece el historial del cliente anterior.
7. Entrar como administrador, crear/editar productos y avanzar el pedido por los estados.
8. Volver al cliente y observar la actualización del historial, sin recrear el pedido.
9. Mostrar reportes.

Validación local:

```bash
bash gradlew :app:assembleDebug :app:lintDebug :app:testDebugUnitTest
```

Los tests JVM verifican serialización de productos, reemplazo del historial, cálculo
 y secuencia de estados. No sustituyen las pruebas de Authentication, Google y reglas
en un dispositivo/proyecto real. Esas pruebas requieren completar la configuración anterior.

## Fuentes oficiales

- https://firebase.google.com/docs/android/setup
- https://firebase.google.com/docs/auth/android/password-auth
- https://firebase.google.com/docs/auth/android/google-signin
- https://firebase.google.com/docs/firestore/manage-data/transactions
- https://firebase.google.com/docs/firestore/security/rules-conditions
- https://firebase.google.com/docs/auth/admin/custom-claims

## Desarrollo local sin proyecto remoto

Firebase Emulator Suite permite probar correo/contraseña y Firestore con el ID
local `demo-panaderia`. No crea recursos en una cuenta de Google.
Se requiere Node.js 20 o superior y Java. Ejecutar desde la raíz:

```bash
bash scripts/start_emulators.sh
# En otra terminal:
bash gradlew -PfirebaseEmulators=true :app:assembleDebug
```

Instalar ese APK en un emulador Android: `10.0.2.2` conecta los SDK con los puertos
9099 (Auth) y 8080 (Firestore) de la máquina anfitriona. Un dispositivo físico
necesita un mecanismo de acceso a esa máquina distinto y no es el destino de esta
configuración. La pantalla identifica explícitamente el modo local. La compilación
release siempre conserva desactivados los emuladores. Los datos del emulador son
temporales por defecto. La descarga inicial de Firebase CLI/emuladores requiere red.

Google OAuth requiere el proyecto remoto configurado; no se simula ese proveedor
con tokens inventados. Para volver al APK remoto compilar sin la propiedad
`firebaseEmulators`. No se incluye ninguna clave de servicio en el modo local.

La configuración de red de debug permite HTTP únicamente hacia `10.0.2.2` para
los emuladores; la configuración release no incluye esa excepción.

Para comprobar solo Authentication cuando Firestore todavía no puede descargarse:

```bash
npx --yes firebase-tools@14.22.0 emulators:start --only auth --project demo-panaderia
# Otra terminal:
python3 scripts/smoke_auth_emulator.py
```

Ese smoke test registra una cuenta temporal, verifica acceso e identidad, comprueba
el rechazo de una contraseña incorrecta y elimina la cuenta al finalizar.

## Prueba inmediata del proyecto existente

Después de `git pull origin main`, sincronizar y ejecutar **app**. Si no hay
`google-services.json`, usar **Probar como cliente** y **Probar administración**.
No se requieren servidores. Los pedidos y el catálogo se conservan localmente.
Para usar Firebase real, agregar su configuración y volver a compilar; el modo
local no se ofrece cuando está configurado Firebase. El modo con
`-PfirebaseEmulators=true` sigue disponible por separado para probar los SDK.
En Windows Gradle genera archivos fuera de OneDrive automáticamente, sin mover
el repositorio. Esta configuración no elimina los antiguos archivos `app/build`.
