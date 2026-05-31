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

    fun formatDistanceKm(km: Float, metric: Boolean = true): String {
        if (!metric) {
            val miles = km * 0.621371f
            return if (miles < 0.1f) "0 mi" else "%.2f mi".format(Locale.US, miles)
        }
        return when {
            km < 0.1f -> "0 m"
            km < 1f -> "%.0f m".format(Locale.US, km * 1000f)
            else -> "%.2f km".format(Locale.US, km).replace('.', ',')
        }
    }
}
