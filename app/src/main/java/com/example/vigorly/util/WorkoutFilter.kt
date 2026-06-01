package com.example.vigorly.util

import com.example.vigorly.data.model.WorkoutDetail
import com.example.vigorly.data.model.WorkoutType

enum class WorkoutSort { DURATION_ASC, DURATION_DESC, NAME_ASC }

object WorkoutFilter {
    fun filter(
        workouts: List<WorkoutDetail>,
        query: String,
        type: WorkoutType?,
        sort: WorkoutSort
    ): List<WorkoutDetail> {
        var result = workouts
        if (type != null) result = result.filter { it.type == type }
        if (query.isNotBlank()) {
            val q = normalizeForSearch(query)
            result = result.filter {
                normalizeForSearch(it.name).contains(q) ||
                    normalizeForSearch(it.description).contains(q) ||
                    normalizeForSearch(it.targetMuscles).contains(q) ||
                    normalizeForSearch(it.targetDescription).contains(q) ||
                    normalizeForSearch(it.type.name).contains(q)
            }
        }
        result = when (sort) {
            WorkoutSort.DURATION_ASC -> result.sortedBy { it.durationMinutes }
            WorkoutSort.DURATION_DESC -> result.sortedByDescending { it.durationMinutes }
            WorkoutSort.NAME_ASC -> result.sortedBy { it.name }
        }
        return result
    }

    fun filterFavorites(workouts: List<WorkoutDetail>, favoriteIds: Set<String>): List<WorkoutDetail> =
        workouts.filter { favoriteIds.contains(it.id) }

    private fun normalizeForSearch(text: String): String =
        java.text.Normalizer.normalize(text.trim().lowercase(), java.text.Normalizer.Form.NFD)
            .replace(Regex("\\p{M}+"), "")
}
