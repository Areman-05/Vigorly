package com.example.vigorly

import com.example.vigorly.data.model.WorkoutHistoryItem
import com.example.vigorly.util.HistorySummaryCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class HistorySummaryTest {

    @Test
    fun totals_aggregateSessions() {
        val items = listOf(
            WorkoutHistoryItem("1", "A", "Today", 30, 200, "fitness_center"),
            WorkoutHistoryItem("2", "B", "Today", 45, 300, "fitness_center")
        )
        val summary = HistorySummaryCalculator.from(items)
        assertEquals(2, summary.totalSessions)
        assertEquals(75, summary.totalMinutes)
        assertEquals(500, summary.totalCalories)
    }
}
