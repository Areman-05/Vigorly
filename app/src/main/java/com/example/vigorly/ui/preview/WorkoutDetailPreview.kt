package com.example.vigorly.ui.preview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.vigorly.data.catalog.WorkoutCatalog
import com.example.vigorly.ui.components.AuthGradientBackground
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.VigorlyTheme
import com.example.vigorly.ui.workout.WorkoutDetailMetricsGrid
import com.example.vigorly.ui.workout.WorkoutDetailTargetCard
import com.example.vigorly.ui.workout.WorkoutDetailTitleSection
import com.example.vigorly.ui.workout.WorkoutDetailTypeRing
import com.example.vigorly.ui.workout.WorkoutTypeTheme

@Preview(showBackground = true, backgroundColor = 0xFF121317, heightDp = 900)
@Composable
fun WorkoutDetailPreview() {
    val workout = WorkoutCatalog.allWorkouts().values.first()
    val accent = WorkoutTypeTheme.accent(workout.type)
    val exerciseCount = workout.blocks.sumOf { it.exercises.size }

    VigorlyTheme {
        AuthGradientBackground {
            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(Dimens.ContainerMargin)
            ) {
                WorkoutDetailTypeRing(workout = workout)
                WorkoutDetailTitleSection(workout = workout)
                WorkoutDetailMetricsGrid(
                    durationMinutes = workout.durationMinutes,
                    intensity = workout.intensity,
                    calories = workout.estimatedCalories,
                    exerciseCount = exerciseCount,
                    accent = accent,
                    modifier = Modifier.padding(top = Dimens.Lg)
                )
                WorkoutDetailTargetCard(
                    targetMuscles = workout.targetMuscles,
                    targetDescription = workout.targetDescription,
                    accent = accent,
                    modifier = Modifier.padding(top = Dimens.Md)
                )
            }
        }
    }
}
