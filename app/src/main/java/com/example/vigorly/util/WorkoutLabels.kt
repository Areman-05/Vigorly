package com.example.vigorly.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.vigorly.R
import com.example.vigorly.data.model.WorkoutType

object WorkoutLabels {

    @Composable
    fun typeLabel(type: WorkoutType): String = when (type) {
        WorkoutType.STRENGTH -> stringResource(R.string.workout_type_strength)
        WorkoutType.CARDIO -> stringResource(R.string.workout_type_cardio)
        WorkoutType.HIIT -> stringResource(R.string.workout_type_hiit)
        WorkoutType.RECOVERY -> stringResource(R.string.workout_type_recovery)
        WorkoutType.SWIM -> stringResource(R.string.workout_type_swim)
    }

    @Composable
    fun intensityLabel(intensity: String): String {
        val key = intensity.trim().lowercase()
        return when {
            key in listOf("high", "alta", "alto") -> stringResource(R.string.intensity_high)
            key in listOf("moderate", "moderada", "medio", "media") -> stringResource(R.string.intensity_moderate)
            key in listOf("low", "baja", "bajo") -> stringResource(R.string.intensity_low)
            else -> intensity
        }
    }

    fun intensityIsHigh(intensity: String): Boolean {
        val key = intensity.trim().lowercase()
        return key in listOf("high", "alta", "alto")
    }

    fun intensityProgress(intensity: String): Float {
        val key = intensity.trim().lowercase()
        return when {
            key in listOf("high", "alta", "alto") -> 0.92f
            key in listOf("moderate", "moderada", "medio", "media") -> 0.62f
            key in listOf("low", "baja", "bajo") -> 0.35f
            else -> 0.5f
        }
    }

    fun durationLabel(minutes: Int): String = "$minutes min"
}
