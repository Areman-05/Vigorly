package com.example.vigorly

import com.example.vigorly.util.BirthDateFormatter
import org.junit.Assert.assertEquals
import org.junit.Test

class BirthDateFormatterTest {
    @Test
    fun formatsDigitsWithSlashes() {
        assertEquals("15/03/1995", BirthDateFormatter.toFormatted("15031995"))
        assertEquals("15/03", BirthDateFormatter.toFormatted("1503"))
        assertEquals("1", BirthDateFormatter.toFormatted("1"))
    }

    @Test
    fun digitsOnlyStripsNonDigits() {
        assertEquals("15031995", BirthDateFormatter.digitsOnly("15/03/1995"))
        assertEquals("1503", BirthDateFormatter.digitsOnly("15/03abc"))
    }
}
