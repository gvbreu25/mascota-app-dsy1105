package cl.duoc.mascotas.data

import androidx.room.Database
import androidx.room.RoomDatabase
import cl.duoc.mascotas.model.Pet

@Database(entities = [Pet::class], version = 1, exportSchema = true)
abstract class PetDatabase : RoomDatabase() {
    abstract fun petDao(): PetDao
}
