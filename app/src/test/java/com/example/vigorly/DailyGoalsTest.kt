package com.example.vigorly

import com.example.vigorly.data.repository.VigorlyRepository
import org.junit.Assert.assertEquals
import org.junit.Test

class DailyGoalsTest {

    @Test
    fun dailyGoalPercent_averagesRingProgress() {
        val goals = VigorlyRepository.defaultDailyGoals()
        assertEquals(69, goals.dailyGoalPercent)
    }
}
