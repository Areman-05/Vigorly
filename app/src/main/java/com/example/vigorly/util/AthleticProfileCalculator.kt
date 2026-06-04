package com.example.vigorly.util

import com.example.vigorly.data.model.AthleticStat
import com.example.vigorly.data.model.WorkoutHistoryItem
import com.example.vigorly.data.model.WorkoutType
import kotlin.math.ln
import kotlin.math.roundToInt

/**
 * Perfil atlético derivado solo del historial real. Sin entrenamientos registrados devuelve lista vacía.
 */
object AthleticProfileCalculator {

    private const val MAX_SCORE = 100
    private const val RECENT_WINDOW_MS = 14L * 24 * 60 * 60 * 1000

    private val statOrder = listOf(
        AthleticStatKeys.STRENGTH,
        AthleticStatKeys.ENDURANCE,
        AthleticStatKeys.MOBILITY,
        AthleticStatKeys.SPEED,
        AthleticStatKeys.POWER,
        AthleticStatKeys.STAMINA
    )

    fun hasProfile(stats: List<AthleticStat>): Boolean =
        stats.isNotEmpty() && stats.any { it.value > 0 }

    fun compute(
        history: List<WorkoutHistoryItem>,
        streakDays: Int
    ): List<AthleticStat> {
        if (history.isEmpty()) return emptyList()

        val scores = statOrder.associateWith { 0.0 }.toMutableMap()
        val now = System.currentTimeMillis()
        val typeCounts = mutableMapOf<WorkoutType, Int>()

        history.forEach { item ->
            val type = parseWorkoutType(item.workoutType) ?: return@forEach
            typeCounts[type] = (typeCounts[type] ?: 0) + 1

            val duration = item.durationMinutes.coerceAtLeast(1)
            val intensity = (
                (duration / 42.0) + (item.calories.coerceAtLeast(1) / 240.0)
                ) / 2.0
            val intensityClamped = intensity.coerceIn(0.5, 1.75)
            val isRecent = item.completedAtMillis > 0L &&
                (now - item.completedAtMillis) <= RECENT_WINDOW_MS
            val recency = if (isRecent) 1.28 else 1.0
            val boost = intensityClamped * recency

            applyWorkoutBoost(scores, type, boost)
        }

        val totalMinutes = history.sumOf { it.durationMinutes }
        val totalSessions = history.size
        val volumeFactor = ln(1.0 + totalMinutes / 35.0) * 7.5
        val frequencyFactor = ln(1.0 + totalSessions) * 5.5

        scores[AthleticStatKeys.STAMINA] = scores.getValue(AthleticStatKeys.STAMINA) + volumeFactor
        scores[AthleticStatKeys.ENDURANCE] = scores.getValue(AthleticStatKeys.ENDURANCE) +
            volumeFactor * 0.75 + frequencyFactor
        scores[AthleticStatKeys.MOBILITY] = scores.getValue(AthleticStatKeys.MOBILITY) +
            frequencyFactor * 0.45

        val streakBoost = streakDays.coerceIn(0, 28) * 0.85
        scores[AthleticStatKeys.STAMINA] = scores.getValue(AthleticStatKeys.STAMINA) + streakBoost
        scores[AthleticStatKeys.ENDURANCE] = scores.getValue(AthleticStatKeys.ENDURANCE) + streakBoost * 0.55

        if (typeCounts.size >= 4) {
            statOrder.forEach { key ->
                scores[key] = scores.getValue(key) + 3.0
            }
        }

        val dominantShare = typeCounts.values.maxOrNull()
            ?.let { max -> max.toDouble() / totalSessions }
            ?: 0.0
        if (dominantShare > 0.78 && totalSessions >= 5) {
            val dominantType = typeCounts.maxByOrNull { it.value }?.key
            statOrder.forEach { key ->
                if (!isPrimaryStatForType(key, dominantType)) {
                    scores[key] = scores.getValue(key) - 2.5
                }
            }
        }

        return statOrder.map { key ->
            AthleticStat(
                label = key,
                value = scores.getValue(key).roundToInt().coerceIn(0, MAX_SCORE)
            )
        }
    }

    private fun applyWorkoutBoost(
        scores: MutableMap<String, Double>,
        type: WorkoutType,
        boost: Double
    ) {
        when (type) {
            WorkoutType.STRENGTH -> {
                add(scores, AthleticStatKeys.STRENGTH, 5.2 * boost)
                add(scores, AthleticStatKeys.POWER, 4.0 * boost)
                add(scores, AthleticStatKeys.MOBILITY, 0.8 * boost)
            }
            WorkoutType.HIIT -> {
                add(scores, AthleticStatKeys.SPEED, 5.0 * boost)
                add(scores, AthleticStatKeys.STAMINA, 3.8 * boost)
                add(scores, AthleticStatKeys.POWER, 2.2 * boost)
            }
            WorkoutType.CARDIO -> {
                add(scores, AthleticStatKeys.ENDURANCE, 5.4 * boost)
                add(scores, AthleticStatKeys.STAMINA, 3.6 * boost)
            }
            WorkoutType.RECOVERY -> {
                add(scores, AthleticStatKeys.MOBILITY, 5.5 * boost)
                add(scores, AthleticStatKeys.ENDURANCE, 1.2 * boost)
            }
            WorkoutType.SWIM -> {
                add(scores, AthleticStatKeys.ENDURANCE, 4.2 * boost)
                add(scores, AthleticStatKeys.STAMINA, 4.8 * boost)
                add(scores, AthleticStatKeys.MOBILITY, 2.0 * boost)
            }
        }
    }

    private fun add(scores: MutableMap<String, Double>, key: String, delta: Double) {
        scores[key] = scores.getValue(key) + delta
    }

    private fun isPrimaryStatForType(statKey: String, type: WorkoutType?): Boolean = when (type) {
        WorkoutType.STRENGTH -> statKey in setOf(
            AthleticStatKeys.STRENGTH,
            AthleticStatKeys.POWER
        )
        WorkoutType.HIIT -> statKey in setOf(AthleticStatKeys.SPEED, AthleticStatKeys.STAMINA)
        WorkoutType.CARDIO -> statKey in setOf(AthleticStatKeys.ENDURANCE, AthleticStatKeys.STAMINA)
        WorkoutType.RECOVERY -> statKey == AthleticStatKeys.MOBILITY
        WorkoutType.SWIM -> statKey in setOf(AthleticStatKeys.ENDURANCE, AthleticStatKeys.STAMINA)
        null -> false
    }

    private fun parseWorkoutType(raw: String?): WorkoutType? = runCatching {
        WorkoutType.valueOf(raw?.uppercase() ?: return null)
    }.getOrNull()
}
