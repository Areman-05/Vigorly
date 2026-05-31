package com.example.vigorly.data.activity

import com.example.vigorly.data.model.DailyGoals

/**
 * Objetivos diarios alineados con recomendaciones de actividad saludable (OMS / estilo Apple Fitness).
 * 100 % en cada anillo = meta diaria alcanzada.
 */
object DailyGoalsCalculator {
    const val MOVE_CALORIES_GOAL = 500
    const val EXERCISE_MINUTES_GOAL = 30
    const val STAND_HOURS_GOAL = 12
    const val STEPS_GOAL = 10_000

    /** ~0,04 kcal por paso (estimación para caminar moderado). */
    private const val CALORIES_PER_STEP = 0.04f

    fun build(
        steps: Int,
        workoutCalories: Int,
        exerciseMinutes: Int,
        standHours: Int,
        heartRateBpm: Int = 0,
        sleepHours: Float = 0f
    ): DailyGoals {
        val moveCaloriesFromSteps = (steps * CALORIES_PER_STEP).toInt()
        val moveCalories = (moveCaloriesFromSteps + workoutCalories).coerceAtMost(MOVE_CALORIES_GOAL * 2)
        return DailyGoals(
            moveProgress = (moveCalories.toFloat() / MOVE_CALORIES_GOAL).coerceIn(0f, 1f),
            exerciseProgress = (exerciseMinutes.toFloat() / EXERCISE_MINUTES_GOAL).coerceIn(0f, 1f),
            standProgress = (standHours.toFloat() / STAND_HOURS_GOAL).coerceIn(0f, 1f),
            moveCalories = moveCalories,
            moveCaloriesGoal = MOVE_CALORIES_GOAL,
            steps = steps.coerceAtMost(STEPS_GOAL * 2),
            stepsGoal = STEPS_GOAL,
            exerciseMinutes = exerciseMinutes.coerceAtMost(EXERCISE_MINUTES_GOAL * 2),
            exerciseMinutesGoal = EXERCISE_MINUTES_GOAL,
            standHours = standHours.coerceAtMost(STAND_HOURS_GOAL),
            standHoursGoal = STAND_HOURS_GOAL,
            heartRateBpm = heartRateBpm,
            sleepHours = sleepHours
        )
    }
}
