package com.example.vigorly.data.model

data class WorkoutSessionState(
    val workoutId: String,
    val workoutName: String,
    val currentExerciseIndex: Int,
    val totalExercises: Int,
    val elapsedSeconds: Int,
    val isPaused: Boolean,
    val restSecondsRemaining: Int = 0
)

data class AppSettings(
    val notificationsEnabled: Boolean = true,
    val unitsMetric: Boolean = true
)
