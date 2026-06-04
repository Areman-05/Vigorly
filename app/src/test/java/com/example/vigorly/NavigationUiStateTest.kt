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
    fun settings_isSubScreenWithoutBottomBar() {
        val state = NavigationUiState.fromRoute(VigorlyRoutes.Settings)
        assertFalse(state.showBottomBar)
        assertTrue(state.isSubScreen)
        assertTrue(state.showGradientBackground)
    }

    @Test
    fun login_isAuthFlowWithoutGradient() {
        val state = NavigationUiState.fromRoute(VigorlyRoutes.Login)
        assertTrue(state.isAuthFlow)
        assertFalse(state.showGradientBackground)
    }
}
