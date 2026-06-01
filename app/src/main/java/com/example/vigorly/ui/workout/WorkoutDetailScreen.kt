package com.example.vigorly.ui.workout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.data.model.WorkoutDetail
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.ui.components.FavoriteToggle
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurfaceVariant

@Composable
fun WorkoutDetailScreen(
    workout: WorkoutDetail,
    repository: VigorlyRepository,
    onStartWorkout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val favorites by repository.favorites.collectAsState()
    val accent = WorkoutTypeTheme.accent(workout.type)
    val exercises = workout.blocks.flatMap { it.exercises }
    val contentVisible = rememberWorkoutDetailVisible()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.ContainerMargin)
    ) {
        Spacer(Modifier.height(Dimens.Sm))

        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 0) {
            Box(Modifier.fillMaxWidth()) {
                WorkoutDetailTypeRing(workout = workout)
                Box(
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = Dimens.Xs)
                ) {
                    FavoriteToggle(
                        isFavorite = favorites.contains(workout.id),
                        onToggle = { repository.toggleFavorite(workout.id) }
                    )
                }
            }
        }

        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 140) {
            WorkoutDetailTitleSection(
                workout = workout,
                modifier = Modifier.padding(top = Dimens.Sm)
            )
        }

        Spacer(Modifier.height(Dimens.Lg))

        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 260) {
            WorkoutDetailMetricsGrid(
                durationMinutes = workout.durationMinutes,
                intensity = workout.intensity,
                calories = workout.estimatedCalories,
                exerciseCount = exercises.size,
                accent = accent
            )
        }

        Spacer(Modifier.height(Dimens.Md))

        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 380) {
            WorkoutDetailTargetCard(
                targetMuscles = workout.targetMuscles,
                targetDescription = workout.targetDescription,
                accent = accent
            )
        }

        Spacer(Modifier.height(Dimens.Lg))

        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 500) {
            WorkoutDetailStartCta(onClick = onStartWorkout)
        }

        Spacer(Modifier.height(Dimens.Xl))

        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 620) {
            Column {
                Text(
                    stringResource(R.string.workout_detail_session).uppercase(),
                    style = LabelCaps.copy(fontSize = 11.sp),
                    color = OnSurfaceVariant.copy(alpha = 0.7f)
                )
                Text(
                    stringResource(R.string.workout_session_exercise_count, exercises.size),
                    style = LabelCaps,
                    color = OnSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.padding(top = 4.dp, bottom = Dimens.Md)
                )
            }
        }

        var exerciseIndex = 0
        workout.blocks.forEach { block ->
            if (workout.blocks.size > 1) {
                WorkoutDetailSectionEnter(
                    visible = contentVisible,
                    enterDelayMillis = 700 + exerciseIndex * 40
                ) {
                    Text(
                        block.title,
                        style = LabelCaps,
                        color = accent.copy(alpha = 0.85f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
            block.exercises.forEach { exercise ->
                exerciseIndex++
                WorkoutDetailSectionEnter(
                    visible = contentVisible,
                    enterDelayMillis = 680 + exerciseIndex * 70
                ) {
                    WorkoutDetailExerciseRow(
                        exercise = exercise,
                        index = exerciseIndex,
                        accent = accent,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(Dimens.Xl))
    }
}
