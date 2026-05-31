package com.example.vigorly

import com.example.vigorly.data.activity.DailyGoalsCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class DailyGoalsCalculatorTest {

    @Test
    fun fullGoals_reachOneHundredPercent() {
        val goals = DailyGoalsCalculator.build(
            steps = DailyGoalsCalculator.STEPS_GOAL,
            workoutCalories = 100,
            exerciseMinutes = DailyGoalsCalculator.EXERCISE_MINUTES_GOAL,
            standHours = DailyGoalsCalculator.STAND_HOURS_GOAL
        )
        assertEquals(100, goals.dailyGoalPercent)
        assertEquals(1f, goals.moveProgress, 0.01f)
        assertEquals(1f, goals.exerciseProgress, 0.01f)
        assertEquals(1f, goals.standProgress, 0.01f)
    }

    @Test
    fun emptyGoals_startAtZero() {
        val goals = DailyGoalsCalculator.build(0, 0, 0, 0)
        assertEquals(0, goals.dailyGoalPercent)
    }
}
