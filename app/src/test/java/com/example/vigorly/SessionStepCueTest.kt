package com.example.vigorly

import com.example.vigorly.util.SessionStepCue
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Locale

class SessionStepCueTest {

    private val es = Locale("es", "ES")
    private val en = Locale.ENGLISH

    @Test
    fun forStep_knownMovements_returnSpecificSpanishCues() {
        val cases = listOf(
            "Sentadilla goblet" to "cuádriceps",
            "Peso muerto rumano" to "cadena posterior",
            "Zancada caminando" to "glúteo",
            "Press banca" to "pectoral",
            "Press militar" to "deltoides",
            "Flexiones" to "pectoral",
            "Remo con mancuerna" to "dorsal",
            "Dominadas" to "dorsal",
            "Plancha" to "core",
            "Burpee" to "cuerpo completo",
            "Mountain climbers" to "core",
            "Jumping jacks" to "coordinación",
            "Carrera suave" to "cardiovascular",
            "Marcha en el sitio" to "circulación",
            "Puente de glúteos" to "glúteo",
            "Curl de bíceps" to "bíceps",
            "Fondos de tríceps" to "tríceps",
            "Crunch abdominal" to "abdominal",
            "Respiración diafragmática" to "nariz",
            "Brazada crol" to "brazada",
            "Kettlebell swing" to "cadera",
            "Step-up" to "cajón"
        )
        cases.forEach { (name, needle) ->
            val cue = SessionStepCue.forStep(name, "3x10", isWarmup = false, locale = es)
            assertTrue("$name should mention $needle", cue.contains(needle, ignoreCase = true))
            assertFalse(cue.isBlank())
        }
    }

    @Test
    fun forStep_knownMovements_returnEnglishCues() {
        val cue = SessionStepCue.forStep("Barbell squat", "4x8", isWarmup = false, locale = en)
        assertTrue(cue.contains("quads", ignoreCase = true))
        assertTrue(cue.contains("glutes", ignoreCase = true))
    }

    @Test
    fun forStep_unknownExercise_usesGenericWithDose() {
        val cue = SessionStepCue.forStep(
            "Movimiento inventado X",
            "40s",
            isWarmup = false,
            locale = es
        )
        assertTrue(cue.contains("40s"))
        assertTrue(cue.contains("técnica limpia"))
    }

    @Test
    fun forStep_warmupGeneric_prioritizesActivation() {
        val cue = SessionStepCue.forStep(
            "Movimiento inventado Y",
            "30s",
            isWarmup = true,
            locale = es
        )
        assertTrue(cue.contains("activación"))
        assertFalse(cue.contains("técnica limpia"))
    }

    @Test
    fun forStep_rotationAndMobilityAndStretch() {
        assertTrue(
            SessionStepCue.forStep("Rotación de cadera", "8 ciclos", true, es)
                .contains("articulación", ignoreCase = true)
        )
        assertTrue(
            SessionStepCue.forStep("Movilidad de tobillo", "45s", true, es)
                .contains("rango", ignoreCase = true)
        )
        assertTrue(
            SessionStepCue.forStep("Estiramiento de isquios", "30s", true, es)
                .contains("tensión", ignoreCase = true)
        )
    }
}
