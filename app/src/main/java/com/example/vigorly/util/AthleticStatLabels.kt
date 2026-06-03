package com.example.vigorly.util

import com.example.vigorly.data.model.AthleticStat

object AthleticStatKeys {
    const val STRENGTH = "strength"
    const val ENDURANCE = "endurance"
    const val MOBILITY = "mobility"
    const val SPEED = "speed"
    const val POWER = "power"
    const val STAMINA = "stamina"
}

object AthleticStatLabels {

    private val legacyToKey = mapOf(
        "Strength" to AthleticStatKeys.STRENGTH,
        "Endurance" to AthleticStatKeys.ENDURANCE,
        "Mobility" to AthleticStatKeys.MOBILITY,
        "Speed" to AthleticStatKeys.SPEED,
        "Power" to AthleticStatKeys.POWER,
        "Stamina" to AthleticStatKeys.STAMINA
    )

    private val spanishLabels = mapOf(
        AthleticStatKeys.STRENGTH to "Fuerza",
        AthleticStatKeys.ENDURANCE to "Resistencia",
        AthleticStatKeys.MOBILITY to "Movilidad",
        AthleticStatKeys.SPEED to "Velocidad",
        AthleticStatKeys.POWER to "Potencia",
        AthleticStatKeys.STAMINA to "Estamina"
    )

    private val spanishToKey = spanishLabels.entries.associate { (key, display) ->
        display.lowercase() to key
    }

    fun normalizeKey(label: String): String =
        legacyToKey[label]
            ?: spanishToKey[label.lowercase()]
            ?: label.lowercase()

    fun displayLabel(label: String): String =
        spanishLabels[normalizeKey(label)] ?: label

    fun forDisplay(stats: List<AthleticStat>): List<AthleticStat> =
        stats.map { stat ->
            stat.copy(label = displayLabel(stat.label))
        }

    fun dominantStat(stats: List<AthleticStat>): AthleticStat? =
        stats.maxByOrNull { it.value }
}
