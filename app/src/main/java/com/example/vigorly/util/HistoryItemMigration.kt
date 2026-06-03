package com.example.vigorly.util

import com.example.vigorly.data.model.WorkoutHistoryItem
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale

object HistoryItemMigration {
    private val timePattern = DateTimeFormatter.ofPattern("H:mm")

    fun withResolvedTimestamp(
        item: WorkoutHistoryItem,
        zone: ZoneId = ZoneId.systemDefault()
    ): WorkoutHistoryItem {
        if (item.completedAtMillis > 0L) {
            val label = HistoryLabels.formatTimestamp(item.completedAtMillis)
            return if (label == item.timestampLabel) item else item.copy(timestampLabel = label)
        }
        val millis = inferMillisFromLabel(item.timestampLabel, zone) ?: return item
        val label = HistoryLabels.formatTimestamp(millis)
        return item.copy(completedAtMillis = millis, timestampLabel = label)
    }

    private fun inferMillisFromLabel(label: String, zone: ZoneId): Long? {
        val trimmed = label.trim()
        if (trimmed.isBlank()) return null
        val today = LocalDate.now(zone)
        val time = trimmed.substringAfter(",", "").trim().takeIf { it.isNotBlank() }?.let { part ->
            runCatching { LocalTime.parse(part, timePattern) }.getOrNull()
        } ?: LocalTime.NOON
        val lower = trimmed.lowercase(Locale.getDefault())
        val date = when {
            lower.startsWith("hoy") || lower.startsWith("today") -> today
            lower.startsWith("ayer") || lower.startsWith("yesterday") -> today.minusDays(1)
            else -> parseWeekdayDate(trimmed.substringBefore(",").trim(), today) ?: return null
        }
        return date.atTime(time).atZone(zone).toInstant().toEpochMilli()
    }

    private fun parseWeekdayDate(token: String, today: LocalDate): LocalDate? {
        val normalized = token.lowercase(Locale.getDefault())
            .removeSuffix(".")
            .take(3)
        val targets = listOf(
            "lun" to java.time.DayOfWeek.MONDAY,
            "mar" to java.time.DayOfWeek.TUESDAY,
            "mié" to java.time.DayOfWeek.WEDNESDAY,
            "mie" to java.time.DayOfWeek.WEDNESDAY,
            "jue" to java.time.DayOfWeek.THURSDAY,
            "vie" to java.time.DayOfWeek.FRIDAY,
            "sáb" to java.time.DayOfWeek.SATURDAY,
            "sab" to java.time.DayOfWeek.SATURDAY,
            "dom" to java.time.DayOfWeek.SUNDAY,
            "mon" to java.time.DayOfWeek.MONDAY,
            "tue" to java.time.DayOfWeek.TUESDAY,
            "wed" to java.time.DayOfWeek.WEDNESDAY,
            "thu" to java.time.DayOfWeek.THURSDAY,
            "fri" to java.time.DayOfWeek.FRIDAY,
            "sat" to java.time.DayOfWeek.SATURDAY,
            "sun" to java.time.DayOfWeek.SUNDAY
        )
        val dayOfWeek = targets.firstOrNull { (prefix, _) -> normalized.startsWith(prefix) }?.second
            ?: return null
        return today.with(TemporalAdjusters.previousOrSame(dayOfWeek))
    }
}
