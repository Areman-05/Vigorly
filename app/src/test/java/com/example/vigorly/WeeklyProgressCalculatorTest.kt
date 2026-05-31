package com.example.vigorly

import com.example.vigorly.data.model.WeeklyGoal
import com.example.vigorly.util.WeeklyProgressCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class WeeklyProgressCalculatorTest {

    @Test
    fun remainingSessions_neverNegative() {
        val goal = WeeklyGoal(targetSessions = 5, completedSessions = 7)
        assertEquals(0, WeeklyProgressCalculator.remainingSessions(goal))
    }

    @Test
    fun percent_roundsDownProgress() {
        val goal = WeeklyGoal(targetSessions = 8, completedSessions = 3)
        assertEquals(37, WeeklyProgressCalculator.percent(goal))
    }

    @Test
    fun displayCompletedSessions_capsAtTarget() {
        val goal = WeeklyGoal(targetSessions = 5, completedSessions = 7)
        assertEquals(5, WeeklyProgressCalculator.displayCompletedSessions(goal))
        assertEquals(100, WeeklyProgressCalculator.percent(goal))
    }
}
