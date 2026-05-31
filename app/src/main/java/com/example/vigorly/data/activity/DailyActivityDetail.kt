package com.example.vigorly.data.activity

data class DailyActivityDetail(
    val moveCalories: Int = 0,
    val moveCaloriesByHour: List<Int> = List(HourlyActivityCodec.HOURS) { 0 },
    val exerciseMinutes: Int = 0,
    val exerciseMinutesByHour: List<Int> = List(HourlyActivityCodec.HOURS) { 0 },
    val standHours: Int = 0,
    val standByHour: List<Boolean> = List(HourlyActivityCodec.HOURS) { false },
    val steps: Int = 0,
    val distanceKm: Float = 0f
) {
    companion object {
        /** ~0,762 m por paso (cadencia media adulto). */
        const val KM_PER_STEP = 0.000762f

        fun distanceKmFromSteps(steps: Int): Float = steps * KM_PER_STEP
    }
}

object DailyActivityDetailBuilder {
    private const val CALORIES_PER_STEP = 0.04f

    fun build(
        stepsPerHour: IntArray,
        exerciseMinutesPerHour: IntArray,
        workoutCaloriesPerHour: IntArray,
        standHours: Set<Int>,
        totalSteps: Int,
        totalExerciseMinutes: Int,
        totalWorkoutCalories: Int
    ): DailyActivityDetail {
        val moveByHour = IntArray(HourlyActivityCodec.HOURS) { hour ->
            val fromSteps = (stepsPerHour[hour] * CALORIES_PER_STEP).toInt()
            fromSteps + workoutCaloriesPerHour[hour]
        }
        val moveTotal = (totalSteps * CALORIES_PER_STEP).toInt() + totalWorkoutCalories
        return DailyActivityDetail(
            moveCalories = moveTotal,
            moveCaloriesByHour = moveByHour.toList(),
            exerciseMinutes = totalExerciseMinutes,
            exerciseMinutesByHour = exerciseMinutesPerHour.toList(),
            standHours = standHours.size,
            standByHour = List(HourlyActivityCodec.HOURS) { standHours.contains(it) },
            steps = totalSteps,
            distanceKm = DailyActivityDetail.distanceKmFromSteps(totalSteps)
        )
    }
}
