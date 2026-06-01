package com.example.vigorly

import com.example.vigorly.util.HistoryGrouper
import com.example.vigorly.util.HistorySectionKind
import com.example.vigorly.data.model.WorkoutHistoryItem
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class HistoryGrouperTest {

    @Test
    fun group_splitsByCompletedAtMillis() {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone).atStartOfDay(zone).toInstant().toEpochMilli()
        val yesterday = LocalDate.now(zone).minusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
        val items = listOf(
            WorkoutHistoryItem("1", "A", "", 30, 200, "fitness_center", completedAtMillis = today + 3_600_000),
            WorkoutHistoryItem("2", "B", "", 40, 300, "fitness_center", completedAtMillis = yesterday + 3_600_000),
            WorkoutHistoryItem("3", "C", "", 50, 400, "fitness_center", completedAtMillis = yesterday - 86_400_000)
        )
        val sections = HistoryGrouper.group(items)
        assertEquals(3, sections.size)
        assertEquals(HistorySectionKind.TODAY, sections[0].kind)
        assertEquals(1, sections[0].items.size)
    }
}
