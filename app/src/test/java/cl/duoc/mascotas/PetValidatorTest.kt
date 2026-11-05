package cl.duoc.mascotas

import cl.duoc.mascotas.model.PetForm
import cl.duoc.mascotas.model.PetValidator
import org.junit.Assert.*
import org.junit.Test

class PetValidatorTest {
    private val valid = PetForm(name = "Luna", species = "Gato", age = "2", owner = "Ana Pérez", phone = "912345678")
    @Test fun validFormHasNoErrors() { assertTrue(PetValidator.validate(valid).isEmpty()) }
    @Test fun emptyFormReportsAllRequiredFields() {
        assertEquals(setOf("name", "species", "age", "owner", "phone"), PetValidator.validate(PetForm()).keys)
    }
    @Test fun ageRejectsNegativeDecimalTextAndOverflow() {
        listOf("-1", "31", "2.5", "abc", "99999999999").forEach {
            assertTrue("Should reject $it", "age" in PetValidator.validate(valid.copy(age = it)))
        }
        listOf("0", "30").forEach { assertFalse("age" in PetValidator.validate(valid.copy(age = it))) }
    }
    @Test fun phoneRequiresNineDigits() {
        listOf("9123", "+56912345678", "91234abcd", "912 345678").forEach {
            assertTrue("phone" in PetValidator.validate(valid.copy(phone = it)))
        }
    }
    @Test fun whitespaceOnlyNamesAndUnknownSpeciesAreInvalid() {
        val errors = PetValidator.validate(valid.copy(name = "  ", owner = "  ", species = "Caballo"))
        assertEquals(setOf("name", "owner", "species"), errors.keys)
    }
}
