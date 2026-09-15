package com.example.vigorly.util

import com.example.vigorly.data.model.WorkoutDetail
import com.example.vigorly.data.model.WorkoutHistoryItem
import com.example.vigorly.data.model.WorkoutType

object WorkoutRecommender {
    fun recommend(
        workouts: List<WorkoutDetail>,
        history: List<WorkoutHistoryItem>,
        favorites: Set<String>,
        fitnessGoal: String = "",
        workoutLocation: String = ""
    ): WorkoutDetail? = recommendMany(
        workouts, history, favorites, count = 1, fitnessGoal, workoutLocation
    ).firstOrNull()

    fun recommendMany(
        workouts: List<WorkoutDetail>,
        history: List<WorkoutHistoryItem>,
        favorites: Set<String>,
        count: Int = 5,
        fitnessGoal: String = "",
        workoutLocation: String = ""
    ): List<WorkoutDetail> {
        if (workouts.isEmpty() || count <= 0) return emptyList()
        val recentTitles = history.take(5).map { it.title }.toSet()
        val goals = fitnessGoal.split(',').map { it.trim() }.filter { it.isNotEmpty() }.toSet()
        val location = workoutLocation.split(',').map { it.trim() }.firstOrNull().orEmpty()

        return workouts
            .sortedWith(
                compareByDescending<WorkoutDetail> { it.id in favorites }
                    .thenByDescending { preferenceScore(it, goals, location) }
                    .thenByDescending { it.name !in recentTitles }
            )
            .distinctBy { it.id }
            .take(count)
    }

    private fun preferenceScore(
        workout: WorkoutDetail,
        goals: Set<String>,
        location: String
    ): Int {
        var score = 0
        goals.forEach { goal ->
            score += when (goal) {
                "strength", "muscle" -> if (workout.type == WorkoutType.STRENGTH) 4 else 0
                "cardio", "endurance" -> when (workout.type) {
                    WorkoutType.CARDIO, WorkoutType.HIIT, WorkoutType.SWIM -> 4
                    else -> 0
                }
                "weight" -> when (workout.type) {
                    WorkoutType.HIIT, WorkoutType.CARDIO -> 3
                    else -> 0
                }
                "flexibility" -> when (workout.type) {
                    WorkoutType.MOBILITY, WorkoutType.PILATES -> 4
                    else -> 0
                }
                "wellness" -> when (workout.type) {
                    WorkoutType.RECOVERY, WorkoutType.PILATES, WorkoutType.MOBILITY -> 3
                    else -> 0
                }
                else -> 0
            }
        }
        score += when (location) {
            "gym" -> when (workout.type) {
                WorkoutType.STRENGTH, WorkoutType.HIIT -> 3
                else -> 0
            }
            "home" -> when (workout.type) {
                WorkoutType.PILATES, WorkoutType.MOBILITY, WorkoutType.RECOVERY, WorkoutType.HIIT -> 3
                else -> 0
            }
            "outdoor" -> when (workout.type) {
                WorkoutType.CARDIO, WorkoutType.HIIT -> 3
                else -> 0
            }
            else -> 0
        }
        // Prefer moderate durations for home; longer for gym/outdoor athlete sessions
        if (location == "home" && workout.durationMinutes <= 30) score += 1
        if (location == "gym" && workout.durationMinutes >= 35) score += 1
        return score
    }
}
