package com.example.vigorly

import com.example.vigorly.util.LevelCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class LevelCalculatorTest {

    @Test
    fun progress_atLevelBoundary_isZero() {
        assertEquals(0f, LevelCalculator.progressToNextLevel(100), 0.01f)
    }

    @Test
    fun workoutsUntilNext_atMidLevel() {
        assertEquals(25, LevelCalculator.workoutsUntilNextLevel(325))
    }
}
