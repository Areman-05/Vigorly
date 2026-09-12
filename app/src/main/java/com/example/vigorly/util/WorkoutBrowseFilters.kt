package com.example.vigorly.util

import com.example.vigorly.data.model.WorkoutDetail
import com.example.vigorly.data.model.WorkoutType

enum class WorkoutZone {
    FULL_BODY,
    UPPER_BODY,
    LOWER_BODY,
    CORE,
    BACK,
    ARMS,
    SHOULDERS,
    GLUTES,
    LEGS
}

enum class DurationBucket {
    SHORT,
    MEDIUM,
    LONG
}

data class WorkoutBrowseFilters(
    val zones: Set<WorkoutZone> = emptySet(),
    val intensities: Set<String> = emptySet(),
    val durations: Set<DurationBucket> = emptySet(),
    val types: Set<WorkoutType> = emptySet()
) {
    val selectedCount: Int
        get() = zones.size + intensities.size + durations.size + types.size

    val isEmpty: Boolean
        get() = selectedCount == 0

    fun matches(workout: WorkoutDetail): Boolean {
        if (types.isNotEmpty() && workout.type !in types) return false
        if (intensities.isNotEmpty()) {
            val key = intensityKey(workout.intensity)
            if (key !in intensities) return false
        }
        if (durations.isNotEmpty() && durations.none { it.contains(workout.durationMinutes) }) {
            return false
        }
        if (zones.isNotEmpty() && zones.none { it.matches(workout) }) {
            return false
        }
        return true
    }

    companion object {
        fun intensityKey(raw: String): String {
            val key = raw.trim().lowercase()
            return when {
                key in listOf("high", "alta", "alto") -> "high"
                key in listOf("moderate", "moderada", "medio", "media") -> "moderate"
                key in listOf("low", "baja", "bajo") -> "low"
                else -> key
            }
        }
    }
}

private fun DurationBucket.contains(minutes: Int): Boolean = when (this) {
    DurationBucket.SHORT -> minutes <= 20
    DurationBucket.MEDIUM -> minutes in 21..40
    DurationBucket.LONG -> minutes >= 41
}

private fun WorkoutZone.matches(workout: WorkoutDetail): Boolean {
    val haystack = listOf(
        workout.targetMuscles,
        workout.targetDescription,
        workout.name,
        workout.description
    ).joinToString(" ").lowercase()

    val keywords = when (this) {
        WorkoutZone.FULL_BODY -> listOf("cuerpo completo", "full body", "completo")
        WorkoutZone.UPPER_BODY -> listOf("tren superior", "pecho", "espalda", "hombros", "bíceps", "biceps", "tríceps", "triceps")
        WorkoutZone.LOWER_BODY -> listOf("tren inferior", "piernas", "glúteos", "gluteos", "isquios", "caderas")
        WorkoutZone.CORE -> listOf("core", "abdomen", "abdominal")
        WorkoutZone.BACK -> listOf("espalda", "tirón", "tiron", "remo")
        WorkoutZone.ARMS -> listOf("bíceps", "biceps", "tríceps", "triceps", "brazos", "curl")
        WorkoutZone.SHOULDERS -> listOf("hombros", "hombro", "shoulder")
        WorkoutZone.GLUTES -> listOf("glúteos", "gluteos", "glute")
        WorkoutZone.LEGS -> listOf("piernas", "pierna", "cuadriceps", "cuádriceps", "isquios")
    }
    return keywords.any { haystack.contains(it) }
}
