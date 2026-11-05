# Registro de validación

Verificación realizada el 4 de noviembre de 2026 (America/Santiago). Este documento registra las pruebas ejecutadas y sus límites.

## Compilación y pruebas

Comando ejecutado: `assembleDebug testDebugUnitTest lintDebug` con Gradle 8.11.1, Java 17 y SDK 35. Resultado de la versión final con DataStore: **BUILD SUCCESSFUL**.

- `PetValidatorTest`: **5 pruebas**, cero fallos/errores. Formulario válido, cinco campos obligatorios, límites y entradas inválidas de edad, teléfono y espacios/especie desconocida.
- `PetDatabaseTest`: **1 prueba**, cero fallos/errores. Base Room sobre entorno Robolectric/API 28: insertar, consultar, actualizar, cerrar/reabrir base y eliminar.
- Android Lint: **0 errores y 14 advertencias**. Incluyen versiones más nuevas disponibles y recomendaciones de API/estilo. No se afirma ausencia de advertencias. Las versiones se fijaron para esta compilación.

## Ejecución en emulador

Dispositivo virtual Pixel 5, imagen Google APIs Android 15/API 35, x86_64. Captura mediante cámara virtual de Android; no es una cámara física ni una fotografía real de mascota.

| Prueba observada | Resultado |
|---|---|
| Abrir la Activity principal | Listado visible, sin cierre por error. |
| Formulario completamente vacío → Guardar | Cinco mensajes por campo e íconos; no se guarda ficha. |
| Datos ficticios Luna/Gato/2/Ana Perez/912345678 | Ficha guardada y detalle con los datos ingresados. |
| Editar ficha y elegir imagen mediante Galería | Selector nativo abre y resultado se muestra en UI. |
| Guardar imagen, cerrar completamente la app y reabrir | Ficha e imagen recuperadas. |
| Cámara → disparar → confirmar | Resultado de cámara virtual mostrado y guardado en la ficha. |
| Cambiar a tema oscuro, cerrar/reabrir | Preferencia recuperada mediante DataStore. |
| Pantalla amplia 1600 × 1200, densidad 240 | Formulario en dos paneles, foto a la izquierda y campos a la derecha. |

## Límites de las pruebas

- No se probó en dispositivo físico ni en todas las combinaciones de fabricantes y versiones de Android.
- Queda por ampliar la comprobación de cancelación de recursos, errores de almacenamiento y accesibilidad.
- El borrado y la actualización de datos se verificaron mediante Room en pruebas automáticas; queda por ampliar la cobertura de las interacciones visuales correspondientes.
