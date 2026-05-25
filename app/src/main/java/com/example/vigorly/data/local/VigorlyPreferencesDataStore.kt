package com.example.vigorly.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.vigorly.data.model.DailyGoals
import com.example.vigorly.data.model.UserProfile
import com.example.vigorly.data.model.WorkoutHistoryItem
import com.example.vigorly.data.repository.VigorlyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.vigorlyDataStore: DataStore<Preferences> by preferencesDataStore(name = "vigorly_prefs")

class VigorlyPreferencesDataStore(private val context: Context) {

    val userProfile: Flow<UserProfile> = context.vigorlyDataStore.data.map { prefs ->
        UserProfile(
            displayName = prefs[PreferenceKeys.DISPLAY_NAME] ?: VigorlyRepository.defaultProfile().displayName,
            avatarUrl = prefs[PreferenceKeys.AVATAR_URL] ?: VigorlyRepository.defaultProfile().avatarUrl,
            isProMember = prefs[PreferenceKeys.IS_PRO_MEMBER] ?: true,
            totalWorkouts = prefs[PreferenceKeys.TOTAL_WORKOUTS] ?: VigorlyRepository.defaultProfile().totalWorkouts,
            activeStreakDays = prefs[PreferenceKeys.ACTIVE_STREAK_DAYS] ?: VigorlyRepository.defaultProfile().activeStreakDays,
            level = prefs[PreferenceKeys.LEVEL] ?: VigorlyRepository.defaultProfile().level
        )
    }

    val dailyGoals: Flow<DailyGoals> = context.vigorlyDataStore.data.map { prefs ->
        val defaults = VigorlyRepository.defaultDailyGoals()
        DailyGoals(
            moveProgress = prefs[PreferenceKeys.MOVE_PROGRESS] ?: defaults.moveProgress,
            exerciseProgress = prefs[PreferenceKeys.EXERCISE_PROGRESS] ?: defaults.exerciseProgress,
            standProgress = prefs[PreferenceKeys.STAND_PROGRESS] ?: defaults.standProgress,
            moveCalories = prefs[PreferenceKeys.MOVE_CALORIES] ?: defaults.moveCalories,
            moveCaloriesGoal = prefs[PreferenceKeys.MOVE_CALORIES_GOAL] ?: defaults.moveCaloriesGoal,
            steps = prefs[PreferenceKeys.STEPS] ?: defaults.steps,
            stepsGoal = prefs[PreferenceKeys.STEPS_GOAL] ?: defaults.stepsGoal,
            heartRateBpm = prefs[PreferenceKeys.HEART_RATE_BPM] ?: defaults.heartRateBpm,
            sleepHours = prefs[PreferenceKeys.SLEEP_HOURS] ?: defaults.sleepHours
        )
    }

    val notificationsEnabled: Flow<Boolean> = context.vigorlyDataStore.data.map {
        it[PreferenceKeys.NOTIFICATIONS_ENABLED] ?: true
    }

    val unitsMetric: Flow<Boolean> = context.vigorlyDataStore.data.map {
        it[PreferenceKeys.UNITS_METRIC] ?: true
    }

    val workoutHistory: Flow<List<WorkoutHistoryItem>> = context.vigorlyDataStore.data.map { prefs ->
        val raw = prefs[PreferenceKeys.WORKOUT_HISTORY]
        val decoded = HistoryCodec.decode(raw)
        decoded.ifEmpty { VigorlyRepository.defaultHistory() }
    }

    suspend fun updateProfile(profile: UserProfile) {
        context.vigorlyDataStore.edit { prefs ->
            prefs[PreferenceKeys.DISPLAY_NAME] = profile.displayName
            prefs[PreferenceKeys.AVATAR_URL] = profile.avatarUrl.orEmpty()
            prefs[PreferenceKeys.IS_PRO_MEMBER] = profile.isProMember
            prefs[PreferenceKeys.TOTAL_WORKOUTS] = profile.totalWorkouts
            prefs[PreferenceKeys.ACTIVE_STREAK_DAYS] = profile.activeStreakDays
            prefs[PreferenceKeys.LEVEL] = profile.level
        }
    }

    suspend fun updateDailyGoals(goals: DailyGoals) {
        context.vigorlyDataStore.edit { prefs ->
            prefs[PreferenceKeys.MOVE_PROGRESS] = goals.moveProgress
            prefs[PreferenceKeys.EXERCISE_PROGRESS] = goals.exerciseProgress
            prefs[PreferenceKeys.STAND_PROGRESS] = goals.standProgress
            prefs[PreferenceKeys.MOVE_CALORIES] = goals.moveCalories
            prefs[PreferenceKeys.MOVE_CALORIES_GOAL] = goals.moveCaloriesGoal
            prefs[PreferenceKeys.STEPS] = goals.steps
            prefs[PreferenceKeys.STEPS_GOAL] = goals.stepsGoal
            prefs[PreferenceKeys.HEART_RATE_BPM] = goals.heartRateBpm
            prefs[PreferenceKeys.SLEEP_HOURS] = goals.sleepHours
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.vigorlyDataStore.edit { it[PreferenceKeys.NOTIFICATIONS_ENABLED] = enabled }
    }

    suspend fun setUnitsMetric(metric: Boolean) {
        context.vigorlyDataStore.edit { it[PreferenceKeys.UNITS_METRIC] = metric }
    }

    suspend fun saveWorkoutHistory(items: List<WorkoutHistoryItem>) {
        context.vigorlyDataStore.edit { prefs ->
            prefs[PreferenceKeys.WORKOUT_HISTORY] = HistoryCodec.encode(items)
        }
    }
}
