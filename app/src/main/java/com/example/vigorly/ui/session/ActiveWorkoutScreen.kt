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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.rounded.FitnessCenter
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
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer
import com.example.vigorly.ui.workout.WorkoutDetailStartCta
import com.example.vigorly.ui.workout.WorkoutDetailSectionEnter
import com.example.vigorly.ui.workout.WorkoutTypeTheme
import com.example.vigorly.ui.workout.rememberWorkoutDetailVisible
import com.example.vigorly.util.TimeFormatter
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

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
    val contentVisible = rememberWorkoutDetailVisible()

    LaunchedEffect(workoutId) {
        if (repository.activeSession.value == null) {
            repository.startWorkoutSession(workoutId)
        }
    }

    LaunchedEffect(workoutId) {
        while (isActive) {
            delay(1000L)
            val active = repository.activeSession.value ?: break
            if (active.workoutId != workoutId) break
            // El descanso sigue aunque el ejercicio esté en pausa; la pausa solo frena el tiempo de sesión.
            if (active.restSecondsRemaining > 0 || !active.isPaused) {
                repository.tickSession()
            }
        }
    }

    val current = session ?: return
    val exercises = workout?.let { repository.flatExercises(it) } ?: emptyList()
    val exercise = exercises.getOrNull(current.currentExerciseIndex)
    val isResting = current.restSecondsRemaining > 0
    val isPaused = current.isPaused && !isResting
    val isExerciseDone = exercise?.id?.let { it in current.completedExerciseIds } == true
    val completedCount = current.completedExerciseIds.size

    val restTotal = current.restDurationSeconds
        .takeIf { it > 0 }
        ?: VigorlyRepository.REST_SECONDS_BETWEEN_EXERCISES
    val plannedSeconds = ((workout?.durationMinutes ?: 30).coerceAtLeast(1)) * 60
    val elapsedProgress = (current.elapsedSeconds.toFloat() / plannedSeconds).coerceIn(0f, 1f)
    val restProgress = (current.restSecondsRemaining.toFloat() / restTotal.coerceAtLeast(1)).coerceIn(0f, 1f)

    val timerMode = when {
        isResting -> SessionTimerMode.REST
        isPaused -> SessionTimerMode.PAUSED
        else -> SessionTimerMode.ACTIVE
    }
    val ringProgress = if (isResting) restProgress else elapsedProgress
    val ringAccent = if (isResting) PrimaryContainer else accent

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.ContainerMargin, vertical = Dimens.Md),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 0) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    current.workoutName,
                    style = HeadlineLgMobile.copy(fontSize = 22.sp),
                    color = OnSurface,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    when {
                        isResting -> stringResource(R.string.session_rest)
                        isPaused -> stringResource(R.string.session_paused_label)
                        else -> stringResource(
                            R.string.session_exercise_progress,
                            current.currentExerciseIndex + 1,
                            current.totalExercises
                        )
                    },
                    style = LabelCaps.copy(fontSize = 10.sp),
                    color = ringAccent.copy(alpha = 0.9f),
                    modifier = Modifier.padding(top = Dimens.Xs, bottom = Dimens.Sm)
                )
                if (!isResting) {
                    Text(
                        stringResource(
                            R.string.session_exercises_completed,
                            completedCount,
                            current.totalExercises
                        ),
                        style = LabelCaps.copy(fontSize = 9.sp),
                        color = OnSurfaceVariant.copy(alpha = 0.55f)
                    )
                }
            }
        }

        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 120) {
            WorkoutSessionTimerRing(
                primaryText = when {
                    isResting -> TimeFormatter.formatRestCountdown(current.restSecondsRemaining)
                    else -> TimeFormatter.formatElapsed(current.elapsedSeconds)
                },
                secondaryText = when {
                    isResting -> stringResource(R.string.session_rest_ring_label)
                    isPaused -> stringResource(R.string.session_paused_label)
                    else -> stringResource(R.string.session_elapsed_label)
                },
                progress = ringProgress,
                accent = ringAccent,
                mode = timerMode
            )
        }

        Spacer(Modifier.height(Dimens.Lg))

        when {
            isResting -> WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 240) {
                SessionRestPanel(onSkipRest = repository::skipRest)
            }
            else -> WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 240) {
                SessionExercisePanel(
                    exerciseName = exercise?.name ?: "—",
                    setsLabel = exercise?.setsRepsLabel.orEmpty(),
                    isDone = isExerciseDone,
                    accent = accent,
                    onMarkDone = repository::markCurrentExerciseComplete,
                    isDoneEnabled = !isExerciseDone && exercise != null
                )
            }
        }

        if (!isResting) {
            Spacer(Modifier.height(Dimens.Lg))
            WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 360) {
                SessionTransportControls(
                    isPaused = current.isPaused,
                    accent = accent,
                    onPrevious = repository::previousExercise,
                    onTogglePause = repository::toggleSessionPause,
                    onNext = repository::nextExercise
                )
            }
        }

        Spacer(Modifier.height(Dimens.Xl))

        if (current.currentExerciseIndex >= current.totalExercises - 1 && !isResting) {
            WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 480) {
                WorkoutDetailStartCta(
                    onClick = {
                        repository.completeWorkoutSession()
                        onComplete()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    labelRes = R.string.finish_workout,
                    showPlayIcon = false
                )
            }
        } else if (!isResting) {
            WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 480) {
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

        Spacer(Modifier.height(Dimens.Md))
    }
}

@Composable
private fun SessionRestPanel(onSkipRest: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        PrimaryContainer.copy(alpha = 0.14f),
                        Primary.copy(alpha = 0.05f)
                    )
                )
            )
            .padding(vertical = Dimens.Md, horizontal = Dimens.Lg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(R.string.session_rest_hint),
            style = BodyMd.copy(fontSize = 14.sp, lineHeight = 20.sp),
            color = OnSurfaceVariant.copy(alpha = 0.85f),
            textAlign = TextAlign.Center
        )
        TextButton(
            onClick = onSkipRest,
            modifier = Modifier.padding(top = Dimens.Sm)
        ) {
            Text(stringResource(R.string.session_skip_rest), style = ButtonText, color = PrimaryAccent)
        }
    }
}

@Composable
private fun SessionExercisePanel(
    exerciseName: String,
    setsLabel: String,
    isDone: Boolean,
    accent: androidx.compose.ui.graphics.Color,
    onMarkDone: () -> Unit,
    isDoneEnabled: Boolean
) {
    Column(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            accent.copy(alpha = 0.14f),
                            Primary.copy(alpha = 0.06f),
                            PrimaryAccent.copy(alpha = 0.03f)
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
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.16f))
                    .padding(11.dp)
            )
            Column(
                Modifier
                    .weight(1f)
                    .padding(horizontal = Dimens.Md)
            ) {
                Text(
                    exerciseName,
                    style = HeadlineMd.copy(fontSize = 18.sp),
                    color = OnSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    setsLabel,
                    style = BodyMd.copy(fontSize = 13.sp),
                    color = OnSurfaceVariant.copy(alpha = 0.75f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            if (isDone) {
                Icon(Icons.Default.CheckCircle, null, tint = accent, modifier = Modifier.size(30.dp))
            }
        }
        Spacer(Modifier.height(Dimens.Md))
        TextButton(
            onClick = onMarkDone,
            enabled = isDoneEnabled,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (isDone) stringResource(R.string.exercise_completed)
                else stringResource(R.string.mark_exercise_done),
                style = ButtonText,
                color = if (isDoneEnabled) accent else accent.copy(alpha = 0.45f)
            )
        }
    }
}

@Composable
private fun SessionTransportControls(
    isPaused: Boolean,
    accent: androidx.compose.ui.graphics.Color,
    onPrevious: () -> Unit,
    onTogglePause: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrevious) {
            Icon(Icons.Default.SkipPrevious, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(32.dp))
        }
        IconButton(
            onClick = onTogglePause,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(accent.copy(alpha = 0.22f), accent.copy(alpha = 0.08f))
                    )
                )
        ) {
            Icon(
                if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(32.dp)
            )
        }
        IconButton(onClick = onNext) {
            Icon(Icons.Default.SkipNext, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(32.dp))
        }
    }
}
