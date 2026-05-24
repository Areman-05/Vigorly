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
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
        if (session == null) repository.startWorkoutSession(workoutId)
    }

    LaunchedEffect(session?.isPaused) {
        while (true) {
            delay(1000)
            if (repository.activeSession.value?.isPaused != true) {
                repository.tickSession()
            }
        }
    }

    val current = session ?: return
    val exercises = workout?.let { repository.flatExercises(it) } ?: emptyList()
    val exercise = exercises.getOrNull(current.currentExerciseIndex)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.ContainerMargin),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(current.workoutName, style = HeadlineLgMobile, color = OnSurface)
        Text(TimeFormatter.formatElapsed(current.elapsedSeconds), style = DisplayStat, color = Primary)
        Text(
            "EXERCISE ${current.currentExerciseIndex + 1} / ${current.totalExercises}",
            style = LabelCaps,
            color = OnSurfaceVariant
        )
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
            }
        }
        Spacer(Modifier.height(Dimens.Lg))
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
        Spacer(Modifier.weight(1f))
        if (current.currentExerciseIndex >= current.totalExercises - 1) {
            Button(
                onClick = {
                    repository.completeWorkoutSession()
                    onComplete()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainer, contentColor = OnPrimaryContainer)
            ) {
                Text("FINISH WORKOUT", style = ButtonText)
            }
        } else {
            Button(
                onClick = {
                    repository.cancelWorkoutSession()
                    onCancel()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = OnSurfaceVariant.copy(alpha = 0.2f))
            ) {
                Text("Cancel session", style = BodyMd, color = OnSurfaceVariant)
            }
        }
    }
}
