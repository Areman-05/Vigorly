package com.example.vigorly

import com.example.vigorly.data.activity.DailyGoalsCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class DailyGoalsTest {

    @Test
    fun dailyGoalPercent_averagesRingProgress() {
        val goals = DailyGoalsCalculator.build(
            steps = 5000,
            workoutCalories = 150,
            exerciseMinutes = 15,
            standHours = 6
        )
        assertEquals(
            ((goals.moveProgress + goals.exerciseProgress + goals.standProgress) / 3f * 100).toInt(),
            goals.dailyGoalPercent
        )
    }
}
