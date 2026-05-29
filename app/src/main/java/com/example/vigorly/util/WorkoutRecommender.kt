package com.example.vigorly.util

import com.example.vigorly.data.model.WorkoutDetail
import com.example.vigorly.data.model.WorkoutHistoryItem

object WorkoutRecommender {
    fun recommend(
        workouts: List<WorkoutDetail>,
        history: List<WorkoutHistoryItem>,
        favorites: Set<String>
    ): WorkoutDetail? {
        if (workouts.isEmpty()) return null
        val recentTitles = history.take(5).map { it.title }.toSet()
        val favoriteWorkouts = workouts.filter { favorites.contains(it.id) }
        if (favoriteWorkouts.isNotEmpty()) {
            return favoriteWorkouts.first()
        }
        return workouts.firstOrNull { it.name !in recentTitles } ?: workouts.random()
    }
}
