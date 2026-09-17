package com.example.vigorly.util

import com.example.vigorly.data.model.WorkoutDetail
import com.example.vigorly.data.model.WorkoutType
import java.util.Locale

/**
 * Resumen técnico del entreno para la ficha de detalle (2–4 líneas).
 * Explica qué vas a hacer y qué se trabaja; no copy de marketing.
 */
object WorkoutDetailBrief {

    fun forWorkout(workout: WorkoutDetail, locale: Locale = Locale.getDefault()): String {
        val es = locale.language.lowercase(Locale.ROOT).let { it == "es" || it == "ca" }
        val exercises = workout.blocks.flatMap { it.exercises }
        val count = exercises.size
        val focus = workout.targetMuscles.ifBlank { workout.targetDescription }.trim()
        val intensity = workout.intensity.trim().lowercase(Locale.ROOT)
        val typeLine = typeLine(workout.type, es)
        val focusLine = if (es) {
            if (focus.isNotEmpty()) "Enfoque: $focus."
            else "Enfoque según el bloque de ejercicios del plan."
        } else {
            if (focus.isNotEmpty()) "Focus: $focus."
            else "Focus follows the planned exercise blocks."
        }
        val doseLine = if (es) {
            "Sesión de ${workout.durationMinutes} min · $count ejercicios" +
                intensitySuffixEs(intensity) + "."
        } else {
            "${workout.durationMinutes} min session · $count exercises" +
                intensitySuffixEn(intensity) + "."
        }
        val workLine = if (es) {
            "Haz cada ejercicio con control; técnica antes que velocidad."
        } else {
            "Do each exercise with control; form before speed."
        }
        return listOf(typeLine, focusLine, doseLine, workLine).joinToString("\n")
    }

    private fun typeLine(type: WorkoutType, es: Boolean): String = when (type) {
        WorkoutType.STRENGTH -> if (es)
            "Fuerza: series que cargan los grupos musculares principales."
        else
            "Strength: sets that load the main muscle groups."
        WorkoutType.HIIT -> if (es)
            "HIIT: bloques cortos de trabajo con recuperación breve."
        else
            "HIIT: short work blocks with brief recovery."
        WorkoutType.CARDIO -> if (es)
            "Cardio: ritmo sostenido o por bloques para elevar el pulso."
        else
            "Cardio: steady or blocked work to raise heart rate."
        WorkoutType.RECOVERY -> if (es)
            "Recuperación: movilidad y activación suave, sin buscar fatiga."
        else
            "Recovery: easy mobility and activation, not fatigue."
        WorkoutType.PILATES -> if (es)
            "Pilates: core, respiración y alineación en cada repetición."
        else
            "Pilates: core, breathing and alignment on every rep."
        WorkoutType.MOBILITY -> if (es)
            "Movilidad: rango articular controlado en la zona objetivo."
        else
            "Mobility: controlled joint range for the target area."
        WorkoutType.SWIM -> if (es)
            "Natación: brazada y respiración; carga aeróbica de bajo impacto."
        else
            "Swim: stroke and breathing; low-impact aerobic load."
    }

    private fun intensitySuffixEs(intensity: String): String = when {
        "high" in intensity || "alta" in intensity -> " · intensidad alta"
        "low" in intensity || "baja" in intensity -> " · intensidad baja"
        "mod" in intensity || "media" in intensity -> " · intensidad media"
        else -> ""
    }

    private fun intensitySuffixEn(intensity: String): String = when {
        "high" in intensity || "alta" in intensity -> " · high intensity"
        "low" in intensity || "baja" in intensity -> " · low intensity"
        "mod" in intensity || "media" in intensity -> " · moderate intensity"
        else -> ""
    }
}
