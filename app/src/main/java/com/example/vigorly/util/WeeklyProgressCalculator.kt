package com.example.vigorly.util

import com.example.vigorly.data.model.WeeklyGoal

object WeeklyProgressCalculator {
    fun remainingSessions(goal: WeeklyGoal): Int =
        (goal.targetSessions - goal.completedSessions).coerceAtLeast(0)

    fun percent(goal: WeeklyGoal): Int =
        (goal.progress * 100f).toInt()
}
