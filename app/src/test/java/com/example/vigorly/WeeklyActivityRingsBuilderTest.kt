package com.example.vigorly

import com.example.vigorly.data.activity.DailyActivityDaySummary
import com.example.vigorly.data.activity.DailyActivityDetail
import com.example.vigorly.data.activity.WeeklyActivityRingsBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.util.Locale

class WeeklyActivityRingsBuilderTest {
    private val locale = Locale("es", "ES")
    private val monday = LocalDate.of(2026, 5, 25)

    @Test
    fun build_returnsSevenDaysStartingMondayInSpain() {
        val wednesday = LocalDate.of(2026, 5, 27)
        val days = WeeklyActivityRingsBuilder.build(
            history = emptyMap(),
            liveTodayDetail = DailyActivityDetail(),
            today = wednesday,
            locale = locale
        )
        assertEquals(7, days.size)
        assertEquals(monday, days.first().date)
        assertEquals(LocalDate.of(2026, 5, 31), days.last().date)
    }

    @Test
    fun build_marksFutureDaysWithoutActivity() {
        val wednesday = LocalDate.of(2026, 5, 27)
        val days = WeeklyActivityRingsBuilder.build(
            history = emptyMap(),
            liveTodayDetail = DailyActivityDetail(),
            today = wednesday,
            locale = locale
        )
        val future = days.filter { it.date.isAfter(wednesday) }
        assertTrue(future.isNotEmpty())
        future.forEach { day ->
            assertTrue(day.isFuture)
            assertFalse(day.hasActivity)
        }
    }

    @Test
    fun build_usesHistoryForPastDaysAndLiveForToday() {
        val wednesday = LocalDate.of(2026, 5, 27)
        val history = mapOf(
            "2026-05-26" to DailyActivityDaySummary(
                dateKey = "2026-05-26",
                moveProgress = 0.5f,
                exerciseProgress = 0.25f,
                standProgress = 0.75f
            )
        )
        val live = DailyActivityDetail(moveCalories = 500, exerciseMinutes = 30, standHours = 12)
        val days = WeeklyActivityRingsBuilder.build(
            history = history,
            liveTodayDetail = live,
            today = wednesday,
            locale = locale
        )
        val tuesday = days.first { it.date == LocalDate.of(2026, 5, 26) }
        val today = days.first { it.isToday }
        assertEquals(0.5f, tuesday.moveProgress, 0.001f)
        assertTrue(today.moveProgress > 0.9f)
    }

    @Test
    fun weekDatesFor_resetsEachCalendarWeek() {
        val week1 = WeeklyActivityRingsBuilder.weekDatesFor(
            LocalDate.of(2026, 5, 31),
            locale
        )
        val week2 = WeeklyActivityRingsBuilder.weekDatesFor(
            LocalDate.of(2026, 6, 1),
            locale
        )
        assertEquals(LocalDate.of(2026, 6, 1), week2.first())
        assertFalse(week2.contains(week1.last()))
    }
}
