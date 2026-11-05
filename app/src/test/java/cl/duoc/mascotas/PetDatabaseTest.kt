package cl.duoc.mascotas

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import cl.duoc.mascotas.data.PetDatabase
import cl.duoc.mascotas.model.Pet
import cl.duoc.mascotas.repository.PetRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], application = android.app.Application::class)
class PetDatabaseTest {
    @Test fun crudSurvivesDatabaseReopen() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val filename = "persistence-test.db"
        context.deleteDatabase(filename)
        fun open() = Room.databaseBuilder(context, PetDatabase::class.java, filename).build()
        var db = open()
        try {
            var repository = PetRepository(db.petDao())
            val original = Pet(name = "Luna", species = "Gato", age = 2, owner = "Ana Pérez", phone = "912345678", vaccinated = false)
            val id = repository.save(original)
            assertTrue(id > 0)
            assertEquals("Luna", repository.pets.first().single().name)
            repository.save(original.copy(id = id, age = 3, vaccinated = true))
            db.close()
            db = open(); repository = PetRepository(db.petDao())
            assertEquals(3, repository.find(id)!!.age)
            assertTrue(repository.find(id)!!.vaccinated)
            repository.delete(id)
            assertTrue(repository.pets.first().isEmpty())
            assertNull(repository.find(id))
        } finally { db.close(); context.deleteDatabase(filename) }
    }
}
