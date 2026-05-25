package com.example.vigorly

import com.example.vigorly.util.TimeFormatter
import org.junit.Assert.assertEquals
import org.junit.Test

class TimeFormatterTest {

    @Test
    fun formatElapsed_zeroSeconds() {
        assertEquals("00:00", TimeFormatter.formatElapsed(0))
    }

    @Test
    fun formatElapsed_ninetySeconds() {
        assertEquals("01:30", TimeFormatter.formatElapsed(90))
    }

    @Test
    fun formatRestCountdown_formatsSeconds() {
        assertEquals("45", TimeFormatter.formatRestCountdown(45))
    }
}
