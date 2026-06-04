package com.example.vigorly

import com.example.vigorly.data.model.WorkoutHistoryItem
import com.example.vigorly.util.AthleticProfileCalculator
import com.example.vigorly.util.AthleticStatKeys
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AthleticProfileCalculatorTest {

    @Test
    fun emptyHistory_startsAtBaseline() {
        val stats = AthleticProfileCalculator.compute(emptyList(), streakDays = 0)
        stats.forEach { assertEquals(25, it.value) }
    }

    @Test
    fun strengthWorkouts_raiseStrengthAndPower() {
        val history = List(6) { i ->
            WorkoutHistoryItem(
                id = "$i",
                title = "Fuerza",
                timestampLabel = "Hoy",
                durationMinutes = 50,
                calories = 320,
                iconName = "fitness_center",
                completedAtMillis = System.currentTimeMillis(),
                workoutType = "STRENGTH"
            )
        }
        val stats = AthleticProfileCalculator.compute(history, streakDays = 3)
        val strength = stats.first { it.label == AthleticStatKeys.STRENGTH }.value
        val power = stats.first { it.label == AthleticStatKeys.POWER }.value
        val mobility = stats.first { it.label == AthleticStatKeys.MOBILITY }.value
        assertTrue(strength > mobility)
        assertTrue(power > mobility)
    }

    @Test
    fun cardioWorkouts_raiseEndurance() {
        val history = listOf(
            session("CARDIO", 45, 280),
            session("CARDIO", 40, 260),
            session("CARDIO", 50, 300)
        )
        val stats = AthleticProfileCalculator.compute(history, streakDays = 5)
        val endurance = stats.first { it.label == AthleticStatKeys.ENDURANCE }.value
        assertTrue(endurance >= 40)
    }

    private fun session(type: String, minutes: Int, kcal: Int) = WorkoutHistoryItem(
        id = type + minutes,
        title = type,
        timestampLabel = "Hoy",
        durationMinutes = minutes,
        calories = kcal,
        iconName = "fitness_center",
        completedAtMillis = System.currentTimeMillis(),
        workoutType = type
    )
}
