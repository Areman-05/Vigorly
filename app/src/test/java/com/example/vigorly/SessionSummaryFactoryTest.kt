package com.example.vigorly

import com.example.vigorly.data.catalog.WorkoutCatalog
import com.example.vigorly.data.model.WorkoutSessionState
import com.example.vigorly.data.repository.SessionSummaryFactory
import com.example.vigorly.util.SessionStepsBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SessionSummaryFactoryTest {
    @Test
    fun from_mapsSessionFields() {
        val workout = WorkoutCatalog.allWorkouts().values.first()
        val steps = SessionStepsBuilder.build(workout)
        val session = WorkoutSessionState(
            workoutId = workout.id,
            workoutName = workout.name,
            currentExerciseIndex = 2,
            totalExercises = steps.size,
            elapsedSeconds = 120,
            isPaused = false,
            completedExerciseIds = steps.take(2).map { it.id }.toSet()
        )
        val summary = SessionSummaryFactory.from(
            session = session,
            workout = workout,
            totalWorkoutsAfter = 6,
            streakDays = 3,
            weeklyCompleted = 2,
            weeklyTarget = 4
        )
        assertEquals(workout.id, summary.workoutId)
        assertEquals(2, summary.exercisesCompleted)
        assertEquals(120, summary.elapsedSeconds)
        assertEquals(2, summary.levelBefore)
        assertEquals(2, summary.levelAfter)
        assertTrue(summary.totalExercises >= 2)
    }
}
