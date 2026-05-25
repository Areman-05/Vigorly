package com.example.vigorly.util

import com.example.vigorly.data.model.WorkoutHistoryItem

data class HistorySection(
    val title: String,
    val items: List<WorkoutHistoryItem>
)

object HistoryGrouper {
    fun group(items: List<WorkoutHistoryItem>): List<HistorySection> {
        val today = mutableListOf<WorkoutHistoryItem>()
        val yesterday = mutableListOf<WorkoutHistoryItem>()
        val earlier = mutableListOf<WorkoutHistoryItem>()

        items.forEach { item ->
            val label = item.timestampLabel.lowercase()
            when {
                label.startsWith("today") -> today.add(item)
                label.startsWith("yesterday") -> yesterday.add(item)
                else -> earlier.add(item)
            }
        }

        return buildList {
            if (today.isNotEmpty()) add(HistorySection("Today", today))
            if (yesterday.isNotEmpty()) add(HistorySection("Yesterday", yesterday))
            if (earlier.isNotEmpty()) add(HistorySection("Earlier", earlier))
        }
    }
}
