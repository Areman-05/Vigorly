package com.example.vigorly.presentation.feature.profile

import androidx.lifecycle.ViewModel
import com.example.vigorly.data.model.AthleticStat
import com.example.vigorly.data.model.Milestone
import com.example.vigorly.data.model.UserProfile
import com.example.vigorly.data.model.WeeklyGoal
import com.example.vigorly.data.model.WorkoutHistoryItem
import com.example.vigorly.data.repository.VigorlyRepository
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel(
    private val repository: VigorlyRepository
) : ViewModel() {
    val profile: StateFlow<UserProfile> = repository.profile
    val athleticStats: StateFlow<List<AthleticStat>> = repository.athleticStats
    val milestones: StateFlow<List<Milestone>> = repository.milestones
    val milestoneShowcase: StateFlow<List<String?>> = repository.milestoneShowcase
    val history: StateFlow<List<WorkoutHistoryItem>> = repository.history
    val weeklyGoal: StateFlow<WeeklyGoal> = repository.weeklyGoal

    fun setAvatarPreset(presetId: String) = repository.setAvatarPreset(presetId)

    fun setMilestoneShowcaseSlot(slotIndex: Int, milestoneId: String?) =
        repository.setMilestoneShowcaseSlot(slotIndex, milestoneId)
}
