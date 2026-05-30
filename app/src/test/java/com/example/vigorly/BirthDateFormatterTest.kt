package com.example.vigorly

import com.example.vigorly.util.BirthDateFormatter
import org.junit.Assert.assertEquals
import org.junit.Test

class BirthDateFormatterTest {
    @Test
    fun formatsDigitsWithSlashes() {
        assertEquals("15/03/1995", BirthDateFormatter.formatInput("15031995"))
        assertEquals("15/03", BirthDateFormatter.formatInput("1503"))
        assertEquals("1", BirthDateFormatter.formatInput("1"))
    }
}
