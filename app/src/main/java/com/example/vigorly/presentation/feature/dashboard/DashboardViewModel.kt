package com.example.vigorly.presentation.feature.dashboard

import androidx.lifecycle.ViewModel
import com.example.vigorly.data.model.DailyGoals
import com.example.vigorly.data.model.CoachingTip
import com.example.vigorly.data.model.UserProfile
import com.example.vigorly.data.model.WeeklyGoal
import com.example.vigorly.data.model.WorkoutDetail
import com.example.vigorly.data.repository.VigorlyRepository
import kotlinx.coroutines.flow.StateFlow

class DashboardViewModel(
    private val repository: VigorlyRepository
) : ViewModel() {
    val dailyGoals: StateFlow<DailyGoals> = repository.dailyGoals
    val weeklyGoal: StateFlow<WeeklyGoal> = repository.weeklyGoal
    val profile: StateFlow<UserProfile> = repository.profile
    val dailyTip: StateFlow<CoachingTip> = repository.dailyTip
    val showStreakBanner: StateFlow<Boolean> = repository.showStreakBanner

    fun getRecommendedWorkout(): WorkoutDetail? = repository.getRecommendedWorkout()

    fun dismissStreakBanner() = repository.dismissStreakBanner()
}
