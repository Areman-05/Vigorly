package com.example.vigorly.util

import com.example.vigorly.data.model.WorkoutHistoryItem
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

enum class HistorySectionKind {
    TODAY,
    YESTERDAY,
    EARLIER
}

data class HistorySection(
    val kind: HistorySectionKind,
    val items: List<WorkoutHistoryItem>
)

object HistoryGrouper {

    fun group(items: List<WorkoutHistoryItem>): List<HistorySection> {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val yesterday = today.minusDays(1)

        val todayItems = mutableListOf<WorkoutHistoryItem>()
        val yesterdayItems = mutableListOf<WorkoutHistoryItem>()
        val earlierItems = mutableListOf<WorkoutHistoryItem>()

        items.forEach { item ->
            when (resolveSection(item, today, yesterday, zone)) {
                HistorySectionKind.TODAY -> todayItems.add(item)
                HistorySectionKind.YESTERDAY -> yesterdayItems.add(item)
                HistorySectionKind.EARLIER -> earlierItems.add(item)
            }
        }

        return buildList {
            if (todayItems.isNotEmpty()) add(HistorySection(HistorySectionKind.TODAY, todayItems))
            if (yesterdayItems.isNotEmpty()) add(HistorySection(HistorySectionKind.YESTERDAY, yesterdayItems))
            if (earlierItems.isNotEmpty()) add(HistorySection(HistorySectionKind.EARLIER, earlierItems))
        }
    }

    private fun resolveSection(
        item: WorkoutHistoryItem,
        today: LocalDate,
        yesterday: LocalDate,
        zone: ZoneId
    ): HistorySectionKind {
        if (item.completedAtMillis > 0L) {
            val date = Instant.ofEpochMilli(item.completedAtMillis).atZone(zone).toLocalDate()
            return when (date) {
                today -> HistorySectionKind.TODAY
                yesterday -> HistorySectionKind.YESTERDAY
                else -> HistorySectionKind.EARLIER
            }
        }
        val label = item.timestampLabel.lowercase()
        return when {
            label.startsWith("today") || label.startsWith("hoy") -> HistorySectionKind.TODAY
            label.startsWith("yesterday") || label.startsWith("ayer") -> HistorySectionKind.YESTERDAY
            else -> HistorySectionKind.EARLIER
        }
    }
}
