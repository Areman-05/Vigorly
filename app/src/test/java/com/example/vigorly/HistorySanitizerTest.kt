package com.example.vigorly

import com.example.vigorly.data.model.WorkoutHistoryItem
import com.example.vigorly.util.HistoryItemMigration
import com.example.vigorly.util.HistorySanitizer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class HistorySanitizerTest {

    @Test
    fun clean_removesDemoAndDuplicates() {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone).atStartOfDay(zone).toInstant().toEpochMilli() + 3_600_000L
        val items = listOf(
            WorkoutHistoryItem("h1", "Demo", "Hoy", 30, 100, "fitness_center"),
            WorkoutHistoryItem(
                "a", "Respiración y calma", "Hoy, 12:00", 12, 30, "self_improvement",
                completedAtMillis = today, workoutId = "meditation_breath"
            ),
            WorkoutHistoryItem(
                "b", "Respiración y calma", "Hoy, 12:00", 12, 30, "self_improvement",
                completedAtMillis = today + 1_000, workoutId = "meditation_breath"
            ),
            WorkoutHistoryItem(
                "c", "Comba cardio", "Hoy, 12:00", 18, 210, "directions_run",
                completedAtMillis = today + 2_000, workoutId = "jump_rope_cardio"
            )
        )
        val cleaned = HistorySanitizer.clean(items, zone)
        assertEquals(2, cleaned.size)
        assertTrue(cleaned.none { it.id == "h1" })
        assertEquals("b", cleaned.first { it.workoutId == "meditation_breath" }.id)
    }

    @Test
    fun migration_doesNotReanchorHoyLabelToToday() {
        val item = WorkoutHistoryItem("x", "Test", "Hoy, 08:00", 10, 50, "timer")
        val migrated = HistoryItemMigration.withResolvedTimestamp(item)
        assertEquals(0L, migrated.completedAtMillis)
    }

    @Test
    fun clean_dropsEntriesWithoutTimestamp() {
        val items = listOf(
            WorkoutHistoryItem("x", "Sin fecha", "Hoy, 08:00", 10, 50, "timer")
        )
        assertTrue(HistorySanitizer.clean(items).isEmpty())
    }
}
