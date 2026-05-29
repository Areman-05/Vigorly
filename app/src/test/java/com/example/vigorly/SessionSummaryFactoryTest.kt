package com.example.vigorly

import com.example.vigorly.data.catalog.WorkoutCatalog
import com.example.vigorly.data.model.WorkoutSessionState
import com.example.vigorly.data.repository.SessionSummaryFactory
import org.junit.Assert.assertEquals
import org.junit.Test

class SessionSummaryFactoryTest {
    @Test
    fun from_mapsSessionFields() {
        val workout = WorkoutCatalog.allWorkouts().first()
        val session = WorkoutSessionState(
            workoutId = workout.id,
            workoutName = workout.name,
            currentExerciseIndex = 2,
            totalExercises = 5,
            elapsedSeconds = 120,
            isPaused = false,
            completedExerciseIds = setOf("e1", "e2")
        )
        val summary = SessionSummaryFactory.from(session, workout)
        assertEquals(workout.id, summary.workoutId)
        assertEquals(2, summary.exercisesCompleted)
        assertEquals(120, summary.elapsedSeconds)
    }
}
