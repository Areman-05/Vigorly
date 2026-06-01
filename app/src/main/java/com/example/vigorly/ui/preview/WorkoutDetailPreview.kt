package com.example.vigorly.ui.preview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.vigorly.data.catalog.WorkoutCatalog
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.VigorlyTheme
import com.example.vigorly.ui.workout.WorkoutDetailHero
import com.example.vigorly.ui.workout.WorkoutDetailMetricsRow
import com.example.vigorly.ui.workout.WorkoutTypeTheme

@Preview(showBackground = true, backgroundColor = 0xFF121317)
@Composable
fun WorkoutDetailHeroPreview() {
    val workout = WorkoutCatalog.allWorkouts().values.first()
    VigorlyTheme {
        Column(Modifier.padding(Dimens.Md)) {
            WorkoutDetailHero(
                workout = workout,
                isFavorite = false,
                onFavoriteToggle = {}
            )
            WorkoutDetailMetricsRow(
                durationMinutes = workout.durationMinutes,
                intensity = workout.intensity,
                calories = workout.estimatedCalories,
                accent = WorkoutTypeTheme.accent(workout.type),
                modifier = Modifier.padding(top = Dimens.Md)
            )
        }
    }
}
