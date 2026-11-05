package cl.duoc.mascotas

import android.app.Application
import androidx.room.Room
import cl.duoc.mascotas.data.PetDatabase
import cl.duoc.mascotas.repository.PetRepository

class MascotaApplication : Application() {
    val database by lazy {
        Room.databaseBuilder(this, PetDatabase::class.java, "mascotas.db").build()
    }
    val repository by lazy { PetRepository(database.petDao()) }
}
