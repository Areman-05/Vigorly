package com.example.vigorly

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.NavType
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
import com.example.vigorly.ui.profile.ProfileScreen
import com.example.vigorly.ui.workout.WorkoutDetailScreen
import com.example.vigorly.ui.workout.WorkoutsScreen

@Composable
fun VigorlyApp() {
    val repository = remember { VigorlyRepository() }
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route ?: VigorlyRoutes.Dashboard
    val profile by repository.profile.collectAsState()

    val showBottomBar = currentRoute in listOf(
        VigorlyRoutes.Dashboard,
        VigorlyRoutes.Workouts,
        VigorlyRoutes.History,
        VigorlyRoutes.Profile
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (currentRoute.startsWith("workout/")) {
                VigorlyDetailTopBar(
                    onBackClick = { navController.popBackStack() },
                    onSettingsClick = {}
                )
            } else if (showBottomBar) {
                VigorlyMainTopBar(
                    avatarUrl = profile.avatarUrl,
                    onSettingsClick = {}
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
                    onStartWorkout = {
                        navController.navigate(VigorlyRoutes.workoutDetail("titan_protocol"))
                    }
                )
            }
            composable(VigorlyRoutes.Workouts) {
                WorkoutsScreen(
                    repository = repository,
                    onWorkoutClick = { id ->
                        navController.navigate(VigorlyRoutes.workoutDetail(id))
                    }
                )
            }
            composable(VigorlyRoutes.History) {
                HistoryScreen(repository = repository)
            }
            composable(VigorlyRoutes.Profile) {
                ProfileScreen(repository = repository)
            }
            composable(
                route = VigorlyRoutes.WorkoutDetail,
                arguments = listOf(navArgument("workoutId") { type = NavType.StringType })
            ) { entry ->
                val id = entry.arguments?.getString("workoutId") ?: return@composable
                val workout = repository.getWorkout(id) ?: return@composable
                WorkoutDetailScreen(
                    workout = workout,
                    onStartWorkout = { repository.recordWorkoutCompletion(id) }
                )
            }
        }
    }
}
