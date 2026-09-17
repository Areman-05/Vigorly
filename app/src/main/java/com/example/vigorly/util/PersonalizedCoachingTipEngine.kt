package com.example.vigorly.util

import android.content.Context
import com.example.vigorly.R
import com.example.vigorly.data.model.CoachingTip
import com.example.vigorly.data.model.DailyGoals
import com.example.vigorly.data.model.WeeklyGoal
import java.time.LocalDate
import java.util.Calendar

data class PersonalizedTipContext(
    val fitnessGoal: String,
    val activityLevel: String,
    val workoutLocation: String,
    val preferredTime: String,
    val dailyGoals: DailyGoals,
    val weeklyGoal: WeeklyGoal,
    val streakDays: Int,
    val recentWorkoutTitles: List<String>,
    val hourOfDay: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
)

object PersonalizedCoachingTipEngine {

    fun generate(context: Context, input: PersonalizedTipContext): CoachingTip =
        generateMany(context, input, count = 1).first()

    fun generateMany(context: Context, input: PersonalizedTipContext, count: Int): List<CoachingTip> {
        val candidates = buildCandidates(context, input)
        if (candidates.isEmpty()) {
            return listOf(
                CoachingTip(
                    id = "personalized-0",
                    text = context.getString(R.string.coaching_tip_fallback)
                )
            )
        }
        val day = LocalDate.now().toEpochDay()
        val size = candidates.size
        val take = count.coerceAtLeast(1).coerceAtMost(size)
        return List(take) { offset ->
            val index = ((day + offset) % size).toInt().coerceAtLeast(0)
            CoachingTip(id = "personalized-$index-$offset", text = candidates[index])
        }.distinctBy { it.text }
    }

    private fun buildCandidates(context: Context, input: PersonalizedTipContext): List<String> {
        val tips = mutableListOf<String>()
        val goals = input.dailyGoals
        val weekly = input.weeklyGoal
        val remainingWeekly = WeeklyProgressCalculator.remainingSessions(weekly)

        if (weekly.completedSessions >= weekly.targetSessions) {
            tips += context.getString(R.string.coaching_tip_weekly_complete)
        } else if (remainingWeekly == 1) {
            tips += context.getString(R.string.coaching_tip_weekly_one_left, weekly.targetSessions)
        } else if (remainingWeekly in 2..3 && weekly.progress < 0.5f) {
            tips += context.getString(
                R.string.coaching_tip_weekly_catch_up,
                remainingWeekly,
                weekly.targetSessions
            )
        }

        if (goals.exerciseProgress < 0.4f && input.hourOfDay >= 16) {
            tips += context.getString(R.string.coaching_tip_low_exercise_evening)
        } else if (goals.exerciseProgress < 0.5f) {
            tips += context.getString(R.string.coaching_tip_low_exercise)
        }

        if (goals.moveProgress < 0.35f && input.hourOfDay in 12..20) {
            tips += context.getString(R.string.coaching_tip_low_move)
        }

        if (goals.standProgress < 0.5f && input.hourOfDay >= 10) {
            tips += context.getString(R.string.coaching_tip_low_stand)
        }

        if (input.streakDays >= 3) {
            tips += context.getString(R.string.coaching_tip_streak_keep, input.streakDays)
        }

        val goalKeys = csvKeys(input.fitnessGoal)
        val intensityKeys = csvKeys(input.activityLevel)
        val durationKeys = csvKeys(input.workoutLocation)

        if (goalKeys.any { it in setOf("strength", "muscle") }) {
            tips += context.getString(
                R.string.coaching_tip_goal_strength,
                locationHint(context, input.workoutLocation)
            )
        }
        if (goalKeys.any { it in setOf("cardio", "endurance", "hiit", "swim") }) {
            tips += context.getString(
                R.string.coaching_tip_goal_cardio,
                timeHint(context, input.preferredTime)
            )
        }
        if (goalKeys.contains("weight") || intensityKeys.contains("high")) {
            tips += context.getString(
                R.string.coaching_tip_goal_weight,
                activityHint(context, input.activityLevel)
            )
        }
        if (goalKeys.any { it in setOf("flexibility", "mobility", "pilates") }) {
            tips += context.getString(R.string.coaching_tip_goal_flexibility)
        }
        if (goalKeys.any { it in setOf("wellness", "recovery") }) {
            tips += context.getString(R.string.coaching_tip_goal_wellness)
        }

        when {
            durationKeys.contains("home") -> tips += context.getString(R.string.coaching_tip_location_home)
            durationKeys.contains("gym") -> tips += context.getString(R.string.coaching_tip_location_gym)
            durationKeys.contains("outdoor") -> tips += context.getString(R.string.coaching_tip_location_outdoor)
            durationKeys.contains("short") -> tips += context.getString(R.string.coaching_tip_location_home)
            durationKeys.contains("long") -> tips += context.getString(R.string.coaching_tip_location_gym)
        }

        when (input.preferredTime) {
            "morning" -> tips += context.getString(R.string.coaching_tip_time_morning)
            "evening" -> tips += context.getString(R.string.coaching_tip_time_evening)
        }

        when {
            intensityKeys.any { it in setOf("sedentary", "light", "low") } ->
                tips += context.getString(R.string.coaching_tip_activity_beginner)
            intensityKeys.any { it in setOf("athlete", "active", "high") } ->
                tips += context.getString(R.string.coaching_tip_activity_advanced)
        }

        if (input.recentWorkoutTitles.isEmpty()) {
            tips += context.getString(R.string.coaching_tip_no_recent_workouts)
        } else {
            val last = input.recentWorkoutTitles.first()
            tips += context.getString(R.string.coaching_tip_after_session, last)
        }

        if (tips.isEmpty()) {
            tips += context.getString(R.string.coaching_tip_fallback)
        }

        return tips.distinct()
    }

    private fun locationHint(context: Context, location: String): String {
        val keys = csvKeys(location)
        return when {
            keys.any { it in setOf("gym", "long") } -> context.getString(R.string.coaching_hint_gym)
            keys.any { it in setOf("outdoor", "medium") } -> context.getString(R.string.coaching_hint_outdoor)
            else -> context.getString(R.string.coaching_hint_home)
        }
    }

    private fun timeHint(context: Context, time: String): String = when (time) {
        "morning" -> context.getString(R.string.coaching_hint_morning)
        "evening" -> context.getString(R.string.coaching_hint_evening)
        else -> context.getString(R.string.coaching_hint_flexible)
    }

    private fun activityHint(context: Context, level: String): String {
        val keys = csvKeys(level)
        return when {
            keys.any { it in setOf("sedentary", "light", "low") } ->
                context.getString(R.string.coaching_hint_gradual)
            keys.any { it in setOf("athlete", "active", "high") } ->
                context.getString(R.string.coaching_hint_intensity)
            else -> context.getString(R.string.coaching_hint_steady)
        }
    }

    private fun csvKeys(raw: String): Set<String> =
        raw.split(',').map { it.trim().lowercase() }.filter { it.isNotEmpty() }.toSet()
}
