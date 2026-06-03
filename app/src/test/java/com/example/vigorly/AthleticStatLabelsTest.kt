package com.example.vigorly

import com.example.vigorly.util.AthleticStatKeys
import com.example.vigorly.util.AthleticStatLabels
import org.junit.Assert.assertEquals
import org.junit.Test

class AthleticStatLabelsTest {

    @Test
    fun normalizeKey_mapsSpanishDisplayLabels() {
        assertEquals(AthleticStatKeys.STRENGTH, AthleticStatLabels.normalizeKey("Fuerza"))
        assertEquals(AthleticStatKeys.SPEED, AthleticStatLabels.normalizeKey("Velocidad"))
    }

    @Test
    fun normalizeKey_mapsEnglishKeysAndLegacyLabels() {
        assertEquals(AthleticStatKeys.POWER, AthleticStatLabels.normalizeKey("power"))
        assertEquals(AthleticStatKeys.ENDURANCE, AthleticStatLabels.normalizeKey("Endurance"))
    }
}
