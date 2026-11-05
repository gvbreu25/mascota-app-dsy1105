# Registro de validación

Preparación realizada el 4 de noviembre de 2026 (America/Santiago). Este documento registra pruebas reales y sus límites; no asigna una calificación.

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

## Pendientes y límites

- El estudiante debe repetir las pruebas personalmente y practicar los cambios de defensa sin ayuda.
- No se probó en dispositivo físico, ni todas las combinaciones de fabricantes/versiones/permisos.
- Cancelación de recursos, errores de almacenamiento y todos los flujos de accesibilidad deben comprobarse además con el checklist de preparación.
- El CRUD de borrado y la actualización de datos se verificaron en la prueba automatizada Room; eso no sustituye revisar cada interacción visual en el equipo de defensa.
- La modalidad individual debe confirmarse con docente. Trello se omite por decisión del estudiante y no se declara ese requisito cumplido.
- El acceso específico del docente y la entrega AVA dependen de datos y acciones que todavía no se han realizado. Publicar GitHub no equivale a entregar en AVA.

No se modificará el repositorio después de la entrega y antes de la evaluación. Practicar en una copia independiente y sin remoto.
