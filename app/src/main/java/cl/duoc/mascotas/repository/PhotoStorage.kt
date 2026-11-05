package cl.duoc.mascotas.repository

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

/** Las imágenes se copian a almacenamiento privado: no dependemos de permisos temporales. */
class PhotoStorage(private val context: Context) {
    private val directory get() = File(context.filesDir, "photos").apply { mkdirs() }
    fun createCapture(): Uri = uri(File.createTempFile("camera_", ".jpg", directory))
    private fun uri(file: File): Uri = FileProvider.getUriForFile(
        context, "${context.packageName}.fileprovider", file
    )
    suspend fun importImage(source: Uri): String = withContext(Dispatchers.IO) {
        val file = File(directory, "${UUID.randomUUID()}.img")
        try {
            context.contentResolver.openInputStream(source).use { input ->
                requireNotNull(input) { "No se pudo abrir la imagen." }
                file.outputStream().use { output ->
                    val buffer = ByteArray(8192)
                    var total = 0L
                    while (true) {
                        val read = input.read(buffer)
                        if (read == -1) break
                        total += read
                        require(total <= 20 * 1024 * 1024) { "La foto debe pesar como máximo 20 MB." }
                        output.write(buffer, 0, read)
                    }
                }
            }
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(file.absolutePath, options)
            require(options.outWidth > 0 && options.outHeight > 0) { "Selecciona una imagen válida." }
            uri(file).toString()
        } catch (e: Exception) { file.delete(); throw e }
    }
    suspend fun validateCapture(source: Uri): String = withContext(Dispatchers.IO) {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(source).use {
            BitmapFactory.decodeStream(it, null, options)
        }
        require(options.outWidth > 0 && options.outHeight > 0) { "La cámara no guardó una foto válida." }
        source.toString()
    }
    fun discardCapture(uri: String) {
        runCatching { context.contentResolver.delete(Uri.parse(uri), null, null) }
    }
}
