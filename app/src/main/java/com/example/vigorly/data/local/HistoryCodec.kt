package com.example.vigorly.data.local

import com.example.vigorly.data.model.WorkoutHistoryItem

object HistoryCodec {
    private const val SEP_ENTRY = ";;"
    private const val SEP_FIELD = "|"

    fun encode(items: List<WorkoutHistoryItem>): String =
        items.joinToString(SEP_ENTRY) { item ->
            listOf(
                item.id,
                item.title,
                item.timestampLabel,
                item.durationMinutes.toString(),
                item.calories.toString(),
                item.iconName,
                item.completedAtMillis.toString(),
                item.workoutId.orEmpty(),
                item.workoutType.orEmpty()
            ).joinToString(SEP_FIELD)
        }

    fun decode(raw: String?): List<WorkoutHistoryItem> {
        if (raw.isNullOrBlank()) return emptyList()
        return raw.split(SEP_ENTRY).mapNotNull { entry ->
            val parts = entry.split(SEP_FIELD)
            if (parts.size < 6) return@mapNotNull null
            WorkoutHistoryItem(
                id = parts[0],
                title = parts[1],
                timestampLabel = parts[2],
                durationMinutes = parts[3].toIntOrNull() ?: 0,
                calories = parts[4].toIntOrNull() ?: 0,
                iconName = parts[5],
                completedAtMillis = parts.getOrNull(6)?.toLongOrNull() ?: 0L,
                workoutId = parts.getOrNull(7)?.takeIf { it.isNotBlank() },
                workoutType = parts.getOrNull(8)?.takeIf { it.isNotBlank() }
            )
        }
    }
}
