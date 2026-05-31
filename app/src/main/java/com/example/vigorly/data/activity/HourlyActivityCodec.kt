package com.example.vigorly.data.activity

object HourlyActivityCodec {
    const val HOURS = 24

    fun encode(values: IntArray): String =
        values.take(HOURS).joinToString(",")

    fun decode(raw: String?): IntArray {
        if (raw.isNullOrBlank()) return IntArray(HOURS)
        val parts = raw.split(",")
        return IntArray(HOURS) { index ->
            parts.getOrNull(index)?.toIntOrNull()?.coerceAtLeast(0) ?: 0
        }
    }
}
