package com.example.vigorly.ui.setup

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessibilityNew
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DirectionsBike
import androidx.compose.material.icons.rounded.DirectionsRun
import androidx.compose.material.icons.rounded.DirectionsWalk
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.EventSeat
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.MonitorWeight
import androidx.compose.material.icons.rounded.Nightlight
import androidx.compose.material.icons.rounded.Park
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material.icons.rounded.WbTwilight
import androidx.compose.ui.graphics.vector.ImageVector

object SetupStepIcons {
    fun goal(key: String): ImageVector = when (key) {
        "strength" -> Icons.Rounded.FitnessCenter
        "cardio" -> Icons.Rounded.DirectionsRun
        "weight" -> Icons.Rounded.MonitorWeight
        "muscle" -> Icons.Rounded.Bolt
        "endurance" -> Icons.Rounded.DirectionsBike
        "flexibility" -> Icons.Rounded.SelfImprovement
        else -> Icons.Rounded.Spa
    }

    fun activity(key: String): ImageVector = when (key) {
        "sedentary" -> Icons.Rounded.EventSeat
        "light" -> Icons.Rounded.DirectionsWalk
        "moderate" -> Icons.Rounded.Speed
        "active" -> Icons.Rounded.FitnessCenter
        else -> Icons.Rounded.EmojiEvents
    }

    fun location(key: String): ImageVector = when (key) {
        "gym" -> Icons.Rounded.FitnessCenter
        "home" -> Icons.Rounded.Home
        "outdoor" -> Icons.Rounded.Park
        else -> Icons.Rounded.Layers
    }

    fun time(key: String): ImageVector = when (key) {
        "morning" -> Icons.Rounded.WbSunny
        "midday" -> Icons.Rounded.LightMode
        "afternoon" -> Icons.Rounded.WbTwilight
        "evening" -> Icons.Rounded.Nightlight
        else -> Icons.Rounded.Schedule
    }

    val introWelcome: ImageVector = Icons.Rounded.AccessibilityNew
    val introReady: ImageVector = Icons.Rounded.RocketLaunch
    val introComplete: ImageVector = Icons.Rounded.CheckCircle
    val introCalendar: ImageVector = Icons.Rounded.CalendarMonth
}
