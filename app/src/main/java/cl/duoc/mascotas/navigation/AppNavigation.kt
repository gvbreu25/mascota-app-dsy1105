package cl.duoc.mascotas.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import cl.duoc.mascotas.ui.screens.*
import cl.duoc.mascotas.viewmodel.PetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun AppNavigation(vm: PetViewModel, expanded: Boolean) {
    val nav = rememberNavController()
    val entry by nav.currentBackStackEntryAsState()
    val list by vm.listState.collectAsStateWithLifecycle()
    val form by vm.form.collectAsStateWithLifecycle()
    val operation by vm.operation.collectAsStateWithLifecycle()
    val dark by vm.darkTheme.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(operation) { operation?.let { snackbar.showSnackbar(it); vm.clearOperation() } }
    LaunchedEffect(form.savedId) {
        form.savedId?.let { saved ->
            nav.navigate("detail/$saved") { popUpTo("list"); launchSingleTop = true }
            snackbar.showSnackbar("Mascota guardada correctamente")
            vm.consumeSaved()
        }
    }
    Scaffold(topBar = {
        TopAppBar(title = { Text("Mis mascotas") }, navigationIcon = {
            if (entry?.destination?.route != "list" && entry != null)
                IconButton({ nav.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") }
            else Icon(Icons.Default.Pets, null, Modifier.padding(start = 16.dp))
        }, actions = {
            IconButton({ vm.setDarkTheme(!dark) }) {
                Icon(if (dark) Icons.Default.LightMode else Icons.Default.DarkMode,
                    if (dark) "Activar tema claro" else "Activar tema oscuro")
            }
        })
    }, snackbarHost = { SnackbarHost(snackbar) }) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.TopCenter) {
            NavHost(nav, startDestination = "list", modifier = Modifier.widthIn(max = 1100.dp).fillMaxSize()) {
                composable("list") {
                    ListScreen(list, expanded, { vm.resetForm(); nav.navigate("form/-1") }, { nav.navigate("detail/$it") })
                }
                composable("form/{id}", arguments = listOf(navArgument("id") { type = NavType.LongType })) { backStack ->
                    val id = backStack.arguments!!.getLong("id")
                    LaunchedEffect(id) { vm.openForm(id) }
                    FormScreen(form, vm, expanded)
                }
                composable("detail/{id}", arguments = listOf(navArgument("id") { type = NavType.LongType })) { backStack ->
                    val id = backStack.arguments!!.getLong("id")
                    DetailScreen(list.pets.find { it.id == id }, list.loading,
                        onEdit = { nav.navigate("form/$id") },
                        onDelete = { vm.delete(id) { nav.popBackStack("list", false) } })
                }
            }
        }
    }
}
