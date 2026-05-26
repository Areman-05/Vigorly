package com.example.vigorly.data

import com.example.vigorly.data.model.Milestone
import com.example.vigorly.data.model.UserProfile

object MilestoneUnlocker {
    fun apply(profile: UserProfile, milestones: List<Milestone>): List<Milestone> =
        milestones.map { milestone ->
            val unlocked = when (milestone.id) {
                "streak_100" -> profile.activeStreakDays >= 100
                "lift_10k" -> profile.totalWorkouts >= 200
                "run_5k" -> profile.totalWorkouts >= 150
                "elite" -> profile.totalWorkouts >= 350 && profile.isProMember
                else -> milestone.unlocked
            }
            milestone.copy(unlocked = unlocked)
        }
}
