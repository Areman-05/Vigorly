package com.example.vigorly

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
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
import com.example.vigorly.ui.history.HistoryScreen
import com.example.vigorly.ui.milestones.MilestonesScreen
import com.example.vigorly.ui.profile.ProfileScreen
import com.example.vigorly.ui.session.ActiveWorkoutScreen
import com.example.vigorly.ui.settings.SettingsScreen
import com.example.vigorly.ui.workout.WorkoutDetailScreen
import com.example.vigorly.ui.workout.WorkoutsScreen
import kotlinx.coroutines.launch

@Composable
fun VigorlyApp(repository: VigorlyRepository) {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route ?: VigorlyRoutes.Dashboard
    val profile by repository.profile.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val mainTabs = listOf(
        VigorlyRoutes.Dashboard,
        VigorlyRoutes.Workouts,
        VigorlyRoutes.History,
        VigorlyRoutes.Profile
    )
    val showBottomBar = currentRoute in mainTabs
    val isDetailOrSession = currentRoute?.startsWith("workout/") == true ||
        currentRoute?.startsWith("session/") == true
    val isSubScreen = currentRoute in listOf(VigorlyRoutes.Settings, VigorlyRoutes.Milestones)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            when {
                isDetailOrSession -> VigorlyDetailTopBar(
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
                    currentRoute = currentRoute,
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
            startDestination = VigorlyRoutes.Dashboard,
            modifier = Modifier.padding(padding)
        ) {
            composable(VigorlyRoutes.Dashboard) {
                DashboardScreen(
                    repository = repository,
                    onStartWorkout = { navController.navigate(VigorlyRoutes.Workouts) }
                )
            }
            composable(VigorlyRoutes.Workouts) {
                WorkoutsScreen(
                    repository = repository,
                    onWorkoutClick = { id -> navController.navigate(VigorlyRoutes.workoutDetail(id)) }
                )
            }
            composable(VigorlyRoutes.History) {
                HistoryScreen(repository = repository)
            }
            composable(VigorlyRoutes.Profile) {
                ProfileScreen(
                    repository = repository,
                    onViewAllMilestones = { navController.navigate(VigorlyRoutes.Milestones) }
                )
            }
            composable(VigorlyRoutes.Settings) {
                SettingsScreen(repository = repository)
            }
            composable(VigorlyRoutes.Milestones) {
                MilestonesScreen(repository = repository)
            }
            composable(
                route = VigorlyRoutes.WorkoutDetail,
                arguments = listOf(navArgument("workoutId") { type = NavType.StringType })
            ) { entry ->
                val id = entry.arguments?.getString("workoutId") ?: return@composable
                val workout = repository.getWorkout(id) ?: return@composable
                WorkoutDetailScreen(
                    workout = workout,
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
                        scope.launch {
                            snackbarHostState.showSnackbar("Workout completed!")
                        }
                        navController.popBackStack(VigorlyRoutes.Dashboard, false)
                    },
                    onCancel = { navController.popBackStack() }
                )
            }
        }
    }
}
