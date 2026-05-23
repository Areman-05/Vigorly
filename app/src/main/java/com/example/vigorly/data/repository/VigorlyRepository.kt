package com.example.vigorly.data.repository

import com.example.vigorly.data.model.AthleticStat
import com.example.vigorly.data.model.DailyGoals
import com.example.vigorly.data.model.Exercise
import com.example.vigorly.data.model.Milestone
import com.example.vigorly.data.model.RecentActivity
import com.example.vigorly.data.model.UserProfile
import com.example.vigorly.data.model.WorkoutBlock
import com.example.vigorly.data.model.WorkoutDetail
import com.example.vigorly.data.model.WorkoutHistoryItem
import com.example.vigorly.data.model.WorkoutType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class VigorlyRepository {

    private val _profile = MutableStateFlow(defaultProfile())
    val profile: StateFlow<UserProfile> = _profile.asStateFlow()

    private val _dailyGoals = MutableStateFlow(defaultDailyGoals())
    val dailyGoals: StateFlow<DailyGoals> = _dailyGoals.asStateFlow()

    private val _athleticStats = MutableStateFlow(defaultAthleticStats())
    val athleticStats: StateFlow<List<AthleticStat>> = _athleticStats.asStateFlow()

    private val _milestones = MutableStateFlow(defaultMilestones())
    val milestones: StateFlow<List<Milestone>> = _milestones.asStateFlow()

    private val _history = MutableStateFlow(defaultHistory())
    val history: StateFlow<List<WorkoutHistoryItem>> = _history.asStateFlow()

    private val _recentActivity = MutableStateFlow(defaultRecentActivity())
    val recentActivity: StateFlow<List<RecentActivity>> = _recentActivity.asStateFlow()

    private val workouts = defaultWorkouts()

    fun getWorkout(id: String): WorkoutDetail? = workouts[id]

    fun listWorkoutIds(): List<String> = workouts.keys.toList()

    fun recordWorkoutCompletion(workoutId: String) {
        _profile.update { it.copy(totalWorkouts = it.totalWorkouts + 1) }
        _dailyGoals.update { goals ->
            goals.copy(
                moveProgress = (goals.moveProgress + 0.05f).coerceAtMost(1f),
                exerciseProgress = (goals.exerciseProgress + 0.08f).coerceAtMost(1f)
            )
        }
    }

    companion object {
        private const val AVATAR =
            "https://lh3.googleusercontent.com/aida-public/AB6AXuDzR2WvctnwBapv2J6FHSJYaIFfmcx4nHOnSfxS9s9DsMaU2qiczrCg6K6NhCslo0gLmFhJeFxgq3ulqD3z4hn_iwC2SqplrHuVXc8M4dX42iQoUArvxVD8coCeO-eFvEm0nH01AT0YTr7lBWOj1x6PxWej2M0mb_di0SpnHD5YQlNDE8HN_mFBd0v7fi8YXV3vimrg4QhfnOvZyF67cIrb0UZsn17KmNEg51BL1vdtc5iKyvmZjwee-hzGVEGko2Qxq_iTKNCuwcM"

        fun defaultProfile() = UserProfile(
            displayName = "Alex Rivers",
            avatarUrl = AVATAR,
            isProMember = true,
            totalWorkouts = 342,
            activeStreakDays = 14,
            level = 42
        )

        fun defaultDailyGoals() = DailyGoals(
            moveProgress = 0.75f,
            exerciseProgress = 0.50f,
            standProgress = 0.82f,
            moveCalories = 450,
            moveCaloriesGoal = 600,
            steps = 6240,
            stepsGoal = 10000,
            heartRateBpm = 72,
            sleepHours = 7f
        )

        fun defaultAthleticStats() = listOf(
            AthleticStat("Strength", 85),
            AthleticStat("Endurance", 70),
            AthleticStat("Mobility", 65),
            AthleticStat("Speed", 80),
            AthleticStat("Power", 90),
            AthleticStat("Stamina", 75)
        )

        fun defaultMilestones() = listOf(
            Milestone("streak_100", "100 Day", "Streak", "local_fire_department", true),
            Milestone("lift_10k", "10k Lbs", "Lifted", "fitness_center", true),
            Milestone("run_5k", "Sub 20", "5K Run", "timer", true),
            Milestone("elite", "Elite", "Status", "emoji_events", false)
        )

        fun defaultHistory() = listOf(
            WorkoutHistoryItem("h1", "Upper Body Power", "Today, 06:30 AM", 60, 540, "fitness_center"),
            WorkoutHistoryItem("h2", "HIIT Sprint Intervals", "Yesterday, 07:15 AM", 35, 420, "directions_run"),
            WorkoutHistoryItem("h3", "Active Recovery Yoga", "Mon, 18:00 PM", 45, 150, "self_improvement")
        )

        fun defaultRecentActivity() = listOf(
            RecentActivity("r1", "Morning Swim", "TODAY, 6:00 AM", 45, "pool")
        )

        fun defaultWorkouts(): Map<String, WorkoutDetail> {
            val hero =
                "https://lh3.googleusercontent.com/aida-public/AB6AXuBd3x6wDhs4mvlSe6KRxaf0AmxSxvC3I_RXu8RSfCAO3FoX2jJg6HWH3_Eifc7cO0_IP1GRxu8Vc38rAxZH8WzJz7pDH7LO6y_b0V20O6GivmKOPKilmUd2pV5WtIUZZTmgLAeRstDQJJBgS6sURHeKicXGZm4sxk6UXJ_dDoErD6EhHiAU_vIWRTPu8iuLh-FBW0WEeIqJRAOWC6i1EGv2imVu8LodYUjEqkm562BgosyImUlmQ6k9I7Te42yfpWZw89XngMZ8xHo"
            val anatomy =
                "https://lh3.googleusercontent.com/aida-public/AB6AXuDWqRccaSnKeyBIBV-wNPyo7S2RP65O4NgOz1N_KNdqBZtawRnUPcrhxVGn46CWHNJVTMzoRzZbMSf1N_cmWTQqE4IEsvFkE8DenS7u-9z9hTD6_4COq0upPONL1fst5YVFMeK6p_EF5Xw4662DlXDIrViVxf5T9uV9KKncyIgDDG88qhExt9wtDQVF7bx2pCKkazL3e8UY34eKiKcCznxbj8dR1t9ROSIN1a_jv1KfC2MYW7nKFX2XYRaiqPLxWkBpn6XyPua1x0s"
            return mapOf(
                "titan_protocol" to WorkoutDetail(
                    id = "titan_protocol",
                    name = "Titan Protocol",
                    description = "A high-volume hypertrophy session focusing on compound movements to build raw power and dense muscle tissue.",
                    type = WorkoutType.STRENGTH,
                    durationMinutes = 45,
                    heroImageUrl = hero,
                    targetMuscles = "Back & Biceps",
                    targetDescription = "Back & Biceps",
                    anatomyImageUrl = anatomy,
                    intensity = "High",
                    estimatedCalories = 450,
                    blocks = listOf(
                        WorkoutBlock(
                            id = "a",
                            label = "A",
                            title = "Heavy Pull",
                            exercises = listOf(
                                Exercise(
                                    "e1",
                                    "Barbell Pendlay Row",
                                    "4 Sets • 8-10 Reps",
                                    "https://lh3.googleusercontent.com/aida-public/AB6AXuDzdXhpA66A7ywqOov7Db9EVkOl7Xw9V7dMk7vDcamU-9OyyW-cZHLbJ46kOvMl878unvhDeH6xoe8k6ZWj0rtXA3AU9F19SXpsKS345peSeHxXb0HwoN16hqZUL0Tp-ueGfLOYeTEyNGjx4Lp7b4OrdzBUWGpr8MmebEJQVoacnfu7EGc494ie9iLUqcTAIl99am1c_Q2__2zQEzjVErKPJ2VPdOre4e0fM1CT5jikfxtgBgHt20E9cplviXRBvw-XjU1UNOOB08E"
                                ),
                                Exercise(
                                    "e2",
                                    "Weighted Pull-ups",
                                    "3 Sets • 6-8 Reps",
                                    "https://lh3.googleusercontent.com/aida-public/AB6AXuC5_mzpDek8339Xw2H0EQnxrj8kLPgaZxYsmHKQ6zkyqgg0Mxc2IHSfbIAExiqWT-Pl7wiapgd-J1964dxsVL-zLkv3B797Rj4NKkty5vYO9y9Ym0sfyN_zQaCaMUkqYLohNr5hmwcgAJsxu_F3QWM4dpp7N2uocaxRM5mpNxDRQD3QgqqTHoF1kGbO1HLhdgSHI3244Az27k3eKUdg7sXBDTor5qlSqx1PnWVf9opET907sVGxhc1F9UBisyQ8i_3thzXAVR7fDWs"
                                )
                            )
                        ),
                        WorkoutBlock(
                            id = "b",
                            label = "B",
                            title = "Hypertrophy Focus",
                            exercises = listOf(
                                Exercise(
                                    "e3",
                                    "Dumbbell Hammer Curls",
                                    "3 Sets • 12-15 Reps",
                                    null,
                                    "fitness_center"
                                )
                            )
                        )
                    )
                )
            )
        }
    }
}
