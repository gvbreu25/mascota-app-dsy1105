package cl.duoc.mascotas.model

/** Reglas del contexto propuesto; no son reglas impuestas por el documento del profesor. */
object PetValidator {
    val speciesOptions = listOf("Perro", "Gato", "Otro")
    fun validate(form: PetForm): Map<String, String> = buildMap {
        if (form.name.trim().length !in 2..30) put("name", "Escribe entre 2 y 30 caracteres.")
        if (form.species !in speciesOptions) put("species", "Selecciona una especie.")
        val age = form.age.toIntOrNull()
        if (age == null || age !in 0..30) put("age", "Ingresa años enteros entre 0 y 30.")
        if (form.owner.trim().length !in 3..60) put("owner", "Escribe entre 3 y 60 caracteres.")
        if (!Regex("^[0-9]{9}$").matches(form.phone.trim())) put("phone", "Ingresa 9 dígitos, sin espacios ni +56.")
    }
}
