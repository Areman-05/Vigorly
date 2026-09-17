package com.example.vigorly.navigation

/**
 * Mapa de navegación de la app (Compose Navigation).
 *
 * Flujo de autenticación: Splash → Login | Register → Setup → pestañas principales.
 * Pestañas principales: Dashboard, Workouts, Analysis, Profile.
 * Pantallas secundarias: History, Milestones, ActivityDetail,
 * WorkoutDetail, ActiveSession, SessionSummary, HistoryDetail.
 */
object VigorlyDestinationGroups {
    val AUTH = setOf(
        VigorlyRoutes.Splash,
        VigorlyRoutes.Login,
        VigorlyRoutes.Register,
        VigorlyRoutes.Setup
    )

    val MAIN_TABS = listOf(
        VigorlyRoutes.Dashboard,
        VigorlyRoutes.Workouts,
        VigorlyRoutes.Analysis,
        VigorlyRoutes.Profile
    )

    val SECONDARY = setOf(
        VigorlyRoutes.Milestones,
        VigorlyRoutes.History
    )
}
