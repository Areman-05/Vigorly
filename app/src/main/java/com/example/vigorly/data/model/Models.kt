package com.example.vigorly.data.model

enum class WorkoutType { STRENGTH, CARDIO, HIIT, RECOVERY, SWIM }

data class UserProfile(
    val displayName: String,
    val avatarUrl: String?,
    val isProMember: Boolean,
    val totalWorkouts: Int,
    val activeStreakDays: Int,
    val level: Int
)

data class DailyGoals(
    val moveProgress: Float,
    val exerciseProgress: Float,
    val standProgress: Float,
    val moveCalories: Int,
    val moveCaloriesGoal: Int,
    val steps: Int,
    val stepsGoal: Int,
    val heartRateBpm: Int,
    val sleepHours: Float
) {
    val dailyGoalPercent: Int
        get() = ((moveProgress + exerciseProgress + standProgress) / 3f * 100).toInt()
}

data class AthleticStat(
    val label: String,
    val value: Int
)

data class Milestone(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconName: String,
    val unlocked: Boolean
)

data class WorkoutHistoryItem(
    val id: String,
    val title: String,
    val timestampLabel: String,
    val durationMinutes: Int,
    val calories: Int,
    val iconName: String
)

data class RecentActivity(
    val id: String,
    val title: String,
    val timeLabel: String,
    val durationMinutes: Int,
    val iconName: String
)

data class Exercise(
    val id: String,
    val name: String,
    val setsRepsLabel: String,
    val imageUrl: String?,
    val iconName: String? = null
)

data class WorkoutBlock(
    val id: String,
    val label: String,
    val title: String,
    val exercises: List<Exercise>
)

data class WorkoutDetail(
    val id: String,
    val name: String,
    val description: String,
    val type: WorkoutType,
    val durationMinutes: Int,
    val heroImageUrl: String,
    val targetMuscles: String,
    val targetDescription: String,
    val anatomyImageUrl: String?,
    val intensity: String,
    val estimatedCalories: Int,
    val blocks: List<WorkoutBlock>
)
