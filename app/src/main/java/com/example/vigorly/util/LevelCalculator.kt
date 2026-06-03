package com.example.vigorly.util

object LevelCalculator {
    const val MAX_LEVEL = 10
    const val WORKOUTS_PER_LEVEL = 5

    fun levelFromWorkouts(totalWorkouts: Int): Int =
        minOf(MAX_LEVEL, 1 + totalWorkouts / WORKOUTS_PER_LEVEL)

    fun progressToNextLevel(totalWorkouts: Int): Float {
        val level = levelFromWorkouts(totalWorkouts)
        if (level >= MAX_LEVEL) return 1f
        val workoutsIntoLevel = totalWorkouts - (level - 1) * WORKOUTS_PER_LEVEL
        return workoutsIntoLevel / WORKOUTS_PER_LEVEL.toFloat()
    }

    fun workoutsUntilNextLevel(totalWorkouts: Int): Int {
        val level = levelFromWorkouts(totalWorkouts)
        if (level >= MAX_LEVEL) return 0
        val workoutsIntoLevel = totalWorkouts - (level - 1) * WORKOUTS_PER_LEVEL
        return WORKOUTS_PER_LEVEL - workoutsIntoLevel
    }

    fun isMaxLevel(totalWorkouts: Int): Boolean = levelFromWorkouts(totalWorkouts) >= MAX_LEVEL
}
