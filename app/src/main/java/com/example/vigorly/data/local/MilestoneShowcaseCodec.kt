package com.example.vigorly.data.local

object MilestoneShowcaseCodec {
    private const val SEP = "|"
    const val SLOT_COUNT = 4

    fun encode(slots: List<String?>): String =
        (0 until SLOT_COUNT).joinToString(SEP) { index ->
            slots.getOrNull(index).orEmpty()
        }

    fun decode(raw: String?): List<String?> {
        if (raw.isNullOrBlank()) return List(SLOT_COUNT) { null }
        val parts = raw.split(SEP)
        return (0 until SLOT_COUNT).map { index ->
            parts.getOrNull(index)?.takeIf { it.isNotBlank() }
        }
    }
}
