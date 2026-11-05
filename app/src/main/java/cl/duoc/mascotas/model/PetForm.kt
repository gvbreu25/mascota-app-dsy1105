package cl.duoc.mascotas.model

data class PetForm(
    val id: Long = 0,
    val name: String = "",
    val species: String = "",
    val age: String = "",
    val owner: String = "",
    val phone: String = "",
    val vaccinated: Boolean = false,
    val photoUri: String? = null,
    val touched: Set<String> = emptySet(),
    val errors: Map<String, String> = emptyMap(),
    val attempted: Boolean = false,
    val busy: Boolean = false,
    val message: String? = null,
    val savedId: Long? = null
)
