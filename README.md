# Mis mascotas — DSY1105 EA2

Aplicación Android local para registrar, consultar, editar y eliminar fichas de mascotas y asociar fotografías. Contexto propuesto: mantener datos del responsable y mascota disponibles sin conexión.

## Identificación: completar ANTES de entregar

- Estudiante: **Gabriel Alejandro Abreu Quintero**.
- Equipo/sección: **PENDIENTE — confirmar con docente**.
- Modalidad actual: desarrollo individual. La pauta solicita parejas; no se declara colaboración que no ocurrió. Confirmar la situación con el docente.
- Repositorio público: **PENDIENTE**.
- Tablero de planificación: **PENDIENTE**.

## Funcionalidades en el código

- Listado, registro y detalle con Navigation Compose; edición y borrado con confirmación.
- Formulario con validación por campo en `PetValidator`, errores reactivos, íconos y bloqueo de guardado mientras procesa.
- MVVM, Repository, StateFlow y observación asociada al ciclo de vida.
- SQLite mediante Room: insertar, consultar, actualizar y eliminar.
- Preferencia de tema claro/oscuro guardada con DataStore Preferences desde el ícono de la barra superior.
- Cámara delegada con `TakePicture`/`FileProvider`, selector de fotos con `PickVisualMedia` y copia privada de las imágenes seleccionadas.
- Errores con `AnimatedVisibility`, indicador de proceso y transiciones de Navigation Compose.
- Material 3, tema oscuro y distribución por Window Size Classes: una/dos columnas y formulario con uno/dos paneles.

Estas funciones deben contrastarse con pruebas reales. El resumen de validación de la entrega acompaña el proyecto; el código por sí solo no acredita que se probó cámara o galería en un teléfono.

## Ejecutar

1. Android Studio con soporte para AGP 8.9.1, JDK 17 y Android SDK 35.
2. Abrir esta carpeta raíz (`settings.gradle.kts`). Sincronizar Gradle.
3. Android Studio configura `local.properties` con el SDK de ese computador. No compartir este archivo.
4. Seleccionar teléfono o emulador Android 8.0/API 26 o superior y ejecutar `app`.
5. Para compilar desde terminal con Java y SDK configurados: `gradlew.bat assembleDebug` (Windows) o `./gradlew assembleDebug` (macOS/Linux).
6. Pruebas: `gradlew.bat testDebugUnitTest`. Revisión Android: `gradlew.bat lintDebug`.

Versiones fijadas: Gradle 8.11.1, AGP 8.9.1, Kotlin/Compose compiler 2.1.20, KSP 2.1.20-1.0.32, Room 2.7.1, Compose BOM 2025.04.01, Navigation 2.8.9.

## Arquitectura

`ui/screens` → `viewmodel` → `repository` → `data` (Room) → SQLite.
`model` define la entidad, el estado del formulario y las validaciones. `navigation` coordina rutas. La Activity crea un ViewModel compartido; `Application` proporciona repositorio/base de datos. Se separan responsabilidades en paquetes dentro de un módulo `app`; no se afirma tener varios módulos Gradle.

## Reglas de negocio del proyecto

Nombre 2–30 caracteres, especie de lista, edad entera 0–30, responsable 3–60, teléfono de nueve dígitos. Foto opcional. No hay autenticación ni conexión a servidor. El indicador de vacunas es informativo.

## Recursos nativos y permisos

La cámara se ejecuta mediante una aplicación externa y una URI `content://` creada con FileProvider. No se declara `CAMERA` porque esta app no abre el hardware directamente. FileProvider no está exportado y comparte solo `files/photos/` mediante permisos temporales del contrato. El selector de fotos concede acceso a la imagen seleccionada, sin permisos amplios de galería. Se copia a almacenamiento privado en una corrutina de IO; se verifican tamaño y formato. Cancelar no sustituye la imagen actual.

## Datos y límites

Fichas e imágenes permanecen en el dispositivo. Desinstalar o borrar datos las elimina. No hay sincronización, exportación ni migración desde esquemas anteriores. Las fotos reemplazadas o de borradores abandonados pueden permanecer en el almacenamiento privado; no hay limpieza automática de archivos huérfanos en esta versión. No se suben fotos personales ni bases de datos al repositorio.

## Entrega y congelamiento

Completar identidad y enlaces, ejecutar pruebas en el equipo de defensa, subir código fuente a GitHub público y AVA grupal y activar AVA individual dentro de plazo. **Desde la entrega hasta la evaluación no modificar el repositorio, ni siquiera README, ramas o archivos mínimos.** Practicar posteriormente en una copia local separada sin remoto. Las modificaciones en vivo se realizan cuando las solicite el docente durante la defensa. No usar IA ni recursos de apoyo durante la defensa.
