package cl.duoc.mascotas.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.mascotas.model.*
import cl.duoc.mascotas.repository.PetRepository
import cl.duoc.mascotas.repository.PhotoStorage
import cl.duoc.mascotas.repository.SettingsRepository
import android.net.Uri
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class PetListState(val loading: Boolean = true, val pets: List<Pet> = emptyList(), val error: String? = null)

class PetViewModel(
    private val repository: PetRepository,
    private val photos: PhotoStorage,
    private val savedState: SavedStateHandle,
    private val settings: SettingsRepository
) : ViewModel() {
    val darkTheme = settings.darkTheme.catch { emit(false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)
    fun setDarkTheme(dark: Boolean) {
        viewModelScope.launch {
            try { settings.setDarkTheme(dark) }
            catch (e: CancellationException) { throw e }
            catch (e: Exception) { _operation.value = "No se pudo guardar el tema." }
        }
    }
    val listState = repository.pets.map { PetListState(loading = false, pets = it) }
        .catch { emit(PetListState(loading = false, error = "No se pudo leer la base de datos.")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PetListState())
    private val _form = MutableStateFlow(PetForm(
        id = savedState["id"] ?: 0L, name = savedState["name"] ?: "",
        species = savedState["species"] ?: "", age = savedState["age"] ?: "",
        owner = savedState["owner"] ?: "", phone = savedState["phone"] ?: "",
        vaccinated = savedState["vaccinated"] ?: false, photoUri = savedState["photo"]
    ))
    val form = _form.asStateFlow()
    private val _operation = MutableStateFlow<String?>(null)
    val operation = _operation.asStateFlow()
    val pendingCapture: String? get() = savedState["capture"]
    fun prepareCapture(): Uri = photos.createCapture().also { savedState["capture"] = it.toString() }
    fun captureFailed() {
        pendingCapture?.let(photos::discardCapture)
        savedState["capture"] = null
        report("No se pudo abrir la cámara. Puedes usar Galería.")
    }
    fun completeCapture(success: Boolean) {
        val uri = pendingCapture ?: return
        savedState["capture"] = null
        if (!success) { photos.discardCapture(uri); return }
        runPhoto { photos.validateCapture(Uri.parse(uri)) }
    }
    fun importPhoto(uri: Uri) { runPhoto { photos.importImage(uri) } }
    private fun runPhoto(action: suspend () -> String) {
        _form.update { it.copy(busy = true, message = null) }
        viewModelScope.launch {
            try { val photo = action(); _form.update { it.copy(photoUri = photo, busy = false) }; persistDraft() }
            catch (e: CancellationException) { throw e }
            catch (e: Exception) { _form.update { it.copy(busy = false, message = e.message ?: "No se pudo cargar la foto.") } }
        }
    }
    fun report(message: String) { _form.update { it.copy(message = message) } }
    fun change(field: String, value: String) {
        if (_form.value.busy) return
        val previous = _form.value
        val changed = when (field) {
            "name" -> previous.copy(name = value)
            "species" -> previous.copy(species = value)
            "age" -> previous.copy(age = value)
            "owner" -> previous.copy(owner = value)
            "phone" -> previous.copy(phone = value)
            else -> previous
        }.copy(touched = previous.touched + field, message = null)
        val errors = PetValidator.validate(changed).filterKeys { changed.attempted || it in changed.touched }
        _form.value = changed.copy(errors = errors)
        persistDraft()
    }
    fun setVaccinated(value: Boolean) {
        _form.update { it.copy(vaccinated = value) }; persistDraft()
    }
    fun removePhoto() { _form.update { it.copy(photoUri = null) }; persistDraft() }
    fun resetForm() {
        savedState["formKey"] = -1L
        _form.value = PetForm(); persistDraft()
    }
    fun openForm(id: Long) {
        if (savedState.get<Long>("formKey") == id) return
        if (id == -1L) { resetForm(); return }
        _form.value = PetForm(busy = true)
        viewModelScope.launch {
            try {
                val pet = requireNotNull(repository.find(id)) { "El registro ya no existe. Vuelve al listado." }
                _form.value = PetForm(pet.id, pet.name, pet.species, pet.age.toString(), pet.owner, pet.phone, pet.vaccinated, pet.photoUri)
                savedState["formKey"] = id; persistDraft()
            } catch (e: CancellationException) { throw e }
            catch (e: Exception) { _form.value = PetForm(message = e.message) }
        }
    }
    fun save() {
        val current = _form.value
        if (current.busy) return
        val errors = PetValidator.validate(current)
        _form.value = current.copy(errors = errors, attempted = true, message = null)
        if (errors.isNotEmpty()) return
        _form.update { it.copy(busy = true) }
        viewModelScope.launch {
            try {
                val id = repository.save(Pet(current.id, current.name.trim(), current.species,
                    current.age.toInt(), current.owner.trim(), current.phone.trim(), current.vaccinated, current.photoUri))
                _form.update { it.copy(busy = false, savedId = id) }
            } catch (e: CancellationException) { throw e }
            catch (e: Exception) { _form.update { it.copy(busy = false, message = "No se guardó. Inténtalo nuevamente.") } }
        }
    }
    fun consumeSaved() { _form.update { it.copy(savedId = null) } }
    fun delete(id: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try { repository.delete(id); onSuccess() }
            catch (e: CancellationException) { throw e }
            catch (e: Exception) { _operation.value = "No se pudo eliminar. Inténtalo de nuevo." }
        }
    }
    fun clearOperation() { _operation.value = null }
    private fun persistDraft() {
        val f = _form.value
        savedState["id"] = f.id; savedState["name"] = f.name
        savedState["species"] = f.species; savedState["age"] = f.age
        savedState["owner"] = f.owner; savedState["phone"] = f.phone
        savedState["vaccinated"] = f.vaccinated; savedState["photo"] = f.photoUri
    }
}
