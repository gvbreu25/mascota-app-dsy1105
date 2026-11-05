package cl.duoc.mascotas.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.settings by preferencesDataStore(name = "settings")
class SettingsRepository(private val context: Context) {
    private val darkKey = booleanPreferencesKey("dark_theme")
    val darkTheme = context.settings.data.map { it[darkKey] ?: false }
    suspend fun setDarkTheme(dark: Boolean) { context.settings.edit { it[darkKey] = dark } }
}
