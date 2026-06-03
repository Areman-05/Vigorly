package com.example.vigorly.data

import com.example.vigorly.data.model.AthleticStat
import com.example.vigorly.data.model.WorkoutType
import com.example.vigorly.util.AthleticStatKeys
import com.example.vigorly.util.AthleticStatLabels

object AthleticStatBooster {
    fun bump(stats: List<AthleticStat>, type: WorkoutType): List<AthleticStat> {
        val boostMap = when (type) {
            WorkoutType.STRENGTH -> mapOf(AthleticStatKeys.STRENGTH to 2, AthleticStatKeys.POWER to 1)
            WorkoutType.HIIT -> mapOf(AthleticStatKeys.SPEED to 2, AthleticStatKeys.STAMINA to 1)
            WorkoutType.CARDIO -> mapOf(AthleticStatKeys.ENDURANCE to 2, AthleticStatKeys.STAMINA to 1)
            WorkoutType.RECOVERY -> mapOf(AthleticStatKeys.MOBILITY to 2)
            WorkoutType.SWIM -> mapOf(AthleticStatKeys.ENDURANCE to 1, AthleticStatKeys.STAMINA to 2)
        }
        return stats.map { stat ->
            val key = AthleticStatLabels.normalizeKey(stat.label)
            val delta = boostMap[key] ?: 0
            stat.copy(
                label = key,
                value = (stat.value + delta).coerceAtMost(100)
            )
        }
    }
}
