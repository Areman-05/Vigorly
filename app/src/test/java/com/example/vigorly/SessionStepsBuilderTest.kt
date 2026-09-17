package com.example.vigorly

import com.example.vigorly.data.catalog.WorkoutCatalog
import com.example.vigorly.util.SessionStepsBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SessionStepsBuilderTest {

    @Test
    fun build_includesWarmupsThenExercises() {
        val workout = WorkoutCatalog.allWorkouts().values.first()
        val steps = SessionStepsBuilder.build(workout)
        assertTrue(steps.isNotEmpty())
        assertTrue(steps.any { it.isWarmup })
        assertTrue(steps.any { !it.isWarmup })
        val firstExerciseIndex = steps.indexOfFirst { !it.isWarmup }
        assertTrue(firstExerciseIndex > 0)
        assertTrue(steps.take(firstExerciseIndex).all { it.isWarmup })
        assertTrue(steps.drop(firstExerciseIndex).none { it.isWarmup })
    }

    @Test
    fun build_everyStepHasCueAndPositiveDuration() {
        WorkoutCatalog.allWorkouts().values.take(12).forEach { workout ->
            val steps = SessionStepsBuilder.build(workout)
            assertTrue(steps.isNotEmpty())
            steps.forEach { step ->
                assertTrue("${workout.id}/${step.id} cue blank", step.cue.isNotBlank())
                assertTrue("${workout.id}/${step.id} duration", step.durationSeconds > 0)
                assertTrue(step.name.isNotBlank())
            }
        }
    }

    @Test
    fun build_exerciseCountMatchesCatalogBlocks() {
        val workout = WorkoutCatalog.hiitSprint()
        val expectedExercises = workout.blocks.sumOf { it.exercises.size }
        val steps = SessionStepsBuilder.build(workout)
        assertEquals(expectedExercises, steps.count { !it.isWarmup })
    }

    @Test
    fun exerciseDurationFor_clampsBetween45And180() {
        val short = WorkoutCatalog.allWorkouts().values.minBy { it.durationMinutes }
        val long = WorkoutCatalog.allWorkouts().values.maxBy { it.durationMinutes }
        val shortSecs = SessionStepsBuilder.exerciseDurationFor(short, 20)
        val longSecs = SessionStepsBuilder.exerciseDurationFor(long, 1)
        assertTrue(shortSecs in 45..180)
        assertTrue(longSecs in 45..180)
    }

    @Test
    fun build_idsAreUniqueWithinSession() {
        val workout = WorkoutCatalog.allWorkouts().values.first()
        val steps = SessionStepsBuilder.build(workout)
        assertEquals(steps.size, steps.map { it.id }.toSet().size)
        assertFalse(steps.any { it.detailLabel.isBlank() && !it.isWarmup })
    }
}
