package com.example.vigorly.data.model

data class SessionSummary(
    val workoutId: String,
    val workoutName: String,
    val workoutType: WorkoutType,
    val focusZone: String,
    val durationMinutes: Int,
    val caloriesBurned: Int,
    val exercisesCompleted: Int,
    val totalExercises: Int,
    val elapsedSeconds: Int,
    val levelBefore: Int = 1,
    val levelAfter: Int = 1,
    val workoutsUntilNextLevel: Int = 0,
    val streakDays: Int = 0,
    val weeklyCompleted: Int = 0,
    val weeklyTarget: Int = 0,
    val newlyUnlockedTitles: List<String> = emptyList(),
    val warmupStepsCompleted: Int = 0
)
