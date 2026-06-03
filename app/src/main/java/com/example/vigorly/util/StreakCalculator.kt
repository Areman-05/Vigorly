package com.example.vigorly.util

import java.time.LocalDate

object StreakCalculator {
    /**
     * Días consecutivos con actividad, contando desde hoy o desde ayer si hoy aún no hay registro.
     */
    fun consecutiveActiveDays(activeDates: Set<LocalDate>, today: LocalDate = LocalDate.now()): Int {
        if (activeDates.isEmpty()) return 0
        var anchor = today
        if (!activeDates.contains(anchor)) {
            anchor = today.minusDays(1)
        }
        if (!activeDates.contains(anchor)) return 0
        var streak = 0
        var cursor = anchor
        while (activeDates.contains(cursor)) {
            streak++
            cursor = cursor.minusDays(1)
        }
        return streak
    }
}
