package cl.duoc.mascotas.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.duoc.mascotas.model.PetForm
import cl.duoc.mascotas.model.PetValidator
import cl.duoc.mascotas.ui.components.PetPhoto
import cl.duoc.mascotas.viewmodel.PetViewModel

@Composable fun FormScreen(f: PetForm, vm: PetViewModel, expanded: Boolean) {
    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture(), vm::completeCapture)
    val gallery = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) vm.importPhoto(uri)
    }
    Column(Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(if (f.id == 0L) "Una nueva huella" else "Actualizar ficha", style = MaterialTheme.typography.headlineMedium)
        Text("Completa los cinco campos. La foto es opcional.")
        if (expanded) Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            PhotoPanel(f, Modifier.weight(1f), { runCatching { camera.launch(vm.prepareCapture()) }.onFailure { vm.captureFailed() } },
                { runCatching { gallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }.onFailure { vm.report("No se pudo abrir Galería.") } }, vm::removePhoto)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) { Fields(f, vm) }
        } else {
            PhotoPanel(f, Modifier.fillMaxWidth(), { runCatching { camera.launch(vm.prepareCapture()) }.onFailure { vm.captureFailed() } },
                { runCatching { gallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }.onFailure { vm.report("No se pudo abrir Galería.") } }, vm::removePhoto)
            Fields(f, vm)
        }
        AnimatedVisibility(f.message != null) {
            Text(f.message.orEmpty(), color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(8.dp))
        }
        AnimatedVisibility(f.busy) { LinearProgressIndicator(Modifier.fillMaxWidth()) }
        Button(vm::save, Modifier.fillMaxWidth().heightIn(min = 52.dp), enabled = !f.busy) {
            Icon(Icons.Default.Save, null); Spacer(Modifier.width(8.dp)); Text(if (f.busy) "Procesando…" else "Guardar mascota")
        }
        Spacer(Modifier.height(16.dp))
    }
}
@Composable private fun PhotoPanel(f: PetForm, modifier: Modifier, camera: () -> Unit, gallery: () -> Unit, remove: () -> Unit) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        PetPhoto(f.photoUri, f.name, Modifier.fillMaxWidth().height(180.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(camera, Modifier.weight(1f), enabled = !f.busy) { Icon(Icons.Default.PhotoCamera, null); Spacer(Modifier.width(6.dp)); Text("Cámara") }
            OutlinedButton(gallery, Modifier.weight(1f), enabled = !f.busy) { Icon(Icons.Default.PhotoLibrary, null); Spacer(Modifier.width(6.dp)); Text("Galería") }
        }
        if (f.photoUri != null) TextButton(remove, enabled = !f.busy) { Text("Quitar foto") }
    }
}
@Composable private fun Fields(f: PetForm, vm: PetViewModel) {
    Field("Nombre de la mascota", f.name, f.errors["name"], Icons.Default.Pets, !f.busy) { vm.change("name", it) }
    Text("Especie", style = MaterialTheme.typography.labelLarge)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        PetValidator.speciesOptions.forEach { option ->
            FilterChip(f.species == option, { vm.change("species", option) }, label = { Text(option) }, enabled = !f.busy)
        }
    }
    AnimatedVisibility(f.errors["species"] != null) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Default.Error, null, tint = MaterialTheme.colorScheme.error)
            Text(f.errors["species"].orEmpty(), color = MaterialTheme.colorScheme.error)
        }
    }
    Field("Edad en años (0 a 30)", f.age, f.errors["age"], Icons.Default.Cake, !f.busy, KeyboardType.Number) { vm.change("age", it) }
    Field("Nombre del responsable", f.owner, f.errors["owner"], Icons.Default.Person, !f.busy) { vm.change("owner", it) }
    Field("Teléfono (9 dígitos)", f.phone, f.errors["phone"], Icons.Default.Phone, !f.busy, KeyboardType.Phone) { vm.change("phone", it) }
    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
        Checkbox(f.vaccinated, vm::setVaccinated, enabled = !f.busy)
        Text("Vacunas al día")
    }
}
@Composable private fun Field(label: String, value: String, error: String?, icon: ImageVector, enabled: Boolean,
    keyboard: KeyboardType = KeyboardType.Text, change: (String) -> Unit) {
    OutlinedTextField(value, change, Modifier.fillMaxWidth(), enabled = enabled, label = { Text(label) }, singleLine = true,
        leadingIcon = { Icon(icon, null) }, isError = error != null,
        trailingIcon = { if (error != null) Icon(Icons.Default.Error, "Error: $error") },
        supportingText = { AnimatedVisibility(error != null) { Text(error.orEmpty()) } },
        keyboardOptions = KeyboardOptions(keyboardType = keyboard))
}
