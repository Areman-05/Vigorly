package com.example.vigorly

import com.example.vigorly.data.local.AthleticStatsCodec
import com.example.vigorly.data.model.AthleticStat
import org.junit.Assert.assertEquals
import org.junit.Test

class AthleticStatsCodecTest {

    @Test
    fun encodeDecode_preservesStats() {
        val stats = listOf(AthleticStat("Strength", 85), AthleticStat("Speed", 80))
        val expected = listOf(AthleticStat("strength", 85), AthleticStat("speed", 80))
        assertEquals(expected, AthleticStatsCodec.decode(AthleticStatsCodec.encode(stats)))
    }
}
