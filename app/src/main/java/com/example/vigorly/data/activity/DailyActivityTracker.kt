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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    private var stepBaseline: Long? = null
    private var lastStepTotal: Long = 0L
    private var standHoursEarned = mutableSetOf<Int>()
    private var exerciseMinutes = 0
    private var workoutCalories = 0
    private var trackingDateKey: String = todayKey()

    private val _metrics = MutableStateFlow(DailyActivityMetrics())
    val metrics: StateFlow<DailyActivityMetrics> = _metrics.asStateFlow()

    private var listening = false

    suspend fun initialize() {
        loadPersistedState()
        publishGoals(preserveWellness = true)
    }

    fun start() {
        if (listening) return
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            listening = true
        }
        _metrics.value = _metrics.value.copy(sensorAvailable = stepSensor != null)
    }

    fun stop() {
        if (!listening) return
        sensorManager.unregisterListener(this)
        listening = false
    }

    fun addWorkoutContribution(durationMinutes: Int, calories: Int) {
        scope.launch {
            ensureToday()
            exerciseMinutes = (exerciseMinutes + durationMinutes).coerceAtMost(180)
            workoutCalories = (workoutCalories + calories).coerceAtMost(2000)
            markStandHour(currentHour())
            persistState()
            publishGoals(preserveWellness = true)
        }
    }

    suspend fun syncNow() {
        ensureToday()
        publishGoals(preserveWellness = true)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type != Sensor.TYPE_STEP_COUNTER) return
        val total = event.values[0].toLong()
        scope.launch {
            ensureToday()
            if (stepBaseline == null) {
                stepBaseline = total
                lastStepTotal = total
                persistState()
            }
            if (total >= lastStepTotal) {
                val delta = total - lastStepTotal
                if (delta > 0) {
                    markStandHour(currentHour())
                }
                lastStepTotal = total
            } else {
                stepBaseline = total
                lastStepTotal = total
            }
            persistState()
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
            trackingDateKey = today
            stepBaseline = null
            lastStepTotal = 0L
            standHoursEarned.clear()
            exerciseMinutes = 0
            workoutCalories = 0
        }
    }

    private fun markStandHour(hour: Int) {
        if (hour in 0..23) standHoursEarned.add(hour)
    }

    private fun currentHour(): Int = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)

    private fun todayKey(): String = LocalDate.now().format(dateFormatter)

    private suspend fun loadPersistedState() {
        val state = preferences.loadDailyActivityState()
        trackingDateKey = state.dateKey
        stepBaseline = state.stepBaseline
        lastStepTotal = state.lastStepTotal
        exerciseMinutes = state.exerciseMinutes
        workoutCalories = state.workoutCalories
        standHoursEarned = state.standHours.toMutableSet()
        ensureToday()
    }

    private suspend fun persistState() {
        preferences.saveDailyActivityState(
            dateKey = trackingDateKey,
            stepBaseline = stepBaseline,
            lastStepTotal = lastStepTotal,
            exerciseMinutes = exerciseMinutes,
            workoutCalories = workoutCalories,
            standHours = standHoursEarned.toList()
        )
    }
}
