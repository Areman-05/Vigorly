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
        activityLevel: String = "",
        workoutLocation: String = ""
    ): WorkoutDetail? = recommendMany(
        workouts, history, favorites, count = 1, fitnessGoal, activityLevel, workoutLocation
    ).firstOrNull()

    fun recommendMany(
        workouts: List<WorkoutDetail>,
        history: List<WorkoutHistoryItem>,
        favorites: Set<String>,
        count: Int = 5,
        fitnessGoal: String = "",
        activityLevel: String = "",
        workoutLocation: String = ""
    ): List<WorkoutDetail> {
        if (workouts.isEmpty() || count <= 0) return emptyList()
        val recentTitles = history.take(5).map { it.title }.toSet()
        val types = preferredTypes(fitnessGoal)
        val intensities = preferredIntensities(activityLevel)
        val durations = preferredDurations(workoutLocation)

        return workouts
            .sortedWith(
                compareByDescending<WorkoutDetail> { it.id in favorites }
                    .thenByDescending { preferenceScore(it, types, intensities, durations) }
                    .thenByDescending { it.name !in recentTitles }
            )
            .distinctBy { it.id }
            .take(count)
    }

    internal fun preferredTypes(raw: String): Set<WorkoutType> {
        val out = linkedSetOf<WorkoutType>()
        parseKeys(raw).forEach { key ->
            when (key) {
                "strength", "muscle" -> out += WorkoutType.STRENGTH
                "hiit" -> out += WorkoutType.HIIT
                "cardio", "endurance", "weight" -> out += WorkoutType.CARDIO
                "recovery", "wellness", "yoga" -> out += WorkoutType.RECOVERY
                "pilates" -> out += WorkoutType.PILATES
                "mobility", "flexibility" -> out += WorkoutType.MOBILITY
                "swim" -> out += WorkoutType.SWIM
            }
            WorkoutType.entries.find { it.name.equals(key, ignoreCase = true) }?.let { out += it }
        }
        return out
    }

    internal fun preferredIntensities(raw: String): Set<String> =
        parseKeys(raw).mapNotNull { key ->
            when (key) {
                "low", "sedentary", "light" -> "low"
                "moderate" -> "moderate"
                "high", "active", "athlete" -> "high"
                else -> null
            }
        }.toSet()

    internal fun preferredDurations(raw: String): Set<DurationBucket> =
        parseKeys(raw).mapNotNull { key ->
            when (key) {
                "short", "home" -> DurationBucket.SHORT
                "medium", "outdoor", "mixed" -> DurationBucket.MEDIUM
                "long", "gym" -> DurationBucket.LONG
                else -> null
            }
        }.toSet()

    private fun preferenceScore(
        workout: WorkoutDetail,
        types: Set<WorkoutType>,
        intensities: Set<String>,
        durations: Set<DurationBucket>
    ): Int {
        var score = 0
        if (types.isNotEmpty() && workout.type in types) score += 5
        if (intensities.isNotEmpty() &&
            WorkoutBrowseFilters.intensityKey(workout.intensity) in intensities
        ) {
            score += 3
        }
        if (durations.isNotEmpty() && durations.any { it.matches(workout.durationMinutes) }) {
            score += 2
        }
        return score
    }

    private fun parseKeys(raw: String): Set<String> =
        raw.split(',').map { it.trim().lowercase() }.filter { it.isNotEmpty() }.toSet()
}
