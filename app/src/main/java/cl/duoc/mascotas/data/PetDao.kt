package cl.duoc.mascotas.data

import androidx.room.*
import cl.duoc.mascotas.model.Pet
import kotlinx.coroutines.flow.Flow

@Dao
interface PetDao {
    @Query("SELECT * FROM pets ORDER BY name COLLATE NOCASE, id")
    fun observeAll(): Flow<List<Pet>>
    @Query("SELECT * FROM pets WHERE id = :id")
    suspend fun find(id: Long): Pet?
    @Insert suspend fun insert(pet: Pet): Long
    @Update suspend fun update(pet: Pet): Int
    @Query("DELETE FROM pets WHERE id = :id")
    suspend fun delete(id: Long): Int
}
