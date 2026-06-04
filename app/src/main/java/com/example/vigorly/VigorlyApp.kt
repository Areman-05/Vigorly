package com.example.vigorly

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.vigorly.R
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.navigation.AppDestination
import com.example.vigorly.navigation.VigorlyDestinationGroups
import com.example.vigorly.navigation.VigorlyRoutes
import com.example.vigorly.presentation.app.AppViewModel
import com.example.vigorly.presentation.navigation.NavigationUiState
import com.example.vigorly.presentation.navigation.vigorlyNavGraph
import com.example.vigorly.ui.components.ActivityDetailTopBar
import com.example.vigorly.ui.components.AuthGradientBackground
import com.example.vigorly.ui.components.VigorlyBottomBar
import com.example.vigorly.ui.components.VigorlyDetailTopBar
import com.example.vigorly.ui.components.VigorlyMainTopBar
import kotlinx.coroutines.flow.collectLatest

@Composable
fun VigorlyApp(
    repository: VigorlyRepository,
    appViewModel: AppViewModel
) {
    val workoutCompletedMessage = stringResource(R.string.workout_completed)
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val navState = remember(currentRoute) { NavigationUiState.fromRoute(currentRoute) }
    val snackbarHostState = remember { SnackbarHostState() }
    var showActivityCalendar by remember { mutableStateOf(false) }

    LaunchedEffect(appViewModel) {
        appViewModel.messages.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    LaunchedEffect(navState.isActivityDetail) {
        if (!navState.isActivityDetail) {
            showActivityCalendar = false
            repository.resetSelectedActivityDateToToday()
        }
    }

    fun navigateFromSplash(destination: AppDestination) {
        val route = when (destination) {
            AppDestination.Login -> VigorlyRoutes.Login
            AppDestination.Setup -> VigorlyRoutes.Setup
            AppDestination.Main -> VigorlyRoutes.Dashboard
            AppDestination.Splash -> return
            AppDestination.Register -> VigorlyRoutes.Register
        }
        navController.navigate(route) {
            popUpTo(VigorlyRoutes.Splash) { inclusive = true }
        }
    }

    fun navigateToLogin() {
        navController.navigate(VigorlyRoutes.Login) {
            popUpTo(0) { inclusive = true }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            when {
                navState.isAuthFlow || navState.isSummary -> {}
                navState.isActivityDetail && !showActivityCalendar -> ActivityDetailTopBar(
                    onBackClick = { navController.popBackStack() },
                    onCalendarClick = { showActivityCalendar = true }
                )
                navState.isDetailOrSession || navState.isHistoryDetail -> VigorlyDetailTopBar(
                    onBackClick = { navController.popBackStack() },
                    onSettingsClick = { navController.navigate(VigorlyRoutes.Settings) },
                    showBrandTitle = !navState.isDetailOrSession && !navState.isHistoryDetail,
                    showSettingsAction = !navState.isDetailOrSession && !navState.isHistoryDetail
                )
                navState.isSubScreen -> VigorlyDetailTopBar(
                    onBackClick = { navController.popBackStack() },
                    onSettingsClick = {
                        if (navState.currentRoute != VigorlyRoutes.Milestones &&
                            navState.currentRoute != VigorlyRoutes.Insights &&
                            navState.currentRoute != VigorlyRoutes.Settings
                        ) {
                            navController.navigate(VigorlyRoutes.Settings)
                        }
                    },
                    showBrandTitle = navState.currentRoute != VigorlyRoutes.Milestones &&
                        navState.currentRoute != VigorlyRoutes.Insights &&
                        navState.currentRoute != VigorlyRoutes.Settings,
                    showSettingsAction = navState.currentRoute != VigorlyRoutes.Milestones &&
                        navState.currentRoute != VigorlyRoutes.Insights &&
                        navState.currentRoute != VigorlyRoutes.Settings
                )
                navState.showBottomBar -> VigorlyMainTopBar(
                    onSettingsClick = { navController.navigate(VigorlyRoutes.Settings) }
                )
            }
        },
        bottomBar = {
            if (navState.showBottomBar) {
                VigorlyBottomBar(
                    currentRoute = navState.currentRoute ?: VigorlyRoutes.Dashboard,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize()) {
            if (navState.showGradientBackground) {
                AuthGradientBackground(Modifier.fillMaxSize()) {}
            }
            val contentModifier = when {
                navState.isAuthFlow -> Modifier
                navState.isActivityDetail && showActivityCalendar -> Modifier.fillMaxSize()
                else -> Modifier.padding(padding)
            }
            NavHost(
                navController = navController,
                startDestination = VigorlyRoutes.Splash,
                modifier = contentModifier
            ) {
                vigorlyNavGraph(
                    navController = navController,
                    repository = repository,
                    appViewModel = appViewModel,
                    showActivityCalendar = showActivityCalendar,
                    onShowActivityCalendarChange = { showActivityCalendar = it },
                    onNavigateFromSplash = ::navigateFromSplash,
                    onNavigateToLogin = ::navigateToLogin,
                    workoutCompletedMessage = workoutCompletedMessage,
                    contentPaddingModifier = Modifier
                )
            }
        }
    }
}
