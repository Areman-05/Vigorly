package com.example.vigorly.data.model

data class UserSessionSnapshot(
    val profile: UserProfile,
    val dailyGoals: DailyGoals,
    val weeklyGoal: WeeklyGoal,
    val onboardingCompleted: Boolean,
    val fitnessGoal: String,
    val activityLevel: String,
    val workoutLocation: String,
    val preferredTime: String,
    val notificationsEnabled: Boolean,
    val unitsMetric: Boolean,
    val workoutHistory: List<WorkoutHistoryItem>,
    val athleticStats: List<AthleticStat>,
    val favoriteWorkoutIds: Set<String>,
    val dailyTipIndex: Int
)
