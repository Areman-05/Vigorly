package com.example.vigorly.ui.session

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.core.testing.VigorlyTestTags
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.ui.components.FrostedGlassCircleButton
import com.example.vigorly.ui.components.GlassSurface
import com.example.vigorly.ui.components.RemoteCoverImage
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.DisplayStat
import com.example.vigorly.ui.theme.GlassLabel
import com.example.vigorly.ui.theme.HeadlineLgMobile
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.PrimaryAccent
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
    val exercises = workout?.let { repository.flatExercises(it) } ?: emptyList()
    val exercise = exercises.getOrNull(current.currentExerciseIndex)
    val isResting = current.restSecondsRemaining > 0
    val isPaused = current.isPaused && !isResting

    val phaseProgress = when {
        isResting && current.restDurationSeconds > 0 ->
            1f - (current.restSecondsRemaining.toFloat() / current.restDurationSeconds)
        !isResting && current.exerciseDurationSeconds > 0 ->
            1f - (current.exerciseSecondsRemaining.toFloat() / current.exerciseDurationSeconds)
        else -> 0f
    }.coerceIn(0f, 1f)

    Column(
        modifier = modifier
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

        SessionStepRail(
            total = current.totalExercises,
            currentIndex = current.currentExerciseIndex,
            completedIds = current.completedExerciseIds,
            exerciseIds = exercises.map { it.id }
        )

        Spacer(Modifier.height(Dimens.Lg))

        if (isResting) {
            RestPhase(
                secondsLeft = current.restSecondsRemaining,
                progress = phaseProgress,
                nextName = exercise?.name.orEmpty(),
                nextSets = exercise?.setsRepsLabel.orEmpty(),
                onSkip = repository::skipRest,
                modifier = Modifier.weight(1f)
            )
        } else {
            ExercisePhase(
                stepLabel = stringResource(
                    R.string.session_exercise_progress,
                    current.currentExerciseIndex + 1,
                    current.totalExercises
                ),
                exerciseName = exercise?.name ?: "—",
                setsLabel = exercise?.setsRepsLabel.orEmpty(),
                secondsLeft = current.exerciseSecondsRemaining,
                progress = phaseProgress,
                paused = isPaused,
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
private fun SessionStepRail(
    total: Int,
    currentIndex: Int,
    completedIds: Set<String>,
    exerciseIds: List<String>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        repeat(total.coerceAtLeast(1)) { index ->
            val id = exerciseIds.getOrNull(index)
            val done = id != null && id in completedIds
            val current = index == currentIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(3.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(
                        when {
                            done || current -> PrimaryAccent
                            else -> Color.White.copy(alpha = 0.14f)
                        }
                    )
            )
        }
    }
}

@Composable
private fun ExercisePhase(
    stepLabel: String,
    exerciseName: String,
    setsLabel: String,
    secondsLeft: Int,
    progress: Float,
    paused: Boolean,
    onPrevious: () -> Unit,
    onTogglePause: () -> Unit,
    onNext: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stepLabel,
            style = BodyMd.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.1.sp
            ),
            color = PrimaryAccent
        )

        Text(
            text = exerciseName,
            style = HeadlineLgMobile.copy(
                fontSize = 34.sp,
                lineHeight = 40.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp
            ),
            color = OnSurface,
            modifier = Modifier.padding(top = 10.dp)
        )

        if (setsLabel.isNotBlank()) {
            Text(
                text = setsLabel,
                style = BodyMd.copy(
                    fontSize = 16.sp,
                    lineHeight = 23.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.15.sp
                ),
                color = GlassLabel.copy(alpha = 0.88f),
                modifier = Modifier.padding(top = 10.dp)
            )
        }

        Spacer(Modifier.weight(1f))

        Text(
            text = TimeFormatter.formatCountdown(secondsLeft),
            style = DisplayStat.copy(
                fontSize = 56.sp,
                lineHeight = 60.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-1.2).sp
            ),
            color = if (paused) OnSurface.copy(alpha = 0.38f) else OnSurface
        )
        Text(
            text = if (paused) {
                stringResource(R.string.session_paused_label)
            } else {
                stringResource(R.string.session_countdown_label)
            },
            style = BodyMd.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.0.sp
            ),
            color = GlassLabel.copy(alpha = 0.65f),
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(Modifier.height(18.dp))
        PhaseLine(progress = progress)
        Spacer(Modifier.height(Dimens.Xl))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FrostedGlassCircleButton(onClick = onPrevious, size = 54.dp) {
                Icon(
                    Icons.Default.SkipPrevious,
                    null,
                    tint = OnSurface.copy(alpha = 0.9f),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.width(18.dp))
            FrostedGlassCircleButton(onClick = onTogglePause, size = 66.dp) {
                Icon(
                    if (paused) Icons.Default.PlayArrow else Icons.Default.Pause,
                    null,
                    tint = PrimaryAccent,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(Modifier.width(18.dp))
            FrostedGlassCircleButton(onClick = onNext, size = 54.dp) {
                Icon(
                    Icons.Default.SkipNext,
                    null,
                    tint = OnSurface.copy(alpha = 0.9f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        WorkoutDetailStartCta(
            onClick = onComplete,
            labelRes = R.string.mark_exercise_done,
            showPlayIcon = false,
            cornerRadius = 18.dp,
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
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.session_rest),
            style = BodyMd.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.1.sp
            ),
            color = PrimaryAccent
        )

        Text(
            text = stringResource(R.string.session_rest_title),
            style = HeadlineLgMobile.copy(
                fontSize = 34.sp,
                lineHeight = 40.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp
            ),
            color = OnSurface,
            modifier = Modifier.padding(top = 10.dp)
        )

        Text(
            text = stringResource(R.string.session_rest_hint),
            style = BodyMd.copy(
                fontSize = 16.sp,
                lineHeight = 23.sp,
                letterSpacing = 0.1.sp
            ),
            color = GlassLabel.copy(alpha = 0.88f),
            modifier = Modifier.padding(top = 10.dp)
        )

        Spacer(Modifier.weight(1f))

        Text(
            text = TimeFormatter.formatCountdown(secondsLeft),
            style = DisplayStat.copy(
                fontSize = 64.sp,
                lineHeight = 68.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-1.4).sp
            ),
            color = OnSurface
        )

        Spacer(Modifier.height(18.dp))
        PhaseLine(progress = progress, soft = true)

        if (nextName.isNotBlank()) {
            Spacer(Modifier.height(Dimens.Xl))

            Text(
                text = stringResource(R.string.session_up_next),
                style = HeadlineMd.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.2).sp
                ),
                color = OnSurface
            )
            Text(
                text = nextName,
                style = HeadlineMd.copy(
                    fontSize = 21.sp,
                    lineHeight = 26.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.3).sp
                ),
                color = OnSurface,
                modifier = Modifier.padding(top = 10.dp)
            )
            if (nextSets.isNotBlank()) {
                Text(
                    text = nextSets,
                    style = BodyMd.copy(
                        fontSize = 15.sp,
                        letterSpacing = 0.1.sp
                    ),
                    color = GlassLabel.copy(alpha = 0.82f),
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }

        Spacer(Modifier.weight(1f))

        GlassSurface(
            onClick = onSkip,
            shape = RoundedCornerShape(999.dp),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = Dimens.Lg)
        ) {
            Text(
                text = stringResource(R.string.session_skip_rest),
                style = BodyMd.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.1.sp
                ),
                color = OnSurface,
                modifier = Modifier.padding(horizontal = 22.dp, vertical = 12.dp)
            )
        }
    }
}

@Composable
private fun PhaseLine(
    progress: Float,
    soft: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(9.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(Color.White.copy(alpha = 0.12f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0.02f, 1f))
                .height(9.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(
                    if (soft) PrimaryAccent.copy(alpha = 0.7f) else PrimaryAccent
                )
        )
    }
}

