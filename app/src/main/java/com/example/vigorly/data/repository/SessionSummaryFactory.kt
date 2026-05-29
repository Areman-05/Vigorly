package com.example.vigorly.data.repository

import com.example.vigorly.data.model.SessionSummary
import com.example.vigorly.data.model.WorkoutDetail
import com.example.vigorly.data.model.WorkoutSessionState

object SessionSummaryFactory {
    fun from(session: WorkoutSessionState, workout: WorkoutDetail): SessionSummary =
        SessionSummary(
            workoutId = session.workoutId,
            workoutName = workout.name,
            durationMinutes = workout.durationMinutes,
            caloriesBurned = workout.estimatedCalories,
            exercisesCompleted = session.completedExerciseIds.size.coerceAtLeast(1),
            totalExercises = session.totalExercises,
            elapsedSeconds = session.elapsedSeconds
        )
}
