package com.example.vigorly.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Timer
import androidx.compose.ui.graphics.vector.ImageVector

fun iconForName(name: String): ImageVector = when (name) {
    "local_fire_department" -> Icons.Default.LocalFireDepartment
    "fitness_center" -> Icons.Default.FitnessCenter
    "timer" -> Icons.Default.Timer
    "emoji_events" -> Icons.Default.EmojiEvents
    "directions_run" -> Icons.Default.DirectionsRun
    "self_improvement" -> Icons.Default.SelfImprovement
    "pool" -> Icons.Default.Pool
    else -> Icons.Default.FitnessCenter
}
