package com.example.vigorly.data.catalog

import com.example.vigorly.data.model.Exercise
import com.example.vigorly.data.model.WorkoutBlock
import com.example.vigorly.data.model.WorkoutDetail
import com.example.vigorly.data.model.WorkoutType

object WorkoutCatalog {

    private const val HERO_STRENGTH =
        "https://lh3.googleusercontent.com/aida-public/AB6AXuBd3x6wDhs4mvlSe6KRxaf0AmxSxvC3I_RXu8RSfCAO3FoX2jJg6HWH3_Eifc7cO0_IP1GRxu8Vc38rAxZH8WzJz7pDH7LO6y_b0V20O6GivmKOPKilmUd2pV5WtIUZZTmgLAeRstDQJJBgS6sURHeKicXGZm4sxk6UXJ_dDoErD6EhHiAU_vIWRTPu8iuLh-FBW0WEeIqJRAOWC6i1EGv2imVu8LodYUjEqkm562BgosyImUlmQ6k9I7Te42yfpWZw89XngMZ8xHo"
    private const val ANATOMY_BACK =
        "https://lh3.googleusercontent.com/aida-public/AB6AXuDWqRccaSnKeyBIBV-wNPyo7S2RP65O4NgOz1N_KNdqBZtawRnUPcrhxVGn46CWHNJVTMzoRzZbMSf1N_cmWTQqE4IEsvFkE8DenS7u-9z9hTD6_4COq0upPONL1fst5YVFMeK6p_EF5Xw4662DlXDIrViVxf5T9uV9KKncyIgDDG88qhExt9wtDQVF7bx2pCKkazL3e8UY34eKiKcCznxbj8dR1t9ROSIN1a_jv1KfC2MYW7nKFX2XYRaiqPLxWkBpn6XyPua1x0s"

    fun allWorkouts(): Map<String, WorkoutDetail> = mapOf(
        titanProtocol(),
        hiitSprint(),
        recoveryYoga()
    )

    fun titanProtocol() = WorkoutDetail(
        id = "titan_protocol",
        name = "Titan Protocol",
        description = "A high-volume hypertrophy session focusing on compound movements to build raw power and dense muscle tissue.",
        type = WorkoutType.STRENGTH,
        durationMinutes = 45,
        heroImageUrl = HERO_STRENGTH,
        targetMuscles = "Back & Biceps",
        targetDescription = "Back & Biceps",
        anatomyImageUrl = ANATOMY_BACK,
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
                    Exercise("e3", "Dumbbell Hammer Curls", "3 Sets • 12-15 Reps", null, "fitness_center")
                )
            )
        )
    )

    fun hiitSprint() = WorkoutDetail(
        id = "hiit_sprint",
        name = "HIIT Sprint Intervals",
        description = "High-intensity interval training to boost cardiovascular endurance and burn calories fast.",
        type = WorkoutType.HIIT,
        durationMinutes = 35,
        heroImageUrl = HERO_STRENGTH,
        targetMuscles = "Full Body",
        targetDescription = "Cardio & Legs",
        anatomyImageUrl = null,
        intensity = "High",
        estimatedCalories = 420,
        blocks = listOf(
            WorkoutBlock(
                id = "a",
                label = "A",
                title = "Warm Up",
                exercises = listOf(
                    Exercise("h1", "Dynamic Stretching", "5 min", null, "self_improvement"),
                    Exercise("h2", "Light Jog", "5 min", null, "directions_run")
                )
            ),
            WorkoutBlock(
                id = "b",
                label = "B",
                title = "Sprint Intervals",
                exercises = listOf(
                    Exercise("h3", "30s Sprint / 30s Rest", "8 rounds", null, "directions_run"),
                    Exercise("h4", "Cool Down Walk", "5 min", null, "directions_walk")
                )
            )
        )
    )

    fun recoveryYoga() = WorkoutDetail(
        id = "recovery_yoga",
        name = "Active Recovery Yoga",
        description = "Low-impact mobility flow to aid recovery and improve flexibility between heavy training days.",
        type = WorkoutType.RECOVERY,
        durationMinutes = 45,
        heroImageUrl = HERO_STRENGTH,
        targetMuscles = "Full Body",
        targetDescription = "Mobility & Core",
        anatomyImageUrl = null,
        intensity = "Low",
        estimatedCalories = 150,
        blocks = listOf(
            WorkoutBlock(
                id = "a",
                label = "A",
                title = "Flow Sequence",
                exercises = listOf(
                    Exercise("y1", "Sun Salutation A", "3 rounds", null, "self_improvement"),
                    Exercise("y2", "Pigeon Pose Hold", "2 min each side", null, "self_improvement"),
                    Exercise("y3", "Child's Pose", "3 min", null, "self_improvement")
                )
            )
        )
    )
}
