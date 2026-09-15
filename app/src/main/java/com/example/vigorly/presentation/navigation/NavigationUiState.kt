package com.example.vigorly.presentation.navigation

import com.example.vigorly.navigation.VigorlyDestinationGroups
import com.example.vigorly.navigation.VigorlyRoutes

data class NavigationUiState(
    val currentRoute: String?,
    val showBottomBar: Boolean,
    val isAuthFlow: Boolean,
    val isDetailOrSession: Boolean,
    val isSubScreen: Boolean,
    val isActivityDetail: Boolean,
    val isActivityMetricDetail: Boolean,
    val isSummary: Boolean,
    val isHistoryDetail: Boolean,
    val showGradientBackground: Boolean
) {
    companion object {
        fun fromRoute(route: String?): NavigationUiState {
            val showBottomBar = route in VigorlyDestinationGroups.MAIN_TABS
            val isAuthFlow = route in VigorlyDestinationGroups.AUTH
            val isDetailOrSession = route?.startsWith("workout/") == true ||
                route?.startsWith("session/") == true
            val isSubScreen = route in VigorlyDestinationGroups.SECONDARY
            val isActivityDetail = route == VigorlyRoutes.ActivityDetail
            val isActivityMetricDetail = route?.startsWith("activity/metric/") == true
            val isSummary = route == VigorlyRoutes.SessionSummary
            val isHistoryDetail = route?.startsWith("history/") == true &&
                route != VigorlyRoutes.History
            val showGradientBackground = showBottomBar ||
                isSubScreen ||
                isActivityDetail ||
                isActivityMetricDetail ||
                (isDetailOrSession && route?.startsWith("session/") == true) ||
                isSummary ||
                isHistoryDetail

            return NavigationUiState(
                currentRoute = route,
                showBottomBar = showBottomBar,
                isAuthFlow = isAuthFlow,
                isDetailOrSession = isDetailOrSession,
                isSubScreen = isSubScreen,
                isActivityDetail = isActivityDetail,
                isActivityMetricDetail = isActivityMetricDetail,
                isSummary = isSummary,
                isHistoryDetail = isHistoryDetail,
                showGradientBackground = showGradientBackground
            )
        }
    }
}
