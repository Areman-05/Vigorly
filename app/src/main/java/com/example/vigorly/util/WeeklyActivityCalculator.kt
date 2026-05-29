package com.example.vigorly.util

import com.example.vigorly.data.model.WorkoutHistoryItem

data class WeeklyDayMinutes(
    val dayLabel: String,
    val minutes: Int
)

object WeeklyActivityCalculator {
    private val dayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    fun fromHistory(history: List<WorkoutHistoryItem>): List<WeeklyDayMinutes> {
        val buckets = IntArray(7)
        history.take(14).forEachIndexed { index, item ->
            buckets[index % 7] += item.durationMinutes
        }
        return dayLabels.mapIndexed { i, label ->
            WeeklyDayMinutes(label, buckets[i])
        }
    }
}
