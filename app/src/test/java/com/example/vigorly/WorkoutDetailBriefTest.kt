package com.example.vigorly

import com.example.vigorly.data.catalog.WorkoutCatalog
import com.example.vigorly.data.model.WorkoutType
import com.example.vigorly.util.WorkoutDetailBrief
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Locale

class WorkoutDetailBriefTest {

    private val es = Locale("es", "ES")
    private val en = Locale.ENGLISH

    @Test
    fun forWorkout_es_hasAtMostFourLines() {
        WorkoutCatalog.allWorkouts().values.forEach { workout ->
            val brief = WorkoutDetailBrief.forWorkout(workout, es)
            val lines = brief.lines()
            assertEquals("Expected 4 lines for ${workout.id}", 4, lines.size)
            assertTrue(lines.all { it.isNotBlank() })
        }
    }

    @Test
    fun forWorkout_en_hasAtMostFourLines() {
        WorkoutCatalog.allWorkouts().values.forEach { workout ->
            val brief = WorkoutDetailBrief.forWorkout(workout, en)
            assertEquals(4, brief.lines().size)
        }
    }

    @Test
    fun forWorkout_strength_mentionsForceAndFocus() {
        val workout = WorkoutCatalog.allWorkouts().values.first { it.type == WorkoutType.STRENGTH }
        val brief = WorkoutDetailBrief.forWorkout(workout, es)
        assertTrue(brief.startsWith("Fuerza:"))
        assertTrue(brief.contains("Enfoque:"))
        assertTrue(brief.contains("${workout.durationMinutes} min"))
        assertTrue(brief.contains("ejercicios"))
    }

    @Test
    fun forWorkout_hiit_usesHiitLine() {
        val workout = WorkoutCatalog.hiitSprint()
        val brief = WorkoutDetailBrief.forWorkout(workout, es)
        assertTrue(brief.lines().first().startsWith("HIIT:"))
    }

    @Test
    fun forWorkout_includesIntensityWhenKnown() {
        val workout = WorkoutCatalog.allWorkouts().values.first {
            it.intensity.contains("alta", ignoreCase = true) ||
                it.intensity.contains("high", ignoreCase = true)
        }
        val brief = WorkoutDetailBrief.forWorkout(workout, es)
        assertTrue(brief.contains("intensidad alta"))
    }

    @Test
    fun forWorkout_en_usesEnglishPrefixes() {
        val workout = WorkoutCatalog.allWorkouts().values.first()
        val brief = WorkoutDetailBrief.forWorkout(workout, en)
        assertFalse(brief.contains("Enfoque:"))
        assertTrue(brief.contains("Focus:") || brief.contains("Focus follows"))
        assertTrue(brief.lines().last().contains("form before speed"))
    }

    @Test
    fun forWorkout_coversEveryType() {
        WorkoutType.entries.forEach { type ->
            val workout = WorkoutCatalog.allWorkouts().values.firstOrNull { it.type == type }
                ?: return@forEach
            val brief = WorkoutDetailBrief.forWorkout(workout, es)
            assertEquals(4, brief.lines().size)
            assertFalse(brief.isBlank())
        }
    }
}
