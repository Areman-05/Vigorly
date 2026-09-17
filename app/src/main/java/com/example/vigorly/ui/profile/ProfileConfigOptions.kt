package com.example.vigorly.ui.profile

import androidx.annotation.StringRes
import com.example.vigorly.R

object ProfileConfigOptions {
    data class Choice(
        val key: String,
        @StringRes val titleRes: Int,
        @StringRes val subtitleRes: Int
    )

    val fitnessGoals = listOf(
        Choice("strength", R.string.setup_goal_strength, R.string.setup_goal_strength_desc),
        Choice("cardio", R.string.setup_goal_cardio, R.string.setup_goal_cardio_desc),
        Choice("weight", R.string.setup_goal_weight, R.string.setup_goal_weight_desc),
        Choice("muscle", R.string.setup_goal_muscle, R.string.setup_goal_muscle_desc),
        Choice("endurance", R.string.setup_goal_endurance, R.string.setup_goal_endurance_desc),
        Choice("flexibility", R.string.setup_goal_flexibility, R.string.setup_goal_flexibility_desc),
        Choice("wellness", R.string.setup_goal_wellness, R.string.setup_goal_wellness_desc)
    )

    val activityLevels = listOf(
        Choice("sedentary", R.string.setup_activity_sedentary, R.string.setup_activity_sedentary_desc),
        Choice("light", R.string.setup_activity_light, R.string.setup_activity_light_desc),
        Choice("moderate", R.string.setup_activity_moderate, R.string.setup_activity_moderate_desc),
        Choice("active", R.string.setup_activity_active, R.string.setup_activity_active_desc),
        Choice("athlete", R.string.setup_activity_athlete, R.string.setup_activity_athlete_desc)
    )

    val locations = listOf(
        Choice("gym", R.string.setup_location_gym, R.string.setup_location_gym_desc),
        Choice("home", R.string.setup_location_home, R.string.setup_location_home_desc),
        Choice("outdoor", R.string.setup_location_outdoor, R.string.setup_location_outdoor_desc),
        Choice("mixed", R.string.setup_location_mixed, R.string.setup_location_mixed_desc)
    )

    val preferredTimes = listOf(
        Choice("morning", R.string.setup_time_morning, R.string.setup_time_morning_desc),
        Choice("midday", R.string.setup_time_midday, R.string.setup_time_midday_desc),
        Choice("afternoon", R.string.setup_time_afternoon, R.string.setup_time_afternoon_desc),
        Choice("evening", R.string.setup_time_evening, R.string.setup_time_evening_desc),
        Choice("flexible", R.string.setup_time_flexible, R.string.setup_time_flexible_desc)
    )

    val workoutCategories = listOf(
        Choice("strength", R.string.workout_type_strength, R.string.profile_pref_type_strength_desc),
        Choice("hiit", R.string.workout_type_hiit, R.string.profile_pref_type_hiit_desc),
        Choice("cardio", R.string.workout_type_cardio, R.string.profile_pref_type_cardio_desc),
        Choice("recovery", R.string.workout_type_yoga, R.string.profile_pref_type_recovery_desc),
        Choice("pilates", R.string.workout_type_pilates, R.string.profile_pref_type_pilates_desc),
        Choice("mobility", R.string.workout_type_mobility, R.string.profile_pref_type_mobility_desc),
        Choice("swim", R.string.workout_type_swim, R.string.profile_pref_type_swim_desc)
    )

    val intensities = listOf(
        Choice("low", R.string.intensity_low, R.string.profile_pref_intensity_low_desc),
        Choice("moderate", R.string.intensity_moderate, R.string.profile_pref_intensity_moderate_desc),
        Choice("high", R.string.intensity_high, R.string.profile_pref_intensity_high_desc)
    )

    val durations = listOf(
        Choice("short", R.string.workout_duration_short, R.string.profile_pref_duration_short_desc),
        Choice("medium", R.string.workout_duration_medium, R.string.profile_pref_duration_medium_desc),
        Choice("long", R.string.workout_duration_long, R.string.profile_pref_duration_long_desc)
    )

    data class Language(
        val code: String,
        @StringRes val labelRes: Int,
        val flagEmoji: String? = null
    )

    val languages = listOf(
        Language("es", R.string.lang_es, "🇪🇸"),
        Language("ca", R.string.lang_ca, null), // Senyera custom — no Andorra
        Language("en", R.string.lang_en, "🇬🇧"),
        Language("fr", R.string.lang_fr, "🇫🇷"),
        Language("de", R.string.lang_de, "🇩🇪")
    )

    fun parseCsv(raw: String): Set<String> =
        raw.split(',').map { it.trim() }.filter { it.isNotEmpty() }.toSet()

    fun toCsv(keys: Set<String>): String = keys.joinToString(",")

    fun firstKey(raw: String): String = parseCsv(raw).firstOrNull().orEmpty()

    fun labelFor(choices: List<Choice>, raw: String, fallback: String = "—"): String {
        val keys = parseCsv(raw)
        if (keys.isEmpty()) return fallback
        return keys.joinToString(", ") { key ->
            choices.find { it.key == key }?.key ?: key
        }
    }
}
