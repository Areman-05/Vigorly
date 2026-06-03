package com.example.vigorly.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.vigorly.data.activity.ActivityHistoryCodec
import com.example.vigorly.data.activity.DailyActivityDaySummary
import com.example.vigorly.data.activity.HourlyActivityCodec
import com.example.vigorly.data.local.MilestoneShowcaseCodec
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
            exerciseMinutes = prefs[PreferenceKeys.EXERCISE_MINUTES] ?: defaults.exerciseMinutes,
            exerciseMinutesGoal = prefs[PreferenceKeys.EXERCISE_MINUTES_GOAL] ?: defaults.exerciseMinutesGoal,
            standHours = prefs[PreferenceKeys.STAND_HOURS] ?: defaults.standHours,
            standHoursGoal = prefs[PreferenceKeys.STAND_HOURS_GOAL] ?: defaults.standHoursGoal,
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
        decoded
    }

    val milestoneShowcase: Flow<List<String?>> = context.vigorlyDataStore.data.map { prefs ->
        MilestoneShowcaseCodec.decode(prefs[PreferenceKeys.MILESTONE_SHOWCASE])
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
            prefs[PreferenceKeys.EXERCISE_MINUTES] = goals.exerciseMinutes
            prefs[PreferenceKeys.EXERCISE_MINUTES_GOAL] = goals.exerciseMinutesGoal
            prefs[PreferenceKeys.STAND_HOURS] = goals.standHours
            prefs[PreferenceKeys.STAND_HOURS_GOAL] = goals.standHoursGoal
            prefs[PreferenceKeys.HEART_RATE_BPM] = goals.heartRateBpm
            prefs[PreferenceKeys.SLEEP_HOURS] = goals.sleepHours
        }
    }

    suspend fun dailyGoalsState(): DailyGoals = dailyGoals.first()

    suspend fun loadDailyActivityState(): PersistedDailyActivity {
        val prefs = context.vigorlyDataStore.data.first()
        val standRaw = prefs[PreferenceKeys.STAND_HOURS_TODAY].orEmpty()
        val standHours = if (standRaw.isBlank()) emptyList() else {
            standRaw.split(",").mapNotNull { it.toIntOrNull() }
        }
        return PersistedDailyActivity(
            dateKey = prefs[PreferenceKeys.DAILY_ACTIVITY_DATE].orEmpty(),
            stepBaseline = prefs[PreferenceKeys.STEP_COUNTER_BASELINE]?.toLongOrNull(),
            lastStepTotal = prefs[PreferenceKeys.STEP_COUNTER_LAST]?.toLongOrNull() ?: 0L,
            exerciseMinutes = prefs[PreferenceKeys.EXERCISE_MINUTES_TODAY] ?: 0,
            workoutCalories = prefs[PreferenceKeys.WORKOUT_CALORIES_TODAY] ?: 0,
            standHours = standHours,
            stepsPerHour = HourlyActivityCodec.decode(prefs[PreferenceKeys.STEPS_PER_HOUR]),
            exerciseMinutesPerHour = HourlyActivityCodec.decode(prefs[PreferenceKeys.EXERCISE_MINUTES_PER_HOUR]),
            workoutCaloriesPerHour = HourlyActivityCodec.decode(prefs[PreferenceKeys.WORKOUT_CALORIES_PER_HOUR])
        )
    }

    suspend fun saveDailyActivityState(
        dateKey: String,
        stepBaseline: Long?,
        lastStepTotal: Long,
        exerciseMinutes: Int,
        workoutCalories: Int,
        standHours: List<Int>,
        stepsPerHour: IntArray,
        exerciseMinutesPerHour: IntArray,
        workoutCaloriesPerHour: IntArray
    ) {
        context.vigorlyDataStore.edit { prefs ->
            prefs[PreferenceKeys.DAILY_ACTIVITY_DATE] = dateKey
            if (stepBaseline != null) {
                prefs[PreferenceKeys.STEP_COUNTER_BASELINE] = stepBaseline.toString()
            } else {
                prefs.remove(PreferenceKeys.STEP_COUNTER_BASELINE)
            }
            prefs[PreferenceKeys.STEP_COUNTER_LAST] = lastStepTotal.toString()
            prefs[PreferenceKeys.EXERCISE_MINUTES_TODAY] = exerciseMinutes
            prefs[PreferenceKeys.WORKOUT_CALORIES_TODAY] = workoutCalories
            prefs[PreferenceKeys.STAND_HOURS_TODAY] = standHours.sorted().joinToString(",")
            prefs[PreferenceKeys.STEPS_PER_HOUR] = HourlyActivityCodec.encode(stepsPerHour)
            prefs[PreferenceKeys.EXERCISE_MINUTES_PER_HOUR] = HourlyActivityCodec.encode(exerciseMinutesPerHour)
            prefs[PreferenceKeys.WORKOUT_CALORIES_PER_HOUR] = HourlyActivityCodec.encode(workoutCaloriesPerHour)
        }
    }

    suspend fun loadActivityDayHistory(): Map<String, DailyActivityDaySummary> {
        val prefs = context.vigorlyDataStore.data.first()
        return ActivityHistoryCodec.decode(prefs[PreferenceKeys.ACTIVITY_DAY_HISTORY])
    }

    suspend fun saveActivityDaySummary(summary: DailyActivityDaySummary) {
        val current = loadActivityDayHistory().toMutableMap()
        current[summary.dateKey] = summary
        val trimmed = trimHistory(current)
        context.vigorlyDataStore.edit { prefs ->
            prefs[PreferenceKeys.ACTIVITY_DAY_HISTORY] = ActivityHistoryCodec.encode(trimmed)
        }
    }

    private fun trimHistory(
        map: Map<String, DailyActivityDaySummary>,
        maxDays: Int = 400
    ): Map<String, DailyActivityDaySummary> {
        if (map.size <= maxDays) return map
        return map.entries
            .sortedByDescending { it.key }
            .take(maxDays)
            .associate { it.key to it.value }
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

    suspend fun saveMilestoneShowcase(slots: List<String?>) {
        context.vigorlyDataStore.edit { prefs ->
            prefs[PreferenceKeys.MILESTONE_SHOWCASE] = MilestoneShowcaseCodec.encode(slots)
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

    suspend fun getStreakBannerDismissedDate(): String? {
        return context.vigorlyDataStore.data.first()[PreferenceKeys.STREAK_BANNER_DISMISSED_DATE]
    }

    suspend fun setStreakBannerDismissedDate(dateKey: String) {
        context.vigorlyDataStore.edit { prefs ->
            prefs[PreferenceKeys.STREAK_BANNER_DISMISSED_DATE] = dateKey
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
