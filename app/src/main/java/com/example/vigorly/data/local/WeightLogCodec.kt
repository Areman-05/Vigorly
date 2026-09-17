package com.example.vigorly.data.local

import com.example.vigorly.data.model.WeightLogEntry
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

object WeightLogCodec {
    private const val SEP_ENTRY = ";;"
    private const val SEP_FIELD = "|"

    fun encode(items: List<WeightLogEntry>): String =
        items.joinToString(SEP_ENTRY) { item ->
            listOf(
                item.id,
                item.weightKg.toString(),
                item.recordedAtMillis.toString(),
                item.note.replace("|", " ").replace(";", ",")
            ).joinToString(SEP_FIELD)
        }

    fun decode(raw: String?): List<WeightLogEntry> {
        if (raw.isNullOrBlank()) return emptyList()
        return raw.split(SEP_ENTRY).mapNotNull { entry ->
            val parts = entry.split(SEP_FIELD)
            if (parts.size < 3) return@mapNotNull null
            WeightLogEntry(
                id = parts[0],
                weightKg = parts[1].toFloatOrNull() ?: return@mapNotNull null,
                recordedAtMillis = parts[2].toLongOrNull() ?: 0L,
                note = parts.getOrNull(3).orEmpty()
            )
        }.sortedBy { it.recordedAtMillis }
    }

    fun upsertDay(
        items: List<WeightLogEntry>,
        weightKg: Float,
        recordedAtMillis: Long,
        zone: ZoneId = ZoneId.systemDefault()
    ): List<WeightLogEntry> {
        val kg = weightKg.coerceIn(30f, 300f)
        val day = localDateOf(recordedAtMillis, zone)
        val existing = items.find { localDateOf(it.recordedAtMillis, zone) == day }
        return if (existing != null) {
            items.map { entry ->
                if (entry.id == existing.id) {
                    entry.copy(weightKg = kg, recordedAtMillis = recordedAtMillis)
                } else {
                    entry
                }
            }.sortedBy { it.recordedAtMillis }
        } else {
            val entry = WeightLogEntry(
                id = "w-$recordedAtMillis",
                weightKg = kg,
                recordedAtMillis = recordedAtMillis
            )
            (items + entry).sortedBy { it.recordedAtMillis }
        }
    }

    fun shouldApplyPersisted(
        local: List<WeightLogEntry>,
        persisted: List<WeightLogEntry>
    ): Boolean {
        if (local.isEmpty()) return true
        val localIds = local.map { it.id }.toSet()
        val persistedIds = persisted.map { it.id }.toSet()
        if (localIds != persistedIds) return false
        val localNewest = local.maxOf { it.recordedAtMillis }
        val persistedNewest = persisted.maxOfOrNull { it.recordedAtMillis } ?: 0L
        return persistedNewest >= localNewest
    }

    private fun localDateOf(millis: Long, zone: ZoneId): LocalDate =
        Instant.ofEpochMilli(millis).atZone(zone).toLocalDate()

    data class TrendPoint(
        val date: LocalDate,
        val weightKg: Float,
        val logged: Boolean
    )

    fun dailySeries(
        items: List<WeightLogEntry>,
        today: LocalDate = LocalDate.now(),
        zone: ZoneId = ZoneId.systemDefault(),
        maxDays: Int = 21
    ): List<TrendPoint> {
        if (items.isEmpty()) return emptyList()
        val byDay = items
            .sortedBy { it.recordedAtMillis }
            .associate { localDateOf(it.recordedAtMillis, zone) to it.weightKg }
        val firstDay = byDay.keys.minOrNull() ?: return emptyList()
        val start = maxOf(firstDay, today.minusDays((maxDays - 1).toLong()))
        if (start.isAfter(today)) return emptyList()

        val points = mutableListOf<TrendPoint>()
        var cursor = start
        var lastKg = byDay[start] ?: byDay.entries
            .filter { !it.key.isAfter(start) }
            .maxByOrNull { it.key }
            ?.value
            ?: return emptyList()
        while (!cursor.isAfter(today)) {
            val loggedKg = byDay[cursor]
            if (loggedKg != null) lastKg = loggedKg
            points += TrendPoint(
                date = cursor,
                weightKg = lastKg,
                logged = loggedKg != null
            )
            cursor = cursor.plusDays(1)
        }
        return points
    }
}

object MilestoneUnlockDatesCodec {
    private const val SEP_ENTRY = ";;"
    private const val SEP_FIELD = "|"

    fun encode(dates: Map<String, Long>): String =
        dates.entries.joinToString(SEP_ENTRY) { (id, millis) ->
            "$id$SEP_FIELD$millis"
        }

    fun decode(raw: String?): Map<String, Long> {
        if (raw.isNullOrBlank()) return emptyMap()
        return raw.split(SEP_ENTRY).mapNotNull { entry ->
            val parts = entry.split(SEP_FIELD)
            if (parts.size < 2) return@mapNotNull null
            val millis = parts[1].toLongOrNull() ?: return@mapNotNull null
            parts[0] to millis
        }.toMap()
    }
}
