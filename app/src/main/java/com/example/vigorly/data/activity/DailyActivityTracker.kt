package com.example.vigorly.data.activity

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.vigorly.data.local.VigorlyPreferencesDataStore
import com.example.vigorly.data.model.DailyGoals
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class DailyActivityMetrics(
    val steps: Int = 0,
    val exerciseMinutes: Int = 0,
    val workoutCalories: Int = 0,
    val standHours: Int = 0,
    val sensorAvailable: Boolean = false
)

class DailyActivityTracker(
    context: Context,
    private val preferences: VigorlyPreferencesDataStore,
    private val onMetricsUpdated: suspend (DailyGoals) -> Unit
) : SensorEventListener {

    private val appContext = context.applicationContext
    private val sensorManager = appContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + Dispatchers.IO)
    private val stateMutex = Mutex()
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    private var stepBaseline: Long? = null
    private var lastStepTotal: Long = 0L
    private var standHoursEarned = mutableSetOf<Int>()
    private var exerciseMinutes = 0
    private var workoutCalories = 0
    private var stepsPerHour = IntArray(HourlyActivityCodec.HOURS)
    private var exerciseMinutesPerHour = IntArray(HourlyActivityCodec.HOURS)
    private var workoutCaloriesPerHour = IntArray(HourlyActivityCodec.HOURS)
    private var trackingDateKey: String = todayKey()

    private val _metrics = MutableStateFlow(DailyActivityMetrics())
    val metrics: StateFlow<DailyActivityMetrics> = _metrics.asStateFlow()

    private val _detail = MutableStateFlow(DailyActivityDetail())
    val detail: StateFlow<DailyActivityDetail> = _detail.asStateFlow()

    private var listening = false
    @Volatile
    private var initialized = false
    private var pendingStart = false

    suspend fun initialize() {
        stateMutex.withLock {
            loadPersistedState()
        }
        publishGoals(preserveWellness = true)
        initialized = true
        if (pendingStart) {
            pendingStart = false
            startInternal()
        }
    }

    fun start() {
        if (listening) return
        _metrics.value = _metrics.value.copy(sensorAvailable = stepSensor != null)
        if (!initialized) {
            pendingStart = true
            return
        }
        startInternal()
    }

    fun stop() {
        if (!listening) return
        sensorManager.unregisterListener(this)
        listening = false
        if (!job.isActive) return
        scope.launch {
            stateMutex.withLock {
                persistTodaySummary()
            }
        }
    }

    fun close() {
        stop()
        pendingStart = false
        initialized = false
        scope.cancel()
    }

    fun addWorkoutContribution(durationMinutes: Int, calories: Int) {
        if (!job.isActive) return
        scope.launch {
            stateMutex.withLock {
                ensureToday()
                exerciseMinutes = (exerciseMinutes + durationMinutes).coerceAtMost(180)
                workoutCalories = (workoutCalories + calories).coerceAtMost(2000)
                val hour = currentHour()
                exerciseMinutesPerHour[hour] = (exerciseMinutesPerHour[hour] + durationMinutes)
                    .coerceAtMost(180)
                workoutCaloriesPerHour[hour] = (workoutCaloriesPerHour[hour] + calories)
                    .coerceAtMost(500)
                markStandHour(hour)
                persistState()
            }
            publishGoals(preserveWellness = true)
        }
    }

    suspend fun syncNow() {
        stateMutex.withLock { ensureToday() }
        publishGoals(preserveWellness = true)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (!initialized || !job.isActive) return
        if (event?.sensor?.type != Sensor.TYPE_STEP_COUNTER) return
        val total = event.values[0].toLong()
        scope.launch {
            stateMutex.withLock {
                ensureToday()
                if (stepBaseline == null) {
                    stepBaseline = total
                    lastStepTotal = total
                } else if (total >= lastStepTotal) {
                    val delta = total - lastStepTotal
                    if (delta > 0) {
                        val hour = currentHour()
                        stepsPerHour[hour] = (stepsPerHour[hour] + delta.toInt()).coerceAtMost(50_000)
                        markStandHour(hour)
                    }
                    lastStepTotal = total
                } else {
                    stepBaseline = total
                    lastStepTotal = total
                }
                persistState()
            }
            publishGoals(preserveWellness = true)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    private suspend fun publishGoals(preserveWellness: Boolean) {
        val steps = todaySteps()
        val standCount = standHoursEarned.size
        _metrics.value = DailyActivityMetrics(
            steps = steps,
            exerciseMinutes = exerciseMinutes,
            workoutCalories = workoutCalories,
            standHours = standCount,
            sensorAvailable = stepSensor != null
        )
        _detail.value = DailyActivityDetailBuilder.build(
            stepsPerHour = stepsPerHour,
            exerciseMinutesPerHour = exerciseMinutesPerHour,
            workoutCaloriesPerHour = workoutCaloriesPerHour,
            standHours = standHoursEarned,
            totalSteps = steps,
            totalExerciseMinutes = exerciseMinutes,
            totalWorkoutCalories = workoutCalories
        )
        val previous = preferences.dailyGoalsState()
        val goals = DailyGoalsCalculator.build(
            steps = steps,
            workoutCalories = workoutCalories,
            exerciseMinutes = exerciseMinutes,
            standHours = standCount,
            heartRateBpm = if (preserveWellness) previous.heartRateBpm else 0,
            sleepHours = if (preserveWellness) previous.sleepHours else 0f
        )
        preferences.updateDailyGoals(goals)
        onMetricsUpdated(goals)
    }

    private fun todaySteps(): Int {
        val baseline = stepBaseline ?: return 0
        return (lastStepTotal - baseline).coerceAtLeast(0).toInt()
    }

    private suspend fun ensureToday() {
        val today = todayKey()
        if (today != trackingDateKey) {
            archiveCurrentDayIfNeeded()
            trackingDateKey = today
            stepBaseline = null
            lastStepTotal = 0L
            standHoursEarned.clear()
            exerciseMinutes = 0
            workoutCalories = 0
            stepsPerHour = IntArray(HourlyActivityCodec.HOURS)
            exerciseMinutesPerHour = IntArray(HourlyActivityCodec.HOURS)
            workoutCaloriesPerHour = IntArray(HourlyActivityCodec.HOURS)
        }
    }

    private suspend fun archiveCurrentDayIfNeeded() {
        if (trackingDateKey.isBlank()) return
        val detail = buildDetailSnapshot()
        if (detail.steps == 0 && detail.exerciseMinutes == 0 && detail.standHours == 0) return
        preferences.saveActivityDaySummary(
            DailyActivityDaySummary.fromDetail(trackingDateKey, detail)
        )
    }

    private suspend fun persistTodaySummary() {
        if (trackingDateKey.isBlank()) return
        preferences.saveActivityDaySummary(
            DailyActivityDaySummary.fromDetail(trackingDateKey, buildDetailSnapshot())
        )
    }

    private fun buildDetailSnapshot(): DailyActivityDetail {
        val steps = todaySteps()
        return DailyActivityDetailBuilder.build(
            stepsPerHour = stepsPerHour,
            exerciseMinutesPerHour = exerciseMinutesPerHour,
            workoutCaloriesPerHour = workoutCaloriesPerHour,
            standHours = standHoursEarned,
            totalSteps = steps,
            totalExerciseMinutes = exerciseMinutes,
            totalWorkoutCalories = workoutCalories
        )
    }

    private fun markStandHour(hour: Int) {
        if (hour in 0..23) standHoursEarned.add(hour)
    }

    private fun currentHour(): Int = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)

    private fun todayKey(): String = LocalDate.now().format(dateFormatter)

    private suspend fun loadPersistedState() {
        val state = preferences.loadDailyActivityState()
        trackingDateKey = state.dateKey.ifBlank { todayKey() }
        stepBaseline = state.stepBaseline
        lastStepTotal = state.lastStepTotal
        exerciseMinutes = state.exerciseMinutes
        workoutCalories = state.workoutCalories
        standHoursEarned = state.standHours.toMutableSet()
        stepsPerHour = state.stepsPerHour.copyOf()
        exerciseMinutesPerHour = state.exerciseMinutesPerHour.copyOf()
        workoutCaloriesPerHour = state.workoutCaloriesPerHour.copyOf()
        ensureToday()
    }

    private fun startInternal() {
        if (listening) return
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            listening = true
        }
    }

    private suspend fun persistState() {
        preferences.saveDailyActivityState(
            dateKey = trackingDateKey,
            stepBaseline = stepBaseline,
            lastStepTotal = lastStepTotal,
            exerciseMinutes = exerciseMinutes,
            workoutCalories = workoutCalories,
            standHours = standHoursEarned.toList(),
            stepsPerHour = stepsPerHour,
            exerciseMinutesPerHour = exerciseMinutesPerHour,
            workoutCaloriesPerHour = workoutCaloriesPerHour
        )
    }
}
