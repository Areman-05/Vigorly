package com.example.vigorly

import com.example.vigorly.data.local.MilestoneShowcaseCodec
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MilestoneShowcaseCodecTest {

    @Test
    fun encode_decode_roundTrip() {
        val slots = listOf("first_workout", null, "streak_7", null)
        val raw = MilestoneShowcaseCodec.encode(slots)
        val decoded = MilestoneShowcaseCodec.decode(raw)
        assertEquals(slots, decoded)
    }

    @Test
    fun decode_blank_returnsEmptySlots() {
        val decoded = MilestoneShowcaseCodec.decode(null)
        assertEquals(MilestoneShowcaseCodec.SLOT_COUNT, decoded.size)
        assertEquals(List(MilestoneShowcaseCodec.SLOT_COUNT) { null }, decoded)
    }

    @Test
    fun decode_ignoresBlankSegments() {
        val decoded = MilestoneShowcaseCodec.decode("workouts_5||")
        assertEquals("workouts_5", decoded[0])
        assertNull(decoded[1])
        assertNull(decoded[2])
    }
}
