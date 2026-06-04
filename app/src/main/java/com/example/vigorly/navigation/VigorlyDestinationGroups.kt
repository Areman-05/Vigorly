package com.example.vigorly.navigation

/**
 * Mapa de navegación de la app (Compose Navigation).
 *
 * Flujo de autenticación: Splash → Login | Register → Setup → pestañas principales.
 * Pestañas principales: Dashboard, Workouts, History, Profile.
 * Pantallas secundarias: Settings, Milestones, Insights, ActivityDetail,
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
        VigorlyRoutes.History,
        VigorlyRoutes.Profile
    )

    val SECONDARY = setOf(
        VigorlyRoutes.Settings,
        VigorlyRoutes.Milestones,
        VigorlyRoutes.Insights
    )
}
