package com.example.vigorly.ui.session

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.vigorly.R
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.ui.components.GlassCard
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.ButtonText
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.DisplayStat
import com.example.vigorly.ui.theme.HeadlineLgMobile
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnPrimaryContainer
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryContainer
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.ContainerMargin),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(current.workoutName, style = HeadlineLgMobile, color = OnSurface)
        Text(TimeFormatter.formatElapsed(current.elapsedSeconds), style = DisplayStat, color = Primary)
        Text(
            if (isResting) stringResource(R.string.session_rest)
            else stringResource(R.string.session_exercise_progress, current.currentExerciseIndex + 1, current.totalExercises),
            style = LabelCaps,
            color = OnSurfaceVariant
        )
        if (isResting) {
            GlassCard(Modifier.fillMaxWidth().padding(vertical = Dimens.Lg)) {
                Column(
                    Modifier.padding(Dimens.Lg),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "${TimeFormatter.formatRestCountdown(current.restSecondsRemaining)}s",
                        style = DisplayStat,
                        color = Primary
                    )
                    Text(stringResource(R.string.session_rest_hint), style = BodyMd, color = OnSurfaceVariant)
                    TextButton(onClick = repository::skipRest) {
                        Text(stringResource(R.string.session_skip_rest), style = ButtonText, color = Primary)
                    }
                }
            }
        } else {
            LinearProgressIndicator(
                progress = { (current.currentExerciseIndex + 1f) / current.totalExercises },
                modifier = Modifier.fillMaxWidth().padding(vertical = Dimens.Lg),
                color = Primary,
                trackColor = OnSurfaceVariant.copy(alpha = 0.2f)
            )
            GlassCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(Dimens.Lg), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(exercise?.name ?: "—", style = HeadlineLgMobile, color = OnSurface)
                    Text(exercise?.setsRepsLabel ?: "", style = BodyMd, color = OnSurfaceVariant)
                    if (isExerciseDone) {
                        Icon(Icons.Default.CheckCircle, null, tint = Primary, modifier = Modifier.padding(top = Dimens.Sm))
                    }
                }
            }
        }
        Spacer(Modifier.height(Dimens.Lg))
        if (!isResting) {
            Button(
                onClick = repository::markCurrentExerciseComplete,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isExerciseDone,
                colors = ButtonDefaults.buttonColors(containerColor = Primary.copy(alpha = 0.2f))
            ) {
                Text(
                    if (isExerciseDone) stringResource(R.string.exercise_completed)
                    else stringResource(R.string.mark_exercise_done),
                    style = ButtonText,
                    color = Primary
                )
            }
            Spacer(Modifier.height(Dimens.Sm))
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.Md)) {
                IconButton(onClick = repository::previousExercise) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = "Previous", tint = OnSurfaceVariant)
                }
                IconButton(onClick = repository::toggleSessionPause) {
                    Icon(
                        if (current.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = "Pause",
                        tint = Primary
                    )
                }
                IconButton(onClick = repository::nextExercise) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Next", tint = OnSurfaceVariant)
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
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryContainer,
                    contentColor = OnPrimaryContainer
                )
            ) {
                Text(stringResource(R.string.finish_workout), style = ButtonText)
            }
        } else if (!isResting) {
            Button(
                onClick = {
                    repository.cancelWorkoutSession()
                    onCancel()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = OnSurfaceVariant.copy(alpha = 0.2f))
            ) {
                Text(stringResource(R.string.session_cancel), style = BodyMd, color = OnSurfaceVariant)
            }
        }
    }
}
