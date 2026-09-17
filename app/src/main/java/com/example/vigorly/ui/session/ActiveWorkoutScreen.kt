package com.example.vigorly.ui.session

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.core.testing.VigorlyTestTags
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.ui.components.CircularStatRing
import com.example.vigorly.ui.components.FrostedGlassCircleButton
import com.example.vigorly.ui.components.RemoteCoverImage
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.DisplayStat
import com.example.vigorly.ui.theme.GlassLabel
import com.example.vigorly.ui.theme.HeadlineLgMobile
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.StatRingMove
import com.example.vigorly.ui.theme.StatRingTime
import com.example.vigorly.ui.workout.WorkoutDetailStartCta
import com.example.vigorly.util.TimeFormatter
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

/**
 * Continuidad con Detalle/Entrenamientos: misma tipografía,
 * FrostedGlass en chrome (mismo cristal que las cards), CTA degradado.
 */
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

    LaunchedEffect(workoutId) {
        while (isActive) {
            delay(1000L)
            val active = repository.activeSession.value ?: break
            if (active.workoutId != workoutId) break
            if (active.restSecondsRemaining > 0 || !active.isPaused) {
                if (repository.tickSession()) {
                    onComplete()
                    break
                }
            }
        }
    }

    val current = session ?: return
    val steps = workout?.let { repository.sessionSteps(it) } ?: emptyList()
    val step = steps.getOrNull(current.currentExerciseIndex)
    val isWarmup = step?.isWarmup == true
    val isResting = current.restSecondsRemaining > 0
    val isPaused = current.isPaused && !isResting
    val warmupTotal = steps.count { it.isWarmup }
    val workTotal = steps.count { !it.isWarmup }
    val warmupIndex = steps.take(current.currentExerciseIndex + 1).count { it.isWarmup }
    val workIndex = steps.take(current.currentExerciseIndex + 1).count { !it.isWarmup }
    val phaseColor = when {
        isResting -> StatRingTime
        isWarmup -> StatRingMove
        else -> PrimaryAccent
    }

    val phaseProgress = when {
        isResting && current.restDurationSeconds > 0 ->
            1f - (current.restSecondsRemaining.toFloat() / current.restDurationSeconds)
        !isResting && current.exerciseDurationSeconds > 0 ->
            1f - (current.exerciseSecondsRemaining.toFloat() / current.exerciseDurationSeconds)
        else -> 0f
    }.coerceIn(0f, 1f)

    val sessionProgress = if (steps.isEmpty()) {
        0f
    } else {
        ((current.currentExerciseIndex + phaseProgress) / steps.size.toFloat()).coerceIn(0f, 1f)
    }

    Box(modifier = modifier.fillMaxSize()) {
        SessionStageBackground(showConfetti = false)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = Dimens.ContainerMargin)
                .testTag(VigorlyTestTags.WORKOUTS)
        ) {
            SessionHeaderBar(
                title = current.workoutName,
                coverUrl = workout?.heroImageUrl,
                elapsed = TimeFormatter.formatElapsed(current.elapsedSeconds),
                onBack = {
                    repository.cancelWorkoutSession()
                    onCancel()
                }
            )

            SessionProgressLine(progress = sessionProgress, accent = phaseColor)

            Spacer(Modifier.height(Dimens.Lg))

            if (isResting) {
                RestPhase(
                    secondsLeft = current.restSecondsRemaining,
                    progress = phaseProgress,
                    nextName = step?.name.orEmpty(),
                    nextSets = step?.detailLabel.orEmpty(),
                    nextIsWarmup = isWarmup,
                    accent = StatRingTime,
                    onSkip = repository::skipRest,
                    modifier = Modifier.weight(1f)
                )
            } else {
                ExercisePhase(
                    isWarmup = isWarmup,
                    stepLabel = if (isWarmup) {
                        stringResource(R.string.session_warmup_progress, warmupIndex, warmupTotal)
                    } else {
                        stringResource(R.string.session_exercise_progress, workIndex, workTotal)
                    },
                    exerciseName = step?.name ?: "—",
                    setsLabel = step?.detailLabel.orEmpty(),
                    secondsLeft = current.exerciseSecondsRemaining,
                    progress = phaseProgress,
                    paused = isPaused,
                    accent = phaseColor,
                    completeLabelRes = if (isWarmup) {
                        R.string.mark_warmup_done
                    } else {
                        R.string.mark_exercise_done
                    },
                    onPrevious = repository::previousExercise,
                    onTogglePause = repository::toggleSessionPause,
                    onNext = {
                        if (repository.markCurrentExerciseComplete()) onComplete()
                    },
                    onComplete = {
                        if (repository.markCurrentExerciseComplete()) onComplete()
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SessionHeaderBar(
    title: String,
    coverUrl: String?,
    elapsed: String,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = Dimens.Md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FrostedGlassCircleButton(
            onClick = onBack,
            modifier = Modifier.testTag(VigorlyTestTags.TOPBAR_BACK)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.go_back),
                tint = OnSurface,
                modifier = Modifier.size(20.dp)
            )
        }

        if (coverUrl != null) {
            Box(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.06f))
            ) {
                RemoteCoverImage(url = coverUrl, contentDescription = title)
            }
        }

        Text(
            text = title,
            style = BodyMd.copy(
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.15).sp
            ),
            color = OnSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        )

        Text(
            text = elapsed,
            style = BodyMd.copy(
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.15.sp
            ),
            color = GlassLabel.copy(alpha = 0.85f)
        )
    }
}

@Composable
private fun SessionProgressLine(
    progress: Float,
    accent: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(Color.White.copy(alpha = 0.12f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0.04f, 1f))
                .height(4.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(accent)
        )
    }
}

@Composable
private fun ExercisePhase(
    isWarmup: Boolean,
    stepLabel: String,
    exerciseName: String,
    setsLabel: String,
    secondsLeft: Int,
    progress: Float,
    paused: Boolean,
    accent: Color,
    completeLabelRes: Int,
    onPrevious: () -> Unit,
    onTogglePause: () -> Unit,
    onNext: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(
                    if (isWarmup) R.string.session_phase_warmup else R.string.session_phase_work
                ).uppercase(),
                style = BodyMd.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.4.sp
                ),
                color = accent
            )
            Text(
                text = stepLabel,
                style = BodyMd.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = GlassLabel.copy(alpha = 0.72f)
            )
        }

        Spacer(Modifier.weight(1f))

        SessionTimerRing(
            secondsLeft = secondsLeft,
            progress = progress,
            paused = paused,
            accent = accent,
            size = 228.dp
        )

        Text(
            text = exerciseName,
            style = HeadlineLgMobile.copy(
                fontSize = 28.sp,
                lineHeight = 34.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.4).sp
            ),
            color = OnSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 28.dp)
        )
        if (setsLabel.isNotBlank()) {
            Text(
                text = setsLabel,
                style = BodyMd.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = GlassLabel.copy(alpha = 0.78f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FrostedGlassCircleButton(onClick = onPrevious, size = 52.dp) {
                Icon(
                    Icons.Default.SkipPrevious,
                    null,
                    tint = OnSurface.copy(alpha = 0.9f),
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(20.dp))
            FrostedGlassCircleButton(onClick = onTogglePause, size = 76.dp) {
                Icon(
                    if (paused) Icons.Default.PlayArrow else Icons.Default.Pause,
                    null,
                    tint = accent,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(Modifier.width(20.dp))
            FrostedGlassCircleButton(onClick = onNext, size = 52.dp) {
                Icon(
                    Icons.Default.SkipNext,
                    null,
                    tint = OnSurface.copy(alpha = 0.9f),
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        WorkoutDetailStartCta(
            onClick = onComplete,
            labelRes = completeLabelRes,
            showPlayIcon = false,
            cornerRadius = 999.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.Lg, bottom = Dimens.Md)
        )
    }
}

@Composable
private fun RestPhase(
    secondsLeft: Int,
    progress: Float,
    nextName: String,
    nextSets: String,
    nextIsWarmup: Boolean,
    accent: Color,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.session_phase_rest).uppercase(),
            style = BodyMd.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.4.sp
            ),
            color = accent,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.weight(1f))

        SessionTimerRing(
            secondsLeft = secondsLeft,
            progress = progress,
            paused = false,
            accent = accent,
            size = 236.dp,
            caption = stringResource(R.string.session_rest_title)
        )

        if (nextName.isNotBlank()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(
                        if (nextIsWarmup) {
                            R.string.session_next_is_warmup
                        } else {
                            R.string.session_next_is_work
                        }
                    ).uppercase(),
                    style = BodyMd.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.1.sp
                    ),
                    color = if (nextIsWarmup) StatRingMove else PrimaryAccent
                )
                Text(
                    text = nextName,
                    style = HeadlineMd.copy(
                        fontSize = 22.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.3).sp
                    ),
                    color = OnSurface,
                    modifier = Modifier.padding(top = 8.dp)
                )
                if (nextSets.isNotBlank()) {
                    Text(
                        text = nextSets,
                        style = BodyMd.copy(fontSize = 15.sp),
                        color = GlassLabel.copy(alpha = 0.78f),
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        }

        Spacer(Modifier.weight(1f))

        Text(
            text = stringResource(R.string.session_skip_rest),
            style = BodyMd.copy(
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = OnSurface.copy(alpha = 0.88f),
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .clickable(onClick = onSkip)
                .padding(horizontal = 18.dp, vertical = 12.dp)
                .padding(bottom = Dimens.Md)
        )
    }
}

@Composable
private fun SessionTimerRing(
    secondsLeft: Int,
    progress: Float,
    paused: Boolean,
    accent: Color,
    size: Dp,
    caption: String? = null
) {
    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        CircularStatRing(
            progress = progress,
            accent = accent,
            size = size,
            strokeWidth = 9.dp
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = TimeFormatter.formatCountdown(secondsLeft),
                style = DisplayStat.copy(
                    fontSize = if (size >= 210.dp) 48.sp else 42.sp,
                    lineHeight = if (size >= 210.dp) 50.sp else 44.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-1.0).sp
                ),
                color = if (paused) OnSurface.copy(alpha = 0.4f) else OnSurface
            )
            Text(
                text = caption ?: stringResource(
                    if (paused) R.string.session_paused_label else R.string.session_countdown_label
                ),
                style = BodyMd.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.9.sp
                ),
                color = GlassLabel.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

