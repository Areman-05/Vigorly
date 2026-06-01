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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.vigorly.R
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.navigation.AppDestination
import com.example.vigorly.navigation.VigorlyRoutes
import com.example.vigorly.ui.VigorlyViewModel
import com.example.vigorly.ui.auth.LoginScreen
import com.example.vigorly.ui.auth.RegisterScreen
import com.example.vigorly.ui.components.AuthGradientBackground
import com.example.vigorly.ui.components.RouteFallbackScreen
import com.example.vigorly.ui.components.VigorlyBottomBar
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.vigorly.ui.components.ActivityDetailTopBar
import com.example.vigorly.ui.components.VigorlyDetailTopBar
import com.example.vigorly.ui.components.VigorlyMainTopBar
import com.example.vigorly.ui.dashboard.ActivityDetailScreen
import com.example.vigorly.ui.dashboard.DashboardScreen
import com.example.vigorly.ui.history.HistoryDetailScreen
import com.example.vigorly.ui.history.HistoryScreen
import com.example.vigorly.ui.insights.InsightsScreen
import com.example.vigorly.ui.milestones.MilestonesScreen
import com.example.vigorly.ui.profile.ProfileScreen
import com.example.vigorly.ui.session.ActiveWorkoutScreen
import com.example.vigorly.ui.session.SessionSummaryScreen
import com.example.vigorly.ui.settings.SettingsScreen
import com.example.vigorly.ui.setup.SetupWizardScreen
import com.example.vigorly.ui.splash.SplashScreen
import com.example.vigorly.ui.workout.WorkoutDetailScreen
import com.example.vigorly.ui.workout.WorkoutsScreen
import kotlinx.coroutines.flow.collectLatest

@Composable
fun VigorlyApp(
    repository: VigorlyRepository,
    viewModel: VigorlyViewModel
) {
    val workoutCompletedMessage = stringResource(R.string.workout_completed)
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val profile by repository.profile.collectAsState()
    val lastSummary by repository.lastSessionSummary.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.messages.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    val authRoutes = setOf(
        VigorlyRoutes.Splash,
        VigorlyRoutes.Login,
        VigorlyRoutes.Register,
        VigorlyRoutes.Setup
    )
    val mainTabs = listOf(
        VigorlyRoutes.Dashboard,
        VigorlyRoutes.Workouts,
        VigorlyRoutes.History,
        VigorlyRoutes.Profile
    )
    val showBottomBar = currentRoute in mainTabs
    val isAuthFlow = currentRoute in authRoutes
    val isDetailOrSession = currentRoute?.startsWith("workout/") == true ||
        currentRoute?.startsWith("session/") == true
    val isSubScreen = currentRoute in listOf(
        VigorlyRoutes.Settings,
        VigorlyRoutes.Milestones,
        VigorlyRoutes.Insights
    )
    val isActivityDetail = currentRoute == VigorlyRoutes.ActivityDetail
    var showActivityCalendar by remember { mutableStateOf(false) }

    LaunchedEffect(isActivityDetail) {
        if (!isActivityDetail) {
            showActivityCalendar = false
            repository.resetSelectedActivityDateToToday()
        }
    }
    val isSummary = currentRoute == VigorlyRoutes.SessionSummary
    val isHistoryDetail = currentRoute?.startsWith("history/") == true && currentRoute != VigorlyRoutes.History

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
                isAuthFlow || isSummary -> {}
                isActivityDetail && !showActivityCalendar -> ActivityDetailTopBar(
                    onBackClick = { navController.popBackStack() },
                    onCalendarClick = { showActivityCalendar = true }
                )
                isDetailOrSession || isHistoryDetail -> VigorlyDetailTopBar(
                    onBackClick = { navController.popBackStack() },
                    onSettingsClick = { navController.navigate(VigorlyRoutes.Settings) },
                    showBrandTitle = !isDetailOrSession
                )
                isSubScreen -> VigorlyDetailTopBar(
                    onBackClick = { navController.popBackStack() },
                    onSettingsClick = {}
                )
                showBottomBar -> VigorlyMainTopBar(
                    avatarUrl = profile.avatarUrl,
                    onProfileClick = {
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
        },
        bottomBar = {
            if (showBottomBar) {
                VigorlyBottomBar(
                    currentRoute = currentRoute ?: VigorlyRoutes.Dashboard,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
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
            if (showBottomBar || (isActivityDetail && !showActivityCalendar) || isDetailOrSession) {
                AuthGradientBackground(Modifier.fillMaxSize()) {}
            }
            NavHost(
                navController = navController,
                startDestination = VigorlyRoutes.Splash,
                modifier = when {
                    isAuthFlow -> Modifier
                    isActivityDetail && showActivityCalendar -> Modifier.fillMaxSize()
                    else -> Modifier.padding(padding)
                }
            ) {
            composable(VigorlyRoutes.Splash) {
                SplashScreen(
                    repository = repository,
                    onFinished = ::navigateFromSplash
                )
            }
            composable(VigorlyRoutes.Login) {
                LoginScreen(
                    repository = repository,
                    onLoginSuccess = { needsSetup ->
                        val target = if (needsSetup) {
                            VigorlyRoutes.Setup
                        } else {
                            VigorlyRoutes.Dashboard
                        }
                        navController.navigate(target) {
                            popUpTo(VigorlyRoutes.Login) { inclusive = true }
                        }
                    },
                    onNavigateRegister = {
                        navController.navigate(VigorlyRoutes.Register)
                    }
                )
            }
            composable(VigorlyRoutes.Register) {
                RegisterScreen(
                    repository = repository,
                    onRegisterSuccess = {
                        navController.navigate(VigorlyRoutes.Setup) {
                            popUpTo(VigorlyRoutes.Login) { inclusive = true }
                        }
                    },
                    onNavigateLogin = { navController.popBackStack() }
                )
            }
            composable(VigorlyRoutes.Setup) {
                SetupWizardScreen(
                    repository = repository,
                    onComplete = {
                        navController.navigate(VigorlyRoutes.Dashboard) {
                            popUpTo(VigorlyRoutes.Setup) { inclusive = true }
                        }
                    }
                )
            }
            composable(VigorlyRoutes.Dashboard) {
                DashboardScreen(
                    repository = repository,
                    onActivityDetailClick = { navController.navigate(VigorlyRoutes.ActivityDetail) },
                    onRecommendedWorkoutClick = { id ->
                        navController.navigate(VigorlyRoutes.workoutDetail(id))
                    }
                )
            }
            composable(VigorlyRoutes.ActivityDetail) {
                ActivityDetailScreen(
                    repository = repository,
                    showCalendar = showActivityCalendar,
                    onDismissCalendar = { showActivityCalendar = false },
                    onDateSelected = {
                        repository.selectActivityDate(it)
                        showActivityCalendar = false
                    }
                )
            }
            composable(VigorlyRoutes.Workouts) {
                WorkoutsScreen(
                    repository = repository,
                    onWorkoutClick = { id -> navController.navigate(VigorlyRoutes.workoutDetail(id)) }
                )
            }
            composable(VigorlyRoutes.History) {
                HistoryScreen(
                    repository = repository,
                    onHistoryItemClick = { id ->
                        navController.navigate(VigorlyRoutes.historyDetail(id))
                    }
                )
            }
            composable(VigorlyRoutes.Profile) {
                ProfileScreen(
                    repository = repository,
                    onViewAllMilestones = { navController.navigate(VigorlyRoutes.Milestones) },
                    onOpenWorkouts = { navController.navigate(VigorlyRoutes.Workouts) },
                    onOpenInsights = { navController.navigate(VigorlyRoutes.Insights) }
                )
            }
            composable(VigorlyRoutes.Settings) {
                SettingsScreen(
                    repository = repository,
                    onOpenInsights = { navController.navigate(VigorlyRoutes.Insights) },
                    onLogout = {
                        repository.logout()
                        navigateToLogin()
                    }
                )
            }
            composable(VigorlyRoutes.Milestones) {
                MilestonesScreen(repository = repository)
            }
            composable(VigorlyRoutes.Insights) {
                InsightsScreen(repository = repository)
            }
            composable(
                route = VigorlyRoutes.WorkoutDetail,
                arguments = listOf(navArgument("workoutId") { type = NavType.StringType })
            ) { entry ->
                val id = entry.arguments?.getString("workoutId") ?: return@composable
                val workout = repository.getWorkout(id)
                if (workout == null) {
                    RouteFallbackScreen(
                        title = stringResource(R.string.fallback_workout_title),
                        message = stringResource(R.string.fallback_workout_message),
                        onGoBack = { navController.popBackStack() }
                    )
                } else {
                    WorkoutDetailScreen(
                        workout = workout,
                        repository = repository,
                        onStartWorkout = { navController.navigate(VigorlyRoutes.activeSession(id)) }
                    )
                }
            }
            composable(
                route = VigorlyRoutes.ActiveSession,
                arguments = listOf(navArgument("workoutId") { type = NavType.StringType })
            ) { entry ->
                val id = entry.arguments?.getString("workoutId") ?: return@composable
                ActiveWorkoutScreen(
                    repository = repository,
                    workoutId = id,
                    onComplete = {
                        viewModel.showMessage(workoutCompletedMessage)
                        navController.navigate(VigorlyRoutes.SessionSummary) {
                            popUpTo(VigorlyRoutes.activeSession(id)) { inclusive = true }
                        }
                    },
                    onCancel = { navController.popBackStack() }
                )
            }
            composable(VigorlyRoutes.SessionSummary) {
                val summary = lastSummary
                if (summary != null) {
                    SessionSummaryScreen(
                        summary = summary,
                        onDone = {
                            repository.clearSessionSummary()
                            navController.popBackStack(VigorlyRoutes.Dashboard, false)
                        }
                    )
                } else {
                    RouteFallbackScreen(
                        title = stringResource(R.string.fallback_summary_title),
                        message = stringResource(R.string.fallback_summary_message),
                        onGoBack = {
                            navController.popBackStack(VigorlyRoutes.Dashboard, false)
                        }
                    )
                }
            }
            composable(
                route = VigorlyRoutes.HistoryDetail,
                arguments = listOf(navArgument("historyId") { type = NavType.StringType })
            ) { entry ->
                val id = entry.arguments?.getString("historyId") ?: return@composable
                val item = repository.getHistoryItem(id)
                if (item == null) {
                    RouteFallbackScreen(
                        title = stringResource(R.string.fallback_history_title),
                        message = stringResource(R.string.fallback_history_message),
                        onGoBack = { navController.popBackStack() }
                    )
                } else {
                    HistoryDetailScreen(item = item)
                }
            }
        }
        }
    }
}
