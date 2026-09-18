package com.example.vigorly

import com.example.vigorly.navigation.VigorlyRoutes
import com.example.vigorly.presentation.navigation.NavigationUiState
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NavigationUiStateTest {

    @Test
    fun dashboard_showsBottomBarAndGradient() {
        val state = NavigationUiState.fromRoute(VigorlyRoutes.Dashboard)
        assertTrue(state.showBottomBar)
        assertTrue(state.showGradientBackground)
        assertFalse(state.isAuthFlow)
    }

    @Test
    fun milestones_isSubScreenWithoutBottomBar() {
        val state = NavigationUiState.fromRoute(VigorlyRoutes.Milestones)
        assertFalse(state.showBottomBar)
        assertTrue(state.isSubScreen)
        assertTrue(state.showGradientBackground)
    }

    @Test
    fun workoutDetail_isDetailWithoutBottomBar() {
        val state = NavigationUiState.fromRoute(VigorlyRoutes.workoutDetail("hiit_sprint"))
        assertTrue(state.isDetailOrSession)
        assertFalse(state.showBottomBar)
    }

    @Test
    fun activeSession_usesOwnStageBackground() {
        val state = NavigationUiState.fromRoute(VigorlyRoutes.activeSession("hiit_sprint"))
        assertTrue(state.isDetailOrSession)
        assertFalse(state.showGradientBackground)
        assertFalse(state.showBottomBar)
    }

    @Test
    fun sessionSummary_usesOwnStageBackground() {
        val state = NavigationUiState.fromRoute(VigorlyRoutes.SessionSummary)
        assertTrue(state.isSummary)
        assertFalse(state.showGradientBackground)
        assertFalse(state.showBottomBar)
    }

    @Test
    fun login_isAuthFlowWithoutGradient() {
        val state = NavigationUiState.fromRoute(VigorlyRoutes.Login)
        assertTrue(state.isAuthFlow)
        assertFalse(state.showGradientBackground)
    }

    @Test
    fun registerAndSetup_areAuthFlow() {
        val register = NavigationUiState.fromRoute(VigorlyRoutes.Register)
        val setup = NavigationUiState.fromRoute(VigorlyRoutes.Setup)
        assertTrue(register.isAuthFlow)
        assertTrue(setup.isAuthFlow)
        assertFalse(register.showBottomBar)
        assertFalse(setup.showBottomBar)
    }

    @Test
    fun analysisAndProfile_areMainTabs() {
        val analysis = NavigationUiState.fromRoute(VigorlyRoutes.Analysis)
        val profile = NavigationUiState.fromRoute(VigorlyRoutes.Profile)
        assertTrue(analysis.showBottomBar)
        assertTrue(profile.showBottomBar)
        assertTrue(analysis.showGradientBackground)
        assertTrue(profile.showGradientBackground)
    }

    @Test
    fun history_isSubScreenWithGradient() {
        val state = NavigationUiState.fromRoute(VigorlyRoutes.History)
        assertTrue(state.isSubScreen)
        assertFalse(state.showBottomBar)
        assertTrue(state.showGradientBackground)
        assertFalse(state.isHistoryDetail)
    }

    @Test
    fun historyDetail_isDetailWithGradient() {
        val state = NavigationUiState.fromRoute(VigorlyRoutes.historyDetail("abc"))
        assertTrue(state.isHistoryDetail)
        assertFalse(state.showBottomBar)
        assertTrue(state.showGradientBackground)
    }

    @Test
    fun activityDetail_opensCalendarShell() {
        val detail = NavigationUiState.fromRoute(VigorlyRoutes.ActivityDetail)
        val metric = NavigationUiState.fromRoute(VigorlyRoutes.activityMetric("move"))
        assertTrue(detail.isActivityDetail)
        assertTrue(metric.isActivityMetricDetail)
        assertTrue(detail.showGradientBackground)
        assertTrue(metric.showGradientBackground)
    }
}
