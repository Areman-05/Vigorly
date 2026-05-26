package com.example.vigorly.data.repository

import android.content.Context
import com.example.vigorly.data.AthleticStatBooster
import com.example.vigorly.data.MilestoneUnlocker
import com.example.vigorly.data.catalog.WorkoutCatalog
import com.example.vigorly.data.local.VigorlyPreferencesDataStore
import com.example.vigorly.data.model.WorkoutType
import com.example.vigorly.data.model.AthleticStat
import com.example.vigorly.data.model.DailyGoals
import com.example.vigorly.data.model.Exercise
import com.example.vigorly.data.model.Milestone
import com.example.vigorly.data.model.RecentActivity
import com.example.vigorly.data.model.UserProfile
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
    private val preferences = VigorlyPreferencesDataStore(context.applicationContext)
    private val workouts = WorkoutCatalog.allWorkouts()

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

    init {
        preferences.athleticStats.onEach { _athleticStats.value = it }.launchIn(scope)
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
        recordWorkoutCompletion(session.workoutId, workout.durationMinutes, workout.estimatedCalories)
        _activeSession.value = null
    }

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
