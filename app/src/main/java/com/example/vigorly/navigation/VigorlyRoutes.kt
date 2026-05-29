package com.example.vigorly.navigation

object VigorlyRoutes {
    const val Onboarding = "onboarding"
    const val Dashboard = "dashboard"
    const val Workouts = "workouts"
    const val History = "history"
    const val Profile = "profile"
    const val Insights = "insights"
    const val Settings = "settings"
    const val Milestones = "milestones"
    const val WorkoutDetail = "workout/{workoutId}"
    const val ActiveSession = "session/{workoutId}"
    const val SessionSummary = "session/summary"
    const val HistoryDetail = "history/{historyId}"

    fun workoutDetail(workoutId: String) = "workout/$workoutId"
    fun activeSession(workoutId: String) = "session/$workoutId"
    fun historyDetail(historyId: String) = "history/$historyId"
}
