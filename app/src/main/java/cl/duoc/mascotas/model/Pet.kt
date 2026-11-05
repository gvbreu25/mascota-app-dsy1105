package cl.duoc.mascotas.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pets")
data class Pet(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val species: String,
    val age: Int,
    val owner: String,
    val phone: String,
    val vaccinated: Boolean,
    val photoUri: String? = null
)
