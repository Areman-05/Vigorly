package com.example.vigorly.data

import com.example.vigorly.data.model.Milestone
import com.example.vigorly.data.model.UserProfile
import com.example.vigorly.util.LevelCalculator

object MilestoneUnlocker {
    fun apply(profile: UserProfile, milestones: List<Milestone>): List<Milestone> {
        val level = LevelCalculator.levelFromWorkouts(profile.totalWorkouts)
        return milestones.map { milestone ->
            milestone.copy(unlocked = isUnlocked(milestone.id, profile, level))
        }
    }

    private fun isUnlocked(id: String, profile: UserProfile, level: Int): Boolean {
        val total = profile.totalWorkouts
        val streak = profile.activeStreakDays
        return when (id) {
            "first_workout" -> total >= 1
            "workouts_5" -> total >= 5
            "workouts_10" -> total >= 10
            "workouts_25" -> total >= 25
            "workouts_50" -> total >= 50
            "workouts_75" -> total >= 75
            "workouts_100" -> total >= 100
            "workouts_125" -> total >= 125
            "workouts_150" -> total >= 150
            "workouts_175" -> total >= 175
            "workouts_200" -> total >= 200
            "workouts_250" -> total >= 250
            "workouts_300" -> total >= 300
            "consistency_20" -> total >= 20
            "consistency_40" -> total >= 40
            "streak_3" -> streak >= 3
            "streak_7" -> streak >= 7
            "streak_14" -> streak >= 14
            "streak_21" -> streak >= 21
            "streak_30" -> streak >= 30
            "streak_60" -> streak >= 60
            "streak_100" -> streak >= 100
            "level_2" -> level >= 2
            "level_4" -> level >= 4
            "level_6" -> level >= 6
            "level_8" -> level >= 8
            "level_10" -> level >= 10
            "pro_member" -> profile.isProMember
            "elite" -> level >= LevelCalculator.MAX_LEVEL && profile.isProMember
            "run_5k" -> total >= 150
            else -> false
        }
    }
}
