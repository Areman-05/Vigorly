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

    fun generate(context: Context, input: PersonalizedTipContext): CoachingTip {
        val candidates = buildCandidates(context, input)
        val index = (LocalDate.now().toEpochDay() % candidates.size).toInt().coerceAtLeast(0)
        return CoachingTip(id = "personalized-$index", text = candidates[index])
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

        when (input.fitnessGoal) {
            "strength", "muscle" -> tips += context.getString(
                R.string.coaching_tip_goal_strength,
                locationHint(context, input.workoutLocation)
            )
            "cardio", "endurance" -> tips += context.getString(
                R.string.coaching_tip_goal_cardio,
                timeHint(context, input.preferredTime)
            )
            "weight" -> tips += context.getString(
                R.string.coaching_tip_goal_weight,
                activityHint(context, input.activityLevel)
            )
            "flexibility" -> tips += context.getString(R.string.coaching_tip_goal_flexibility)
            "wellness" -> tips += context.getString(R.string.coaching_tip_goal_wellness)
        }

        when (input.workoutLocation) {
            "home" -> tips += context.getString(R.string.coaching_tip_location_home)
            "gym" -> tips += context.getString(R.string.coaching_tip_location_gym)
            "outdoor" -> tips += context.getString(R.string.coaching_tip_location_outdoor)
        }

        when (input.preferredTime) {
            "morning" -> tips += context.getString(R.string.coaching_tip_time_morning)
            "evening" -> tips += context.getString(R.string.coaching_tip_time_evening)
        }

        when (input.activityLevel) {
            "sedentary", "light" -> tips += context.getString(R.string.coaching_tip_activity_beginner)
            "athlete", "active" -> tips += context.getString(R.string.coaching_tip_activity_advanced)
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

    private fun locationHint(context: Context, location: String): String = when (location) {
        "gym" -> context.getString(R.string.coaching_hint_gym)
        "outdoor" -> context.getString(R.string.coaching_hint_outdoor)
        else -> context.getString(R.string.coaching_hint_home)
    }

    private fun timeHint(context: Context, time: String): String = when (time) {
        "morning" -> context.getString(R.string.coaching_hint_morning)
        "evening" -> context.getString(R.string.coaching_hint_evening)
        else -> context.getString(R.string.coaching_hint_flexible)
    }

    private fun activityHint(context: Context, level: String): String = when (level) {
        "sedentary", "light" -> context.getString(R.string.coaching_hint_gradual)
        "athlete", "active" -> context.getString(R.string.coaching_hint_intensity)
        else -> context.getString(R.string.coaching_hint_steady)
    }
}
