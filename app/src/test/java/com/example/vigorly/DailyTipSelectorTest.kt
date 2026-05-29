package com.example.vigorly

import com.example.vigorly.data.model.CoachingTip
import com.example.vigorly.util.DailyTipSelector
import org.junit.Assert.assertEquals
import org.junit.Test

class DailyTipSelectorTest {
    @Test
    fun pick_wrapsIndex() {
        val tips = listOf(
            CoachingTip("a", "A"),
            CoachingTip("b", "B")
        )
        assertEquals("b", DailyTipSelector.pick(tips, 3).id)
    }
}
