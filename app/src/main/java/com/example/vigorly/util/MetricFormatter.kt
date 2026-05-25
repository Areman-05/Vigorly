package com.example.vigorly.util

import java.util.Locale

object MetricFormatter {
    fun formatSteps(steps: Int, goal: Int, compactGoal: Boolean = true): String {
        val goalLabel = if (compactGoal && goal >= 1000) {
            "%.0fk".format(Locale.US, goal / 1000f)
        } else {
            "%,d".format(Locale.US, goal)
        }
        return "%,d / $goalLabel".format(Locale.US, steps)
    }

    fun formatCalories(current: Int, goal: Int): String =
        "%,d / %,d kcal".format(Locale.US, current, goal)

    fun formatSleepHours(hours: Float): String =
        if (hours % 1f == 0f) "${hours.toInt()}h" else "%.1fh".format(Locale.US, hours)
}
