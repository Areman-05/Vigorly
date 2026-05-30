package com.example.vigorly.data.repository

import android.content.Context
import com.example.vigorly.data.AthleticStatBooster
import com.example.vigorly.data.MilestoneUnlocker
import com.example.vigorly.data.catalog.WorkoutCatalog
import com.example.vigorly.data.local.CoachingTipLoader
import com.example.vigorly.data.local.VigorlyPreferencesDataStore
import com.example.vigorly.data.model.CoachingTip
import com.example.vigorly.data.model.SessionSummary
import com.example.vigorly.data.model.WorkoutType
import com.example.vigorly.data.model.AuthError
import com.example.vigorly.data.model.AuthResult
import com.example.vigorly.data.model.UserAccount
import com.example.vigorly.util.LocaleManager
import com.example.vigorly.util.DailyTipSelector
import com.example.vigorly.util.WorkoutRecommender
import com.example.vigorly.navigation.AppDestination
import com.example.vigorly.data.model.AthleticStat
import com.example.vigorly.data.model.DailyGoals
import com.example.vigorly.data.model.Exercise
import com.example.vigorly.data.model.Milestone
import com.example.vigorly.data.model.RecentActivity
import com.example.vigorly.data.model.UserProfile
import com.example.vigorly.data.model.WeeklyGoal
import com.example.vigorly.data.model.WorkoutDetail
import com.example.vigorly.data.model.WorkoutHistoryItem
import com.example.vigorly.data.model.WorkoutSessionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class VigorlyRepository(context: Context) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val appContext = context.applicationContext
    private val preferences = VigorlyPreferencesDataStore(appContext)
    private val workouts = WorkoutCatalog.allWorkouts()
    private val coachingTips: List<CoachingTip> = CoachingTipLoader.load(appContext)

    val profile: StateFlow<UserProfile> = preferences.userProfile.stateIn(
        scope, SharingStarted.Eagerly, defaultProfile()
    )

    val dailyGoals: StateFlow<DailyGoals> = preferences.dailyGoals.stateIn(
        scope, SharingStarted.Eagerly, defaultDailyGoals()
    )

    val notificationsEnabled: StateFlow<Boolean> = preferences.notificationsEnabled.stateIn(
        scope, SharingStarted.Eagerly, true
    )

    val unitsMetric: StateFlow<Boolean> = preferences.unitsMetric.stateIn(
        scope, SharingStarted.Eagerly, true
    )

    val weeklyGoal: StateFlow<WeeklyGoal> = preferences.weeklyGoal.stateIn(
        scope, SharingStarted.Eagerly, WeeklyGoal(targetSessions = 5, completedSessions = 0)
    )

    private val _athleticStats = MutableStateFlow(defaultAthleticStats())
    val athleticStats: StateFlow<List<AthleticStat>> = _athleticStats.asStateFlow()

    private val _milestones = MutableStateFlow(defaultMilestones())
    val milestones: StateFlow<List<Milestone>> = _milestones.asStateFlow()

    private val _history = MutableStateFlow(defaultHistory())
    val history: StateFlow<List<WorkoutHistoryItem>> = _history.asStateFlow()

    private val _recentActivity = MutableStateFlow(defaultRecentActivity())
    val recentActivity: StateFlow<List<RecentActivity>> = _recentActivity.asStateFlow()

    private val _activeSession = MutableStateFlow<WorkoutSessionState?>(null)
    val activeSession: StateFlow<WorkoutSessionState?> = _activeSession.asStateFlow()

    private val _dailyTip = MutableStateFlow(coachingTips.firstOrNull() ?: CoachingTip("tip-001", ""))
    val dailyTip: StateFlow<CoachingTip> = _dailyTip.asStateFlow()

    private val _favorites = MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> = _favorites.asStateFlow()

    private val _onboardingCompleted = MutableStateFlow(false)
    val onboardingCompleted: StateFlow<Boolean> = _onboardingCompleted.asStateFlow()

    private val _lastSessionSummary = MutableStateFlow<SessionSummary?>(null)
    val lastSessionSummary: StateFlow<SessionSummary?> = _lastSessionSummary.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _appLocale = MutableStateFlow("es")
    val appLocale: StateFlow<String> = _appLocale.asStateFlow()

    private val _accounts = MutableStateFlow<List<UserAccount>>(emptyList())
    private var appDataPreloaded = false

    init {
        preferences.isLoggedIn.onEach { _isLoggedIn.value = it }.launchIn(scope)
        preferences.appLocale.onEach { _appLocale.value = it }.launchIn(scope)
        preferences.registeredAccounts.onEach { _accounts.value = it }.launchIn(scope)
        preferences.athleticStats.onEach { _athleticStats.value = it }.launchIn(scope)
        preferences.favoriteWorkoutIds.onEach { _favorites.value = it }.launchIn(scope)
        preferences.onboardingCompleted.onEach { _onboardingCompleted.value = it }.launchIn(scope)
        preferences.dailyTipIndex.onEach { index ->
            _dailyTip.value = DailyTipSelector.pick(coachingTips, index)
        }.launchIn(scope)
        scope.launch {
            if (coachingTips.isNotEmpty()) {
                preferences.advanceDailyTip(coachingTips.size)
            }
        }
        preferences.workoutHistory.onEach { stored ->
            _history.value = stored
            _recentActivity.value = stored.take(5).map { item ->
                RecentActivity(
                    id = item.id,
                    title = item.title,
                    timeLabel = item.timestampLabel.uppercase(Locale.getDefault()),
                    durationMinutes = item.durationMinutes,
                    iconName = item.iconName
                )
            }.ifEmpty { defaultRecentActivity() }
            refreshMilestones()
        }.launchIn(scope)
        preferences.userProfile.onEach { refreshMilestones() }.launchIn(scope)
    }

    private fun refreshMilestones() {
        _milestones.value = MilestoneUnlocker.apply(profile.value, defaultMilestones())
    }

    fun getWorkout(id: String): WorkoutDetail? = workouts[id]

    fun listWorkoutIds(): List<String> = workouts.keys.toList()

    fun listWorkouts(): List<WorkoutDetail> = workouts.values.toList()

    fun flatExercises(workout: WorkoutDetail): List<Exercise> =
        workout.blocks.flatMap { it.exercises }

    fun startWorkoutSession(workoutId: String): WorkoutSessionState? {
        val workout = getWorkout(workoutId) ?: return null
        val exercises = flatExercises(workout)
        if (exercises.isEmpty()) return null
        val session = WorkoutSessionState(
            workoutId = workoutId,
            workoutName = workout.name,
            currentExerciseIndex = 0,
            totalExercises = exercises.size,
            elapsedSeconds = 0,
            isPaused = false
        )
        _activeSession.value = session
        return session
    }

    fun tickSession() {
        _activeSession.updateSession { session ->
            if (session.restSecondsRemaining > 0) {
                session.copy(restSecondsRemaining = session.restSecondsRemaining - 1)
            } else {
                session.copy(elapsedSeconds = session.elapsedSeconds + 1)
            }
        }
    }

    fun toggleSessionPause() {
        _activeSession.updateSession { it.copy(isPaused = !it.isPaused) }
    }

    fun nextExercise() {
        _activeSession.updateSession { session ->
            if (session.currentExerciseIndex < session.totalExercises - 1) {
                session.copy(
                    currentExerciseIndex = session.currentExerciseIndex + 1,
                    restSecondsRemaining = REST_SECONDS_BETWEEN_EXERCISES
                )
            } else session
        }
    }

    fun skipRest() {
        _activeSession.updateSession { it.copy(restSecondsRemaining = 0) }
    }

    fun markCurrentExerciseComplete() {
        val workout = _activeSession.value?.workoutId?.let { getWorkout(it) } ?: return
        val exercises = flatExercises(workout)
        val current = _activeSession.value ?: return
        val exerciseId = exercises.getOrNull(current.currentExerciseIndex)?.id ?: return
        _activeSession.updateSession {
            it.copy(completedExerciseIds = it.completedExerciseIds + exerciseId)
        }
    }

    fun previousExercise() {
        _activeSession.updateSession { session ->
            if (session.currentExerciseIndex > 0) {
                session.copy(currentExerciseIndex = session.currentExerciseIndex - 1)
            } else session
        }
    }

    fun completeWorkoutSession() {
        val session = _activeSession.value ?: return
        val workout = getWorkout(session.workoutId) ?: return
        _lastSessionSummary.value = SessionSummaryFactory.from(session, workout)
        recordWorkoutCompletion(session.workoutId, workout.durationMinutes, workout.estimatedCalories)
        _activeSession.value = null
    }

    fun clearSessionSummary() {
        _lastSessionSummary.value = null
    }

    fun getRecommendedWorkout(): WorkoutDetail? =
        WorkoutRecommender.recommend(workouts.values.toList(), _history.value, _favorites.value)

    fun toggleFavorite(workoutId: String) {
        val updated = _favorites.value.toMutableSet().apply {
            if (contains(workoutId)) remove(workoutId) else add(workoutId)
        }
        _favorites.value = updated
        scope.launch { preferences.setFavoriteWorkoutIds(updated) }
    }

    fun isFavorite(workoutId: String): Boolean = workoutId in _favorites.value

    fun completeOnboarding() {
        scope.launch { preferences.setOnboardingCompleted(true) }
    }

    fun resetOnboarding() {
        scope.launch { preferences.setOnboardingCompleted(false) }
    }

    fun resetDailyGoals() {
        scope.launch { preferences.resetDailyGoals() }
    }

    fun resetWeeklyProgress() {
        scope.launch { preferences.resetWeeklyProgress() }
    }

    fun resolveStartDestination(): AppDestination = when {
        !_isLoggedIn.value -> com.example.vigorly.navigation.AppDestination.Login
        !_onboardingCompleted.value -> com.example.vigorly.navigation.AppDestination.Setup
        else -> com.example.vigorly.navigation.AppDestination.Main
    }

    suspend fun preloadAppData() {
        if (appDataPreloaded) return
        listWorkouts()
        coachingTips.size
        preferences.registeredAccounts.first()
        appDataPreloaded = true
    }

    suspend fun initializeLocale() {
        val locale = preferences.getAppLocaleSync()
        LocaleManager.applyLocale(locale)
        _appLocale.value = locale
    }

    suspend fun effectiveLocale(): String = preferences.getAppLocaleSync()

    fun setAppLocale(code: String) {
        scope.launch {
            preferences.setAppLocale(code)
            LocaleManager.applyLocale(code)
            _appLocale.value = code
        }
    }

    suspend fun login(email: String, password: String): AuthResult {
        val normalizedEmail = email.trim().lowercase()
        val account = _accounts.value.find {
            it.email.equals(normalizedEmail, ignoreCase = true) && it.password == password
        } ?: return AuthResult.Error(AuthError.INVALID_CREDENTIALS)
        return completeLogin(account)
    }

    suspend fun register(
        email: String,
        password: String,
        username: String,
        birthDate: String
    ): AuthResult {
        val normalizedEmail = email.trim().lowercase()
        val cleanUsername = username.trim()
        if (normalizedEmail.isBlank() || password.isBlank() || cleanUsername.isBlank() || birthDate.isBlank()) {
            return AuthResult.Error(AuthError.FIELDS_REQUIRED)
        }
        if (password.length < 6) return AuthResult.Error(AuthError.PASSWORD_TOO_SHORT)
        if (_accounts.value.any { it.email.equals(normalizedEmail, ignoreCase = true) }) {
            return AuthResult.Error(AuthError.EMAIL_ALREADY_EXISTS)
        }
        val account = UserAccount(
            id = UUID.randomUUID().toString(),
            email = normalizedEmail,
            password = password,
            username = cleanUsername,
            birthDate = birthDate
        )
        val updated = _accounts.value + account
        _accounts.value = updated
        preferences.saveRegisteredAccounts(updated)
        return completeLogin(account, isNewUser = true)
    }

    fun logout() {
        scope.launch {
            preferences.setLoggedIn(loggedIn = false, userId = null)
            _isLoggedIn.value = false
        }
    }

    private suspend fun completeLogin(account: UserAccount, isNewUser: Boolean = false): AuthResult {
        preferences.setLoggedIn(loggedIn = true, userId = account.id)
        _isLoggedIn.value = true
        if (isNewUser) {
            preferences.setOnboardingCompleted(false)
            _onboardingCompleted.value = false
            preferences.updateProfile(
                UserProfile(
                    displayName = account.username,
                    avatarUrl = defaultProfile().avatarUrl,
                    isProMember = false,
                    totalWorkouts = 0,
                    activeStreakDays = 0,
                    level = 1
                )
            )
            preferences.saveWorkoutHistory(emptyList())
            _history.value = emptyList()
            _recentActivity.value = emptyList()
        } else {
            preferences.updateProfile(profile.value.copy(displayName = account.username))
        }
        return AuthResult.Success
    }

    fun saveSetupPreferences(
        fitnessGoal: String,
        activityLevel: String,
        weeklySessions: Int,
        notifications: Boolean,
        workoutLocation: String,
        preferredTime: String
    ) {
        scope.launch {
            preferences.setFitnessGoal(fitnessGoal)
            preferences.setActivityLevel(activityLevel)
            preferences.setWorkoutLocation(workoutLocation)
            preferences.setPreferredTime(preferredTime)
            preferences.setNotificationsEnabled(notifications)
            preferences.saveWeeklyGoal(
                weeklyGoal.value.copy(
                    targetSessions = weeklySessions.coerceIn(1, 14),
                    completedSessions = 0
                )
            )
            preferences.setOnboardingCompleted(true)
            _onboardingCompleted.value = true
        }
    }

    fun getHistoryItem(id: String): WorkoutHistoryItem? = _history.value.find { it.id == id }

    fun cancelWorkoutSession() {
        _activeSession.value = null
    }

    fun recordWorkoutCompletion(workoutId: String, durationMinutes: Int? = null, calories: Int? = null) {
        val workout = getWorkout(workoutId) ?: return
        val duration = durationMinutes ?: workout.durationMinutes
        val kcal = calories ?: workout.estimatedCalories
        val nowLabel = SimpleDateFormat("'Today,' hh:mm a", Locale.getDefault()).format(Date())

        val boostedStats = AthleticStatBooster.bump(_athleticStats.value, workout.type)
        _athleticStats.value = boostedStats

        scope.launch {
            val currentProfile = profile.value
            preferences.updateProfile(
                currentProfile.copy(
                    totalWorkouts = currentProfile.totalWorkouts + 1,
                    activeStreakDays = currentProfile.activeStreakDays + 1
                )
            )
            val goals = dailyGoals.value
            preferences.updateDailyGoals(
                goals.copy(
                    moveProgress = (goals.moveProgress + 0.05f).coerceAtMost(1f),
                    exerciseProgress = (goals.exerciseProgress + 0.08f).coerceAtMost(1f),
                    standProgress = (goals.standProgress + 0.03f).coerceAtMost(1f),
                    moveCalories = (goals.moveCalories + kcal / 10).coerceAtMost(goals.moveCaloriesGoal),
                    steps = (goals.steps + 500).coerceAtMost(goals.stepsGoal)
                )
            )
            preferences.saveAthleticStats(boostedStats)
            val goal = weeklyGoal.value
            preferences.saveWeeklyGoal(goal.copy(completedSessions = goal.completedSessions + 1))
        }
        refreshMilestones()

        val historyItem = WorkoutHistoryItem(
            id = UUID.randomUUID().toString(),
            title = workout.name,
            timestampLabel = nowLabel,
            durationMinutes = duration,
            calories = kcal,
            iconName = iconForWorkoutType(workout.type.name)
        )
        _history.value = listOf(historyItem) + _history.value

        val recent = RecentActivity(
            id = historyItem.id,
            title = workout.name,
            timeLabel = nowLabel.uppercase(Locale.getDefault()),
            durationMinutes = duration,
            iconName = historyItem.iconName
        )
        _recentActivity.value = listOf(recent) + _recentActivity.value.take(4)

        scope.launch {
            preferences.saveWorkoutHistory(_history.value)
        }
    }

    fun updateDisplayName(name: String) {
        scope.launch {
            preferences.updateProfile(profile.value.copy(displayName = name))
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        scope.launch { preferences.setNotificationsEnabled(enabled) }
    }

    fun setUnitsMetric(metric: Boolean) {
        scope.launch { preferences.setUnitsMetric(metric) }
    }

    fun clearWorkoutHistory() {
        _history.value = emptyList()
        _recentActivity.value = emptyList()
        scope.launch { preferences.saveWorkoutHistory(emptyList()) }
    }

    fun setWeeklyTargetSessions(target: Int) {
        if (target <= 0) return
        scope.launch {
            val current = weeklyGoal.value
            preferences.saveWeeklyGoal(
                current.copy(
                    targetSessions = target,
                    completedSessions = current.completedSessions.coerceAtMost(target)
                )
            )
        }
    }

    fun refreshDailyGoalsFromActivity() {
        scope.launch {
            val goals = dailyGoals.value
            preferences.updateDailyGoals(
                goals.copy(
                    steps = (goals.steps + 120).coerceAtMost(goals.stepsGoal),
                    heartRateBpm = (68..78).random()
                )
            )
        }
    }

    private fun MutableStateFlow<WorkoutSessionState?>.updateSession(
        transform: (WorkoutSessionState) -> WorkoutSessionState
    ) {
        value?.let { value = transform(it) }
    }

    private fun iconForWorkoutType(type: String): String = when (type) {
        "HIIT" -> "directions_run"
        "RECOVERY" -> "self_improvement"
        "CARDIO" -> "directions_run"
        "SWIM" -> "pool"
        else -> "fitness_center"
    }

    companion object {
        const val REST_SECONDS_BETWEEN_EXERCISES = 45

        private const val AVATAR =
            "https://lh3.googleusercontent.com/aida-public/AB6AXuDzR2WvctnwBapv2J6FHSJYaIFfmcx4nHOnSfxS9s9DsMaU2qiczrCg6K6NhCslo0gLmFhJeFxgq3ulqD3z4hn_iwC2SqplrHuVXc8M4dX42iQoUArvxVD8coCeO-eFvEm0nH01AT0YTr7lBWOj1x6PxWej2M0mb_di0SpnHD5YQlNDE8HN_mFBd0v7fi8YXV3vimrg4QhfnOvZyF67cIrb0UZsn17KmNEg51BL1vdtc5iKyvmZjwee-hzGVEGko2Qxq_iTKNCuwcM"

        fun defaultProfile() = UserProfile(
            displayName = "Alex Rivers",
            avatarUrl = AVATAR,
            isProMember = true,
            totalWorkouts = 342,
            activeStreakDays = 14,
            level = 42
        )

        fun defaultDailyGoals() = DailyGoals(
            moveProgress = 0.75f,
            exerciseProgress = 0.50f,
            standProgress = 0.82f,
            moveCalories = 450,
            moveCaloriesGoal = 600,
            steps = 6240,
            stepsGoal = 10000,
            heartRateBpm = 72,
            sleepHours = 7f
        )

        fun defaultAthleticStats() = listOf(
            AthleticStat("Strength", 85),
            AthleticStat("Endurance", 70),
            AthleticStat("Mobility", 65),
            AthleticStat("Speed", 80),
            AthleticStat("Power", 90),
            AthleticStat("Stamina", 75)
        )

        fun defaultMilestones() = listOf(
            Milestone("streak_100", "100 Day", "Streak", "local_fire_department", true),
            Milestone("lift_10k", "10k Lbs", "Lifted", "fitness_center", true),
            Milestone("run_5k", "Sub 20", "5K Run", "timer", true),
            Milestone("elite", "Elite", "Status", "emoji_events", false)
        )

        fun defaultHistory() = listOf(
            WorkoutHistoryItem("h1", "Upper Body Power", "Today, 06:30 AM", 60, 540, "fitness_center"),
            WorkoutHistoryItem("h2", "HIIT Sprint Intervals", "Yesterday, 07:15 AM", 35, 420, "directions_run"),
            WorkoutHistoryItem("h3", "Active Recovery Yoga", "Mon, 18:00 PM", 45, 150, "self_improvement")
        )

        fun defaultRecentActivity() = listOf(
            RecentActivity("r1", "Morning Swim", "TODAY, 6:00 AM", 45, "pool")
        )
    }
}
