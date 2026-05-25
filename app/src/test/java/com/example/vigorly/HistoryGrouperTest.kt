package com.example.vigorly

import com.example.vigorly.util.HistoryGrouper
import com.example.vigorly.data.model.WorkoutHistoryItem
import org.junit.Assert.assertEquals
import org.junit.Test

class HistoryGrouperTest {

    @Test
    fun group_splitsByTimeLabels() {
        val items = listOf(
            WorkoutHistoryItem("1", "A", "Today, 08:00 AM", 30, 200, "fitness_center"),
            WorkoutHistoryItem("2", "B", "Yesterday, 07:00 AM", 40, 300, "fitness_center"),
            WorkoutHistoryItem("3", "C", "Mon, 18:00 PM", 50, 400, "fitness_center")
        )
        val sections = HistoryGrouper.group(items)
        assertEquals(3, sections.size)
        assertEquals("Today", sections[0].title)
        assertEquals(1, sections[0].items.size)
    }
}
