package com.example.vigorly.navigation

object VigorlyRoutes {
    const val Splash = "splash"
    const val Login = "login"
    const val Register = "register"
    const val Setup = "setup"
    const val Dashboard = "dashboard"
    const val Workouts = "workouts"
    const val Analysis = "analysis"
    const val History = "history"
    const val Profile = "profile"
    const val Milestones = "milestones"
    const val WorkoutDetail = "workout/{workoutId}"
    const val ActiveSession = "session/{workoutId}"
    const val SessionSummary = "session/summary"
    const val HistoryDetail = "history/{historyId}"
    const val ActivityDetail = "activity/detail"
    const val ActivityMetricDetail = "activity/metric/{metric}"

    fun workoutDetail(workoutId: String) = "workout/$workoutId"
    fun activeSession(workoutId: String) = "session/$workoutId"
    fun historyDetail(historyId: String) = "history/$historyId"
    fun activityMetric(metric: String) = "activity/metric/$metric"
}
