package com.example.vigorly.util

import com.example.vigorly.data.model.WeeklyGoal

object WeeklyProgressCalculator {
    fun remainingSessions(goal: WeeklyGoal): Int =
        (goal.targetSessions - goal.completedSessions).coerceAtLeast(0)

    fun displayCompletedSessions(goal: WeeklyGoal): Int =
        goal.completedSessions.coerceAtMost(goal.targetSessions)

    fun percent(goal: WeeklyGoal): Int =
        (displayCompletedSessions(goal) / goal.targetSessions.toFloat().coerceAtLeast(1f) * 100f).toInt()
            .coerceIn(0, 100)

    fun daysRemainingInWeek(): Int {
        val today = java.time.LocalDate.now()
        val weekFields = java.time.temporal.WeekFields.of(java.util.Locale.getDefault())
        val firstDay = weekFields.firstDayOfWeek
        val daysFromStart = (today.dayOfWeek.value - firstDay.value + 7) % 7
        return (6 - daysFromStart).coerceAtLeast(0)
    }

    fun isComplete(goal: WeeklyGoal): Boolean =
        goal.completedSessions >= goal.targetSessions
}
