package com.example.vigorly.data

import com.example.vigorly.data.model.AthleticStat
import com.example.vigorly.data.model.WorkoutType

object AthleticStatBooster {
    fun bump(stats: List<AthleticStat>, type: WorkoutType): List<AthleticStat> {
        val boostMap = when (type) {
            WorkoutType.STRENGTH -> mapOf("Strength" to 2, "Power" to 1)
            WorkoutType.HIIT -> mapOf("Speed" to 2, "Stamina" to 1)
            WorkoutType.CARDIO -> mapOf("Endurance" to 2, "Stamina" to 1)
            WorkoutType.RECOVERY -> mapOf("Mobility" to 2)
            WorkoutType.SWIM -> mapOf("Endurance" to 1, "Stamina" to 2)
        }
        return stats.map { stat ->
            val delta = boostMap[stat.label] ?: 0
            stat.copy(value = (stat.value + delta).coerceAtMost(100))
        }
    }
}
