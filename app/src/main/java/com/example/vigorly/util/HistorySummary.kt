package com.example.vigorly.util

import com.example.vigorly.data.model.WorkoutHistoryItem

data class HistorySummary(
    val totalSessions: Int,
    val totalMinutes: Int,
    val totalCalories: Int
)

object HistorySummaryCalculator {
    fun from(items: List<WorkoutHistoryItem>): HistorySummary = HistorySummary(
        totalSessions = items.size,
        totalMinutes = items.sumOf { it.durationMinutes },
        totalCalories = items.sumOf { it.calories }
    )
}
