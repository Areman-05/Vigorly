package com.example.vigorly.data.catalog

import com.example.vigorly.data.model.Exercise
import com.example.vigorly.data.model.WorkoutBlock
import com.example.vigorly.data.model.WorkoutDetail
import com.example.vigorly.data.model.WorkoutType

internal object WorkoutCatalogBuilder {

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
        anatomyImageUrl: String? = null,
        heroImageUrl: String? = null
    ) = WorkoutDetail(
        id = id,
        name = name,
        description = description,
        type = type,
        durationMinutes = durationMinutes,
        heroImageUrl = heroImageUrl ?: WorkoutCoverUrls.forKey(id),
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
        iconName: String = "fitness_center",
        imageUrl: String? = null
    ) = Exercise(
        id = id,
        name = name,
        setsRepsLabel = setsReps,
        imageUrl = imageUrl ?: WorkoutCoverUrls.forKey("ex_$id"),
        iconName = iconName
    )
}
