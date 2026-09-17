package com.example.vigorly.util

import com.example.vigorly.data.model.SessionStep
import com.example.vigorly.data.model.WorkoutDetail

object SessionStepsBuilder {

    fun build(workout: WorkoutDetail): List<SessionStep> {
        val warmups = WorkoutWarmupBuilder.forWorkout(workout).map { step ->
            SessionStep(
                id = step.id,
                name = step.name,
                detailLabel = step.durationLabel,
                isWarmup = true,
                durationSeconds = parseWarmupSeconds(step.durationLabel),
                cue = SessionStepCue.forStep(step.name, step.durationLabel, isWarmup = true)
            )
        }
        val exerciseCount = workout.blocks.sumOf { it.exercises.size }.coerceAtLeast(1)
        val exerciseSecs = exerciseDurationFor(workout, exerciseCount)
        val exercises = workout.blocks.flatMap { block ->
            block.exercises.map { exercise ->
                SessionStep(
                    id = exercise.id,
                    name = exercise.name,
                    detailLabel = exercise.setsRepsLabel,
                    isWarmup = false,
                    durationSeconds = exerciseSecs,
                    cue = SessionStepCue.forStep(
                        exercise.name,
                        exercise.setsRepsLabel,
                        isWarmup = false
                    )
                )
            }
        }
        return warmups + exercises
    }

    fun exerciseDurationFor(workout: WorkoutDetail, exerciseCount: Int): Int {
        val count = exerciseCount.coerceAtLeast(1)
        val fromPlan = (workout.durationMinutes.coerceAtLeast(1) * 60) / count
        return fromPlan.coerceIn(45, 180)
    }

    private fun parseWarmupSeconds(label: String): Int {
        val lower = label.lowercase()
        val number = Regex("(\\d+)").find(lower)?.groupValues?.getOrNull(1)?.toIntOrNull()
        return when {
            number == null -> 45
            "min" in lower -> (number * 60).coerceIn(30, 180)
            "s" in lower || "seg" in lower -> number.coerceIn(20, 120)
            "rep" in lower || "ciclo" in lower || "lado" in lower || "cambio" in lower ||
                "sentido" in lower -> (number * 3).coerceIn(30, 90)
            else -> 45
        }
    }
}
