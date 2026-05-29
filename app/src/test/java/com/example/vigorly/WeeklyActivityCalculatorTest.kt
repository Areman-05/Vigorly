package com.example.vigorly

import com.example.vigorly.data.model.WorkoutHistoryItem
import com.example.vigorly.util.WeeklyActivityCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class WeeklyActivityCalculatorTest {
    @Test
    fun fromHistory_returnsSevenDays() {
        val history = listOf(
            WorkoutHistoryItem("1", "A", "Today", 30, 100, "fitness_center"),
            WorkoutHistoryItem("2", "B", "Yesterday", 45, 200, "fitness_center")
        )
        val days = WeeklyActivityCalculator.fromHistory(history)
        assertEquals(7, days.size)
        assertEquals("Mon", days.first().dayLabel)
    }
}
