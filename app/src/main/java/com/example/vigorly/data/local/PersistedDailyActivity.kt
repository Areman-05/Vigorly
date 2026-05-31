package com.example.vigorly.data.local

data class PersistedDailyActivity(
    val dateKey: String,
    val stepBaseline: Long?,
    val lastStepTotal: Long,
    val exerciseMinutes: Int,
    val workoutCalories: Int,
    val standHours: List<Int>
)
