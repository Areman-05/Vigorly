package com.example.vigorly.util

import com.example.vigorly.data.model.WorkoutHistoryItem
import java.time.ZoneId

object HistorySanitizer {
    /** IDs de entrenamientos de demostración que no deben mostrarse al usuario. */
    val DEMO_HISTORY_IDS = setOf("h1", "h2", "h3")

    /**
     * Pipeline completo: quita demo, normaliza fechas, elimina duplicados y entradas sin fecha fiable.
     */
    fun clean(
        items: List<WorkoutHistoryItem>,
        zone: ZoneId = ZoneId.systemDefault()
    ): List<WorkoutHistoryItem> {
        return items
            .filter { it.id !in DEMO_HISTORY_IDS }
            .map { HistoryItemMigration.withResolvedTimestamp(it, zone) }
            .filter { it.completedAtMillis > 0L }
            .let { deduplicateSessions(it, zone) }
            .sortedByDescending { it.completedAtMillis }
    }

    fun withoutDemoEntries(items: List<WorkoutHistoryItem>): List<WorkoutHistoryItem> =
        items.filter { it.id !in DEMO_HISTORY_IDS }

    fun removedCount(before: List<WorkoutHistoryItem>, after: List<WorkoutHistoryItem>): Int =
        (before.size - after.size).coerceAtLeast(0)

    /**
     * Conserva la sesión más reciente cuando hay duplicados del mismo entrenamiento el mismo día.
     */
    private fun deduplicateSessions(
        items: List<WorkoutHistoryItem>,
        zone: ZoneId
    ): List<WorkoutHistoryItem> {
        val seen = mutableSetOf<String>()
        return items
            .sortedByDescending { it.completedAtMillis }
            .filter { item ->
                val key = sessionFingerprint(item, zone)
                seen.add(key)
            }
    }

    private fun sessionFingerprint(item: WorkoutHistoryItem, zone: ZoneId): String {
        val day = HistoryLabels.itemLocalDate(item, zone)?.toString() ?: "unknown"
        val workoutKey = item.workoutId?.takeIf { it.isNotBlank() } ?: item.title.trim()
        return "$workoutKey|$day|${item.durationMinutes}|${item.calories}"
    }
}
