package com.example.vigorly.presentation.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.vigorly.R
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.navigation.VigorlyRoutes
import com.example.vigorly.ui.analysis.AnalysisScreen
import com.example.vigorly.ui.auth.LoginScreen
import com.example.vigorly.ui.auth.RegisterScreen
import com.example.vigorly.ui.components.RouteFallbackScreen
import com.example.vigorly.data.activity.ActivityMetric
import com.example.vigorly.ui.dashboard.ActivityDetailScreen
import com.example.vigorly.ui.dashboard.ActivityMetricDetailScreen
import com.example.vigorly.ui.dashboard.DashboardScreen
import com.example.vigorly.ui.history.HistoryDetailScreen
import com.example.vigorly.ui.history.HistoryScreen
import com.example.vigorly.ui.milestones.MilestonesScreen
import com.example.vigorly.ui.profile.ProfileScreen
import com.example.vigorly.ui.session.ActiveWorkoutScreen
import com.example.vigorly.ui.session.SessionSummaryScreen
import com.example.vigorly.ui.setup.SetupWizardScreen
import com.example.vigorly.ui.workout.WorkoutDetailScreen
import com.example.vigorly.ui.workout.WorkoutsScreen

fun NavGraphBuilder.vigorlyNavGraph(
    navController: NavHostController,
    repository: VigorlyRepository,
    showActivityCalendar: Boolean,
    onShowActivityCalendarChange: (Boolean) -> Unit,
    onNavigateToLogin: () -> Unit,
    contentPaddingModifier: Modifier,
    onWorkoutsFilterOverlayChange: (Boolean) -> Unit = {}
) {
    composable(VigorlyRoutes.Login) {
        LoginScreen(
            repository = repository,
            onLoginSuccess = { needsSetup ->
                val target = if (needsSetup) VigorlyRoutes.Setup else VigorlyRoutes.Dashboard
                navController.navigate(target) {
                    popUpTo(VigorlyRoutes.Login) { inclusive = false }
                    launchSingleTop = true
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
                    popUpTo(VigorlyRoutes.Login) { inclusive = false }
                    launchSingleTop = true
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
                    popUpTo(VigorlyRoutes.Login) { inclusive = false }
                    launchSingleTop = true
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
            },
            onViewAllWorkoutsClick = {
                navController.navigate(VigorlyRoutes.Workouts) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
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
            },
            onOpenMetric = { metric ->
                navController.navigate(VigorlyRoutes.activityMetric(metric.name.lowercase()))
            }
        )
    }
    composable(
        route = VigorlyRoutes.ActivityMetricDetail,
        arguments = listOf(navArgument("metric") { type = NavType.StringType })
    ) { entry ->
        val metric = ActivityMetric.fromRoute(entry.arguments?.getString("metric"))
        ActivityMetricDetailScreen(
            metric = metric,
            repository = repository,
            onBack = { navController.popBackStack() },
            modifier = Modifier.fillMaxSize()
        )
    }
    composable(VigorlyRoutes.Workouts) {
        WorkoutsScreen(
            repository = repository,
            modifier = contentPaddingModifier,
            onWorkoutClick = { id -> navController.navigate(VigorlyRoutes.workoutDetail(id)) },
            onFilterOverlayChange = onWorkoutsFilterOverlayChange
        )
    }
    composable(VigorlyRoutes.Analysis) {
        AnalysisScreen(
            repository = repository,
            modifier = contentPaddingModifier
        )
    }
    composable(VigorlyRoutes.Profile) {
        ProfileScreen(
            repository = repository,
            modifier = contentPaddingModifier,
            onOpenHistory = { navController.navigate(VigorlyRoutes.History) },
            onOpenHistoryItem = { id -> navController.navigate(VigorlyRoutes.historyDetail(id)) },
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
    composable(VigorlyRoutes.History) {
        HistoryScreen(
            repository = repository,
            modifier = contentPaddingModifier,
            onHistoryItemClick = { id ->
                navController.navigate(VigorlyRoutes.historyDetail(id))
            }
        )
    }
    composable(VigorlyRoutes.Milestones) {
        MilestonesScreen(repository = repository, modifier = contentPaddingModifier)
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
                modifier = Modifier.fillMaxSize(),
                onBackClick = { navController.popBackStack() },
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
                coverUrl = repository.getWorkout(summary.workoutId)?.heroImageUrl,
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
