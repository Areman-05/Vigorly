package com.example.vigorly.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.presentation.app.AppViewModel
import com.example.vigorly.presentation.feature.dashboard.DashboardViewModel
import com.example.vigorly.presentation.feature.profile.ProfileViewModel
import com.example.vigorly.presentation.feature.settings.SettingsViewModel
import com.example.vigorly.ui.workout.WorkoutsViewModel

class AppViewModelFactory(
    private val repository: VigorlyRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AppViewModel::class.java) -> AppViewModel() as T
            modelClass.isAssignableFrom(WorkoutsViewModel::class.java) ->
                WorkoutsViewModel(repository) as T
            modelClass.isAssignableFrom(DashboardViewModel::class.java) ->
                DashboardViewModel(repository) as T
            modelClass.isAssignableFrom(ProfileViewModel::class.java) ->
                ProfileViewModel(repository) as T
            modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
                SettingsViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}
