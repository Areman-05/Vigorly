package com.example.vigorly

import com.example.vigorly.data.activity.DailyActivityDetailBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DailyActivityDetailBuilderTest {

    @Test
    fun build_aggregatesHourlyMoveAndStand() {
        val stepsPerHour = IntArray(24)
        stepsPerHour[9] = 1000
        val exercisePerHour = IntArray(24)
        exercisePerHour[18] = 25
        val workoutCalPerHour = IntArray(24)
        workoutCalPerHour[18] = 120

        val detail = DailyActivityDetailBuilder.build(
            stepsPerHour = stepsPerHour,
            exerciseMinutesPerHour = exercisePerHour,
            workoutCaloriesPerHour = workoutCalPerHour,
            standHours = setOf(9, 18),
            totalSteps = 1000,
            totalExerciseMinutes = 25,
            totalWorkoutCalories = 120
        )

        assertEquals(160, detail.moveCalories)
        assertEquals(25, detail.exerciseMinutes)
        assertEquals(2, detail.standHours)
        assertTrue(detail.standByHour[9])
        assertTrue(detail.standByHour[18])
        assertEquals(1000, detail.steps)
    }
}
