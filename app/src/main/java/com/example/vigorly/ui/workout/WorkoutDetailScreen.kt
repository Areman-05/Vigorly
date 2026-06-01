package com.example.vigorly.ui.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.data.model.WorkoutDetail
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.ui.components.PulsingButton
import com.example.vigorly.ui.components.WorkoutChip
import com.example.vigorly.util.WorkoutLabels
import com.example.vigorly.ui.theme.BodyLg
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.ButtonText
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.HeadlineLgMobile
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
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

    Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        WorkoutDetailHero(
            workout = workout,
            isFavorite = favorites.contains(workout.id),
            onFavoriteToggle = { repository.toggleFavorite(workout.id) }
        )

        Column(Modifier.padding(horizontal = Dimens.ContainerMargin)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                WorkoutChip(text = WorkoutLabels.typeLabel(workout.type), accent = accent)
                WorkoutChip(
                    text = stringResource(R.string.workout_duration_chip, workout.durationMinutes),
                    accent = accent,
                    filled = true
                )
            }

            Text(
                workout.name,
                style = HeadlineLgMobile.copy(fontSize = 26.sp, lineHeight = 30.sp),
                color = OnSurface,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = Dimens.Md)
            )
            Text(
                workout.description,
                style = BodyLg.copy(fontSize = 15.sp, lineHeight = 22.sp),
                color = OnSurfaceVariant.copy(alpha = 0.85f),
                modifier = Modifier.padding(top = Dimens.Sm)
            )

            Spacer(Modifier.height(Dimens.Lg))

            WorkoutDetailMetricsRow(
                durationMinutes = workout.durationMinutes,
                intensity = workout.intensity,
                calories = workout.estimatedCalories,
                accent = accent
            )

            Spacer(Modifier.height(Dimens.Md))

            WorkoutDetailTargetStrip(
                targetMuscles = workout.targetMuscles,
                targetDescription = workout.targetDescription,
                accent = accent
            )

            Spacer(Modifier.height(Dimens.Lg))

            PulsingButton(
                onClick = onStartWorkout,
                modifier = Modifier.fillMaxWidth(),
                containerColor = PrimaryAccent,
                shape = RoundedCornerShape(14.dp)
            ) {
                androidx.compose.material3.Icon(Icons.Default.PlayArrow, contentDescription = null)
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
                color = OnSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = Dimens.Md)
            )

            workout.blocks.forEach { block ->
                Row(
                    Modifier.padding(bottom = Dimens.Sm),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Text(
                        block.title,
                        style = BodyMd.copy(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
                        color = accent.copy(alpha = 0.9f)
                    )
                    Text(
                        stringResource(R.string.workout_block_exercise_count, block.exercises.size),
                        style = BodyMd.copy(fontSize = 13.sp),
                        color = OnSurfaceVariant.copy(alpha = 0.55f)
                    )
                }
                block.exercises.forEachIndexed { index, exercise ->
                    val globalIndex = exercises.indexOf(exercise)
                    WorkoutExerciseTimelineRow(
                        exercise = exercise,
                        accent = accent,
                        isLast = globalIndex == exercises.lastIndex
                    )
                }
                Spacer(Modifier.height(Dimens.Sm))
            }

            Spacer(Modifier.height(Dimens.Xl))
        }
    }
}
