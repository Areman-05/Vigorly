package com.example.vigorly.presentation.feature.settings

import androidx.lifecycle.ViewModel
import com.example.vigorly.data.model.UserProfile
import com.example.vigorly.data.model.WeeklyGoal
import com.example.vigorly.data.repository.VigorlyRepository
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(
    private val repository: VigorlyRepository
) : ViewModel() {
    val profile: StateFlow<UserProfile> = repository.profile
    val notificationsEnabled: StateFlow<Boolean> = repository.notificationsEnabled
    val unitsMetric: StateFlow<Boolean> = repository.unitsMetric
    val weeklyGoal: StateFlow<WeeklyGoal> = repository.weeklyGoal

    fun updateDisplayName(name: String) = repository.updateDisplayName(name)

    fun setNotificationsEnabled(enabled: Boolean) =
        repository.setNotificationsEnabled(enabled)

    fun setUnitsMetric(metric: Boolean) = repository.setUnitsMetric(metric)

    fun setWeeklyTargetSessions(target: Int) =
        repository.setWeeklyTargetSessions(target)

    fun resetOnboarding() = repository.resetOnboarding()

    fun resetDailyGoals() = repository.resetDailyGoals()

    fun resetWeeklyProgress() = repository.resetWeeklyProgress()

    fun clearWorkoutHistory() = repository.clearWorkoutHistory()

    fun logout() = repository.logout()
}
