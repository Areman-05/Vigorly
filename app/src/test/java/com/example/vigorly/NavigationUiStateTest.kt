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
}
