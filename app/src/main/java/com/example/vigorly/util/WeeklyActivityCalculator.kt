package com.example.vigorly.util

import com.example.vigorly.data.model.WorkoutHistoryItem
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

data class WeeklyDayMinutes(
    val dayLabel: String,
    val minutes: Int,
    val isToday: Boolean = false
)

object WeeklyActivityCalculator {
    private val dayLabelsEs = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")

    fun fromHistory(
        history: List<WorkoutHistoryItem>,
        zone: ZoneId = ZoneId.systemDefault(),
        referenceDate: LocalDate = LocalDate.now(zone)
    ): List<WeeklyDayMinutes> {
        val weekStart = referenceDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val weekEnd = weekStart.plusDays(6)
        val buckets = IntArray(7)
        history.forEach { item ->
            val date = HistoryLabels.itemLocalDate(item, zone) ?: return@forEach
            if (date.isBefore(weekStart) || date.isAfter(weekEnd)) return@forEach
            val index = date.dayOfWeek.value - 1
            if (index in buckets.indices) {
                buckets[index] += item.durationMinutes
            }
        }
        val todayIndex = referenceDate.dayOfWeek.value - 1
        return dayLabelsEs.mapIndexed { index, label ->
            WeeklyDayMinutes(
                dayLabel = label,
                minutes = buckets[index],
                isToday = index == todayIndex
            )
        }
    }
}
