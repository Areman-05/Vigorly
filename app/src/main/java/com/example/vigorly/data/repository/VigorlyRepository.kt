package com.example.vigorly.data.repository

import android.content.Context
import com.example.vigorly.data.activity.DailyActivityTracker
import com.example.vigorly.data.activity.DailyGoalsCalculator
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
import com.example.vigorly.data.model.UserSessionSnapshot
import com.example.vigorly.ui.setup.SetupDevFlags
import com.example.vigorly.auth.GoogleUserInfo
import com.example.vigorly.util.AuthValidator
import com.example.vigorly.util.LocaleManager
import com.example.vigorly.util.PasswordHasher
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
    private val activityTracker = DailyActivityTracker(appContext, preferences) { }
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
        scope.launch { restoreActiveUserSessionIfNeeded() }
        scope.launch { activityTracker.initialize() }
    }

    fun startActivityTracking() {
        activityTracker.start()
    }

    fun stopActivityTracking() {
        activityTracker.stop()
    }

    fun closeActivityTracking() {
        activityTracker.close()
    }

    private suspend fun restoreActiveUserSessionIfNeeded() {
        if (!preferences.isLoggedIn.first()) return
        val userId = preferences.currentUserId.first() ?: return
        val saved = preferences.loadUserSession(userId) ?: return
        applyUserSession(saved)
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

    suspend fun resolveStartDestination(): AppDestination = when {
        SetupDevFlags.FORCE_SPLASH_TO_LOGIN -> AppDestination.Login
        SetupDevFlags.FORCE_SPLASH_TO_SETUP -> AppDestination.Setup
        preferences.isLoggedIn.first() -> AppDestination.Main
        else -> AppDestination.Login
    }

    suspend fun preloadAppData() {
        if (appDataPreloaded) return
        restoreActiveUserSessionIfNeeded()
        listWorkouts()
        coachingTips.size
        preferences.registeredAccounts.first()
        appDataPreloaded = true
    }

    suspend fun initializeLocale() {
        LocaleManager.applyLocale("es")
        _appLocale.value = "es"
    }

    suspend fun effectiveLocale(): String = "es"

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
            it.email.equals(normalizedEmail, ignoreCase = true)
        } ?: return AuthResult.Error(AuthError.INVALID_CREDENTIALS)
        if (account.authProvider == "google" && account.passwordHash.isBlank()) {
            return AuthResult.Error(AuthError.INVALID_CREDENTIALS)
        }
        if (!PasswordHasher.verify(password, account.passwordSalt, account.passwordHash)) {
            return AuthResult.Error(AuthError.INVALID_CREDENTIALS)
        }
        persistCurrentUserSessionIfNeeded()
        val upgradedAccount = upgradeLegacyPasswordIfNeeded(account, password)
        return completeLogin(upgradedAccount)
    }

    suspend fun loginWithGoogle(info: GoogleUserInfo): AuthResult {
        persistCurrentUserSessionIfNeeded()
        val normalizedEmail = info.email.trim().lowercase()

        _accounts.value.find { it.googleId == info.id }?.let { return completeLogin(it) }

        val existingByEmail = _accounts.value.find {
            it.email.equals(normalizedEmail, ignoreCase = true)
        }
        if (existingByEmail != null) {
            val linked = if (existingByEmail.googleId == null) {
                existingByEmail.copy(googleId = info.id)
            } else {
                existingByEmail
            }
            if (linked != existingByEmail) {
                val updated = _accounts.value.map { if (it.id == linked.id) linked else it }
                _accounts.value = updated
                preferences.saveRegisteredAccounts(updated)
            }
            return completeLogin(linked)
        }

        val account = UserAccount(
            id = UUID.randomUUID().toString(),
            email = normalizedEmail,
            passwordHash = "",
            passwordSalt = "",
            username = usernameFromGoogle(info.displayName, normalizedEmail),
            birthDate = "",
            createdAtMillis = System.currentTimeMillis(),
            authProvider = "google",
            googleId = info.id
        )
        val updated = _accounts.value + account
        _accounts.value = updated
        preferences.saveRegisteredAccounts(updated)
        return completeLogin(account, isNewUser = true)
    }

    private fun usernameFromGoogle(displayName: String?, email: String): String {
        displayName?.trim()?.takeIf { AuthValidator.validateUsername(it) == null }?.let { return it }
        val fromEmail = email.substringBefore("@")
            .replace(Regex("[^\\p{L}0-9._-]"), "")
            .take(30)
        if (fromEmail.length >= 3) return fromEmail
        return "user${email.hashCode().toUInt().toString(16).take(6)}"
    }

    suspend fun register(
        email: String,
        password: String,
        username: String,
        birthDate: String
    ): AuthResult {
        val validationError = AuthValidator.validateRegistration(email, password, username, birthDate)
        if (validationError != null) return AuthResult.Error(validationError)

        val normalizedEmail = email.trim().lowercase()
        val cleanUsername = username.trim()
        if (_accounts.value.any { it.email.equals(normalizedEmail, ignoreCase = true) }) {
            return AuthResult.Error(AuthError.EMAIL_ALREADY_EXISTS)
        }
        persistCurrentUserSessionIfNeeded()
        val (salt, hash) = PasswordHasher.hash(password)
        val account = UserAccount(
            id = UUID.randomUUID().toString(),
            email = normalizedEmail,
            passwordHash = hash,
            passwordSalt = salt,
            username = cleanUsername,
            birthDate = birthDate.trim(),
            createdAtMillis = System.currentTimeMillis()
        )
        val updated = _accounts.value + account
        _accounts.value = updated
        preferences.saveRegisteredAccounts(updated)
        return completeLogin(account, isNewUser = true)
    }

    fun logout() {
        scope.launch {
            persistCurrentUserSessionIfNeeded()
            preferences.setLoggedIn(loggedIn = false, userId = null)
            _isLoggedIn.value = false
        }
    }

    private suspend fun persistCurrentUserSessionIfNeeded() {
        val userId = preferences.currentUserId.first() ?: return
        if (!_isLoggedIn.value) return
        preferences.saveUserSession(userId, captureUserSession())
    }

    private suspend fun captureUserSession(): UserSessionSnapshot {
        return UserSessionSnapshot(
            profile = profile.value,
            dailyGoals = dailyGoals.value,
            weeklyGoal = weeklyGoal.value,
            onboardingCompleted = onboardingCompleted.value,
            fitnessGoal = preferences.fitnessGoal.first(),
            activityLevel = preferences.activityLevel.first(),
            workoutLocation = preferences.workoutLocation.first(),
            preferredTime = preferences.preferredTime.first(),
            notificationsEnabled = notificationsEnabled.value,
            unitsMetric = unitsMetric.value,
            workoutHistory = history.value,
            athleticStats = athleticStats.value,
            favoriteWorkoutIds = favorites.value,
            dailyTipIndex = preferences.dailyTipIndex.first()
        )
    }

    private suspend fun applyUserSession(snapshot: UserSessionSnapshot) {
        preferences.updateProfile(snapshot.profile)
        preferences.updateDailyGoals(snapshot.dailyGoals)
        preferences.saveWeeklyGoal(snapshot.weeklyGoal)
        preferences.setOnboardingCompleted(snapshot.onboardingCompleted)
        preferences.setFitnessGoal(snapshot.fitnessGoal)
        preferences.setActivityLevel(snapshot.activityLevel)
        preferences.setWorkoutLocation(snapshot.workoutLocation)
        preferences.setPreferredTime(snapshot.preferredTime)
        preferences.setNotificationsEnabled(snapshot.notificationsEnabled)
        preferences.setUnitsMetric(snapshot.unitsMetric)
        preferences.saveWorkoutHistory(snapshot.workoutHistory)
        preferences.saveAthleticStats(snapshot.athleticStats)
        preferences.setFavoriteWorkoutIds(snapshot.favoriteWorkoutIds)
        preferences.setDailyTipIndex(snapshot.dailyTipIndex)
        _history.value = snapshot.workoutHistory
        _recentActivity.value = snapshot.workoutHistory.take(5).map { item ->
            RecentActivity(
                id = item.id,
                title = item.title,
                timeLabel = item.timestampLabel.uppercase(Locale.getDefault()),
                durationMinutes = item.durationMinutes,
                iconName = item.iconName
            )
        }.ifEmpty { defaultRecentActivity() }
        _athleticStats.value = snapshot.athleticStats
        _favorites.value = snapshot.favoriteWorkoutIds
        _onboardingCompleted.value = snapshot.onboardingCompleted
        refreshMilestones()
    }

    private suspend fun upgradeLegacyPasswordIfNeeded(account: UserAccount, password: String): UserAccount {
        if (!PasswordHasher.isLegacy(account.passwordHash)) return account
        val (salt, hash) = PasswordHasher.hash(password)
        val upgraded = account.copy(passwordSalt = salt, passwordHash = hash)
        val updated = _accounts.value.map { if (it.id == account.id) upgraded else it }
        _accounts.value = updated
        preferences.saveRegisteredAccounts(updated)
        return upgraded
    }

    private suspend fun completeLogin(account: UserAccount, isNewUser: Boolean = false): AuthResult {
        preferences.setLoggedIn(loggedIn = true, userId = account.id)
        _isLoggedIn.value = true
        if (isNewUser) {
            val freshProfile = UserProfile(
                displayName = account.username,
                avatarUrl = defaultProfile().avatarUrl,
                isProMember = false,
                totalWorkouts = 0,
                activeStreakDays = 0,
                level = 1
            )
            preferences.setOnboardingCompleted(false)
            preferences.updateProfile(freshProfile)
            preferences.saveWorkoutHistory(emptyList())
            preferences.setFitnessGoal("wellness")
            preferences.setActivityLevel("moderate")
            preferences.setWorkoutLocation("home")
            preferences.setPreferredTime("flexible")
            preferences.saveWeeklyGoal(WeeklyGoal(targetSessions = 4, completedSessions = 0))
            preferences.saveAthleticStats(defaultAthleticStats())
            preferences.setFavoriteWorkoutIds(emptySet())
            _onboardingCompleted.value = false
            _history.value = emptyList()
            _recentActivity.value = emptyList()
            _athleticStats.value = defaultAthleticStats()
            _favorites.value = emptySet()
            refreshMilestones()
        } else {
            val saved = preferences.loadUserSession(account.id)
            if (saved != null) {
                applyUserSession(saved.copy(profile = saved.profile.copy(displayName = account.username)))
            } else {
                preferences.updateProfile(profile.value.copy(displayName = account.username))
            }
        }
        return AuthResult.Success(needsSetup = isNewUser)
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
            activityTracker.addWorkoutContribution(duration, kcal)
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
        scope.launch { activityTracker.syncNow() }
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

        fun defaultDailyGoals() = DailyGoalsCalculator.build(
            steps = 0,
            workoutCalories = 0,
            exerciseMinutes = 0,
            standHours = 0
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
