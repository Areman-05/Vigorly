package com.example.vigorly.data.catalog

import com.example.vigorly.data.model.Exercise
import com.example.vigorly.data.model.WorkoutBlock
import com.example.vigorly.data.model.WorkoutDetail
import com.example.vigorly.data.model.WorkoutType

internal object WorkoutCatalogBuilder {

    private const val HERO =
        "https://lh3.googleusercontent.com/aida-public/AB6AXuBd3x6wDhs4mvlSe6KRxaf0AmxSxvC3I_RXu8RSfCAO3FoX2jJg6HWH3_Eifc7cO0_IP1GRxu8Vc38rAxZH8WzJz7pDH7LO6y_b0V20O6GivmKOPKilmUd2pV5WtIUZZTmgLAeRstDQJJBgS6sURHeKicXGZm4sxk6UXJ_dDoErD6EhHiAU_vIWRTPu8iuLh-FBW0WEeIqJRAOWC6i1EGv2imVu8LodYUjEqkm562BgosyImUlmQ6k9I7Te42yfpWZw89XngMZ8xHo"

    fun detail(
        id: String,
        name: String,
        description: String,
        type: WorkoutType,
        durationMinutes: Int,
        targetMuscles: String,
        targetDescription: String,
        intensity: String,
        estimatedCalories: Int,
        blocks: List<WorkoutBlock>,
        anatomyImageUrl: String? = null
    ) = WorkoutDetail(
        id = id,
        name = name,
        description = description,
        type = type,
        durationMinutes = durationMinutes,
        heroImageUrl = HERO,
        targetMuscles = targetMuscles,
        targetDescription = targetDescription,
        anatomyImageUrl = anatomyImageUrl,
        intensity = intensity,
        estimatedCalories = estimatedCalories,
        blocks = blocks
    )

    fun block(
        id: String,
        label: String,
        title: String,
        exercises: List<Exercise>
    ) = WorkoutBlock(id, label, title, exercises)

    fun exercise(
        id: String,
        name: String,
        setsReps: String,
        iconName: String = "fitness_center"
    ) = Exercise(id, name, setsReps, null, iconName)
}
