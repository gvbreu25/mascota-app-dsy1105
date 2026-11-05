package cl.duoc.mascotas.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage

@Composable fun PetPhoto(uri: String?, name: String, modifier: Modifier = Modifier) {
    Box(modifier.clip(RoundedCornerShape(20.dp)).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
        if (uri == null) Icon(Icons.Default.Pets, "Sin foto", Modifier.size(42.dp), tint = MaterialTheme.colorScheme.primary)
        else SubcomposeAsyncImage(model = uri, contentDescription = "Foto de $name", contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(), loading = { CircularProgressIndicator(Modifier.size(24.dp)) },
            error = { Text("Foto no disponible", style = MaterialTheme.typography.labelSmall) })
    }
}
