# Validación del 3 de noviembre de 2026

## Comprobado en el entorno cloud

- JDK completo 17 y Android SDK 35 instalados; Build Tools 34.0.0.
- El script reutilizable de instalación ejecutó `assembleDebug`, `lintDebug` y
  `testDebugUnitTest` sobre el código final con resultado `BUILD SUCCESSFUL`.
- Cuatro pruebas JVM ejecutadas: cero fallos, errores o pruebas omitidas.
- APK de depuración generado en `app/build/outputs/apk/debug/app-debug.apk`.
- La opción `-PfirebaseEmulators=true` también compiló con el rediseño Compose.
- Smoke funcional del emulador Firebase Authentication: registro, inicio de sesión,
  consulta de identidad y rechazo de contraseña incorrecta. Cuenta temporal eliminada.
- `git diff --check` sin errores.

## Pendiente de configuración o dispositivo

- No se creó un proyecto Firebase remoto: no hay acceso a una cuenta propietaria.
- Google OAuth real no fue probado; requiere proyecto, proveedor habilitado,
  certificados y `app/google-services.json`.
- Firestore Emulator no pudo descargarse: la política de red bloqueó
  `storage.googleapis.com`. El dominio está guardado en el borrador del entorno,
  pero todavía no está aplicado. Reintentar después de guardar la configuración.
- Las reglas de Firestore se suministran en `firestore.rules`; aún no se ejecutaron
  pruebas de reglas ni un pedido completo contra Firestore.
- No hay dispositivo Android conectado ni `/dev/kvm`. No se realizó una prueba visual
  ni de interacción del APK en un dispositivo. Compilar no demuestra esos flujos.
- El video de demostración debe grabarse con la configuración real o local operativa.

Se guardaron `install_script`, `start_skill` y el dominio adicional de red en el
borrador. Guardar el borrador no aplica la red, publica el entorno ni valida una tarea nueva.

## Corrección para probar después de hacer pull

Se agregó demostración local automática cuando no existe configuración Firebase.
Los accesos de cliente y administrador permiten probar catálogo, carrito, pedidos,
historial y cambios de estado sin servidores. Los datos locales se conservan en
un archivo privado del dispositivo, separados de Firebase.

Se ejecutaron seis pruebas JVM, todas aprobadas: las cuatro anteriores y dos
pruebas de persistencia local que recuperan catálogo/pedidos después de recrear
el repositorio y verifican que la entrega y los precios históricos se conservan.
El APK de debug compiló correctamente. No se ejecutaron pruebas de interfaz en
un dispositivo Android ni compilación en un host Windows.

En Windows se redirigieron los directorios de resultados de Gradle a
`%LOCALAPPDATA%/PanaderiaBuild/<identificador>/`, para evitar que los archivos
intermedios se generen dentro de OneDrive. El checkout permanece en su ubicación.
Esto no descarta modificaciones locales de Git ni elimina archivos antiguos.
Los flujos reales de Google y Firestore siguen requiriendo un proyecto Firebase.
