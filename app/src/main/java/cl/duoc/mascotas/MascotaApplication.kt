package cl.duoc.mascotas

import android.app.Application
import androidx.room.Room
import cl.duoc.mascotas.data.PetDatabase
import cl.duoc.mascotas.repository.PetRepository

class MascotaApplication : Application() {
    val chlorineRepository by lazy {
        val db=Room.databaseBuilder(this, cl.duoc.mascotas.data.ChlorineDatabase::class.java,"chlorine.db").build()
        cl.duoc.mascotas.data.ChlorineRepository(db.dao(),cl.duoc.mascotas.data.SimulatedMonitoringApi())
    }
    val database by lazy {
        Room.databaseBuilder(this, PetDatabase::class.java, "mascotas.db").build()
    }
    val repository by lazy { PetRepository(database.petDao()) }
}
