package com.example.vigorly

import com.example.vigorly.data.local.CoachingTipLoader
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class CoachingTipLoaderTest {
    @Test
    fun parseLine_validJson() {
        val tip = CoachingTipLoader.parseLine("""{"id":"tip-001","text":"Stay hydrated."}""")
        assertEquals("tip-001", tip?.id)
        assertEquals("Stay hydrated.", tip?.text)
    }

    @Test
    fun parseLine_blank_returnsNull() {
        assertNull(CoachingTipLoader.parseLine("   "))
    }
}
