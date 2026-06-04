package com.example.vigorly.presentation.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.vigorly.R
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.navigation.AppDestination
import com.example.vigorly.navigation.VigorlyRoutes
import com.example.vigorly.presentation.app.AppViewModel
import com.example.vigorly.ui.auth.LoginScreen
import com.example.vigorly.ui.auth.RegisterScreen
import com.example.vigorly.ui.components.RouteFallbackScreen
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

fun NavGraphBuilder.vigorlyNavGraph(
    navController: NavHostController,
    repository: VigorlyRepository,
    appViewModel: AppViewModel,
    showActivityCalendar: Boolean,
    onShowActivityCalendarChange: (Boolean) -> Unit,
    onNavigateFromSplash: (AppDestination) -> Unit,
    onNavigateToLogin: () -> Unit,
    workoutCompletedMessage: String,
    contentPaddingModifier: Modifier
) {
    composable(VigorlyRoutes.Splash) {
        SplashScreen(
            repository = repository,
            onFinished = onNavigateFromSplash
        )
    }
    composable(VigorlyRoutes.Login) {
        LoginScreen(
            repository = repository,
            onLoginSuccess = { needsSetup ->
                val target = if (needsSetup) VigorlyRoutes.Setup else VigorlyRoutes.Dashboard
                navController.navigate(target) {
                    popUpTo(VigorlyRoutes.Login) { inclusive = true }
                }
            },
            onNavigateRegister = { navController.navigate(VigorlyRoutes.Register) }
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
            modifier = contentPaddingModifier,
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
            onDismissCalendar = { onShowActivityCalendarChange(false) },
            onDateSelected = {
                repository.selectActivityDate(it)
                onShowActivityCalendarChange(false)
            }
        )
    }
    composable(VigorlyRoutes.Workouts) {
        WorkoutsScreen(
            repository = repository,
            modifier = contentPaddingModifier,
            onWorkoutClick = { id -> navController.navigate(VigorlyRoutes.workoutDetail(id)) }
        )
    }
    composable(VigorlyRoutes.History) {
        HistoryScreen(
            repository = repository,
            modifier = contentPaddingModifier,
            onHistoryItemClick = { id ->
                navController.navigate(VigorlyRoutes.historyDetail(id))
            }
        )
    }
    composable(VigorlyRoutes.Profile) {
        ProfileScreen(
            repository = repository,
            modifier = contentPaddingModifier,
            onViewAllMilestones = { navController.navigate(VigorlyRoutes.Milestones) },
            onOpenInsights = { navController.navigate(VigorlyRoutes.Insights) }
        )
    }
    composable(VigorlyRoutes.Settings) {
        SettingsScreen(
            repository = repository,
            modifier = contentPaddingModifier,
            onOpenInsights = { navController.navigate(VigorlyRoutes.Insights) },
            onRestartOnboarding = {
                navController.navigate(VigorlyRoutes.Setup) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = false
                    }
                    launchSingleTop = true
                }
            },
            onLogout = {
                repository.logout()
                onNavigateToLogin()
            }
        )
    }
    composable(VigorlyRoutes.Milestones) {
        MilestonesScreen(repository = repository, modifier = contentPaddingModifier)
    }
    composable(VigorlyRoutes.Insights) {
        InsightsScreen(repository = repository, modifier = contentPaddingModifier)
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
                appViewModel.showMessage(workoutCompletedMessage)
                navController.navigate(VigorlyRoutes.SessionSummary) {
                    popUpTo(VigorlyRoutes.activeSession(id)) { inclusive = true }
                }
            },
            onCancel = { navController.popBackStack() }
        )
    }
    composable(VigorlyRoutes.SessionSummary) {
        val summaryState by repository.lastSessionSummary.collectAsState()
        val summary = summaryState
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
                onGoBack = { navController.popBackStack(VigorlyRoutes.Dashboard, false) }
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
            HistoryDetailScreen(item = item, repository = repository)
        }
    }
}
