package com.example.vigorly.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.vigorly.data.model.AthleticStat
import com.example.vigorly.data.model.DailyGoals
import com.example.vigorly.data.model.UserProfile
import com.example.vigorly.data.model.WeeklyGoal
import com.example.vigorly.data.model.WorkoutHistoryItem
import com.example.vigorly.data.repository.VigorlyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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

    val athleticStats: Flow<List<AthleticStat>> = context.vigorlyDataStore.data.map { prefs ->
        val decoded = AthleticStatsCodec.decode(prefs[PreferenceKeys.ATHLETIC_STATS])
        decoded.ifEmpty { VigorlyRepository.defaultAthleticStats() }
    }

    val workoutHistory: Flow<List<WorkoutHistoryItem>> = context.vigorlyDataStore.data.map { prefs ->
        val raw = prefs[PreferenceKeys.WORKOUT_HISTORY]
        val decoded = HistoryCodec.decode(raw)
        decoded.ifEmpty { VigorlyRepository.defaultHistory() }
    }

    val weeklyGoal: Flow<WeeklyGoal> = context.vigorlyDataStore.data.map { prefs ->
        WeeklyGoal(
            targetSessions = prefs[PreferenceKeys.WEEKLY_TARGET_SESSIONS] ?: 5,
            completedSessions = prefs[PreferenceKeys.WEEKLY_COMPLETED_SESSIONS] ?: 0
        )
    }

    val onboardingCompleted: Flow<Boolean> = context.vigorlyDataStore.data.map {
        it[PreferenceKeys.ONBOARDING_COMPLETED] ?: false
    }

    val favoriteWorkoutIds: Flow<Set<String>> = context.vigorlyDataStore.data.map { prefs ->
        FavoritesCodec.decode(prefs[PreferenceKeys.FAVORITE_WORKOUTS])
    }

    val dailyTipIndex: Flow<Int> = context.vigorlyDataStore.data.map {
        it[PreferenceKeys.DAILY_TIP_INDEX] ?: 0
    }

    val appLocale: Flow<String> = context.vigorlyDataStore.data.map { prefs ->
        if (prefs[PreferenceKeys.LOCALE_USER_SELECTED] == true) {
            prefs[PreferenceKeys.APP_LOCALE] ?: "es"
        } else {
            "es"
        }
    }

    val isLoggedIn: Flow<Boolean> = context.vigorlyDataStore.data.map {
        it[PreferenceKeys.IS_LOGGED_IN] ?: false
    }

    val currentUserId: Flow<String?> = context.vigorlyDataStore.data.map {
        it[PreferenceKeys.CURRENT_USER_ID]
    }

    val registeredAccounts: Flow<List<com.example.vigorly.data.model.UserAccount>> =
        context.vigorlyDataStore.data.map { prefs ->
            AccountsCodec.decode(prefs[PreferenceKeys.REGISTERED_ACCOUNTS])
        }

    val preferredTime: Flow<String> = context.vigorlyDataStore.data.map {
        it[PreferenceKeys.PREFERRED_TIME] ?: "flexible"
    }

    val userSessions: Flow<Map<String, com.example.vigorly.data.model.UserSessionSnapshot>> =
        context.vigorlyDataStore.data.map { prefs ->
            UserSessionCodec.decode(prefs[PreferenceKeys.USER_SESSIONS])
        }

    val fitnessGoal: Flow<String> = context.vigorlyDataStore.data.map {
        it[PreferenceKeys.FITNESS_GOAL] ?: "wellness"
    }

    val activityLevel: Flow<String> = context.vigorlyDataStore.data.map {
        it[PreferenceKeys.ACTIVITY_LEVEL] ?: "moderate"
    }

    val workoutLocation: Flow<String> = context.vigorlyDataStore.data.map {
        it[PreferenceKeys.WORKOUT_LOCATION] ?: "home"
    }

    suspend fun getAppLocaleSync(): String {
        val prefs = context.vigorlyDataStore.data.first()
        val userSelected = prefs[PreferenceKeys.LOCALE_USER_SELECTED] == true
        val locale = if (userSelected) {
            prefs[PreferenceKeys.APP_LOCALE] ?: "es"
        } else {
            "es"
        }
        context.vigorlyDataStore.edit {
            it[PreferenceKeys.APP_LOCALE] = locale
            if (!userSelected) {
                it[PreferenceKeys.LOCALE_USER_SELECTED] = false
            }
        }
        return locale
    }

    suspend fun setAppLocale(code: String) {
        context.vigorlyDataStore.edit {
            it[PreferenceKeys.APP_LOCALE] = code
            it[PreferenceKeys.LOCALE_USER_SELECTED] = true
        }
    }

    suspend fun setLoggedIn(loggedIn: Boolean, userId: String?) {
        context.vigorlyDataStore.edit { prefs ->
            prefs[PreferenceKeys.IS_LOGGED_IN] = loggedIn
            if (userId != null) {
                prefs[PreferenceKeys.CURRENT_USER_ID] = userId
            } else {
                prefs.remove(PreferenceKeys.CURRENT_USER_ID)
            }
        }
    }

    suspend fun saveRegisteredAccounts(accounts: List<com.example.vigorly.data.model.UserAccount>) {
        context.vigorlyDataStore.edit {
            it[PreferenceKeys.REGISTERED_ACCOUNTS] = AccountsCodec.encode(accounts)
        }
    }

    suspend fun saveUserSession(userId: String, snapshot: com.example.vigorly.data.model.UserSessionSnapshot) {
        context.vigorlyDataStore.edit { prefs ->
            val current = UserSessionCodec.decode(prefs[PreferenceKeys.USER_SESSIONS]).toMutableMap()
            current[userId] = snapshot
            prefs[PreferenceKeys.USER_SESSIONS] = UserSessionCodec.encode(current)
        }
    }

    suspend fun loadUserSession(userId: String): com.example.vigorly.data.model.UserSessionSnapshot? {
        val prefs = context.vigorlyDataStore.data.first()
        return UserSessionCodec.decode(prefs[PreferenceKeys.USER_SESSIONS])[userId]
    }

    suspend fun setFitnessGoal(goal: String) {
        context.vigorlyDataStore.edit { it[PreferenceKeys.FITNESS_GOAL] = goal }
    }

    suspend fun setActivityLevel(level: String) {
        context.vigorlyDataStore.edit { it[PreferenceKeys.ACTIVITY_LEVEL] = level }
    }

    suspend fun setWorkoutLocation(location: String) {
        context.vigorlyDataStore.edit { it[PreferenceKeys.WORKOUT_LOCATION] = location }
    }

    suspend fun setPreferredTime(time: String) {
        context.vigorlyDataStore.edit { it[PreferenceKeys.PREFERRED_TIME] = time }
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

    suspend fun saveAthleticStats(stats: List<AthleticStat>) {
        context.vigorlyDataStore.edit { prefs ->
            prefs[PreferenceKeys.ATHLETIC_STATS] = AthleticStatsCodec.encode(stats)
        }
    }

    suspend fun saveWeeklyGoal(goal: WeeklyGoal) {
        context.vigorlyDataStore.edit { prefs ->
            prefs[PreferenceKeys.WEEKLY_TARGET_SESSIONS] = goal.targetSessions
            prefs[PreferenceKeys.WEEKLY_COMPLETED_SESSIONS] = goal.completedSessions
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.vigorlyDataStore.edit { it[PreferenceKeys.ONBOARDING_COMPLETED] = completed }
    }

    suspend fun setFavoriteWorkoutIds(ids: Set<String>) {
        context.vigorlyDataStore.edit { it[PreferenceKeys.FAVORITE_WORKOUTS] = FavoritesCodec.encode(ids) }
    }

    suspend fun advanceDailyTip(tipCount: Int) {
        if (tipCount <= 0) return
        context.vigorlyDataStore.edit { prefs ->
            val current = prefs[PreferenceKeys.DAILY_TIP_INDEX] ?: 0
            prefs[PreferenceKeys.DAILY_TIP_INDEX] = (current + 1) % tipCount
        }
    }

    suspend fun setDailyTipIndex(index: Int) {
        context.vigorlyDataStore.edit { prefs ->
            prefs[PreferenceKeys.DAILY_TIP_INDEX] = index.coerceAtLeast(0)
        }
    }

    suspend fun resetDailyGoals() {
        val defaults = VigorlyRepository.defaultDailyGoals()
        updateDailyGoals(defaults)
    }

    suspend fun resetWeeklyProgress() {
        val current = weeklyGoal
        context.vigorlyDataStore.edit { prefs ->
            val target = prefs[PreferenceKeys.WEEKLY_TARGET_SESSIONS] ?: 5
            prefs[PreferenceKeys.WEEKLY_COMPLETED_SESSIONS] = 0
            prefs[PreferenceKeys.WEEKLY_TARGET_SESSIONS] = target
        }
    }
}
