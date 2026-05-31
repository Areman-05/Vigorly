package com.example.vigorly.data.local

import com.example.vigorly.data.model.AthleticStat
import com.example.vigorly.data.model.DailyGoals
import com.example.vigorly.data.model.UserProfile
import com.example.vigorly.data.model.UserSessionSnapshot
import com.example.vigorly.data.model.WeeklyGoal
import org.json.JSONArray
import org.json.JSONObject

object UserSessionCodec {
    fun encode(sessions: Map<String, UserSessionSnapshot>): String {
        val root = JSONObject()
        sessions.forEach { (userId, snapshot) ->
            root.put(userId, encodeSnapshot(snapshot))
        }
        return root.toString()
    }

    fun decode(raw: String?): Map<String, UserSessionSnapshot> {
        if (raw.isNullOrBlank()) return emptyMap()
        val root = JSONObject(raw)
        return buildMap {
            root.keys().forEach { userId ->
                put(userId, decodeSnapshot(root.getJSONObject(userId)))
            }
        }
    }

    private fun encodeSnapshot(snapshot: UserSessionSnapshot): JSONObject {
        return JSONObject()
            .put("profile", encodeProfile(snapshot.profile))
            .put("dailyGoals", encodeDailyGoals(snapshot.dailyGoals))
            .put("weeklyGoal", JSONObject()
                .put("targetSessions", snapshot.weeklyGoal.targetSessions)
                .put("completedSessions", snapshot.weeklyGoal.completedSessions))
            .put("onboardingCompleted", snapshot.onboardingCompleted)
            .put("fitnessGoal", snapshot.fitnessGoal)
            .put("activityLevel", snapshot.activityLevel)
            .put("workoutLocation", snapshot.workoutLocation)
            .put("preferredTime", snapshot.preferredTime)
            .put("notificationsEnabled", snapshot.notificationsEnabled)
            .put("unitsMetric", snapshot.unitsMetric)
            .put("workoutHistory", HistoryCodec.encode(snapshot.workoutHistory))
            .put("athleticStats", AthleticStatsCodec.encode(snapshot.athleticStats))
            .put("favoriteWorkoutIds", FavoritesCodec.encode(snapshot.favoriteWorkoutIds))
            .put("dailyTipIndex", snapshot.dailyTipIndex)
    }

    private fun decodeSnapshot(json: JSONObject): UserSessionSnapshot {
        val profileJson = json.getJSONObject("profile")
        val dailyJson = json.getJSONObject("dailyGoals")
        val weeklyJson = json.getJSONObject("weeklyGoal")
        return UserSessionSnapshot(
            profile = decodeProfile(profileJson),
            dailyGoals = decodeDailyGoals(dailyJson),
            weeklyGoal = WeeklyGoal(
                targetSessions = weeklyJson.getInt("targetSessions"),
                completedSessions = weeklyJson.getInt("completedSessions")
            ),
            onboardingCompleted = json.getBoolean("onboardingCompleted"),
            fitnessGoal = json.getString("fitnessGoal"),
            activityLevel = json.getString("activityLevel"),
            workoutLocation = json.getString("workoutLocation"),
            preferredTime = json.getString("preferredTime"),
            notificationsEnabled = json.getBoolean("notificationsEnabled"),
            unitsMetric = json.getBoolean("unitsMetric"),
            workoutHistory = HistoryCodec.decode(json.getString("workoutHistory")),
            athleticStats = AthleticStatsCodec.decode(json.getString("athleticStats")),
            favoriteWorkoutIds = FavoritesCodec.decode(json.getString("favoriteWorkoutIds")),
            dailyTipIndex = json.getInt("dailyTipIndex")
        )
    }

    private fun encodeProfile(profile: UserProfile): JSONObject {
        return JSONObject()
            .put("displayName", profile.displayName)
            .put("avatarUrl", profile.avatarUrl.orEmpty())
            .put("isProMember", profile.isProMember)
            .put("totalWorkouts", profile.totalWorkouts)
            .put("activeStreakDays", profile.activeStreakDays)
            .put("level", profile.level)
    }

    private fun decodeProfile(json: JSONObject): UserProfile {
        return UserProfile(
            displayName = json.getString("displayName"),
            avatarUrl = json.optString("avatarUrl").ifBlank { null },
            isProMember = json.getBoolean("isProMember"),
            totalWorkouts = json.getInt("totalWorkouts"),
            activeStreakDays = json.getInt("activeStreakDays"),
            level = json.getInt("level")
        )
    }

    private fun encodeDailyGoals(goals: DailyGoals): JSONObject {
        return JSONObject()
            .put("moveProgress", goals.moveProgress.toDouble())
            .put("exerciseProgress", goals.exerciseProgress.toDouble())
            .put("standProgress", goals.standProgress.toDouble())
            .put("moveCalories", goals.moveCalories)
            .put("moveCaloriesGoal", goals.moveCaloriesGoal)
            .put("steps", goals.steps)
            .put("stepsGoal", goals.stepsGoal)
            .put("exerciseMinutes", goals.exerciseMinutes)
            .put("exerciseMinutesGoal", goals.exerciseMinutesGoal)
            .put("standHours", goals.standHours)
            .put("standHoursGoal", goals.standHoursGoal)
            .put("heartRateBpm", goals.heartRateBpm)
            .put("sleepHours", goals.sleepHours.toDouble())
    }

    private fun decodeDailyGoals(json: JSONObject): DailyGoals {
        return DailyGoals(
            moveProgress = json.getDouble("moveProgress").toFloat(),
            exerciseProgress = json.getDouble("exerciseProgress").toFloat(),
            standProgress = json.getDouble("standProgress").toFloat(),
            moveCalories = json.getInt("moveCalories"),
            moveCaloriesGoal = json.getInt("moveCaloriesGoal"),
            steps = json.getInt("steps"),
            stepsGoal = json.getInt("stepsGoal"),
            exerciseMinutes = json.optInt("exerciseMinutes", 0),
            exerciseMinutesGoal = json.optInt("exerciseMinutesGoal", 30),
            standHours = json.optInt("standHours", 0),
            standHoursGoal = json.optInt("standHoursGoal", 12),
            heartRateBpm = json.getInt("heartRateBpm"),
            sleepHours = json.getDouble("sleepHours").toFloat()
        )
    }
}
