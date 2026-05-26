package com.example.vigorly.util

object LevelCalculator {
    private const val WORKOUTS_PER_LEVEL = 50

    fun progressToNextLevel(totalWorkouts: Int): Float {
        val intoLevel = totalWorkouts % WORKOUTS_PER_LEVEL
        return intoLevel / WORKOUTS_PER_LEVEL.toFloat()
    }

    fun workoutsUntilNextLevel(totalWorkouts: Int): Int =
        WORKOUTS_PER_LEVEL - (totalWorkouts % WORKOUTS_PER_LEVEL)
}
