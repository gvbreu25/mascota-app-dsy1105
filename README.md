# Cloro en línea

Autor: Gabriel Alejandro Abreu Quintero.

Monitoreo local de cloro libre por granja, galpón y punto. Adaptación del proyecto existente: Kotlin, Compose, MVVM, StateFlow, Room y DataStore.

## Uso
1. Abrir el proyecto raíz en Android Studio, con JDK 17 y SDK 35.
2. Ejecutar app en Android 8 o superior.
3. Abrir Rangos y configurar cuatro umbrales crecientes. No hay límites oficiales predefinidos. Unidad de demostración: mg/L.
4. Registrar una medición con punto, concentración, fecha y hora; consultar Histórico y abrir el detalle.
5. Para valores fuera de rango, registrar acciones correctivas y marcar la desviación atendida.
6. Habilitar notificaciones desde Rangos. En Android 13 o superior se solicita autorización; si se deniega, las alertas siguen dentro de la app.

## Origen simulado
MonitoringApi y SimulatedMonitoringApi proporcionan un catálogo ficticio en memoria. No hay HTTP ni integración con AMINO/ERP. El botón de lectura crítica genera un dato de prueba en función de los rangos configurados. No representa un sensor real.

## Persistencia y reglas
Room conserva mediciones, umbrales usados y acciones. DataStore conserva los rangos. Cada histórico mantiene su clasificación original. La validación comprueba punto, número finito no negativo y fecha válida no futura. La evidencia fotográfica utiliza cámara delegada, FileProvider y selector de galería.

## Arquitectura
- data/ChlorineData.kt: entidades, DAO, base, repositorio y contrato de API simulada.
- model/ChlorineRules.kt: umbrales y validación.
- viewmodel/ChlorineViewModel.kt: estado, operaciones y notificaciones.
- ui/screens/ChlorineScreens.kt: inicio, formulario, histórico, detalle y rangos.

## Continuidad
Se conserva la identidad interna del paquete para reutilizar la instalación y las dependencias. La nueva base chlorine.db es independiente de mascotas.db; no se borran datos anteriores. Las clases antiguas permanecen como código legado sin ser el flujo de inicio. No hay monitoreo en segundo plano ni notificaciones remotas: las notificaciones locales se producen al registrar lecturas.

## Verificación
Consultar el informe de esta revisión. No confundir las pruebas anteriores del registro de mascotas con pruebas del nuevo dominio.
