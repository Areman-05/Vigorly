package com.example.vigorly.ui.workout

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DirectionsRun
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Pool
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.vigorly.data.model.WorkoutType
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer

object WorkoutTypeTheme {

    fun accent(type: WorkoutType): Color = when (type) {
        WorkoutType.STRENGTH -> PrimaryAccent
        WorkoutType.HIIT -> PrimaryContainer
        WorkoutType.CARDIO -> Primary
        WorkoutType.RECOVERY -> OnSurfaceVariant
        WorkoutType.SWIM -> Primary.copy(alpha = 0.88f)
    }

    fun icon(type: WorkoutType): ImageVector = when (type) {
        WorkoutType.STRENGTH -> Icons.Rounded.FitnessCenter
        WorkoutType.HIIT -> Icons.Rounded.LocalFireDepartment
        WorkoutType.CARDIO -> Icons.Rounded.DirectionsRun
        WorkoutType.RECOVERY -> Icons.Rounded.SelfImprovement
        WorkoutType.SWIM -> Icons.Rounded.Pool
    }
}
