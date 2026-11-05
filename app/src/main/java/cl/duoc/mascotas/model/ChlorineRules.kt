package cl.duoc.mascotas.model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class ChlorineRanges(val criticalLow: Double, val normalLow: Double, val normalHigh: Double, val criticalHigh: Double) {
    fun valid() = listOf(criticalLow, normalLow, normalHigh, criticalHigh).all { it.isFinite() && it >= 0 } &&
        criticalLow < normalLow && normalLow < normalHigh && normalHigh < criticalHigh
    fun status(value: Double): String {
        require(valid() && value.isFinite() && value >= 0)
        return if (value < criticalLow || value > criticalHigh) "CRÍTICO"
        else if (value < normalLow || value > normalHigh) "ADVERTENCIA" else "NORMAL"
    }
}
object ChlorineRules {
    val format: DateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm").withResolverStyle(java.time.format.ResolverStyle.STRICT)
    fun number(value: String) = value.trim().replace(',', '.').toDoubleOrNull()?.takeIf { it.isFinite() && it >= 0 }
    fun validate(point: Int, count: Int, value: String, date: String, ranges: ChlorineRanges?): Map<String,String> = buildMap {
        if (point !in 0 until count) put("point", "Selecciona un punto de medición.")
        if (number(value) == null) put("value", "Ingresa un número finito mayor o igual a cero.")
        val parsed = runCatching { LocalDateTime.parse(date, format) }.getOrNull()
        if (parsed == null || parsed.isAfter(LocalDateTime.now().plusMinutes(1))) put("date", "Usa yyyy-MM-dd HH:mm, sin fecha futura.")
        if (ranges?.valid() != true) put("ranges", "Configura y guarda los cuatro umbrales antes de registrar.")
    }
}
