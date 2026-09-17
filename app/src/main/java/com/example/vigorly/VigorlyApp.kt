package com.example.vigorly

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.vigorly.R
import com.example.vigorly.core.testing.UiTestEnvironment
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.navigation.AppDestination
import com.example.vigorly.navigation.VigorlyRoutes
import com.example.vigorly.presentation.app.AppViewModel
import com.example.vigorly.presentation.navigation.NavigationUiState
import com.example.vigorly.presentation.navigation.vigorlyNavGraph
import com.example.vigorly.ui.components.ActivityDetailTopBar
import com.example.vigorly.ui.components.AuthGradientBackground
import com.example.vigorly.ui.components.MainShellBackground
import com.example.vigorly.ui.components.VigorlyBottomBar
import com.example.vigorly.ui.components.VigorlyDetailTopBar
import com.example.vigorly.ui.components.VigorlyMainTopBar
import com.example.vigorly.ui.splash.SplashScreen
import com.example.vigorly.ui.theme.Background
import kotlinx.coroutines.flow.collectLatest

@Composable
fun VigorlyApp(
    repository: VigorlyRepository,
    appViewModel: AppViewModel
) {
    var startDestination by remember { mutableStateOf<String?>(null) }

    if (startDestination == null) {
        BackHandler { }
        SplashScreen(
            repository = repository,
            onFinished = { destination ->
                startDestination = when (destination) {
                    AppDestination.Setup -> VigorlyRoutes.Setup
                    AppDestination.Main -> VigorlyRoutes.Dashboard
                    AppDestination.Register -> VigorlyRoutes.Register
                    AppDestination.Login, AppDestination.Splash -> VigorlyRoutes.Login
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    VigorlyMainNavigation(
        startDestination = startDestination!!,
        repository = repository,
        appViewModel = appViewModel
    )
}

@Composable
private fun VigorlyMainNavigation(
    startDestination: String,
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
    var hideBottomBarOverlay by remember { mutableStateOf(false) }
    val isLoggedIn by repository.isLoggedIn.collectAsState()
    val layoutDirection = LocalLayoutDirection.current
    val activity = LocalContext.current.findActivity()

    BackHandler(enabled = isLoggedIn && navState.showBottomBar) {
        activity?.moveTaskToBack(true)
    }

    LaunchedEffect(isLoggedIn, currentRoute) {
        if (!UiTestEnvironment.isInstrumentedTest || !isLoggedIn) return@LaunchedEffect
        if (currentRoute == VigorlyRoutes.Login) {
            navController.navigate(VigorlyRoutes.Dashboard) {
                popUpTo(VigorlyRoutes.Login) { inclusive = false }
                launchSingleTop = true
            }
        }
    }

    LaunchedEffect(appViewModel) {
        appViewModel.messages.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    LaunchedEffect(navState.currentRoute) {
        if (navState.currentRoute != VigorlyRoutes.Workouts) {
            hideBottomBarOverlay = false
        }
    }

    LaunchedEffect(navState.isActivityDetail, navState.isActivityMetricDetail) {
        if (!navState.isActivityDetail && !navState.isActivityMetricDetail) {
            showActivityCalendar = false
            repository.resetSelectedActivityDateToToday()
        }
    }

    fun navigateToLogin() {
        if (navController.currentDestination?.route == VigorlyRoutes.Login) return
        if (startDestination == VigorlyRoutes.Login) {
            runCatching { navController.popBackStack(VigorlyRoutes.Login, false) }
            return
        }
        runCatching {
            navController.navigate(VigorlyRoutes.Login) {
                popUpTo(startDestination) { inclusive = true }
                launchSingleTop = true
            }
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
                navState.isActivityMetricDetail -> {}
                navState.isDetailOrSession && currentRoute?.startsWith("workout/") == true -> {}
                navState.isDetailOrSession &&
                    currentRoute?.startsWith("session/") == true &&
                    !navState.isSummary -> {}
                navState.isDetailOrSession || navState.isHistoryDetail -> VigorlyDetailTopBar(
                    onBackClick = { navController.popBackStack() },
                    onSettingsClick = {
                        navController.navigate(VigorlyRoutes.Profile) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    showBrandTitle = !navState.isDetailOrSession && !navState.isHistoryDetail,
                    showSettingsAction = !navState.isDetailOrSession && !navState.isHistoryDetail
                )
                navState.isSubScreen -> VigorlyDetailTopBar(
                    onBackClick = { navController.popBackStack() },
                    onSettingsClick = {
                        navController.navigate(VigorlyRoutes.Profile) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    showBrandTitle = navState.currentRoute != VigorlyRoutes.Milestones &&
                        navState.currentRoute != VigorlyRoutes.History,
                    showSettingsAction = navState.currentRoute != VigorlyRoutes.Milestones &&
                        navState.currentRoute != VigorlyRoutes.History
                )
                navState.showBottomBar &&
                    navState.currentRoute != VigorlyRoutes.Dashboard &&
                    navState.currentRoute != VigorlyRoutes.Workouts &&
                    navState.currentRoute != VigorlyRoutes.Analysis &&
                    navState.currentRoute != VigorlyRoutes.Profile ->
                    VigorlyMainTopBar(
                        onSettingsClick = {
                            navController.navigate(VigorlyRoutes.Profile) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize()) {
            when {
                navState.isAuthFlow -> AuthGradientBackground(Modifier.fillMaxSize()) {}
                navState.showGradientBackground -> MainShellBackground(Modifier.fillMaxSize())
                else -> Box(Modifier.fillMaxSize().background(Background))
            }

            val contentPadding = if (navState.showBottomBar) {
                PaddingValues(
                    start = padding.calculateStartPadding(layoutDirection),
                    end = padding.calculateEndPadding(layoutDirection),
                    top = padding.calculateTopPadding(),
                    bottom = 0.dp
                )
            } else {
                padding
            }

            val navHostModifier = when {
                navState.isAuthFlow -> Modifier
                navState.isActivityDetail && showActivityCalendar -> Modifier.fillMaxSize()
                else -> Modifier
            }
            val screenPaddingModifier = when {
                navState.isAuthFlow -> Modifier
                navState.isActivityDetail && showActivityCalendar -> Modifier.fillMaxSize()
                else -> Modifier.padding(contentPadding)
            }

            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = navHostModifier
            ) {
                vigorlyNavGraph(
                    navController = navController,
                    repository = repository,
                    appViewModel = appViewModel,
                    showActivityCalendar = showActivityCalendar,
                    onShowActivityCalendarChange = { showActivityCalendar = it },
                    onNavigateToLogin = ::navigateToLogin,
                    workoutCompletedMessage = workoutCompletedMessage,
                    contentPaddingModifier = screenPaddingModifier,
                    onWorkoutsFilterOverlayChange = { hideBottomBarOverlay = it }
                )
            }

            if (navState.showBottomBar && !hideBottomBarOverlay) {
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
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                )
            }
        }
    }
}

private fun Context.findActivity(): Activity? {
    var ctx: Context? = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return ctx as? Activity
}
