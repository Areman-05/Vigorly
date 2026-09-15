package com.example.vigorly.data.repository

import com.example.vigorly.data.model.SessionSummary
import com.example.vigorly.data.model.WorkoutDetail
import com.example.vigorly.data.model.WorkoutSessionState
import com.example.vigorly.util.LevelCalculator
import com.example.vigorly.util.SessionStepsBuilder

object SessionSummaryFactory {
    fun from(
        session: WorkoutSessionState,
        workout: WorkoutDetail,
        totalWorkoutsAfter: Int = 0,
        streakDays: Int = 0,
        weeklyCompleted: Int = 0,
        weeklyTarget: Int = 0,
        newlyUnlockedTitles: List<String> = emptyList()
    ): SessionSummary {
        val steps = SessionStepsBuilder.build(workout)
        val warmupCount = steps.count { it.isWarmup }
        val completed = session.completedExerciseIds.size.coerceAtLeast(1)
        val levelBefore = LevelCalculator.levelFromWorkouts((totalWorkoutsAfter - 1).coerceAtLeast(0))
        val levelAfter = LevelCalculator.levelFromWorkouts(totalWorkoutsAfter)
        return SessionSummary(
            workoutId = session.workoutId,
            workoutName = workout.name,
            workoutType = workout.type,
            focusZone = workout.targetMuscles,
            durationMinutes = workout.durationMinutes,
            caloriesBurned = workout.estimatedCalories,
            exercisesCompleted = completed,
            totalExercises = session.totalExercises,
            elapsedSeconds = session.elapsedSeconds,
            levelBefore = levelBefore,
            levelAfter = levelAfter,
            workoutsUntilNextLevel = LevelCalculator.workoutsUntilNextLevel(totalWorkoutsAfter),
            streakDays = streakDays,
            weeklyCompleted = weeklyCompleted,
            weeklyTarget = weeklyTarget,
            newlyUnlockedTitles = newlyUnlockedTitles,
            warmupStepsCompleted = warmupCount.coerceAtMost(completed)
        )
    }
}
