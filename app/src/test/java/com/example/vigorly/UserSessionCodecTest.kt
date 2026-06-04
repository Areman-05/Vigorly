package com.example.vigorly

import com.example.vigorly.data.local.UserSessionCodec
import com.example.vigorly.data.model.UserProfile
import com.example.vigorly.data.model.UserSessionSnapshot
import com.example.vigorly.data.model.WeeklyGoal
import com.example.vigorly.data.repository.VigorlyRepository
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class UserSessionCodecTest {

    @Test
    fun encode_decode_preservesWeeklyGoalProgress() {
        val snapshot = UserSessionSnapshot(
            profile = UserProfile(
                displayName = "Test",
                avatarUrl = "preset:spark",
                isProMember = false,
                totalWorkouts = 3,
                activeStreakDays = 2,
                level = 2
            ),
            dailyGoals = VigorlyRepository.defaultDailyGoals(),
            weeklyGoal = WeeklyGoal(targetSessions = 5, completedSessions = 3),
            onboardingCompleted = true,
            fitnessGoal = "wellness",
            activityLevel = "moderate",
            workoutLocation = "home",
            preferredTime = "morning",
            notificationsEnabled = true,
            unitsMetric = true,
            workoutHistory = emptyList(),
            athleticStats = VigorlyRepository.defaultAthleticStats(),
            favoriteWorkoutIds = emptySet(),
            dailyTipIndex = 0
        )
        val encoded = UserSessionCodec.encode(mapOf("user-1" to snapshot))
        val decoded = UserSessionCodec.decode(encoded)["user-1"]!!
        assertEquals(3, decoded.weeklyGoal.completedSessions)
        assertEquals(5, decoded.weeklyGoal.targetSessions)
        assertEquals("Test", decoded.profile.displayName)
    }
}
