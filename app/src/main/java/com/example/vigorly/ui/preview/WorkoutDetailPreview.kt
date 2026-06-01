package com.example.vigorly.ui.preview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.vigorly.data.catalog.WorkoutCatalog
import com.example.vigorly.ui.components.AuthGradientBackground
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.VigorlyTheme
import com.example.vigorly.ui.workout.WorkoutDetailHeader
import com.example.vigorly.ui.workout.WorkoutDetailOverview
import com.example.vigorly.ui.workout.WorkoutTypeTheme

@Preview(showBackground = true, backgroundColor = 0xFF121317)
@Composable
fun WorkoutDetailHeaderPreview() {
    val workout = WorkoutCatalog.allWorkouts().values.first()
    VigorlyTheme {
        AuthGradientBackground {
            Column(Modifier.padding(Dimens.ContainerMargin)) {
                WorkoutDetailHeader(
                    workout = workout,
                    isFavorite = true,
                    onFavoriteToggle = {}
                )
                WorkoutDetailOverview(
                    durationMinutes = workout.durationMinutes,
                    intensity = workout.intensity,
                    calories = workout.estimatedCalories,
                    targetMuscles = workout.targetMuscles,
                    targetDescription = workout.targetDescription,
                    accent = WorkoutTypeTheme.accent(workout.type),
                    modifier = Modifier.padding(top = Dimens.Lg)
                )
            }
        }
    }
}
