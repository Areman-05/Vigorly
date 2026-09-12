package com.example.vigorly.data.model

data class WorkoutPlaylist(
    val id: String,
    val name: String,
    val workoutIds: List<String>,
    val isAuto: Boolean = false
)
