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
            val q = query.trim().lowercase()
            result = result.filter {
                it.name.lowercase().contains(q) ||
                    it.targetMuscles.lowercase().contains(q) ||
                    it.type.name.lowercase().contains(q)
            }
        }
        result = when (sort) {
            WorkoutSort.DURATION_ASC -> result.sortedBy { it.durationMinutes }
            WorkoutSort.DURATION_DESC -> result.sortedByDescending { it.durationMinutes }
            WorkoutSort.NAME_ASC -> result.sortedBy { it.name }
        }
        return result
    }
}
