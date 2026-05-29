package com.example.vigorly.data.model

data class SessionSummary(
    val workoutId: String,
    val workoutName: String,
    val durationMinutes: Int,
    val caloriesBurned: Int,
    val exercisesCompleted: Int,
    val totalExercises: Int,
    val elapsedSeconds: Int
)
