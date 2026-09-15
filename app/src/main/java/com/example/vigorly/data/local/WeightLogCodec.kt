package com.example.vigorly.data.local

import com.example.vigorly.data.model.WeightLogEntry

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
