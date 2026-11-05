# Validación de la adaptación a cloro

## Resultado comprobado
El 5 de noviembre de 2026 se ejecutó assembleDebug y testDebugUnitTest con Gradle 8.11.1, JDK 17 y SDK 35. Resultado: BUILD SUCCESSFUL en 49 segundos. Se generó el APK de la nueva Activity principal de cloro.

Las seis pruebas existentes se conservaron y pasaron, pero corresponden a validación y Room del dominio anterior. No acreditan la clasificación ni la persistencia específica de cloro.

## Implementado
Inicio con catálogo ficticio de granjas/galpones/puntos; registro validado; configuración persistente de cuatro umbrales sin límites oficiales predefinidos; clasificación normal/advertencia/crítico; histórico con filtro; seguimiento de desviaciones y acciones; fotografías; notificaciones locales con comprobación del permiso Android 13+.

Origen de prueba: MonitoringApi/SimulatedMonitoringApi, doble en memoria sin transporte HTTP. No existe integración con sensores, AMINO ni ERP. Los datos y nombres son ficticios. Las notificaciones se disparan al guardar; no hay servicio de monitoreo en segundo plano.

## Aún no verificado en esta adaptación
Ejecución completa del nuevo flujo en teléfono o emulador, permisos concedidos/denegados, entrega visual de notificaciones, cámara/galería asociadas a mediciones, recuperación de mediciones/acciones tras reinicio, adaptabilidad amplia y casos límite del clasificador. Estas funciones están implementadas, pero no deben presentarse como probadas todavía.

## Continuidad
La nueva base chlorine.db no borra ni transforma mascotas.db. Las clases de mascotas permanecen como legado inactivo para conservar la base anterior; MainActivity inicia exclusivamente ChlorineScreens. No se cambió la identidad interna de instalación. El último lint aprobado correspondía al proyecto anterior; no se ejecutó lint de esta revisión antes de publicar.
