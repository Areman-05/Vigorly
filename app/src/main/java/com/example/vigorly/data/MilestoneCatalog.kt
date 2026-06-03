package com.example.vigorly.data

import com.example.vigorly.data.model.Milestone

object MilestoneCatalog {
    fun all(): List<Milestone> = listOf(
        Milestone("first_workout", "Primer", "Paso", "fitness_center", false),
        Milestone("workouts_5", "5", "Sesiones", "directions_walk", false),
        Milestone("workouts_10", "10", "Sesiones", "directions_run", false),
        Milestone("workouts_25", "25", "Sesiones", "timer", false),
        Milestone("workouts_50", "50", "Sesiones", "trending_up", false),
        Milestone("workouts_75", "75", "Sesiones", "bolt", false),
        Milestone("workouts_100", "100", "Sesiones", "emoji_events", false),
        Milestone("workouts_150", "150", "Sesiones", "star", false),
        Milestone("workouts_200", "10 ton", "Levantado", "fitness_center", false),
        Milestone("workouts_250", "250", "Sesiones", "military_tech", false),
        Milestone("workouts_300", "300", "Club", "workspace_premium", false),
        Milestone("streak_3", "3 días", "Racha", "local_fire_department", false),
        Milestone("streak_7", "7 días", "Racha", "local_fire_department", false),
        Milestone("streak_14", "14 días", "Racha", "local_fire_department", false),
        Milestone("streak_30", "30 días", "Racha", "local_fire_department", false),
        Milestone("streak_60", "60 días", "Racha", "whatshot", false),
        Milestone("streak_100", "100 días", "Racha", "local_fire_department", false),
        Milestone("level_2", "Nivel 2", "Alcanzado", "favorite", false),
        Milestone("level_4", "Nivel 4", "Alcanzado", "favorite", false),
        Milestone("level_6", "Nivel 6", "Alcanzado", "favorite", false),
        Milestone("level_8", "Nivel 8", "Alcanzado", "favorite", false),
        Milestone("level_10", "Nivel 10", "Máximo", "emoji_events", false),
        Milestone("pro_member", "Pro", "Miembro", "workspace_premium", false),
        Milestone("elite", "Élite", "Estado", "emoji_events", false),
        Milestone("run_5k", "Sub 20", "5 km", "timer", false),
        Milestone("consistency_20", "20", "Sesiones", "self_improvement", false),
        Milestone("consistency_40", "40", "Sesiones", "pool", false),
        Milestone("streak_21", "21 días", "Racha", "whatshot", false),
        Milestone("workouts_125", "125", "Sesiones", "directions_run", false),
        Milestone("workouts_175", "175", "Sesiones", "directions_run", false)
    )
}

object MilestoneHints {
    fun hint(id: String): String? = when (id) {
        "first_workout" -> "Completa tu primer entrenamiento"
        "workouts_5" -> "Registra 5 entrenamientos"
        "workouts_10" -> "Registra 10 entrenamientos"
        "workouts_25" -> "Registra 25 entrenamientos"
        "workouts_50" -> "Registra 50 entrenamientos"
        "workouts_75" -> "Registra 75 entrenamientos"
        "workouts_100" -> "Registra 100 entrenamientos"
        "workouts_125" -> "Registra 125 entrenamientos"
        "workouts_150" -> "Registra 150 entrenamientos"
        "workouts_175" -> "Registra 175 entrenamientos"
        "workouts_200" -> "Registra 200 entrenamientos"
        "workouts_250" -> "Registra 250 entrenamientos"
        "workouts_300" -> "Registra 300 entrenamientos"
        "consistency_20" -> "Registra 20 entrenamientos"
        "consistency_40" -> "Registra 40 entrenamientos"
        "streak_3" -> "Mantén una racha de 3 días"
        "streak_7" -> "Mantén una racha de 7 días"
        "streak_14" -> "Mantén una racha de 14 días"
        "streak_21" -> "Mantén una racha de 21 días"
        "streak_30" -> "Mantén una racha de 30 días"
        "streak_60" -> "Mantén una racha de 60 días"
        "streak_100" -> "Consigue una racha de 100 días"
        "level_2" -> "Alcanza el nivel 2"
        "level_4" -> "Alcanza el nivel 4"
        "level_6" -> "Alcanza el nivel 6"
        "level_8" -> "Alcanza el nivel 8"
        "level_10" -> "Alcanza el nivel 10"
        "pro_member" -> "Activa la membresía Pro"
        "elite" -> "Nivel 10 y membresía Pro"
        "run_5k" -> "Completa 150 entrenamientos"
        else -> null
    }
}
