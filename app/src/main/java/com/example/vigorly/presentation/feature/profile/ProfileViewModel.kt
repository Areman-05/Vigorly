package com.example.vigorly.presentation.feature.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vigorly.data.model.AuthError
import com.example.vigorly.data.model.UserAccount
import com.example.vigorly.data.model.UserProfile
import com.example.vigorly.data.model.WeeklyGoal
import com.example.vigorly.data.repository.VigorlyRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: VigorlyRepository
) : ViewModel() {
    val profile: StateFlow<UserProfile> = repository.profile
    val currentAccount: StateFlow<UserAccount?> = repository.currentAccount
    val revealablePassword: StateFlow<String?> = repository.revealableAccountPassword
    val weeklyGoal: StateFlow<WeeklyGoal> = repository.weeklyGoal
    val history = repository.history
    val notificationsEnabled: StateFlow<Boolean> = repository.notificationsEnabled
    val unitsMetric: StateFlow<Boolean> = repository.unitsMetric
    val appLocale: StateFlow<String> = repository.appLocale
    val fitnessGoal: StateFlow<String> = repository.fitnessGoal
    val activityLevel: StateFlow<String> = repository.activityLevel
    val workoutLocation: StateFlow<String> = repository.workoutLocation
    val weightGoalKg: StateFlow<Float?> = repository.weightGoalKg

    fun setAvatarPreset(presetId: String) = repository.setAvatarPreset(presetId)

    fun setAvatarFromUri(uri: Uri) = repository.setAvatarFromUri(uri)

    fun updateDisplayName(name: String) = repository.updateDisplayName(name)

    fun updateAccountEmail(email: String, onResult: (AuthError?) -> Unit) {
        viewModelScope.launch {
            onResult(repository.updateCurrentAccountEmail(email))
        }
    }

    fun updateAccountPassword(
        currentPassword: String,
        newPassword: String,
        onResult: (AuthError?) -> Unit
    ) {
        viewModelScope.launch {
            onResult(repository.updateCurrentAccountPassword(currentPassword, newPassword))
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) =
        repository.setNotificationsEnabled(enabled)

    fun setUnitsMetric(metric: Boolean) = repository.setUnitsMetric(metric)

    fun setWeeklyTargetSessions(target: Int) =
        repository.setWeeklyTargetSessions(target)

    fun setAppLocale(code: String) = repository.setAppLocale(code)

    fun setFitnessGoal(goal: String) = repository.setFitnessGoal(goal)

    fun setActivityLevel(level: String) = repository.setActivityLevel(level)

    fun setWorkoutLocation(location: String) = repository.setWorkoutLocation(location)

    fun setWeightGoalKg(goalKg: Float?) = repository.setWeightGoalKg(goalKg)

    fun resetOnboarding() = repository.resetOnboarding()

    fun resetDailyGoals() = repository.resetDailyGoals()

    fun resetWeeklyProgress() = repository.resetWeeklyProgress()

    fun clearWorkoutHistory() = repository.clearWorkoutHistory()
}
