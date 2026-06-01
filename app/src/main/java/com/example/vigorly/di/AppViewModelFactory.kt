package com.example.vigorly.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.ui.VigorlyViewModel
import com.example.vigorly.ui.workout.WorkoutsViewModel

class AppViewModelFactory(
    private val repository: VigorlyRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(VigorlyViewModel::class.java) -> VigorlyViewModel() as T
            modelClass.isAssignableFrom(WorkoutsViewModel::class.java) -> WorkoutsViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}
