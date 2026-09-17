package com.example.vigorly

import com.example.vigorly.data.local.WeightLogCodec
import com.example.vigorly.data.model.WeightLogEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneOffset

class WeightLogCodecTest {

    private val zone = ZoneOffset.UTC

    @Test
    fun upsertDay_addsSecondDayInsteadOfReplacing() {
        val day1 = LocalDate.of(2026, 9, 16).atStartOfDay(zone).toInstant().toEpochMilli()
        val day2 = LocalDate.of(2026, 9, 17).atStartOfDay(zone).toInstant().toEpochMilli()
        val first = WeightLogCodec.upsertDay(emptyList(), 80f, day1, zone)
        val both = WeightLogCodec.upsertDay(first, 79.4f, day2, zone)
        assertEquals(2, both.size)
        assertEquals(80f, both[0].weightKg, 0.01f)
        assertEquals(79.4f, both[1].weightKg, 0.01f)
    }

    @Test
    fun upsertDay_updatesSameDay() {
        val morning = LocalDate.of(2026, 9, 17).atTime(8, 0).atZone(zone).toInstant().toEpochMilli()
        val evening = LocalDate.of(2026, 9, 17).atTime(20, 0).atZone(zone).toInstant().toEpochMilli()
        val first = WeightLogCodec.upsertDay(emptyList(), 80f, morning, zone)
        val updated = WeightLogCodec.upsertDay(first, 79.2f, evening, zone)
        assertEquals(1, updated.size)
        assertEquals(79.2f, updated[0].weightKg, 0.01f)
    }

    @Test
    fun shouldApplyPersisted_ignoresStaleWhileLocalHasNewId() {
        val local = listOf(
            WeightLogEntry("w-1", 80f, 1L),
            WeightLogEntry("w-2", 79f, 2L)
        )
        val persisted = listOf(WeightLogEntry("w-1", 80f, 1L))
        assertFalse(WeightLogCodec.shouldApplyPersisted(local, persisted))
        assertTrue(WeightLogCodec.shouldApplyPersisted(local, local))
    }

    @Test
    fun dailySeries_carriesWeightUntilToday() {
        val first = LocalDate.of(2026, 9, 15).atStartOfDay(zone).toInstant().toEpochMilli()
        val today = LocalDate.of(2026, 9, 17)
        val series = WeightLogCodec.dailySeries(
            items = listOf(WeightLogEntry("w-1", 80f, first)),
            today = today,
            zone = zone
        )
        assertEquals(3, series.size)
        assertTrue(series[0].logged)
        assertFalse(series[1].logged)
        assertFalse(series[2].logged)
        assertEquals(80f, series[2].weightKg, 0.01f)
        assertEquals(today, series.last().date)
    }
}
