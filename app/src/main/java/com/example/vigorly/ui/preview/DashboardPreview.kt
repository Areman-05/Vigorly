package com.example.vigorly.ui.preview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.vigorly.data.model.DailyGoals
import com.example.vigorly.ui.components.ActivityRings
import com.example.vigorly.ui.components.GlassCard
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.VigorlyTheme

private val previewGoals = DailyGoals(
    moveProgress = 0.75f,
    exerciseProgress = 0.5f,
    standProgress = 0.82f,
    moveCalories = 450,
    moveCaloriesGoal = 600,
    steps = 6240,
    stepsGoal = 10000,
    heartRateBpm = 72,
    sleepHours = 7f
)

@Preview(showBackground = true, backgroundColor = 0xFF121317)
@Composable
fun DashboardRingsPreview() {
    VigorlyTheme {
        GlassCard {
            Column(Modifier.padding(Dimens.Md)) {
                ActivityRings(
                    moveProgress = previewGoals.moveProgress,
                    exerciseProgress = previewGoals.exerciseProgress,
                    standProgress = previewGoals.standProgress,
                    centerPercent = previewGoals.dailyGoalPercent
                )
            }
        }
    }
}
