package com.example.vigorly

import com.example.vigorly.util.LevelCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LevelCalculatorTest {

    @Test
    fun progress_atLevelBoundary_isZero() {
        assertEquals(0f, LevelCalculator.progressToNextLevel(5), 0.01f)
        assertEquals(0f, LevelCalculator.progressToNextLevel(10), 0.01f)
    }

    @Test
    fun workoutsUntilNext_atMidLevel() {
        assertEquals(3, LevelCalculator.workoutsUntilNextLevel(7))
    }

    @Test
    fun levelFromWorkouts_capsAtMaxLevel() {
        assertEquals(10, LevelCalculator.levelFromWorkouts(342))
        assertEquals(1, LevelCalculator.levelFromWorkouts(0))
    }

    @Test
    fun maxLevel_hasFullProgressAndNoWorkoutsRemaining() {
        assertEquals(1f, LevelCalculator.progressToNextLevel(50), 0.01f)
        assertEquals(0, LevelCalculator.workoutsUntilNextLevel(50))
        assertTrue(LevelCalculator.isMaxLevel(45))
    }
}
