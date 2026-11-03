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
- PDF de investigación de seis páginas generado y revisado por extracción de texto
  y renderizado de una página.
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
