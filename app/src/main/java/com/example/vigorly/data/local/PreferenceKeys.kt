package com.example.vigorly.data.local

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys {
    val DISPLAY_NAME = stringPreferencesKey("display_name")
    val AVATAR_URL = stringPreferencesKey("avatar_url")
    val IS_PRO_MEMBER = booleanPreferencesKey("is_pro_member")
    val TOTAL_WORKOUTS = intPreferencesKey("total_workouts")
    val ACTIVE_STREAK_DAYS = intPreferencesKey("active_streak_days")
    val LEVEL = intPreferencesKey("level")
    val MOVE_PROGRESS = floatPreferencesKey("move_progress")
    val EXERCISE_PROGRESS = floatPreferencesKey("exercise_progress")
    val STAND_PROGRESS = floatPreferencesKey("stand_progress")
    val MOVE_CALORIES = intPreferencesKey("move_calories")
    val MOVE_CALORIES_GOAL = intPreferencesKey("move_calories_goal")
    val STEPS = intPreferencesKey("steps")
    val STEPS_GOAL = intPreferencesKey("steps_goal")
    val HEART_RATE_BPM = intPreferencesKey("heart_rate_bpm")
    val SLEEP_HOURS = floatPreferencesKey("sleep_hours")
    val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    val UNITS_METRIC = booleanPreferencesKey("units_metric")
    val WORKOUT_HISTORY = stringPreferencesKey("workout_history")
    val ATHLETIC_STATS = stringPreferencesKey("athletic_stats")
    val WEEKLY_TARGET_SESSIONS = intPreferencesKey("weekly_target_sessions")
    val WEEKLY_COMPLETED_SESSIONS = intPreferencesKey("weekly_completed_sessions")
}
