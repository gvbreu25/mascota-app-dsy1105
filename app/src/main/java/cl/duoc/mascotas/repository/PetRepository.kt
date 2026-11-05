package cl.duoc.mascotas.repository

import cl.duoc.mascotas.data.PetDao
import cl.duoc.mascotas.model.Pet

class PetRepository(private val dao: PetDao) {
    val pets = dao.observeAll()
    suspend fun find(id: Long) = dao.find(id)
    suspend fun save(pet: Pet): Long = if (pet.id == 0L) dao.insert(pet) else {
        check(dao.update(pet) == 1) { "El registro ya no existe." }
        pet.id
    }
    suspend fun delete(id: Long) { check(dao.delete(id) == 1) }
}
