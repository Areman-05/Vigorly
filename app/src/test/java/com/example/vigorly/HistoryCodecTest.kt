package com.example.vigorly

import com.example.vigorly.data.local.HistoryCodec
import com.example.vigorly.data.model.WorkoutHistoryItem
import com.example.vigorly.util.MetricFormatter
import org.junit.Assert.assertEquals
import org.junit.Test

class HistoryCodecTest {

    @Test
    fun encodeDecode_roundTrip() {
        val items = listOf(
            WorkoutHistoryItem("1", "Titan Protocol", "Today, 08:00 AM", 45, 450, "fitness_center")
        )
        val encoded = HistoryCodec.encode(items)
        val decoded = HistoryCodec.decode(encoded)
        assertEquals(items, decoded)
    }
}

class MetricFormatterExtraTest {

    @Test
    fun formatSleepHours_wholeNumber() {
        assertEquals("7h", MetricFormatter.formatSleepHours(7f))
    }

    @Test
    fun formatSteps_compactGoal() {
        assertEquals("6,240 / 10k", MetricFormatter.formatSteps(6240, 10000))
    }
}
