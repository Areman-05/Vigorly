package com.example.vigorly.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.ui.graphics.vector.ImageVector

fun iconForName(name: String): ImageVector = when (name) {
    "local_fire_department" -> Icons.Default.LocalFireDepartment
    "fitness_center" -> Icons.Default.FitnessCenter
    "timer" -> Icons.Default.Timer
    "emoji_events" -> Icons.Default.EmojiEvents
    "directions_run" -> Icons.Default.DirectionsRun
    "directions_walk" -> Icons.Default.DirectionsWalk
    "self_improvement" -> Icons.Default.SelfImprovement
    "pool" -> Icons.Default.Pool
    "star" -> Icons.Default.Star
    "military_tech" -> Icons.Default.MilitaryTech
    "bolt" -> Icons.Default.Bolt
    "trending_up" -> Icons.Default.TrendingUp
    "favorite" -> Icons.Default.Favorite
    "whatshot" -> Icons.Default.Whatshot
    "workspace_premium" -> Icons.Default.WorkspacePremium
    else -> Icons.Default.FitnessCenter
}
