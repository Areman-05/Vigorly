package com.example.vigorly.data.activity

import org.json.JSONObject

data class DailyActivityDaySummary(
    val dateKey: String,
    val moveProgress: Float,
    val exerciseProgress: Float,
    val standProgress: Float,
    val moveCalories: Int = 0,
    val exerciseMinutes: Int = 0,
    val standHours: Int = 0,
    val steps: Int = 0,
    val moveCaloriesByHour: List<Int> = emptyList(),
    val exerciseMinutesByHour: List<Int> = emptyList(),
    val standByHour: List<Boolean> = emptyList()
) {
    fun toDetail(): DailyActivityDetail = DailyActivityDetail(
        moveCalories = moveCalories,
        moveCaloriesByHour = moveCaloriesByHour.ifEmpty { List(HourlyActivityCodec.HOURS) { 0 } },
        exerciseMinutes = exerciseMinutes,
        exerciseMinutesByHour = exerciseMinutesByHour.ifEmpty { List(HourlyActivityCodec.HOURS) { 0 } },
        standHours = standHours,
        standByHour = standByHour.ifEmpty { List(HourlyActivityCodec.HOURS) { false } },
        steps = steps,
        distanceKm = DailyActivityDetail.distanceKmFromSteps(steps)
    )

    companion object {
        fun fromDetail(dateKey: String, detail: DailyActivityDetail): DailyActivityDaySummary {
            val moveGoal = DailyGoalsCalculator.MOVE_CALORIES_GOAL
            val exerciseGoal = DailyGoalsCalculator.EXERCISE_MINUTES_GOAL
            val standGoal = DailyGoalsCalculator.STAND_HOURS_GOAL
            return DailyActivityDaySummary(
                dateKey = dateKey,
                moveProgress = (detail.moveCalories.toFloat() / moveGoal).coerceIn(0f, 1f),
                exerciseProgress = (detail.exerciseMinutes.toFloat() / exerciseGoal).coerceIn(0f, 1f),
                standProgress = (detail.standHours.toFloat() / standGoal).coerceIn(0f, 1f),
                moveCalories = detail.moveCalories,
                exerciseMinutes = detail.exerciseMinutes,
                standHours = detail.standHours,
                steps = detail.steps,
                moveCaloriesByHour = detail.moveCaloriesByHour,
                exerciseMinutesByHour = detail.exerciseMinutesByHour,
                standByHour = detail.standByHour
            )
        }

        fun fromJson(dateKey: String, json: JSONObject): DailyActivityDaySummary {
            fun parseInts(raw: String): List<Int> =
                if (raw.isBlank()) emptyList() else raw.split(",").mapNotNull { it.toIntOrNull() }
            fun parseBools(raw: String): List<Boolean> =
                if (raw.isBlank()) emptyList() else raw.split(",").map { it == "1" }
            return DailyActivityDaySummary(
                dateKey = dateKey,
                moveProgress = json.getDouble("moveProgress").toFloat(),
                exerciseProgress = json.getDouble("exerciseProgress").toFloat(),
                standProgress = json.getDouble("standProgress").toFloat(),
                moveCalories = json.optInt("moveCalories"),
                exerciseMinutes = json.optInt("exerciseMinutes"),
                standHours = json.optInt("standHours"),
                steps = json.optInt("steps"),
                moveCaloriesByHour = parseInts(json.optString("moveCaloriesByHour")),
                exerciseMinutesByHour = parseInts(json.optString("exerciseMinutesByHour")),
                standByHour = parseBools(json.optString("standByHour"))
            )
        }
    }
}

object ActivityHistoryCodec {
    fun encode(map: Map<String, DailyActivityDaySummary>): String {
        val root = JSONObject()
        map.forEach { (key, summary) ->
            root.put(key, summary.toJson())
        }
        return root.toString()
    }

    fun decode(raw: String?): Map<String, DailyActivityDaySummary> {
        if (raw.isNullOrBlank()) return emptyMap()
        return try {
            val root = JSONObject(raw)
            buildMap {
                root.keys().forEach { key ->
                    put(key, DailyActivityDaySummary.fromJson(key, root.getJSONObject(key)))
                }
            }
        } catch (_: Exception) {
            emptyMap()
        }
    }

    private fun DailyActivityDaySummary.toJson(): JSONObject = JSONObject()
        .put("moveProgress", moveProgress.toDouble())
        .put("exerciseProgress", exerciseProgress.toDouble())
        .put("standProgress", standProgress.toDouble())
        .put("moveCalories", moveCalories)
        .put("exerciseMinutes", exerciseMinutes)
        .put("standHours", standHours)
        .put("steps", steps)
        .put("moveCaloriesByHour", moveCaloriesByHour.joinToString(","))
        .put("exerciseMinutesByHour", exerciseMinutesByHour.joinToString(","))
        .put("standByHour", standByHour.joinToString(",") { if (it) "1" else "0" })
}
