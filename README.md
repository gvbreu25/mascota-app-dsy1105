# Mis mascotas

Aplicación Android para organizar las fichas y fotografías de tus mascotas y consultar sus datos sin conexión.

## Funcionalidades

- Registrar mascotas con nombre, especie, edad, responsable, teléfono y estado de vacunación.
- Consultar el listado y el detalle de cada ficha.
- Editar registros y eliminarlos con confirmación.
- Validar los campos con mensajes e íconos de error.
- Tomar una fotografía con la cámara o seleccionar una imagen de la galería.
- Conservar fichas y fotografías al cerrar la aplicación.
- Cambiar entre tema claro y oscuro, conservando la preferencia.
- Adaptar el listado y el formulario a pantallas compactas y amplias.

## Tecnologías

Kotlin · Jetpack Compose · Material 3 · Navigation Compose · ViewModel · StateFlow · Corrutinas · Room · DataStore Preferences · Coil.

## Requisitos

- Android Studio compatible con Android Gradle Plugin 8.9.1.
- JDK 17, Android SDK 35 y Build-Tools 35.0.0.
- Dispositivo o emulador con Android 8.0 (API 26) o superior.
- Internet para descargar dependencias durante la primera compilación.

## Ejecutar

1. Clona este repositorio o descarga su código fuente.
2. Abre en Android Studio la carpeta que contiene `settings.gradle.kts`.
3. Selecciona JDK 17 como Gradle JDK y sincroniza el proyecto.
4. Elige un dispositivo o emulador y ejecuta el módulo `app`.

Android Studio configura `local.properties` con la ruta del SDK de cada equipo. Este archivo no se incluye en el repositorio.

### Compilación y pruebas

En Windows:

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat testDebugUnitTest
.\gradlew.bat lintDebug
```

En macOS o Linux, utiliza `./gradlew` con las mismas tareas. El APK se genera en `app/build/outputs/apk/debug/app-debug.apk`.

## Arquitectura

La aplicación utiliza MVVM y separa sus responsabilidades dentro del módulo `app`:

| Paquete | Responsabilidad |
|---|---|
| `model` | Entidad de mascota, estado del formulario y validaciones |
| `data` | Base de datos Room y DAO |
| `repository` | Acceso a fichas, fotografías y preferencias |
| `viewmodel` | Eventos, lógica de presentación y StateFlow |
| `navigation` | Rutas y navegación |
| `ui/screens` | Listado, formulario y detalle |
| `ui/components` | Componentes visuales reutilizables |
| `ui/theme` | Tema Material 3 |

La interfaz envía acciones al ViewModel, que valida los datos y utiliza el repositorio para consultar o modificar Room. Los cambios se exponen mediante StateFlow y actualizan la interfaz de forma reactiva. DataStore guarda la preferencia de tema.

## Validaciones

| Campo | Regla |
|---|---|
| Nombre de mascota | Entre 2 y 30 caracteres |
| Especie | Perro, Gato u Otro |
| Edad | Entero entre 0 y 30 años |
| Responsable | Entre 3 y 60 caracteres |
| Teléfono | Nueve dígitos, sin espacios ni prefijo internacional |
| Fotografía | Opcional; importación de imágenes de hasta 20 MB |

Las reglas se centralizan en `PetValidator`. Los nombres se evalúan después de quitar espacios exteriores. El estado de vacunación es informativo.

## Cámara, galería y persistencia

La captura se delega a la aplicación de cámara mediante `TakePicture` y una URI de `FileProvider`. `PickVisualMedia` permite seleccionar una fotografía sin dar acceso a toda la galería. No se solicita acceso directo al hardware de cámara ni permisos amplios de fotos.

Las imágenes se guardan en el almacenamiento privado y las fichas en SQLite mediante Room. La app funciona sin servidor ni cuentas de usuario.

## Verificación

La compilación y las seis pruebas automáticas finalizaron correctamente. Las pruebas cubren validaciones y operaciones de creación, consulta, actualización, reapertura de la base y eliminación. Android Lint informó cero errores y catorce advertencias.

Se verificaron registro, errores de formulario, cámara virtual, galería, recuperación tras reiniciar y tema persistente en un emulador Android 15. También se revisó el formulario en pantalla compacta y amplia. La app aún no se ha probado en un dispositivo físico.

## Límites actuales

- No hay sincronización, autenticación ni respaldo externo.
- Desinstalar la app o borrar sus datos elimina fichas e imágenes.
- Las fotos reemplazadas y los borradores abandonados pueden dejar archivos privados sin uso; aún no hay limpieza automática.
- La base utiliza la versión inicial del esquema y no incluye migraciones desde versiones anteriores.

## Versiones principales

Gradle 8.11.1 · Android Gradle Plugin 8.9.1 · Kotlin y Compose Compiler 2.1.20 · KSP 2.1.20-1.0.32 · Compose BOM 2025.04.01 · Room 2.7.1 · DataStore 1.1.4 · Navigation Compose 2.8.9.
