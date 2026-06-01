package com.example.vigorly

import com.example.vigorly.data.catalog.WorkoutCatalog
import com.example.vigorly.data.model.WorkoutType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkoutCatalogTest {

    @Test
    fun catalog_containsExpandedSpanishPrograms() {
        val workouts = WorkoutCatalog.allWorkouts()
        assertTrue(workouts.size >= 20)
        assertTrue(workouts.containsKey("titan_protocol"))
        assertTrue(workouts.containsKey("hiit_sprint"))
        assertTrue(workouts.containsKey("full_body_functional"))
        assertTrue(workouts.containsKey("tabata_16"))
        assertEquals("Protocolo Titán", workouts.getValue("titan_protocol").name)
    }

    @Test
    fun hiitSprint_isHiitType() {
        assertEquals(WorkoutType.HIIT, WorkoutCatalog.hiitSprint().type)
    }
}
