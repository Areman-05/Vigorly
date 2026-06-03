package com.example.vigorly.data.local

import com.example.vigorly.data.model.AthleticStat
import com.example.vigorly.util.AthleticStatLabels

object AthleticStatsCodec {
    private const val SEP = "|"

    fun encode(stats: List<AthleticStat>): String =
        stats.joinToString(SEP) { "${it.label}:${it.value}" }

    fun decode(raw: String?): List<AthleticStat> {
        if (raw.isNullOrBlank()) return emptyList()
        return raw.split(SEP).mapNotNull { part ->
            val pieces = part.split(":")
            if (pieces.size != 2) return@mapNotNull null
            val key = AthleticStatLabels.normalizeKey(pieces[0])
            AthleticStat(key, pieces[1].toIntOrNull() ?: return@mapNotNull null)
        }
    }
}
