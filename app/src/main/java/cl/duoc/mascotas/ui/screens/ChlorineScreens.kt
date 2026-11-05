package cl.duoc.mascotas.ui.screens

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.*
import cl.duoc.mascotas.viewmodel.ChlorineViewModel
import cl.duoc.mascotas.ui.components.PetPhoto

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun ChlorineScreens(vm: ChlorineViewModel) {
    val nav=rememberNavController()
    val entries by vm.measurements.collectAsStateWithLifecycle()
    val actions by vm.actions.collectAsStateWithLifecycle()
    val points by vm.points.collectAsStateWithLifecycle()
    val ranges by vm.ranges.collectAsStateWithLifecycle()
    val busy by vm.busy.collectAsStateWithLifecycle()
    val message by vm.message.collectAsStateWithLifecycle()
    val selected by vm.selected.collectAsStateWithLifecycle()
    val value by vm.value.collectAsStateWithLifecycle()
    val date by vm.date.collectAsStateWithLifecycle()
    val errors by vm.errors.collectAsStateWithLifecycle()
    val photo by vm.photo.collectAsStateWithLifecycle()
    val permission=rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { vm.feedback(if(it) "Notificaciones autorizadas." else "Sin permiso: alertas disponibles dentro de la app.") }
    val camera=rememberLauncherForActivityResult(ActivityResultContracts.TakePicture(),vm::captured)
    val gallery=rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { it?.let(vm::importPhoto) }
    Scaffold(topBar={TopAppBar(title={Text("Cloro en línea")})},bottomBar={Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceEvenly) {
        TextButton({nav.navigate("home")}) {Text("Inicio")};TextButton({nav.navigate("history")}) {Text("Histórico")};TextButton({nav.navigate("settings")}) {Text("Rangos")}
    }}) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).imePadding().verticalScroll(rememberScrollState()).padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
            Text("MVP · datos ficticios · sin conexión empresarial",style=MaterialTheme.typography.labelMedium)
            AnimatedVisibility(busy) {LinearProgressIndicator(Modifier.fillMaxWidth())}
            AnimatedVisibility(message!=null) {Text(message.orEmpty())}
            NavHost(nav,startDestination="home",modifier=Modifier.fillMaxWidth().heightIn(min=500.dp)) {
                composable("home") { Column(verticalArrangement=Arrangement.spacedBy(12.dp)) {
                    Text("Monitoreo de agua",style=MaterialTheme.typography.headlineMedium)
                    Text("${entries.size} controles · ${entries.count {m -> m.status!="NORMAL" && actions.none {it.measurementId==m.id && it.resolved}}} desviaciones abiertas")
                    if(ranges==null) Text("Configura los umbrales antes de registrar.",color=MaterialTheme.colorScheme.error)
                    Button({vm.reset();nav.navigate("form")},enabled=!busy) {Text("Registrar medición")}
                    OutlinedButton(vm::refresh,enabled=!busy) {Text("Actualizar catálogo simulado")}
                    OutlinedButton({vm.simulate {nav.navigate("history")}},enabled=!busy) {Text("Generar lectura crítica de prueba")}
                    points.forEach {p -> Card(Modifier.fillMaxWidth()) {Column(Modifier.padding(16.dp)) {
                        Text(p.farm,style=MaterialTheme.typography.titleMedium);Text("${p.shed} · ${p.name}")
                        Text(entries.firstOrNull {it.farm==p.farm && it.point==p.name}?.let {"${it.chlorine} mg/L · ${it.status}\n${it.measuredAt}"} ?: "Sin mediciones locales")
                    }}}
                }}
                composable("form") {Column(verticalArrangement=Arrangement.spacedBy(12.dp)) {
                    Text("Nuevo control",style=MaterialTheme.typography.headlineSmall)
                    points.forEachIndexed {i,p -> FilterChip(selected==i,{vm.selected.value=i},label={Text("${p.farm} · ${p.shed} · ${p.name}")},enabled=!busy)}
                    errors["point"]?.let {ErrorText(it)}
                    Input("Cloro libre (mg/L)",value,errors["value"],!busy) {vm.value.value=it}
                    Input("Fecha y hora: yyyy-MM-dd HH:mm",date,errors["date"],!busy) {vm.date.value=it}
                    errors["ranges"]?.let {ErrorText(it)}
                    photo?.let {PetPhoto(it,"punto de medición",Modifier.fillMaxWidth().height(150.dp))}
                    Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                        OutlinedButton({runCatching {camera.launch(vm.captureUri())}.onFailure {vm.feedback("No se pudo abrir cámara.")}},enabled=!busy) {Text("Cámara")}
                        OutlinedButton({gallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))},enabled=!busy) {Text("Galería")}
                    }
                    Text("Foto opcional como evidencia del control.")
                    Button({vm.save {nav.navigate("history") {popUpTo("home")}}},enabled=!busy) {Text("Guardar medición")}
                }}
                composable("history") {
                    var filter by rememberSaveable {mutableStateOf("")}
                    Column(verticalArrangement=Arrangement.spacedBy(12.dp)) {
                        Text("Histórico local",style=MaterialTheme.typography.headlineSmall)
                        Input("Filtrar granja o punto",filter,null,true) {filter=it}
                        if(entries.isEmpty()) Text("No hay controles guardados.")
                        entries.filter {"${it.farm} ${it.shed} ${it.point}".contains(filter,true)}.forEach {m -> Card(onClick={nav.navigate("detail/${m.id}")},modifier=Modifier.fillMaxWidth()) {Column(Modifier.padding(16.dp)) {
                            Text("${m.chlorine} mg/L · ${m.status}",style=MaterialTheme.typography.titleMedium)
                            Text("${m.farm} · ${m.shed} · ${m.point}\n${m.measuredAt}")
                            if(m.status!="NORMAL") Text(if(actions.any {it.measurementId==m.id && it.resolved}) "Desviación atendida" else "Desviación abierta")
                        }}}
                    }
                }
                composable("detail/{id}") {back ->
                    val m=entries.find {it.id==back.arguments?.getString("id")?.toLongOrNull()}
                    var action by rememberSaveable {mutableStateOf("")};var resolved by rememberSaveable {mutableStateOf(false)}
                    Column(verticalArrangement=Arrangement.spacedBy(12.dp)) {
                        if(m==null) Text("Registro no disponible.") else {
                            Text("${m.chlorine} mg/L · ${m.status}",style=MaterialTheme.typography.headlineSmall)
                            Text("${m.farm}\n${m.shed} · ${m.point}\n${m.measuredAt}")
                            Text("Umbrales usados: ${m.limits}")
                            m.photoUri?.let {PetPhoto(it,"punto de medición",Modifier.fillMaxWidth().height(180.dp))}
                            if(m.status!="NORMAL") {
                                Input("Acción correctiva (5 a 500 caracteres)",action,null,!busy) {action=it}
                                Row {Checkbox(resolved,{resolved=it});Text("Marcar desviación atendida")}
                                Button({vm.addAction(m.id,action,resolved)},enabled=!busy) {Text("Registrar acción")}
                            }
                            actions.filter {it.measurementId==m.id}.forEach {Text("${it.createdAt}: ${it.description}"+if(it.resolved) " · Atendida" else " · Seguimiento")}
                        }
                    }
                }
                composable("settings") {
                    var a by rememberSaveable {mutableStateOf("")};var b by rememberSaveable {mutableStateOf("")};var c by rememberSaveable {mutableStateOf("")};var d by rememberSaveable {mutableStateOf("")}
                    LaunchedEffect(ranges) {ranges?.let {a=it.criticalLow.toString();b=it.normalLow.toString();c=it.normalHigh.toString();d=it.criticalHigh.toString()}}
                    Column(verticalArrangement=Arrangement.spacedBy(12.dp)) {
                        Text("Rangos configurables",style=MaterialTheme.typography.headlineSmall)
                        Text("Sin límites oficiales predefinidos. Ingresa rangos autorizados o ficticios para pruebas. Unidad de demostración: mg/L.")
                        Input("Crítico por debajo de",a,null,!busy) {a=it};Input("Normal desde",b,null,!busy) {b=it};Input("Normal hasta",c,null,!busy) {c=it};Input("Crítico por encima de",d,null,!busy) {d=it}
                        Text("Orden: crítico inferior < normal inferior < normal superior < crítico superior. Entre normal y crítico: advertencia.")
                        Button({vm.saveRanges(listOf(a,b,c,d))},enabled=!busy) {Text("Guardar rangos")}
                        Button({if(Build.VERSION.SDK_INT>=33) permission.launch(android.Manifest.permission.POST_NOTIFICATIONS) else vm.feedback("Revisa las notificaciones en ajustes del sistema.")}) {Text("Habilitar notificaciones")}
                    }
                }
            }
        }
    }
}
@Composable private fun ErrorText(text:String) {Row {Icon(Icons.Default.Error,null,tint=MaterialTheme.colorScheme.error);Text(text,color=MaterialTheme.colorScheme.error)}}
@Composable private fun Input(label:String,value:String,error:String?,enabled:Boolean,onChange:(String)->Unit) {
    OutlinedTextField(value,onChange,modifier=Modifier.fillMaxWidth(),label={Text(label)},enabled=enabled,isError=error!=null,supportingText={if(error!=null) ErrorText(error)},trailingIcon={if(error!=null) Icon(Icons.Default.Error,"Error")})
}
