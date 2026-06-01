package com.example.vigorly.ui.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.data.model.WorkoutDetail
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.ui.theme.ButtonText
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnPrimaryContainer
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.PrimaryAccent

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

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.ContainerMargin)
    ) {
        Spacer(Modifier.height(Dimens.Sm))

        WorkoutDetailHeader(
            workout = workout,
            isFavorite = favorites.contains(workout.id),
            onFavoriteToggle = { repository.toggleFavorite(workout.id) }
        )

        Spacer(Modifier.height(Dimens.Lg))

        WorkoutDetailOverview(
            durationMinutes = workout.durationMinutes,
            intensity = workout.intensity,
            calories = workout.estimatedCalories,
            targetMuscles = workout.targetMuscles,
            targetDescription = workout.targetDescription,
            accent = accent
        )

        Spacer(Modifier.height(Dimens.Lg))

        Button(
            onClick = onStartWorkout,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryAccent,
                contentColor = OnPrimaryContainer
            )
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null)
            Text(
                stringResource(R.string.start_workout),
                style = ButtonText,
                modifier = Modifier.padding(start = 6.dp)
            )
        }

        Spacer(Modifier.height(Dimens.Xl))

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

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            workout.blocks.forEach { block ->
                if (workout.blocks.size > 1) {
                    Text(
                        block.title,
                        style = LabelCaps,
                        color = accent.copy(alpha = 0.85f),
                        modifier = Modifier.padding(top = Dimens.Sm, bottom = 4.dp)
                    )
                }
                block.exercises.forEach { exercise ->
                    WorkoutDetailExerciseRow(exercise = exercise, accent = accent)
                }
            }
        }

        Spacer(Modifier.height(Dimens.Xl))
    }
}
