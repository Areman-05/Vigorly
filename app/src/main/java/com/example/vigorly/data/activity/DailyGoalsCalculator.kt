package com.example.vigorly.data.activity

import com.example.vigorly.data.model.DailyGoals

/**
 * Objetivos diarios alineados con recomendaciones de actividad saludable (OMS / estilo Apple Fitness).
 * El nivel de actividad del perfil escala las metas de forma real.
 */
object DailyGoalsCalculator {
    const val MOVE_CALORIES_GOAL = 500
    const val EXERCISE_MINUTES_GOAL = 30
    const val STAND_HOURS_GOAL = 12
    const val STEPS_GOAL = 10_000

    private const val CALORIES_PER_STEP = 0.04f

    fun goalScale(activityLevel: String): Float = when (
        activityLevel.split(',').map { it.trim() }.firstOrNull().orEmpty()
    ) {
        "sedentary" -> 0.7f
        "light" -> 0.85f
        "moderate" -> 1f
        "active" -> 1.2f
        "athlete" -> 1.4f
        else -> 1f
    }

    fun moveCaloriesGoal(activityLevel: String = "moderate"): Int =
        (MOVE_CALORIES_GOAL * goalScale(activityLevel)).toInt().coerceAtLeast(250)

    fun exerciseMinutesGoal(activityLevel: String = "moderate"): Int =
        (EXERCISE_MINUTES_GOAL * goalScale(activityLevel)).toInt().coerceAtLeast(15)

    fun stepsGoal(activityLevel: String = "moderate"): Int =
        (STEPS_GOAL * goalScale(activityLevel)).toInt().coerceAtLeast(5_000)

    fun build(
        steps: Int,
        workoutCalories: Int,
        exerciseMinutes: Int,
        standHours: Int,
        heartRateBpm: Int = 0,
        sleepHours: Float = 0f,
        activityLevel: String = "moderate"
    ): DailyGoals {
        val moveGoal = moveCaloriesGoal(activityLevel)
        val exerciseGoal = exerciseMinutesGoal(activityLevel)
        val stepsTarget = stepsGoal(activityLevel)
        val moveCaloriesFromSteps = (steps * CALORIES_PER_STEP).toInt()
        val moveCalories = moveCaloriesFromSteps + workoutCalories
        return DailyGoals(
            moveProgress = (moveCalories.toFloat() / moveGoal).coerceIn(0f, 1f),
            exerciseProgress = (exerciseMinutes.toFloat() / exerciseGoal).coerceIn(0f, 1f),
            standProgress = (standHours.toFloat() / STAND_HOURS_GOAL).coerceIn(0f, 1f),
            moveCalories = moveCalories,
            moveCaloriesGoal = moveGoal,
            steps = steps,
            stepsGoal = stepsTarget,
            exerciseMinutes = exerciseMinutes,
            exerciseMinutesGoal = exerciseGoal,
            standHours = standHours.coerceAtMost(STAND_HOURS_GOAL),
            standHoursGoal = STAND_HOURS_GOAL,
            heartRateBpm = heartRateBpm,
            sleepHours = sleepHours
        )
    }
}
