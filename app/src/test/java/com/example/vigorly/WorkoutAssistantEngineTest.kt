package com.example.vigorly

import com.example.vigorly.data.model.WorkoutType
import com.example.vigorly.util.WorkoutAssistantEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkoutAssistantEngineTest {

    @Test
    fun parse_recovery_setsRecoveryType() {
        val r = WorkoutAssistantEngine.parse("algo suave para recuperarme")
        assertEquals(WorkoutType.RECOVERY, r.type)
        assertTrue(r.lowIntensityOnly)
    }

    @Test
    fun parse_legs_addsSearchTerms() {
        val r = WorkoutAssistantEngine.parse("entreno de piernas y glúteos")
        assertTrue(r.searchQuery.contains("piernas"))
    }

    @Test
    fun preset_hiit_short_limitsDuration() {
        val r = WorkoutAssistantEngine.fromPreset(WorkoutAssistantEngine.QuickPreset.HIIT_SHORT)
        assertEquals(WorkoutType.HIIT, r.type)
        assertEquals(30, r.maxDurationMinutes)
    }
}
