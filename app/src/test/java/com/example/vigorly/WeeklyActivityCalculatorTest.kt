package com.example.vigorly

import com.example.vigorly.data.model.WorkoutHistoryItem
import com.example.vigorly.util.WeeklyActivityCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WeeklyActivityCalculatorTest {
    @Test
    fun fromHistory_returnsSevenDaysInSpanish() {
        val days = WeeklyActivityCalculator.fromHistory(emptyList())
        assertEquals(7, days.size)
        assertEquals("Lun", days.first().dayLabel)
        assertEquals("Dom", days.last().dayLabel)
        assertTrue(days.any { it.isToday })
    }

    @Test
    fun fromHistory_bucketsMinutesForCurrentWeek() {
        val now = System.currentTimeMillis()
        val history = listOf(
            WorkoutHistoryItem(
                "1", "A", "Hoy", 30, 100, "fitness_center",
                completedAtMillis = now
            )
        )
        val days = WeeklyActivityCalculator.fromHistory(history)
        val total = days.sumOf { it.minutes }
        assertEquals(30, total)
    }
}
