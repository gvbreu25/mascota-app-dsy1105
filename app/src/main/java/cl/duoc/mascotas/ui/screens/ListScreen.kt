package cl.duoc.mascotas.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.mascotas.model.Pet
import cl.duoc.mascotas.ui.components.PetPhoto
import cl.duoc.mascotas.viewmodel.PetListState

@Composable fun ListScreen(state: PetListState, expanded: Boolean, onNew: () -> Unit, onOpen: (Long) -> Unit) {
    Column(Modifier.fillMaxSize().padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Su historia,\nsiempre contigo.", style = MaterialTheme.typography.headlineLarge)
        Text("Un lugar para organizar los datos y las fotos de tus mascotas.", style = MaterialTheme.typography.bodyLarge)
        FilledTonalButton(onNew, Modifier.fillMaxWidth()) { Icon(Icons.Default.Add, null); Spacer(Modifier.width(8.dp)); Text("Registrar mascota") }
        when {
            state.loading -> CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
            state.error != null -> Text(state.error, color = MaterialTheme.colorScheme.error)
            state.pets.isEmpty() -> Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Default.Pets, null, Modifier.size(36.dp))
                    Text("Tu primera huella", style = MaterialTheme.typography.titleLarge)
                    Text("Registra una mascota para ver aquí su ficha. Puedes añadir una foto ahora o más tarde.")
                }
            }
            else -> {
                Text("${state.pets.size} mascotas registradas", style = MaterialTheme.typography.titleMedium)
                LazyVerticalGrid(columns = GridCells.Fixed(if (expanded) 2 else 1),
                    verticalArrangement = Arrangement.spacedBy(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)) {
                    items(state.pets, key = { it.id }) { pet -> PetCard(pet) { onOpen(pet.id) } }
                }
            }
        }
    }
}
@Composable private fun PetCard(pet: Pet, onClick: () -> Unit) {
    Card(onClick, Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            PetPhoto(pet.photoUri, pet.name, Modifier.size(80.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(pet.name, style = MaterialTheme.typography.titleLarge)
                Text("${pet.species} · ${pet.age} años")
                Text(if (pet.vaccinated) "Vacunas al día" else "Vacunas pendientes", style = MaterialTheme.typography.labelMedium)
            }
            Icon(Icons.Default.ChevronRight, "Ver ficha de ${pet.name}")
        }
    }
}
