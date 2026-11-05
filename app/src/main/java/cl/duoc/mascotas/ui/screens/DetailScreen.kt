package cl.duoc.mascotas.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.mascotas.model.Pet
import cl.duoc.mascotas.ui.components.PetPhoto

@Composable fun DetailScreen(pet: Pet?, loading: Boolean, onEdit: () -> Unit, onDelete: () -> Unit) {
    var confirmDelete by rememberSaveable { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        when {
            loading -> CircularProgressIndicator()
            pet == null -> Text("Este registro no está disponible. Vuelve al listado.")
            else -> {
                PetPhoto(pet.photoUri, pet.name, Modifier.fillMaxWidth().height(240.dp))
                Text(pet.name, style = MaterialTheme.typography.headlineLarge)
                Text("${pet.species} · ${pet.age} años", style = MaterialTheme.typography.titleMedium)
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("RESPONSABLE", style = MaterialTheme.typography.labelLarge)
                        Text(pet.owner, style = MaterialTheme.typography.titleLarge)
                        Text(pet.phone)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(if (pet.vaccinated) Icons.Default.CheckCircle else Icons.Default.Info, null)
                            Text(if (pet.vaccinated) "Vacunas al día" else "Vacunas pendientes")
                        }
                    }
                }
                Button(onEdit, Modifier.fillMaxWidth()) { Icon(Icons.Default.Edit, null); Spacer(Modifier.width(8.dp)); Text("Editar ficha") }
                OutlinedButton({ confirmDelete = true }, Modifier.fillMaxWidth()) { Icon(Icons.Default.Delete, null); Spacer(Modifier.width(8.dp)); Text("Eliminar mascota") }
            }
        }
    }
    if (confirmDelete) AlertDialog(onDismissRequest = { confirmDelete = false },
        title = { Text("¿Eliminar esta mascota?") }, text = { Text("Se eliminará su ficha. Esta acción no se puede deshacer.") },
        confirmButton = { TextButton({ confirmDelete = false; onDelete() }) { Text("Eliminar") } },
        dismissButton = { TextButton({ confirmDelete = false }) { Text("Cancelar") } })
}
