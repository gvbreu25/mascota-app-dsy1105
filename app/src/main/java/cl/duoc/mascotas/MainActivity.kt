package cl.duoc.mascotas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.*
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.duoc.mascotas.navigation.AppNavigation
import cl.duoc.mascotas.repository.PhotoStorage
import cl.duoc.mascotas.repository.SettingsRepository
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import cl.duoc.mascotas.ui.theme.MascotaTheme
import cl.duoc.mascotas.viewmodel.PetViewModel

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as MascotaApplication
        val factory = viewModelFactory { initializer {
            cl.duoc.mascotas.viewmodel.ChlorineViewModel(app, app.chlorineRepository)
        } }
        setContent {
            val vm: cl.duoc.mascotas.viewmodel.ChlorineViewModel = viewModel(factory = factory)
            val dark by SettingsRepository(app).darkTheme.collectAsStateWithLifecycle(false)
            MascotaTheme(dark) {
                val window = calculateWindowSizeClass(this)
                cl.duoc.mascotas.ui.screens.ChlorineScreens(vm)
            }
        }
    }
}
