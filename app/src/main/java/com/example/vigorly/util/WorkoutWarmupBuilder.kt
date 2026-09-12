package com.example.vigorly.util

import com.example.vigorly.data.catalog.WorkoutCoverUrls
import com.example.vigorly.data.model.WorkoutDetail
import com.example.vigorly.data.model.WorkoutType

data class WarmupStep(
    val id: String,
    val name: String,
    val durationLabel: String,
    val imageUrl: String
)

/**
 * Calentamiento específico del entreno actual (no sugerencias a otros workouts).
 */
object WorkoutWarmupBuilder {

    fun forWorkout(workout: WorkoutDetail): List<WarmupStep> {
        val pool = poolFor(workout)
        val seed = workout.id.fold(0) { acc, c -> 31 * acc + c.code }
        val offset = seed.mod(pool.size)
        val rotated = pool.drop(offset) + pool.take(offset)
        val count = when {
            workout.durationMinutes <= 20 -> 2
            workout.durationMinutes <= 40 -> 3
            else -> 4
        }.coerceAtMost(rotated.size)

        return rotated.take(count).mapIndexed { index, (name, duration) ->
            WarmupStep(
                id = "${workout.id}_wu_$index",
                name = name,
                durationLabel = duration,
                imageUrl = WorkoutCoverUrls.forKey("${workout.id}_wu_$name")
            )
        }
    }

    private fun poolFor(workout: WorkoutDetail): List<Pair<String, String>> {
        val target = listOf(
            workout.targetMuscles,
            workout.targetDescription,
            workout.name
        ).joinToString(" ").lowercase()

        val byZone = when {
            listOf("pierna", "glúteo", "gluteo", "inferior", "squat", "sentadilla")
                .any { target.contains(it) } -> lowerBodyWarmup
            listOf("pecho", "hombro", "brazo", "bíceps", "biceps", "tríceps", "triceps", "superior", "press")
                .any { target.contains(it) } -> upperBodyWarmup
            listOf("core", "abdomen", "zona media")
                .any { target.contains(it) } -> coreWarmup
            else -> emptyList()
        }

        val byType = when (workout.type) {
            WorkoutType.STRENGTH -> strengthWarmup
            WorkoutType.HIIT -> hiitWarmup
            WorkoutType.CARDIO -> cardioWarmup
            WorkoutType.RECOVERY -> yogaWarmup
            WorkoutType.PILATES -> pilatesWarmup
            WorkoutType.MOBILITY -> mobilityWarmup
            WorkoutType.SWIM -> swimWarmup
        }

        return (byZone + byType).distinctBy { it.first }.ifEmpty { generalWarmup }
    }

    private val generalWarmup = listOf(
        "Movilidad articular suave" to "2 min",
        "Marcha en el sitio" to "90 s",
        "Rotaciones de cadera" to "10 por lado",
        "Estiramientos dinámicos" to "2 min"
    )

    private val strengthWarmup = listOf(
        "Rotaciones de hombros" to "20 reps",
        "Sentadillas con peso corporal" to "12 reps",
        "Puente de glúteos" to "10 reps",
        "Activación de core (plancha corta)" to "20 s",
        "Aperturas de pecho" to "12 reps"
    )

    private val hiitWarmup = listOf(
        "Skipping suave" to "60 s",
        "Rodillas altas controladas" to "40 s",
        "Aperturas de cadera" to "8 por lado",
        "Jumping jacks lentos" to "30 s",
        "Movilidad de tobillos" to "10 por lado"
    )

    private val cardioWarmup = listOf(
        "Trote suave en el sitio" to "2 min",
        "Balanceos de pierna" to "10 por lado",
        "Círculos de brazos" to "20 reps",
        "Zancadas cortas sin impacto" to "8 por lado"
    )

    private val yogaWarmup = listOf(
        "Respiración diafragmática" to "1 min",
        "Gato-vaca" to "8 ciclos",
        "Rotación suave de cuello" to "30 s",
        "Estiramiento de isquios sentado" to "45 s"
    )

    private val pilatesWarmup = listOf(
        "Respiración lateral costal" to "1 min",
        "Pelvic curl suave" to "6 reps",
        "Círculos de hombros" to "10 reps",
        "Activación de suelo pélvico" to "8 reps"
    )

    private val mobilityWarmup = listOf(
        "CARs de hombro" to "5 por lado",
        "90/90 cadera suave" to "6 cambios",
        "Gato-vaca fluido" to "8 ciclos",
        "Apertura torácica en suelo" to "6 por lado"
    )

    private val swimWarmup = listOf(
        "Rotaciones de hombros" to "20 reps",
        "Círculos de tobillos" to "10 por lado",
        "Movilidad de columna" to "1 min",
        "Activación de dorsal" to "12 reps"
    )

    private val upperBodyWarmup = listOf(
        "Círculos de brazos" to "15 por sentido",
        "Push-up en pared o rodillas" to "8 reps",
        "Retracción escapular" to "12 reps",
        "Estiramiento pecho en puerta" to "30 s"
    )

    private val lowerBodyWarmup = listOf(
        "Sentadilla a silla" to "10 reps",
        "Balanceo de pierna frontal" to "10 por lado",
        "Puente glúteo" to "10 reps",
        "Movilidad de tobillo en pared" to "8 por lado"
    )

    private val coreWarmup = listOf(
        "Dead bug suave" to "6 por lado",
        "Bird dog" to "6 por lado",
        "Plancha sobre rodillas" to "20 s",
        "Respiración 360°" to "1 min"
    )
}
