package com.example.vigorly

import com.example.vigorly.util.StreakCalculator
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class StreakCalculatorTest {

    @Test
    fun emptyDates_returnsZero() {
        assertEquals(0, StreakCalculator.consecutiveActiveDays(emptySet()))
    }

    @Test
    fun countsFromToday_whenActiveToday() {
        val today = LocalDate.of(2026, 5, 29)
        val dates = setOf(
            today,
            today.minusDays(1),
            today.minusDays(2)
        )
        assertEquals(3, StreakCalculator.consecutiveActiveDays(dates, today))
    }

    @Test
    fun countsFromYesterday_whenInactiveToday() {
        val today = LocalDate.of(2026, 5, 29)
        val dates = setOf(today.minusDays(1), today.minusDays(2))
        assertEquals(2, StreakCalculator.consecutiveActiveDays(dates, today))
    }

    @Test
    fun brokenStreak_returnsZeroWhenGap() {
        val today = LocalDate.of(2026, 5, 29)
        val dates = setOf(today, today.minusDays(2))
        assertEquals(1, StreakCalculator.consecutiveActiveDays(dates, today))
    }
}
