package com.example.vigorly.util

import com.example.vigorly.data.model.WorkoutType
import java.text.Normalizer

/**
 * Asistente local: interpreta peticiones en español y devuelve filtros para la lista.
 */
object WorkoutAssistantEngine {

    data class Result(
        val searchQuery: String = "",
        val type: WorkoutType? = null,
        val sort: WorkoutSort? = null,
        val favoritesOnly: Boolean = false,
        val maxDurationMinutes: Int? = null,
        val minDurationMinutes: Int? = null,
        val highIntensityOnly: Boolean = false,
        val lowIntensityOnly: Boolean = false,
        val summaryKey: SummaryKey = SummaryKey.GENERIC
    ) {
        enum class SummaryKey {
            GENERIC,
            RECOVERY,
            HIIT_SHORT,
            LEGS,
            STRENGTH,
            CARDIO,
            SWIM,
            FAVORITES,
            CORE,
            BEGINNER
        }
    }

    enum class QuickPreset {
        RECOVERY,
        HIIT_SHORT,
        LEGS,
        STRENGTH,
        SWIM_LOW_IMPACT,
        FAVORITES,
        CORE,
        BEGINNER
    }

    fun fromPreset(preset: QuickPreset): Result = when (preset) {
        QuickPreset.RECOVERY -> Result(
            type = WorkoutType.RECOVERY,
            lowIntensityOnly = true,
            sort = WorkoutSort.DURATION_ASC,
            summaryKey = Result.SummaryKey.RECOVERY
        )
        QuickPreset.HIIT_SHORT -> Result(
            type = WorkoutType.HIIT,
            maxDurationMinutes = 30,
            highIntensityOnly = true,
            sort = WorkoutSort.DURATION_ASC,
            summaryKey = Result.SummaryKey.HIIT_SHORT
        )
        QuickPreset.LEGS -> Result(
            searchQuery = "piernas glúteos",
            sort = WorkoutSort.DURATION_ASC,
            summaryKey = Result.SummaryKey.LEGS
        )
        QuickPreset.STRENGTH -> Result(
            type = WorkoutType.STRENGTH,
            sort = WorkoutSort.NAME_ASC,
            summaryKey = Result.SummaryKey.STRENGTH
        )
        QuickPreset.SWIM_LOW_IMPACT -> Result(
            type = WorkoutType.SWIM,
            summaryKey = Result.SummaryKey.SWIM
        )
        QuickPreset.FAVORITES -> Result(
            favoritesOnly = true,
            summaryKey = Result.SummaryKey.FAVORITES
        )
        QuickPreset.CORE -> Result(
            searchQuery = "core abdomen",
            summaryKey = Result.SummaryKey.CORE
        )
        QuickPreset.BEGINNER -> Result(
            maxDurationMinutes = 35,
            lowIntensityOnly = true,
            sort = WorkoutSort.DURATION_ASC,
            summaryKey = Result.SummaryKey.BEGINNER
        )
    }

    fun parse(input: String): Result {
        val text = normalize(input)
        if (text.isBlank()) return Result()

        var result = Result()
        if (containsAny(text, "favorit", "guardad", "mis entreno")) {
            return fromPreset(QuickPreset.FAVORITES)
        }

        if (containsAny(text, "recuper", "descans", "movilidad", "estiramiento", "yoga", "suave", "relaj")) {
            result = result.copy(
                type = WorkoutType.RECOVERY,
                lowIntensityOnly = true,
                summaryKey = Result.SummaryKey.RECOVERY
            )
        }

        if (containsAny(text, "hiit", "tabata", "interval", "explosiv", "sprint", "quemar", "grasa")) {
            result = result.copy(
                type = result.type ?: WorkoutType.HIIT,
                highIntensityOnly = result.highIntensityOnly ||
                    containsAny(text, "quemar", "grasa", "intens", "fuerte"),
                summaryKey = if (result.summaryKey == Result.SummaryKey.GENERIC) {
                    Result.SummaryKey.HIIT_SHORT
                } else result.summaryKey
            )
        }

        if (containsAny(text, "natacion", "nado", "piscina", "agua")) {
            result = result.copy(type = WorkoutType.SWIM, summaryKey = Result.SummaryKey.SWIM)
        }

        if (containsAny(text, "cardio", "correr", "carrera", "bicicleta", "ciclismo", "caminata", "aerobic")) {
            result = result.copy(
                type = result.type ?: WorkoutType.CARDIO,
                summaryKey = if (result.type == null) Result.SummaryKey.CARDIO else result.summaryKey
            )
        }

        if (containsAny(text, "fuerza", "pesas", "musculo", "hipertrof", "press", "sentadilla", "levantamiento")) {
            result = result.copy(
                type = result.type ?: WorkoutType.STRENGTH,
                summaryKey = if (result.summaryKey == Result.SummaryKey.GENERIC) {
                    Result.SummaryKey.STRENGTH
                } else result.summaryKey
            )
        }

        if (containsAny(text, "pierna", "gluteo", "cuadriceps", "isquio", "tren inferior")) {
            val legQuery = buildString {
                append("piernas")
                if (containsAny(text, "gluteo")) append(" glúteos")
            }
            result = result.copy(
                searchQuery = mergeQuery(result.searchQuery, legQuery),
                summaryKey = Result.SummaryKey.LEGS
            )
        }

        if (containsAny(text, "core", "abdomen", "abdominal", "tronco", "cintura")) {
            result = result.copy(
                searchQuery = mergeQuery(result.searchQuery, "core abdomen"),
                summaryKey = Result.SummaryKey.CORE
            )
        }

        if (containsAny(text, "espalda", "dorsal", "tiron", "dominadas")) {
            result = result.copy(searchQuery = mergeQuery(result.searchQuery, "espalda"))
        }

        if (containsAny(text, "hombro", "brazo", "pecho", "triceps", "biceps")) {
            val part = when {
                containsAny(text, "hombro") -> "hombros"
                containsAny(text, "pecho") -> "pecho"
                containsAny(text, "brazo", "biceps", "triceps") -> "brazos"
                else -> ""
            }
            if (part.isNotEmpty()) {
                result = result.copy(searchQuery = mergeQuery(result.searchQuery, part))
            }
        }

        if (containsAny(text, "principiante", "empezar", "facil", "suave", "poco tiempo", "corto", "rapido")) {
            result = result.copy(
                maxDurationMinutes = result.maxDurationMinutes ?: 35,
                lowIntensityOnly = result.lowIntensityOnly ||
                    containsAny(text, "principiante", "facil", "suave"),
                sort = result.sort ?: WorkoutSort.DURATION_ASC,
                summaryKey = if (result.summaryKey == Result.SummaryKey.GENERIC) {
                    Result.SummaryKey.BEGINNER
                } else result.summaryKey
            )
        }

        if (containsAny(text, "intens", "duro", "exigente", "alta intensidad")) {
            result = result.copy(highIntensityOnly = true)
        }

        if (containsAny(text, "largo", "larga", "mas de", "hora")) {
            result = result.copy(
                minDurationMinutes = result.minDurationMinutes ?: 45,
                sort = result.sort ?: WorkoutSort.DURATION_DESC
            )
        }

        val durationShort = Regex("""(\d+)\s*(min|minutos?)""").find(text)
        if (durationShort != null) {
            val mins = durationShort.groupValues[1].toIntOrNull()
            if (mins != null) {
                result = result.copy(maxDurationMinutes = mins)
            }
        } else if (containsAny(text, "corto", "breve", "poco tiempo", "express")) {
            result = result.copy(
                maxDurationMinutes = result.maxDurationMinutes ?: 30,
                sort = result.sort ?: WorkoutSort.DURATION_ASC
            )
        }

        if (result.type == null && result.searchQuery.isBlank() && !result.favoritesOnly &&
            !result.highIntensityOnly && !result.lowIntensityOnly &&
            result.maxDurationMinutes == null && result.minDurationMinutes == null
        ) {
            result = result.copy(searchQuery = input.trim())
        }

        return result
    }

    fun matchesConstraints(
        durationMinutes: Int,
        intensity: String,
        filters: Result
    ): Boolean {
        if (filters.maxDurationMinutes != null && durationMinutes > filters.maxDurationMinutes) return false
        if (filters.minDurationMinutes != null && durationMinutes < filters.minDurationMinutes) return false
        if (filters.highIntensityOnly && !WorkoutLabels.intensityIsHigh(intensity)) return false
        if (filters.lowIntensityOnly) {
            val key = normalize(intensity)
            if (WorkoutLabels.intensityIsHigh(intensity)) return false
            if (key !in listOf("baja", "bajo", "low", "moderada", "moderado", "moderate", "media")) return false
        }
        return true
    }

    private fun mergeQuery(existing: String, extra: String): String {
        if (existing.isBlank()) return extra
        if (existing.contains(extra, ignoreCase = true)) return existing
        return "$existing $extra"
    }

    private fun containsAny(text: String, vararg needles: String): Boolean =
        needles.any { text.contains(it) }

    private fun normalize(text: String): String =
        Normalizer.normalize(text.trim().lowercase(), Normalizer.Form.NFD)
            .replace(Regex("\\p{M}+"), "")
}
