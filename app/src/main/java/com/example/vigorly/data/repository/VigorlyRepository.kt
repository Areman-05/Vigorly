package com.example.vigorly.data.repository

import android.content.Context
import com.example.vigorly.data.activity.DailyActivityDetail
import com.example.vigorly.data.activity.DailyActivityDaySummary
import com.example.vigorly.data.activity.DailyActivityTracker
import com.example.vigorly.data.activity.DailyGoalsCalculator
import com.example.vigorly.data.activity.WeeklyActivityRingDay
import com.example.vigorly.data.activity.WeeklyActivityRingsBuilder
import com.example.vigorly.util.AthleticProfileCalculator
import com.example.vigorly.data.MilestoneUnlocker
import com.example.vigorly.data.catalog.WorkoutCatalog
import com.example.vigorly.data.local.CoachingTipLoader
import com.example.vigorly.data.local.VigorlyPreferencesDataStore
import com.example.vigorly.data.local.WeightLogCodec
import com.example.vigorly.data.local.WorkoutPlaylistCodec
import com.example.vigorly.data.model.CoachingTip
import com.example.vigorly.data.model.SessionSummary
import com.example.vigorly.data.model.WorkoutType
import com.example.vigorly.data.model.AccountUniqueness
import com.example.vigorly.data.model.AuthError
import com.example.vigorly.data.model.AuthResult
import com.example.vigorly.data.model.UserAccount
import com.example.vigorly.data.model.UserSessionSnapshot
import com.example.vigorly.ui.setup.SetupDevFlags
import com.example.vigorly.auth.GoogleUserInfo
import com.example.vigorly.util.AuthValidator
import com.example.vigorly.util.LocaleManager
import com.example.vigorly.util.PasswordHasher
import com.example.vigorly.util.PersonalizedCoachingTipEngine
import com.example.vigorly.util.PersonalizedTipContext
import com.example.vigorly.util.WorkoutRecommender
import com.example.vigorly.data.MilestoneCatalog
import com.example.vigorly.data.local.MilestoneShowcaseCodec
import com.example.vigorly.util.HistoryLabels
import com.example.vigorly.util.LevelCalculator
import com.example.vigorly.util.HistorySanitizer
import com.example.vigorly.util.SessionStepsBuilder
import com.example.vigorly.util.StreakCalculator
import com.example.vigorly.notifications.WorkoutReminderScheduler
import java.time.ZoneId
import com.example.vigorly.ui.profile.ProfileAvatarCatalog
import com.example.vigorly.core.testing.UiTestEnvironment
import com.example.vigorly.navigation.AppDestination
import com.example.vigorly.data.model.AthleticStat
import com.example.vigorly.data.model.DailyGoals
import com.example.vigorly.data.model.Exercise
import com.example.vigorly.data.model.Milestone
import com.example.vigorly.data.model.RecentActivity
import com.example.vigorly.data.model.SessionStep
import com.example.vigorly.data.model.UserProfile
import com.example.vigorly.data.model.WeeklyGoal
import com.example.vigorly.data.model.WeightLogEntry
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
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.UUID

class VigorlyRepository(context: Context) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val localeMutex = Mutex()
    private val appContext = context.applicationContext
    private val preferences = VigorlyPreferencesDataStore(appContext)
    private val activityTracker = DailyActivityTracker(appContext, preferences) { }
    private val workouts = WorkoutCatalog.allWorkouts()
    private val workoutList: List<WorkoutDetail> = workouts.values.toList()
    private val coachingTips: List<CoachingTip> = CoachingTipLoader.load(appContext)

    val profile: StateFlow<UserProfile> = preferences.userProfile.stateIn(
        scope, SharingStarted.Eagerly, defaultProfile()
    )

    val dailyGoals: StateFlow<DailyGoals> = preferences.dailyGoals.stateIn(
        scope, SharingStarted.Eagerly, defaultDailyGoals()
    )

    val dailyActivityDetail: StateFlow<DailyActivityDetail> = activityTracker.detail.stateIn(
        scope, SharingStarted.Eagerly, DailyActivityDetail()
    )

    private val _activityDayHistory = MutableStateFlow<Map<String, DailyActivityDaySummary>>(emptyMap())
    val activityDayHistory: StateFlow<Map<String, DailyActivityDaySummary>> = _activityDayHistory.asStateFlow()

    private val _selectedActivityDate = MutableStateFlow(LocalDate.now())
    val selectedActivityDate: StateFlow<LocalDate> = _selectedActivityDate.asStateFlow()

    val displayedActivityDetail: StateFlow<DailyActivityDetail> = combine(
        dailyActivityDetail,
        activityDayHistory,
        selectedActivityDate
    ) { live, history, selected ->
        resolveActivityDetail(selected, live, history)
    }.stateIn(scope, SharingStarted.Eagerly, DailyActivityDetail())

    val currentWeekActivityRings: StateFlow<List<WeeklyActivityRingDay>> = combine(
        dailyActivityDetail,
        activityDayHistory,
        selectedActivityDate
    ) { live, history, selected ->
        WeeklyActivityRingsBuilder.build(
            history = history,
            liveTodayDetail = live,
            reference = selected
        )
    }.stateIn(scope, SharingStarted.Eagerly, emptyList())

    private val activityDateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    val notificationsEnabled: StateFlow<Boolean> = preferences.notificationsEnabled.stateIn(
        scope, SharingStarted.Eagerly, true
    )

    val unitsMetric: StateFlow<Boolean> = preferences.unitsMetric.stateIn(
        scope, SharingStarted.Eagerly, true
    )

    val weeklyGoal: StateFlow<WeeklyGoal> = preferences.weeklyGoal.stateIn(
        scope, SharingStarted.Eagerly, WeeklyGoal(targetSessions = 5, completedSessions = 0)
    )

    val fitnessGoal: StateFlow<String> = preferences.fitnessGoal.stateIn(
        scope, SharingStarted.Eagerly, "wellness"
    )
    val activityLevel: StateFlow<String> = preferences.activityLevel.stateIn(
        scope, SharingStarted.Eagerly, "moderate"
    )
    val workoutLocation: StateFlow<String> = preferences.workoutLocation.stateIn(
        scope, SharingStarted.Eagerly, "home"
    )
    val preferredTime: StateFlow<String> = preferences.preferredTime.stateIn(
        scope, SharingStarted.Eagerly, "flexible"
    )

    private val _athleticStats = MutableStateFlow(defaultAthleticStats())
    val athleticStats: StateFlow<List<AthleticStat>> = _athleticStats.asStateFlow()

    private val _milestones = MutableStateFlow(defaultMilestones())
    val milestones: StateFlow<List<Milestone>> = _milestones.asStateFlow()

    private val _milestoneUnlockDates = MutableStateFlow<Map<String, Long>>(emptyMap())

    private val _milestoneShowcase = MutableStateFlow(List(MilestoneShowcaseCodec.SLOT_COUNT) { null as String? })
    val milestoneShowcase: StateFlow<List<String?>> = _milestoneShowcase.asStateFlow()

    private val _weightLog = MutableStateFlow<List<WeightLogEntry>>(emptyList())
    val weightLog: StateFlow<List<WeightLogEntry>> = _weightLog.asStateFlow()

    private val _weightGoalKg = MutableStateFlow<Float?>(null)
    val weightGoalKg: StateFlow<Float?> = _weightGoalKg.asStateFlow()

    private val _history = MutableStateFlow<List<WorkoutHistoryItem>>(emptyList())
    val history: StateFlow<List<WorkoutHistoryItem>> = _history.asStateFlow()

    private val _recentActivity = MutableStateFlow(defaultRecentActivity())
    val recentActivity: StateFlow<List<RecentActivity>> = _recentActivity.asStateFlow()

    private val _activeSession = MutableStateFlow<WorkoutSessionState?>(null)
    val activeSession: StateFlow<WorkoutSessionState?> = _activeSession.asStateFlow()

    private val _dailyTip = MutableStateFlow(coachingTips.firstOrNull() ?: CoachingTip("tip-001", ""))
    val dailyTip: StateFlow<CoachingTip> = _dailyTip.asStateFlow()
    private val _dailyTips = MutableStateFlow<List<CoachingTip>>(emptyList())
    val dailyTips: StateFlow<List<CoachingTip>> = _dailyTips.asStateFlow()

    private val _showStreakBanner = MutableStateFlow(false)
    val showStreakBanner: StateFlow<Boolean> = _showStreakBanner.asStateFlow()

    private val _favorites = MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> = _favorites.asStateFlow()

    private val _playlists = MutableStateFlow<List<com.example.vigorly.data.model.WorkoutPlaylist>>(emptyList())
    val playlists: StateFlow<List<com.example.vigorly.data.model.WorkoutPlaylist>> = _playlists.asStateFlow()

    private val _onboardingCompleted = MutableStateFlow(false)
    val onboardingCompleted: StateFlow<Boolean> = _onboardingCompleted.asStateFlow()

    private val _lastSessionSummary = MutableStateFlow<SessionSummary?>(null)
    val lastSessionSummary: StateFlow<SessionSummary?> = _lastSessionSummary.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _appLocale = MutableStateFlow("es")
    val appLocale: StateFlow<String> = _appLocale.asStateFlow()

    private val _accounts = MutableStateFlow<List<UserAccount>>(emptyList())
    val accounts: StateFlow<List<UserAccount>> = _accounts.asStateFlow()

    val currentAccount: StateFlow<UserAccount?> = combine(
        preferences.currentUserId,
        preferences.registeredAccounts
    ) { userId, accounts ->
        userId?.let { id -> accounts.find { it.id == id } }
    }.stateIn(scope, SharingStarted.Eagerly, null)

    /** Contraseña en claro de la sesión (login) o recuperada del almacenamiento revelable. */
    private val _sessionPlainPassword = MutableStateFlow<String?>(null)

    val revealableAccountPassword: StateFlow<String?> = combine(
        currentAccount,
        _sessionPlainPassword
    ) { account, session ->
        when {
            account == null -> null
            account.authProvider == "google" &&
                account.password.isBlank() &&
                account.passwordHash.isBlank() -> null
            account.password.isNotBlank() -> account.password
            !session.isNullOrEmpty() -> session
            else -> PasswordHasher.reveal(account.passwordHash)
        }
    }.stateIn(scope, SharingStarted.Eagerly, null)

    private var appDataPreloaded = false
    private var forcedPasswordReauth = false

    @Volatile
    private var lastRecordedWorkoutId: String? = null

    @Volatile
    private var lastRecordedCompletionAt: Long = 0L

    init {
        preferences.isLoggedIn.onEach { _isLoggedIn.value = it }.launchIn(scope)
        preferences.appLocale.onEach { _appLocale.value = it }.launchIn(scope)
        preferences.registeredAccounts.onEach { accounts ->
            val normalized = accounts.map { normalizeStoredPassword(it) }
            _accounts.value = normalized
            if (normalized != accounts) {
                scope.launch { preferences.saveRegisteredAccounts(normalized) }
            }
        }.launchIn(scope)
        // Mantener la contraseña lista para el ojo; si es hash viejo irreversible, pedir login 1 vez.
        currentAccount.onEach { account ->
            if (account == null) {
                if (!_isLoggedIn.value) _sessionPlainPassword.value = null
                return@onEach
            }
            val plain = account.password.ifBlank {
                PasswordHasher.reveal(account.passwordHash).orEmpty()
            }
            if (plain.isNotBlank()) {
                if (_sessionPlainPassword.value != plain) {
                    _sessionPlainPassword.value = plain
                }
                return@onEach
            }
            if (!forcedPasswordReauth &&
                _isLoggedIn.value &&
                account.authProvider != "google" &&
                PasswordHasher.needsRevealUpgrade(account.passwordHash)
            ) {
                forcedPasswordReauth = true
                scope.launch {
                    preferences.setLoggedIn(loggedIn = false, userId = null)
                    _isLoggedIn.value = false
                    _sessionPlainPassword.value = null
                }
            }
        }.launchIn(scope)
        preferences.milestoneShowcase.onEach { _milestoneShowcase.value = it }.launchIn(scope)
        preferences.weightLog.onEach { persisted ->
            _weightLog.update { local ->
                if (WeightLogCodec.shouldApplyPersisted(local, persisted)) persisted else local
            }
        }.launchIn(scope)
        preferences.weightGoalKg.onEach { _weightGoalKg.value = it }.launchIn(scope)
        preferences.milestoneUnlockDates.onEach { dates ->
            _milestoneUnlockDates.value = dates
            refreshMilestones()
        }.launchIn(scope)
        preferences.favoriteWorkoutIds.onEach { ids ->
            _favorites.value = ids
        }.launchIn(scope)
        preferences.workoutPlaylists.onEach { stored ->
            val cleaned = WorkoutPlaylistCodec.sanitizeUserLists(stored)
            _playlists.value = cleaned
            if (cleaned != stored) {
                scope.launch { persistPlaylists(cleaned) }
            }
        }.launchIn(scope)
        preferences.onboardingCompleted.onEach { _onboardingCompleted.value = it }.launchIn(scope)
        combine(
            combine(
                preferences.fitnessGoal,
                preferences.activityLevel,
                preferences.workoutLocation
            ) { fitnessGoal, activityLevel, location ->
                Triple(fitnessGoal, activityLevel, location)
            },
            combine(preferences.preferredTime, dailyGoals, weeklyGoal) { preferredTime, goals, weekly ->
                Triple(preferredTime, goals, weekly)
            },
            combine(profile, history) { prof, hist -> prof to hist }
        ) { profilePrefs, goalPrefs, activity ->
            val (fitnessGoal, activityLevel, location) = profilePrefs
            val (preferredTime, goals, weekly) = goalPrefs
            val (prof, hist) = activity
            PersonalizedTipContext(
                fitnessGoal = fitnessGoal,
                activityLevel = activityLevel,
                workoutLocation = location,
                preferredTime = preferredTime,
                dailyGoals = goals,
                weeklyGoal = weekly,
                streakDays = prof.activeStreakDays,
                recentWorkoutTitles = hist.take(3).map { it.title }
            )
        }
            .debounce(750)
            .onEach { tipContext ->
                val tips = PersonalizedCoachingTipEngine.generateMany(appContext, tipContext, count = 2)
                _dailyTips.value = tips
                _dailyTip.value = tips.firstOrNull()
                    ?: CoachingTip("tip-fallback", appContext.getString(com.example.vigorly.R.string.coaching_tip_fallback))
            }
            .launchIn(scope)
        profile.onEach { refreshStreakBannerVisibility() }.launchIn(scope)
        scope.launch { refreshStreakBannerVisibility() }
        preferences.workoutHistory.onEach { stored ->
            val cleaned = HistorySanitizer.clean(stored)
            _history.value = cleaned
            if (cleaned != stored) {
                scope.launch {
                    preferences.saveWorkoutHistory(cleaned)
                    reconcileProfileAfterHistoryChange(
                        cleaned,
                        HistorySanitizer.removedCount(stored, cleaned)
                    )
                }
            } else {
                scope.launch { reconcileProfileAfterHistoryChange(cleaned, 0) }
            }
            _recentActivity.value = cleaned.take(5).map { item ->
                RecentActivity(
                    id = item.id,
                    title = item.title,
                    timeLabel = HistoryLabels.displayTimestamp(item).uppercase(Locale.getDefault()),
                    durationMinutes = item.durationMinutes,
                    iconName = item.iconName
                )
            }.ifEmpty { defaultRecentActivity() }
            scope.launch { syncActiveStreakDays() }
            refreshMilestones()
        }.launchIn(scope)
        preferences.userProfile.onEach { refreshMilestones() }.launchIn(scope)
        if (!UiTestEnvironment.isInstrumentedTest) {
            scope.launch {
                activityTracker.initialize()
                refreshActivityDayHistory()
                syncActiveStreakDays()
            }
            activityTracker.detail
                .debounce(2_500)
                .onEach {
                    refreshActivityDayHistory()
                    syncActiveStreakDays()
                }
                .launchIn(scope)
        }
    }

    fun selectActivityDate(date: LocalDate) {
        _selectedActivityDate.value = date
    }

    fun dismissStreakBanner() {
        scope.launch {
            preferences.setStreakBannerDismissedDate(LocalDate.now().format(activityDateFormatter))
            _showStreakBanner.value = false
        }
    }

    private suspend fun refreshStreakBannerVisibility() {
        val streak = profile.value.activeStreakDays
        if (streak < 2) {
            _showStreakBanner.value = false
            return
        }
        val dismissed = preferences.getStreakBannerDismissedDate()
        val today = LocalDate.now().format(activityDateFormatter)
        _showStreakBanner.value = dismissed != today
    }

    fun resetSelectedActivityDateToToday() {
        _selectedActivityDate.value = LocalDate.now()
    }

    suspend fun refreshActivityDayHistory() {
        _activityDayHistory.value = preferences.loadActivityDayHistory()
        syncActiveStreakDays()
    }

    private suspend fun syncActiveStreakDays() {
        val streak = StreakCalculator.consecutiveActiveDays(collectActiveDates())
        val current = profile.value
        if (current.activeStreakDays == streak) return
        preferences.updateProfile(current.copy(activeStreakDays = streak))
    }

    private fun collectActiveDates(): Set<LocalDate> {
        val zone = ZoneId.systemDefault()
        val fromWorkouts = _history.value.mapNotNull { HistoryLabels.itemLocalDate(it, zone) }
        val fromActivity = _activityDayHistory.value.mapNotNull { (key, summary) ->
            val date = runCatching { LocalDate.parse(key) }.getOrNull() ?: return@mapNotNull null
            if (summary.hasRecordedActivity()) date else null
        }
        val today = LocalDate.now(zone)
        val todayKey = today.format(activityDateFormatter)
        val liveSummary = DailyActivityDaySummary.fromDetail(todayKey, dailyActivityDetail.value)
        val todaySet = if (liveSummary.hasRecordedActivity()) setOf(today) else emptySet()
        return (fromWorkouts + fromActivity + todaySet).toSet()
    }

    private fun DailyActivityDaySummary.hasRecordedActivity(): Boolean =
        moveCalories > 0 || exerciseMinutes > 0 || standHours > 0 || steps > 0

    private suspend fun reconcileProfileAfterHistoryChange(
        history: List<WorkoutHistoryItem>,
        removedCount: Int
    ) {
        val current = profile.value
        val alignedTotal = history.size
        val alignedLevel = LevelCalculator.levelFromWorkouts(alignedTotal)
        if (current.totalWorkouts == alignedTotal &&
            current.level == alignedLevel &&
            removedCount == 0
        ) {
            return
        }
        preferences.updateProfile(
            current.copy(
                totalWorkouts = alignedTotal,
                level = alignedLevel
            )
        )
    }

    private fun resolveActivityDetail(
        date: LocalDate,
        live: DailyActivityDetail,
        history: Map<String, DailyActivityDaySummary>
    ): DailyActivityDetail {
        if (date == LocalDate.now()) return live
        val key = date.format(activityDateFormatter)
        return history[key]?.toDetail() ?: DailyActivityDetail()
    }

    fun summaryForDate(date: LocalDate): DailyActivityDaySummary? {
        val key = date.format(activityDateFormatter)
        if (date == LocalDate.now()) {
            val live = dailyActivityDetail.value
            return DailyActivityDaySummary.fromDetail(key, live)
        }
        return activityDayHistory.value[key]
    }

    fun currentWeekRangeLabel(locale: Locale = Locale.getDefault()): String {
        val days = currentWeekActivityRings.value
        return WeeklyActivityRingsBuilder.formatWeekRange(days, locale)
    }

    fun startActivityTracking() {
        activityTracker.start()
    }

    fun stopActivityTracking() {
        activityTracker.stop()
    }

    private fun refreshMilestones() {
        val (updated, dates) = MilestoneUnlocker.apply(
            profile = profile.value,
            milestones = MilestoneCatalog.all(),
            unlockDates = _milestoneUnlockDates.value
        )
        _milestones.value = updated
        if (dates != _milestoneUnlockDates.value) {
            _milestoneUnlockDates.value = dates
            scope.launch { preferences.saveMilestoneUnlockDates(dates) }
        }
    }

    fun getWorkout(id: String): WorkoutDetail? = workouts[id]

    fun getMilestone(id: String): Milestone? = _milestones.value.find { it.id == id }

    fun addWeightEntry(weightKg: Float, recordedAtMillis: Long = System.currentTimeMillis()) {
        persistWeightLog { current ->
            WeightLogCodec.upsertDay(current, weightKg, recordedAtMillis)
        }
    }

    fun updateWeightEntry(id: String, weightKg: Float, recordedAtMillis: Long? = null) {
        val kg = weightKg.coerceIn(30f, 300f)
        persistWeightLog { current ->
            current.map { entry ->
                if (entry.id == id) {
                    entry.copy(
                        weightKg = kg,
                        recordedAtMillis = recordedAtMillis ?: entry.recordedAtMillis
                    )
                } else {
                    entry
                }
            }.sortedBy { it.recordedAtMillis }
        }
    }

    fun deleteWeightEntry(id: String) {
        persistWeightLog { current -> current.filterNot { it.id == id } }
    }

    private fun persistWeightLog(transform: (List<WeightLogEntry>) -> List<WeightLogEntry>) {
        var snapshot: List<WeightLogEntry> = emptyList()
        _weightLog.update { current ->
            transform(current).also { snapshot = it }
        }
        scope.launch { preferences.saveWeightLog(snapshot) }
    }

    fun setWeightGoalKg(goalKg: Float?) {
        val normalized = goalKg?.coerceIn(30f, 300f)
        _weightGoalKg.value = normalized
        scope.launch { preferences.saveWeightGoalKg(normalized) }
    }

    fun setMilestoneShowcaseSlot(slotIndex: Int, milestoneId: String?) {
        if (slotIndex !in 0 until MilestoneShowcaseCodec.SLOT_COUNT) return
        _milestoneShowcase.update { slots ->
            val updated = slots.toMutableList()
            if (milestoneId != null) {
                for (i in updated.indices) {
                    if (i != slotIndex && updated[i] == milestoneId) updated[i] = null
                }
            }
            updated[slotIndex] = milestoneId
            updated
        }
        scope.launch { preferences.saveMilestoneShowcase(_milestoneShowcase.value) }
    }

    fun tipCards(count: Int = 2): List<CoachingTip> {
        val personalized = _dailyTips.value.filter { it.text.isNotBlank() }
        if (personalized.isNotEmpty()) {
            return personalized.take(count.coerceAtLeast(1))
        }
        val primary = _dailyTip.value
        return listOf(primary).take(count.coerceAtLeast(1))
    }

    fun listWorkouts(): List<WorkoutDetail> = workoutList

    fun flatExercises(workout: WorkoutDetail): List<Exercise> =
        workout.blocks.flatMap { it.exercises }

    fun sessionSteps(workout: WorkoutDetail): List<SessionStep> =
        SessionStepsBuilder.build(workout)

    fun todaysWorkoutCount(): Int {
        val start = LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        return _history.value.count { it.completedAtMillis >= start }
    }

    fun startWorkoutSession(workoutId: String): WorkoutSessionState? {
        val workout = getWorkout(workoutId) ?: return null
        val steps = sessionSteps(workout)
        if (steps.isEmpty()) return null
        val firstSecs = steps.first().durationSeconds
        val session = WorkoutSessionState(
            workoutId = workoutId,
            workoutName = workout.name,
            currentExerciseIndex = 0,
            totalExercises = steps.size,
            elapsedSeconds = 0,
            isPaused = false,
            exerciseSecondsRemaining = firstSecs,
            exerciseDurationSeconds = firstSecs
        )
        _activeSession.value = session
        return session
    }

    /**
     * @return true si el entrenamiento terminó (hay que navegar al resumen).
     */
    fun tickSession(): Boolean {
        val session = _activeSession.value ?: return false
        if (session.restSecondsRemaining > 0) {
            val nextRest = session.restSecondsRemaining - 1
            if (nextRest <= 0) {
                val workout = getWorkout(session.workoutId) ?: return false
                val steps = sessionSteps(workout)
                val secs = steps.getOrNull(session.currentExerciseIndex)?.durationSeconds
                    ?: EXERCISE_SECONDS_DEFAULT
                _activeSession.value = session.copy(
                    restSecondsRemaining = 0,
                    restDurationSeconds = 0,
                    exerciseSecondsRemaining = secs,
                    exerciseDurationSeconds = secs,
                    isPaused = false
                )
            } else {
                _activeSession.value = session.copy(restSecondsRemaining = nextRest)
            }
            return false
        }
        if (session.isPaused) return false

        val nextExerciseSecs = session.exerciseSecondsRemaining - 1
        val elapsed = session.elapsedSeconds + 1
        return if (nextExerciseSecs <= 0) {
            completeCurrentExerciseAndAdvance(
                session.copy(
                    elapsedSeconds = elapsed,
                    exerciseSecondsRemaining = 0
                )
            )
        } else {
            _activeSession.value = session.copy(
                elapsedSeconds = elapsed,
                exerciseSecondsRemaining = nextExerciseSecs
            )
            false
        }
    }

    fun toggleSessionPause() {
        _activeSession.updateSession { it.copy(isPaused = !it.isPaused) }
    }

    fun nextExercise() {
        val session = _activeSession.value ?: return
        if (session.restSecondsRemaining > 0) return
        completeCurrentExerciseAndAdvance(session)
    }

    fun skipRest() {
        val session = _activeSession.value ?: return
        if (session.restSecondsRemaining <= 0) return
        val workout = getWorkout(session.workoutId) ?: return
        val secs = sessionSteps(workout)
            .getOrNull(session.currentExerciseIndex)
            ?.durationSeconds
            ?: EXERCISE_SECONDS_DEFAULT
        _activeSession.value = session.copy(
            restSecondsRemaining = 0,
            restDurationSeconds = 0,
            exerciseSecondsRemaining = secs,
            exerciseDurationSeconds = secs,
            isPaused = false
        )
    }

    /**
     * Marca el ejercicio actual, para el tiempo y pasa a descanso o resumen.
     * @return true si el entrenamiento terminó.
     */
    fun markCurrentExerciseComplete(): Boolean {
        val session = _activeSession.value ?: return false
        if (session.restSecondsRemaining > 0) return false
        return completeCurrentExerciseAndAdvance(session)
    }

    fun previousExercise() {
        _activeSession.updateSession { session ->
            if (session.currentExerciseIndex > 0) {
                val workout = getWorkout(session.workoutId)
                val secs = workout
                    ?.let { sessionSteps(it) }
                    ?.getOrNull(session.currentExerciseIndex - 1)
                    ?.durationSeconds
                    ?: EXERCISE_SECONDS_DEFAULT
                session.copy(
                    currentExerciseIndex = session.currentExerciseIndex - 1,
                    restSecondsRemaining = 0,
                    restDurationSeconds = 0,
                    exerciseSecondsRemaining = secs,
                    exerciseDurationSeconds = secs,
                    isPaused = false
                )
            } else session
        }
    }

    /**
     * @return true si se cerró la sesión (último ejercicio).
     */
    private fun completeCurrentExerciseAndAdvance(session: WorkoutSessionState): Boolean {
        val workout = getWorkout(session.workoutId) ?: return false
        val steps = sessionSteps(workout)
        val stepId = steps.getOrNull(session.currentExerciseIndex)?.id
        val completed = if (stepId != null) {
            session.completedExerciseIds + stepId
        } else {
            session.completedExerciseIds
        }
        val withDone = session.copy(
            completedExerciseIds = completed,
            exerciseSecondsRemaining = 0,
            isPaused = false
        )

        if (withDone.currentExerciseIndex >= withDone.totalExercises - 1) {
            _activeSession.value = withDone
            completeWorkoutSession()
            return true
        }

        val nextIndex = withDone.currentExerciseIndex + 1
        val nextIsWarmup = steps.getOrNull(nextIndex)?.isWarmup == true
        val currentWasWarmup = steps.getOrNull(session.currentExerciseIndex)?.isWarmup == true
        val restSecs = when {
            nextIsWarmup -> 0
            currentWasWarmup -> REST_SECONDS_AFTER_WARMUP
            else -> REST_SECONDS_BETWEEN_EXERCISES
        }
        val nextSecs = steps.getOrNull(nextIndex)?.durationSeconds ?: EXERCISE_SECONDS_DEFAULT
        if (restSecs <= 0) {
            _activeSession.value = withDone.copy(
                currentExerciseIndex = nextIndex,
                restSecondsRemaining = 0,
                restDurationSeconds = 0,
                exerciseSecondsRemaining = nextSecs,
                exerciseDurationSeconds = nextSecs
            )
            return false
        }

        _activeSession.value = withDone.copy(
            currentExerciseIndex = nextIndex,
            restSecondsRemaining = restSecs,
            restDurationSeconds = restSecs,
            exerciseSecondsRemaining = 0,
            exerciseDurationSeconds = 0
        )
        return false
    }

    fun completeWorkoutSession() {
        val session = _activeSession.value ?: return
        val workout = getWorkout(session.workoutId) ?: return
        val unlockedBefore = _milestones.value.filter { it.unlocked }.map { it.id }.toSet()
        val totalBefore = profile.value.totalWorkouts
        val totalAfter = totalBefore + 1
        val weeklyBefore = weeklyGoal.value
        val optimisticProfile = profile.value.copy(
            totalWorkouts = totalAfter,
            level = LevelCalculator.levelFromWorkouts(totalAfter)
        )
        val (optimisticMilestones, _) = MilestoneUnlocker.apply(
            profile = optimisticProfile,
            milestones = MilestoneCatalog.all(),
            unlockDates = _milestoneUnlockDates.value
        )
        val newlyUnlocked = optimisticMilestones
            .filter { it.unlocked && it.id !in unlockedBefore }
            .map { it.title }
        _lastSessionSummary.value = SessionSummaryFactory.from(
            session = session,
            workout = workout,
            totalWorkoutsAfter = totalAfter,
            streakDays = profile.value.activeStreakDays.coerceAtLeast(1),
            weeklyCompleted = weeklyBefore.completedSessions + 1,
            weeklyTarget = weeklyBefore.targetSessions,
            newlyUnlockedTitles = newlyUnlocked
        )
        recordWorkoutCompletion(session.workoutId, workout.durationMinutes, workout.estimatedCalories)
        _activeSession.value = null
    }

    fun clearSessionSummary() {
        _lastSessionSummary.value = null
    }

    fun getRecommendedWorkout(): WorkoutDetail? =
        WorkoutRecommender.recommend(
            workouts = workouts.values.toList(),
            history = _history.value,
            favorites = _favorites.value,
            fitnessGoal = fitnessGoal.value,
            activityLevel = activityLevel.value,
            workoutLocation = workoutLocation.value
        )

    fun getRecommendedWorkouts(count: Int = 5): List<WorkoutDetail> =
        WorkoutRecommender.recommendMany(
            workouts = workouts.values.toList(),
            history = _history.value,
            favorites = _favorites.value,
            count = count,
            fitnessGoal = fitnessGoal.value,
            activityLevel = activityLevel.value,
            workoutLocation = workoutLocation.value
        )

    fun toggleFavorite(workoutId: String) {
        val updated = _favorites.value.toMutableSet().apply {
            if (contains(workoutId)) remove(workoutId) else add(workoutId)
        }
        _favorites.value = updated
        scope.launch {
            preferences.setFavoriteWorkoutIds(updated)
        }
    }

    fun isFavorite(workoutId: String): Boolean = workoutId in _favorites.value

    fun createPlaylist(name: String, workoutIds: List<String> = emptyList()) {
        val existing = _playlists.value
        val cleanName = WorkoutPlaylistCodec.uniqueName(name, existing)
        val list = com.example.vigorly.data.model.WorkoutPlaylist(
            id = "custom_${System.currentTimeMillis()}",
            name = cleanName,
            workoutIds = workoutIds.distinct(),
            isAuto = false
        )
        val next = existing + list
        _playlists.value = next
        scope.launch { persistPlaylists(next) }
    }

    fun renamePlaylist(playlistId: String, name: String) {
        val existing = _playlists.value
        val cleanName = WorkoutPlaylistCodec.uniqueName(name, existing, excludeId = playlistId)
        if (cleanName.isBlank()) return
        val next = existing.map {
            if (it.id == playlistId) it.copy(name = cleanName) else it
        }
        _playlists.value = next
        scope.launch { persistPlaylists(next) }
    }

    fun updatePlaylistWorkouts(playlistId: String, workoutIds: List<String>) {
        val next = _playlists.value.map {
            if (it.id == playlistId) it.copy(workoutIds = workoutIds.distinct()) else it
        }
        _playlists.value = next
        scope.launch { persistPlaylists(next) }
    }

    fun deletePlaylist(playlistId: String) {
        val next = _playlists.value.filterNot { it.id == playlistId }
        _playlists.value = next
        scope.launch { persistPlaylists(next) }
    }

    private suspend fun persistPlaylists(lists: List<com.example.vigorly.data.model.WorkoutPlaylist>) {
        preferences.setWorkoutPlaylists(lists.filterNot { it.isAuto })
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

    suspend fun preloadAppData(onProgress: (Float) -> Unit = {}) {
        if (appDataPreloaded) {
            onProgress(1f)
            return
        }
        onProgress(0.06f)
        val workouts = listWorkouts()
        onProgress(0.16f)
        coachingTips.size
        onProgress(0.22f)
        _accounts.value = preferences.registeredAccounts.first()
        onProgress(0.30f)

        if (!UiTestEnvironment.isInstrumentedTest) {
            val urls = buildList {
                addAll(getRecommendedWorkouts(8).map { it.heroImageUrl })
                addAll(workouts.take(28).map { it.heroImageUrl })
            }.distinct().filter { it.isNotBlank() }

            if (urls.isNotEmpty()) {
                val loader = coil.ImageLoader(appContext)
                val chunks = urls.chunked(4)
                chunks.forEachIndexed { chunkIndex, chunk ->
                    withContext(Dispatchers.IO) {
                        chunk.forEach { url ->
                            val request = coil.request.ImageRequest.Builder(appContext)
                                .data(url)
                                .size(coil.size.Size(720, 960))
                                .build()
                            runCatching { loader.execute(request) }
                        }
                    }
                    val fraction = (chunkIndex + 1).toFloat() / chunks.size
                    onProgress(0.30f + 0.68f * fraction)
                }
            }
        }

        onProgress(1f)
        appDataPreloaded = true
    }

    suspend fun initializeLocale() {
        val locale = preferences.appLocale.first()
        LocaleManager.applyLocale(locale)
        _appLocale.value = locale
    }

    suspend fun effectiveLocale(): String = preferences.appLocale.first()

    suspend fun setAppLocaleAndAwait(code: String) {
        localeMutex.withLock {
            preferences.setAppLocale(code)
            LocaleManager.applyLocale(code)
            _appLocale.value = code
        }
    }

    /** Aplica locale en memoria de inmediato y persiste en DataStore de forma serializada. */
    fun setAppLocale(code: String) {
        _appLocale.value = code
        LocaleManager.applyLocale(code)
        scope.launch {
            localeMutex.withLock { preferences.setAppLocale(code) }
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
        val upgradedAccount = ensureRevealablePassword(account, password)
        _sessionPlainPassword.value = password
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
            password = "",
            passwordHash = "",
            passwordSalt = "",
            username = uniqueUsername(usernameFromGoogle(info.displayName, normalizedEmail)),
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

    private fun isEmailTaken(email: String, exceptId: String? = null): Boolean =
        AccountUniqueness.isEmailTaken(_accounts.value, email, exceptId)

    private fun isUsernameTaken(username: String, exceptId: String? = null): Boolean =
        AccountUniqueness.isUsernameTaken(_accounts.value, username, exceptId)

    private fun uniqueUsername(base: String): String {
        val seed = base.trim().ifBlank { "user" }.take(28)
        if (!isUsernameTaken(seed)) return seed
        var index = 2
        while (index < 100) {
            val candidate = "${seed.take(28)}$index"
            if (!isUsernameTaken(candidate)) return candidate
            index++
        }
        return "${seed.take(20)}${System.currentTimeMillis().toString().takeLast(6)}"
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

        _accounts.value = preferences.registeredAccounts.first()
        val normalizedEmail = email.trim().lowercase()
        val cleanUsername = username.trim()
        if (isEmailTaken(normalizedEmail)) {
            return AuthResult.Error(AuthError.EMAIL_ALREADY_EXISTS)
        }
        if (isUsernameTaken(cleanUsername)) {
            return AuthResult.Error(AuthError.USERNAME_ALREADY_EXISTS)
        }
        persistCurrentUserSessionIfNeeded()
        val (salt, hash) = PasswordHasher.hash(password)
        val account = UserAccount(
            id = UUID.randomUUID().toString(),
            email = normalizedEmail,
            password = password,
            passwordHash = hash,
            passwordSalt = salt,
            username = cleanUsername,
            birthDate = birthDate.trim(),
            createdAtMillis = System.currentTimeMillis()
        )
        val updated = _accounts.value + account
        _accounts.value = updated
        preferences.saveRegisteredAccounts(updated)
        _sessionPlainPassword.value = password
        return completeLogin(account, isNewUser = true)
    }

    fun logout() {
        scope.launch {
            persistCurrentUserSessionIfNeeded()
            _sessionPlainPassword.value = null
            _isLoggedIn.value = false
            preferences.setLoggedIn(loggedIn = false, userId = null)
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
        val sessionHistory = HistorySanitizer.clean(snapshot.workoutHistory)
        preferences.saveWorkoutHistory(sessionHistory)
        preferences.saveAthleticStats(snapshot.athleticStats)
        preferences.setFavoriteWorkoutIds(snapshot.favoriteWorkoutIds)
        preferences.setDailyTipIndex(snapshot.dailyTipIndex)
        _history.value = sessionHistory
        _recentActivity.value = sessionHistory.take(5).map { item ->
            RecentActivity(
                id = item.id,
                title = item.title,
                timeLabel = HistoryLabels.displayTimestamp(item).uppercase(Locale.getDefault()),
                durationMinutes = item.durationMinutes,
                iconName = item.iconName
            )
        }
        reconcileProfileAfterHistoryChange(
            sessionHistory,
            HistorySanitizer.removedCount(snapshot.workoutHistory, sessionHistory)
        )
        _athleticStats.value = snapshot.athleticStats
        _favorites.value = snapshot.favoriteWorkoutIds
        _onboardingCompleted.value = snapshot.onboardingCompleted
        refreshMilestones()
    }

    private fun normalizeStoredPassword(account: UserAccount): UserAccount {
        val plain = account.password.ifBlank {
            PasswordHasher.reveal(account.passwordHash).orEmpty()
        }
        if (plain.isBlank()) return account
        if (account.password == plain &&
            PasswordHasher.isRevealable(account.passwordHash) &&
            !PasswordHasher.isLegacy(account.passwordHash)
        ) {
            return account
        }
        val (salt, hash) = PasswordHasher.hash(plain)
        return account.copy(password = plain, passwordSalt = salt, passwordHash = hash)
    }

    private suspend fun ensureRevealablePassword(account: UserAccount, password: String): UserAccount {
        if (account.password == password &&
            PasswordHasher.isRevealable(account.passwordHash) &&
            !PasswordHasher.isLegacy(account.passwordHash)
        ) {
            return account
        }
        val (salt, hash) = PasswordHasher.hash(password)
        val upgraded = account.copy(password = password, passwordSalt = salt, passwordHash = hash)
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
            saveSetupPreferencesAndAwait(
                fitnessGoal,
                activityLevel,
                weeklySessions,
                notifications,
                workoutLocation,
                preferredTime
            )
        }
    }

    suspend fun saveSetupPreferencesAndAwait(
        fitnessGoal: String,
        activityLevel: String,
        weeklySessions: Int,
        notifications: Boolean,
        workoutLocation: String,
        preferredTime: String
    ) {
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

    fun getHistoryItem(id: String): WorkoutHistoryItem? = _history.value.find { it.id == id }

    fun cancelWorkoutSession() {
        _activeSession.value = null
    }

    fun recordWorkoutCompletion(workoutId: String, durationMinutes: Int? = null, calories: Int? = null) {
        val workout = getWorkout(workoutId) ?: return
        val now = System.currentTimeMillis()
        if (workoutId == lastRecordedWorkoutId && now - lastRecordedCompletionAt < 5_000L) {
            return
        }
        lastRecordedWorkoutId = workoutId
        lastRecordedCompletionAt = now
        val duration = durationMinutes ?: workout.durationMinutes
        val kcal = calories ?: workout.estimatedCalories
        val completedAt = now
        val nowLabel = com.example.vigorly.util.HistoryLabels.formatTimestamp(completedAt)

        scope.launch {
            val currentProfile = profile.value
            val newTotal = currentProfile.totalWorkouts + 1
            activityTracker.addWorkoutContribution(duration, kcal)
            preferences.updateProfile(
                currentProfile.copy(
                    totalWorkouts = newTotal,
                    level = LevelCalculator.levelFromWorkouts(newTotal)
                )
            )
            val goal = preferences.weeklyGoal.first()
            preferences.saveWeeklyGoal(goal.copy(completedSessions = goal.completedSessions + 1))
        }
        refreshMilestones()

        val historyItem = WorkoutHistoryItem(
            id = UUID.randomUUID().toString(),
            title = workout.name,
            timestampLabel = nowLabel,
            durationMinutes = duration,
            calories = kcal,
            iconName = iconForWorkoutType(workout.type.name),
            completedAtMillis = completedAt,
            workoutId = workoutId,
            workoutType = workout.type.name
        )
        _history.value = listOf(historyItem) + _history.value

        val recent = RecentActivity(
            id = historyItem.id,
            title = workout.name,
            timeLabel = HistoryLabels.displayTimestamp(historyItem).uppercase(Locale.getDefault()),
            durationMinutes = duration,
            iconName = historyItem.iconName
        )
        _recentActivity.value = listOf(recent) + _recentActivity.value.take(4)

        scope.launch {
            preferences.saveWorkoutHistory(_history.value)
            refreshActivityDayHistory()
            syncActiveStreakDays()
        }
    }

    fun updateDisplayName(name: String) {
        scope.launch {
            val clean = name.trim()
            preferences.updateProfile(profile.value.copy(displayName = clean))
            val account = currentAccount.value ?: return@launch
            if (AuthValidator.validateUsername(clean) == null &&
                !account.username.equals(clean, ignoreCase = true) &&
                !isUsernameTaken(clean, exceptId = account.id)
            ) {
                persistAccount(account.copy(username = clean))
            }
        }
    }

    suspend fun updateCurrentAccountEmail(email: String): AuthError? {
        val account = currentAccount.value ?: return AuthError.FIELDS_REQUIRED
        val error = AuthValidator.validateEmail(email)
        if (error != null) return error
        val normalized = email.trim().lowercase()
        if (!normalized.equals(account.email, ignoreCase = true) &&
            _accounts.value.any { it.id != account.id && it.email.equals(normalized, ignoreCase = true) }
        ) {
            return AuthError.EMAIL_ALREADY_EXISTS
        }
        if (normalized == account.email) return null
        persistAccount(account.copy(email = normalized))
        return null
    }

    suspend fun updateCurrentAccountPassword(
        currentPassword: String,
        newPassword: String
    ): AuthError? {
        val account = currentAccount.value ?: return AuthError.FIELDS_REQUIRED
        if (account.authProvider == "google" && account.passwordHash.isBlank()) {
            return AuthError.INVALID_CREDENTIALS
        }
        // Ya autenticado en la app: basta la nueva. Si manda la actual, se valida.
        if (currentPassword.isNotBlank() &&
            !PasswordHasher.verify(currentPassword, account.passwordSalt, account.passwordHash)
        ) {
            return AuthError.INVALID_CREDENTIALS
        }
        val strength = AuthValidator.validatePassword(newPassword)
        if (strength != null) return strength
        val (salt, hash) = PasswordHasher.hash(newPassword)
        persistAccount(
            account.copy(
                password = newPassword,
                passwordSalt = salt,
                passwordHash = hash
            )
        )
        _sessionPlainPassword.value = newPassword
        return null
    }

    private suspend fun persistAccount(account: UserAccount) {
        val updated = _accounts.value.map { if (it.id == account.id) account else it }
        _accounts.value = updated
        preferences.saveRegisteredAccounts(updated)
    }

    fun setAvatarPreset(presetId: String) {
        if (ProfileAvatarCatalog.find(presetId) == null) return
        scope.launch {
            preferences.updateProfile(
                profile.value.copy(avatarUrl = ProfileAvatarCatalog.encode(presetId))
            )
        }
    }

    fun setAvatarFromUri(uri: android.net.Uri) {
        scope.launch {
            runCatching {
                val dest = java.io.File(appContext.filesDir, "profile_avatar.jpg")
                appContext.contentResolver.openInputStream(uri)?.use { input ->
                    dest.outputStream().use { output -> input.copyTo(output) }
                } ?: return@launch
                preferences.updateProfile(
                    profile.value.copy(avatarUrl = dest.toURI().toString())
                )
            }
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        scope.launch {
            preferences.setNotificationsEnabled(enabled)
            if (!UiTestEnvironment.isInstrumentedTest) {
                WorkoutReminderScheduler.sync(
                    context = appContext,
                    enabled = enabled,
                    preferredTime = preferredTime.value
                )
            }
        }
    }

    fun setUnitsMetric(metric: Boolean) {
        scope.launch { preferences.setUnitsMetric(metric) }
    }

    fun setFitnessGoal(goal: String) {
        scope.launch { preferences.setFitnessGoal(goal) }
    }

    fun setActivityLevel(level: String) {
        scope.launch {
            preferences.setActivityLevel(level)
            if (!UiTestEnvironment.isInstrumentedTest) {
                activityTracker.syncNow()
            }
        }
    }

    fun setWorkoutLocation(location: String) {
        scope.launch { preferences.setWorkoutLocation(location) }
    }

    fun setPreferredTime(time: String) {
        scope.launch {
            preferences.setPreferredTime(time)
            if (!UiTestEnvironment.isInstrumentedTest && notificationsEnabled.value) {
                WorkoutReminderScheduler.sync(
                    context = appContext,
                    enabled = true,
                    preferredTime = time
                )
            }
        }
    }

    fun clearWorkoutHistory() {
        lastRecordedWorkoutId = null
        lastRecordedCompletionAt = 0L
        _history.value = emptyList()
        _recentActivity.value = emptyList()
        scope.launch {
            preferences.saveWorkoutHistory(emptyList())
            val current = profile.value
            preferences.updateProfile(
                current.copy(
                    totalWorkouts = 0,
                    level = 1,
                    activeStreakDays = 0
                )
            )
            refreshActivityDayHistory()
            refreshMilestones()
        }
    }

    fun setWeeklyTargetSessions(target: Int) {
        if (target <= 0) return
        scope.launch {
            val current = preferences.weeklyGoal.first()
            preferences.saveWeeklyGoal(
                current.copy(
                    targetSessions = target,
                    completedSessions = current.completedSessions.coerceAtMost(target)
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
        "RECOVERY", "YOGA", "PILATES", "MOBILITY" -> "self_improvement"
        "CARDIO" -> "directions_run"
        "SWIM" -> "pool"
        else -> "fitness_center"
    }

    companion object {
        const val REST_SECONDS_BETWEEN_EXERCISES = 45
        const val REST_SECONDS_AFTER_WARMUP = 20
        const val EXERCISE_SECONDS_DEFAULT = 90

        fun defaultProfile() = UserProfile(
            displayName = "Usuario",
            avatarUrl = ProfileAvatarCatalog.encode(ProfileAvatarCatalog.DEFAULT_ID),
            isProMember = false,
            totalWorkouts = 0,
            activeStreakDays = 0,
            level = 1
        )

        fun defaultDailyGoals() = DailyGoalsCalculator.build(
            steps = 0,
            workoutCalories = 0,
            exerciseMinutes = 0,
            standHours = 0
        )

        fun defaultAthleticStats(): List<AthleticStat> =
            AthleticProfileCalculator.compute(emptyList(), streakDays = 0)

        fun defaultMilestones() = MilestoneCatalog.all()

        fun defaultHistory(): List<WorkoutHistoryItem> = emptyList()

        fun defaultRecentActivity(): List<RecentActivity> = emptyList()
    }
}
