package cl.duoc.mascotas.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Light = lightColorScheme(
    primary = Color(0xFF286658), onPrimary = Color.White,
    primaryContainer = Color(0xFFD8EEE4), onPrimaryContainer = Color(0xFF103C32),
    secondary = Color(0xFF735944), background = Color(0xFFF7F8F3),
    surface = Color(0xFFF7F8F3), surfaceVariant = Color(0xFFE4E9E1)
)
private val Dark = darkColorScheme(primary = Color(0xFFA0D1BE), primaryContainer = Color(0xFF174C40))
@Composable fun MascotaTheme(dark: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (dark) Dark else Light, content = content)
}
