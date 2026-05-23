package com.example.vigorly.navigation

object VigorlyRoutes {
    const val Dashboard = "dashboard"
    const val Workouts = "workouts"
    const val History = "history"
    const val Profile = "profile"
    const val WorkoutDetail = "workout/{workoutId}"

    fun workoutDetail(workoutId: String) = "workout/$workoutId"
}
