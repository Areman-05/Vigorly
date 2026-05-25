package com.example.vigorly

import com.example.vigorly.data.catalog.WorkoutCatalog
import com.example.vigorly.data.model.WorkoutType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkoutCatalogTest {

    @Test
    fun catalog_allWorkouts_containsThreePrograms() {
        val workouts = WorkoutCatalog.allWorkouts()
        assertEquals(5, workouts.size)
        assertTrue(workouts.containsKey("titan_protocol"))
        assertTrue(workouts.containsKey("hiit_sprint"))
        assertTrue(workouts.containsKey("recovery_yoga"))
        assertTrue(workouts.containsKey("upper_body_power"))
        assertTrue(workouts.containsKey("morning_swim"))
    }

    @Test
    fun hiitSprint_isHiitType() {
        assertEquals(WorkoutType.HIIT, WorkoutCatalog.hiitSprint().type)
    }
}
