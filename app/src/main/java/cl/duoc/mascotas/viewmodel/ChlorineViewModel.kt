package cl.duoc.mascotas.viewmodel

import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.mascotas.MainActivity
import cl.duoc.mascotas.R
import cl.duoc.mascotas.data.*
import cl.duoc.mascotas.model.*
import cl.duoc.mascotas.repository.PhotoStorage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime

private val Context.chlorinePreferences by preferencesDataStore("chlorine_ranges")
class ChlorineViewModel(private val context: Context, private val repository: ChlorineRepository) : ViewModel() {
    val measurements = repository.measurements.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val actions = repository.actions.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val points = MutableStateFlow<List<MonitoringPoint>>(emptyList())
    val ranges = context.chlorinePreferences.data.map { p ->
        val v = (0..3).map { p[doublePreferencesKey("range_$it")] }
        if (v.all { it != null }) ChlorineRanges(v[0]!!,v[1]!!,v[2]!!,v[3]!!) else null
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val message = MutableStateFlow<String?>(null)
    val busy = MutableStateFlow(false)
    val errors = MutableStateFlow<Map<String,String>>(emptyMap())
    val selected = MutableStateFlow(-1)
    val value = MutableStateFlow("")
    val date = MutableStateFlow(LocalDateTime.now().format(ChlorineRules.format))
    val photo = MutableStateFlow<String?>(null)
    private val storage = PhotoStorage(context)
    private var pendingPhoto: android.net.Uri? = null
    init { refresh() }
    fun refresh() = task { points.value = repository.points(); message.value = "Catálogo ficticio actualizado desde API simulada local." }
    fun reset() { selected.value = -1; value.value = ""; date.value = LocalDateTime.now().format(ChlorineRules.format); photo.value = null; errors.value = emptyMap() }
    fun saveRanges(values: List<String>) {
        val numbers = values.map(ChlorineRules::number)
        val r = if (numbers.all { it != null }) ChlorineRanges(numbers[0]!!,numbers[1]!!,numbers[2]!!,numbers[3]!!) else null
        if (r?.valid() != true) { message.value = "Orden requerido: crítico inferior < normal inferior < normal superior < crítico superior. Sin valores oficiales predefinidos."; return }
        task { context.chlorinePreferences.edit { p -> numbers.forEachIndexed { i,n -> p[doublePreferencesKey("range_$i")] = n!! } }; message.value = "Umbrales guardados. Se aplican a nuevas mediciones." }
    }
    fun save(onSaved: () -> Unit) {
        if (busy.value) return
        errors.value = ChlorineRules.validate(selected.value, points.value.size, value.value, date.value, ranges.value)
        if (errors.value.isNotEmpty()) return
        val p = points.value[selected.value]; val r = ranges.value!!; val level = ChlorineRules.number(value.value)!!
        val measurement = Measurement(farm=p.farm,shed=p.shed,point=p.name,chlorine=level,
            measuredAt=date.value,status=r.status(level),limits="${r.criticalLow} / ${r.normalLow} / ${r.normalHigh} / ${r.criticalHigh}",photoUri=photo.value)
        task { val id = repository.save(measurement); val notified = notify(measurement.copy(id=id))
            message.value = if (measurement.status != "NORMAL" && !notified) "Medición guardada; alerta visible en la app. Notificaciones del sistema deshabilitadas." else "Medición guardada: ${measurement.status}."
            onSaved()
        }
    }
    fun simulate(onSaved: () -> Unit) {
        if (points.value.isEmpty()) return
        if (ranges.value == null) { message.value = "Configura primero los rangos; el origen de prueba no define límites oficiales."; return }
        selected.value = 0; value.value = (ranges.value!!.criticalHigh + 0.1).toString()
        date.value = LocalDateTime.now().format(ChlorineRules.format); photo.value = null
        save(onSaved)
    }
    fun addAction(id: Long, description: String, resolved: Boolean) {
        if (description.trim().length !in 5..500) { message.value = "La acción debe contener entre 5 y 500 caracteres."; return }
        task { repository.addAction(CorrectiveAction(measurementId=id,description=description.trim(),createdAt=LocalDateTime.now().format(ChlorineRules.format),resolved=resolved)); message.value = "Acción registrada." }
    }
    fun delete(id: Long, after: () -> Unit) = task { repository.delete(id); after() }
    fun captureUri(): android.net.Uri = storage.createCapture().also { pendingPhoto=it }
    fun captured(success: Boolean) { val uri=pendingPhoto ?: return; pendingPhoto=null
        if (!success) { storage.discardCapture(uri.toString()); return }
        task { photo.value=storage.validateCapture(uri) }
    }
    fun importPhoto(uri: android.net.Uri) = task { photo.value=storage.importImage(uri) }
    fun feedback(text: String) { message.value=text }
    private fun task(block: suspend () -> Unit) { if (busy.value) return; busy.value=true
        viewModelScope.launch { try { block() } catch (e: CancellationException) { throw e }
            catch (e: Exception) { message.value="No se completó la operación: ${e.message}" } finally { busy.value=false } }
    }
    private fun notify(m: Measurement): Boolean {
        if (m.status == "NORMAL") return false
        val manager=NotificationManagerCompat.from(context)
        if (!manager.areNotificationsEnabled() || (Build.VERSION.SDK_INT>=33 && ContextCompat.checkSelfPermission(context,android.Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED)) return false
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(NotificationChannel("chlorine_alerts","Desviaciones de cloro",NotificationManager.IMPORTANCE_HIGH))
        val pending=PendingIntent.getActivity(context,0,Intent(context,MainActivity::class.java),PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        return try { manager.notify(m.id.toInt(),NotificationCompat.Builder(context,"chlorine_alerts").setSmallIcon(R.drawable.ic_water)
            .setContentTitle("Cloro: ${m.status}").setContentText("${m.farm} · ${m.point}: ${m.chlorine} mg/L").setContentIntent(pending).setAutoCancel(true).build()); true } catch (_: SecurityException) { false }
    }
}
