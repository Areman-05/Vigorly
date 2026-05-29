package com.example.vigorly

import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.res.stringResource
import com.example.vigorly.R
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.navigation.VigorlyRoutes
import com.example.vigorly.ui.components.VigorlyBottomBar
import com.example.vigorly.ui.components.VigorlyDetailTopBar
import com.example.vigorly.ui.components.VigorlyMainTopBar
import com.example.vigorly.ui.dashboard.DashboardScreen
import com.example.vigorly.ui.history.HistoryDetailScreen
import com.example.vigorly.ui.history.HistoryScreen
import com.example.vigorly.ui.insights.InsightsScreen
import com.example.vigorly.ui.milestones.MilestonesScreen
import com.example.vigorly.ui.onboarding.OnboardingScreen
import com.example.vigorly.ui.profile.ProfileScreen
import com.example.vigorly.ui.session.ActiveWorkoutScreen
import com.example.vigorly.ui.session.SessionSummaryScreen
import com.example.vigorly.ui.settings.SettingsScreen
import com.example.vigorly.ui.workout.WorkoutDetailScreen
import com.example.vigorly.ui.workout.WorkoutsScreen
import com.example.vigorly.ui.VigorlyViewModel
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
    val onboardingCompleted by repository.onboardingCompleted.collectAsState()
    val lastSummary by repository.lastSessionSummary.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(onboardingCompleted) {
        if (!onboardingCompleted && currentRoute != VigorlyRoutes.Onboarding) {
            navController.navigate(VigorlyRoutes.Onboarding) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.messages.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    val mainTabs = listOf(
        VigorlyRoutes.Dashboard,
        VigorlyRoutes.Workouts,
        VigorlyRoutes.History,
        VigorlyRoutes.Profile
    )
    val showBottomBar = currentRoute in mainTabs
    val isDetailOrSession = currentRoute?.startsWith("workout/") == true ||
        currentRoute?.startsWith("session/") == true
    val isSubScreen = currentRoute in listOf(
        VigorlyRoutes.Settings,
        VigorlyRoutes.Milestones,
        VigorlyRoutes.Insights
    )
    val isOnboarding = currentRoute == VigorlyRoutes.Onboarding
    val isSummary = currentRoute == VigorlyRoutes.SessionSummary
    val isHistoryDetail = currentRoute?.startsWith("history/") == true && currentRoute != VigorlyRoutes.History

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            when {
                isOnboarding || isSummary -> {}
                isDetailOrSession || isHistoryDetail -> VigorlyDetailTopBar(
                    onBackClick = { navController.popBackStack() },
                    onSettingsClick = { navController.navigate(VigorlyRoutes.Settings) }
                )
                isSubScreen -> VigorlyDetailTopBar(
                    onBackClick = { navController.popBackStack() },
                    onSettingsClick = {}
                )
                showBottomBar -> VigorlyMainTopBar(
                    avatarUrl = profile.avatarUrl,
                    onSettingsClick = { navController.navigate(VigorlyRoutes.Settings) }
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
                    }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = if (onboardingCompleted) VigorlyRoutes.Dashboard else VigorlyRoutes.Onboarding,
            modifier = Modifier.padding(padding)
        ) {
            composable(VigorlyRoutes.Onboarding) {
                OnboardingScreen(
                    onComplete = {
                        repository.completeOnboarding()
                        navController.navigate(VigorlyRoutes.Dashboard) {
                            popUpTo(VigorlyRoutes.Onboarding) { inclusive = true }
                        }
                    }
                )
            }
            composable(VigorlyRoutes.Dashboard) {
                DashboardScreen(
                    repository = repository,
                    onStartWorkout = { navController.navigate(VigorlyRoutes.Workouts) },
                    onRecommendedWorkoutClick = { id ->
                        navController.navigate(VigorlyRoutes.workoutDetail(id))
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
                    onOpenInsights = { navController.navigate(VigorlyRoutes.Insights) }
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
                val workout = repository.getWorkout(id) ?: return@composable
                WorkoutDetailScreen(
                    workout = workout,
                    repository = repository,
                    onStartWorkout = { navController.navigate(VigorlyRoutes.activeSession(id)) }
                )
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
                }
            }
            composable(
                route = VigorlyRoutes.HistoryDetail,
                arguments = listOf(navArgument("historyId") { type = NavType.StringType })
            ) { entry ->
                val id = entry.arguments?.getString("historyId") ?: return@composable
                val item = repository.getHistoryItem(id) ?: return@composable
                HistoryDetailScreen(item = item)
            }
        }
    }
}
