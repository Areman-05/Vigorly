package com.example.vigorly.ui.session

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.ButtonText
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.HeadlineLgMobile
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnPrimaryContainer
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.workout.WorkoutTypeTheme
import com.example.vigorly.util.TimeFormatter
import kotlinx.coroutines.delay

@Composable
fun ActiveWorkoutScreen(
    repository: VigorlyRepository,
    workoutId: String,
    onComplete: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val session by repository.activeSession.collectAsState()
    val workout = repository.getWorkout(workoutId)
    val accent = workout?.let { WorkoutTypeTheme.accent(it.type) } ?: PrimaryAccent

    LaunchedEffect(workoutId) {
        if (repository.activeSession.value == null) {
            repository.startWorkoutSession(workoutId)
        }
    }

    LaunchedEffect(session?.workoutId, session?.isPaused) {
        val activeId = session?.workoutId ?: return@LaunchedEffect
        while (repository.activeSession.value?.workoutId == activeId) {
            delay(1000)
            if (repository.activeSession.value?.isPaused != true) {
                repository.tickSession()
            }
        }
    }

    val current = session ?: return
    val exercises = workout?.let { repository.flatExercises(it) } ?: emptyList()
    val exercise = exercises.getOrNull(current.currentExerciseIndex)
    val isResting = current.restSecondsRemaining > 0
    val isExerciseDone = exercise?.id in current.completedExerciseIds
    val exerciseProgress = if (current.totalExercises > 0) {
        (current.currentExerciseIndex + 1f) / current.totalExercises
    } else 0f
    val restProgress = if (isResting) {
        1f - (current.restSecondsRemaining / 60f).coerceIn(0f, 1f)
    } else exerciseProgress

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.ContainerMargin, vertical = Dimens.Lg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            current.workoutName,
            style = HeadlineLgMobile.copy(fontSize = 22.sp),
            color = OnSurface,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            if (isResting) stringResource(R.string.session_rest)
            else stringResource(R.string.session_exercise_progress, current.currentExerciseIndex + 1, current.totalExercises),
            style = LabelCaps.copy(fontSize = 10.sp),
            color = OnSurfaceVariant.copy(alpha = 0.75f),
            modifier = Modifier.padding(top = Dimens.Xs, bottom = Dimens.Md)
        )

        WorkoutSessionTimerRing(
            primaryText = if (isResting) {
                "${TimeFormatter.formatRestCountdown(current.restSecondsRemaining)}s"
            } else {
                TimeFormatter.formatElapsed(current.elapsedSeconds)
            },
            secondaryText = if (isResting) {
                stringResource(R.string.session_rest_hint)
            } else if (current.isPaused) {
                "PAUSA"
            } else {
                "TRANSCURRIDO"
            },
            progress = restProgress,
            accent = accent
        )

        Spacer(Modifier.height(Dimens.Lg))

        if (!isResting) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                accent.copy(alpha = 0.12f),
                                accent.copy(alpha = 0.03f)
                            )
                        )
                    )
                    .padding(Dimens.Md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Rounded.FitnessCenter,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(accent.copy(alpha = 0.14f))
                        .padding(10.dp)
                )
                Column(
                    Modifier
                        .weight(1f)
                        .padding(horizontal = Dimens.Md)
                ) {
                    Text(
                        exercise?.name ?: "—",
                        style = HeadlineMd.copy(fontSize = 18.sp),
                        color = OnSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        exercise?.setsRepsLabel.orEmpty(),
                        style = BodyMd.copy(fontSize = 13.sp),
                        color = OnSurfaceVariant.copy(alpha = 0.75f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                if (isExerciseDone) {
                    Icon(Icons.Default.CheckCircle, null, tint = accent, modifier = Modifier.size(28.dp))
                }
            }
        } else {
            TextButton(onClick = repository::skipRest) {
                Text(stringResource(R.string.session_skip_rest), style = ButtonText, color = Primary)
            }
        }

        Spacer(Modifier.height(Dimens.Lg))

        if (!isResting) {
            Button(
                onClick = repository::markCurrentExerciseComplete,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isExerciseDone,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = accent.copy(alpha = 0.22f),
                    contentColor = accent,
                    disabledContainerColor = accent.copy(alpha = 0.12f),
                    disabledContentColor = accent.copy(alpha = 0.5f)
                )
            ) {
                Text(
                    if (isExerciseDone) stringResource(R.string.exercise_completed)
                    else stringResource(R.string.mark_exercise_done),
                    style = ButtonText,
                    color = accent
                )
            }
            Spacer(Modifier.height(Dimens.Md))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = repository::previousExercise) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = null, tint = OnSurfaceVariant)
                }
                IconButton(
                    onClick = repository::toggleSessionPause,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(accent.copy(alpha = 0.16f))
                ) {
                    Icon(
                        if (current.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(28.dp)
                    )
                }
                IconButton(onClick = repository::nextExercise) {
                    Icon(Icons.Default.SkipNext, contentDescription = null, tint = OnSurfaceVariant)
                }
            }
        }

        Spacer(Modifier.weight(1f))

        if (current.currentExerciseIndex >= current.totalExercises - 1 && !isResting) {
            Button(
                onClick = {
                    repository.completeWorkoutSession()
                    onComplete()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryAccent,
                    contentColor = OnPrimaryContainer
                )
            ) {
                Text(stringResource(R.string.finish_workout), style = ButtonText)
            }
        } else if (!isResting) {
            TextButton(
                onClick = {
                    repository.cancelWorkoutSession()
                    onCancel()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.session_cancel), style = BodyMd, color = OnSurfaceVariant)
            }
        }
    }
}
