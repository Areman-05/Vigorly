package com.example.vigorly.util

import com.example.vigorly.data.model.WorkoutHistoryItem
import com.example.vigorly.data.model.WorkoutType
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object HistoryLabels {

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val dateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")
    private val fullDateFormatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM yyyy")

    fun formatTimestamp(millis: Long, locale: Locale = Locale.getDefault()): String {
        if (millis <= 0L) return ""
        val zone = ZoneId.systemDefault()
        val zoned = Instant.ofEpochMilli(millis).atZone(zone)
        val date = zoned.toLocalDate()
        val today = LocalDate.now(zone)
        val time = zoned.format(timeFormatter)
        return when (date) {
            today -> "Hoy, $time"
            today.minusDays(1) -> "Ayer, $time"
            else -> {
                val dayFormatter = DateTimeFormatter.ofPattern("EEE, d MMM · HH:mm", locale)
                zoned.format(dayFormatter)
            }
        }
    }

    fun displayTimestamp(item: WorkoutHistoryItem, locale: Locale = Locale.getDefault()): String {
        if (item.completedAtMillis > 0L) {
            return formatTimestamp(item.completedAtMillis, locale)
        }
        return localizeLegacyLabel(item.timestampLabel)
    }

    fun formatItemDate(millis: Long, locale: Locale = Locale.getDefault()): String {
        if (millis <= 0L) return ""
        val zone = ZoneId.systemDefault()
        return Instant.ofEpochMilli(millis).atZone(zone).format(dateFormatter.withLocale(locale))
    }

    fun formatSessionDateFull(millis: Long, locale: Locale = Locale.getDefault()): String {
        if (millis <= 0L) return ""
        val zone = ZoneId.systemDefault()
        return Instant.ofEpochMilli(millis)
            .atZone(zone)
            .format(fullDateFormatter.withLocale(locale))
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
    }

    fun itemLocalDate(item: WorkoutHistoryItem, zone: ZoneId = ZoneId.systemDefault()): LocalDate? {
        if (item.completedAtMillis <= 0L) return null
        return Instant.ofEpochMilli(item.completedAtMillis).atZone(zone).toLocalDate()
    }

    private fun localizeLegacyLabel(label: String): String {
        return label
            .replaceFirst("Today,", "Hoy,", ignoreCase = true)
            .replaceFirst("Yesterday,", "Ayer,", ignoreCase = true)
    }

    fun parseWorkoutType(typeName: String?): WorkoutType? = runCatching {
        if (typeName.isNullOrBlank()) return null
        WorkoutType.valueOf(typeName)
    }.getOrNull()
}
