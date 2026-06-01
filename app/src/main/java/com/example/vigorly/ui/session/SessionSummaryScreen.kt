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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.SportsScore
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.data.model.SessionSummary
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.DisplayStat
import com.example.vigorly.ui.theme.HeadlineLgMobile
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer
import com.example.vigorly.ui.workout.WorkoutDetailSectionEnter
import com.example.vigorly.ui.workout.WorkoutDetailStartCta
import com.example.vigorly.ui.workout.rememberWorkoutDetailVisible
import com.example.vigorly.util.TimeFormatter

@Composable
fun SessionSummaryScreen(
    summary: SessionSummary,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentVisible = rememberWorkoutDetailVisible()
    val exerciseProgress = if (summary.totalExercises > 0) {
        summary.exercisesCompleted.toFloat() / summary.totalExercises
    } else 1f

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.ContainerMargin, vertical = Dimens.Xl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 0) {
            Icon(
                Icons.Rounded.CheckCircle,
                contentDescription = null,
                tint = PrimaryAccent,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(PrimaryAccent.copy(alpha = 0.16f))
                    .padding(10.dp)
            )
        }

        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 100) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    stringResource(R.string.session_complete),
                    style = LabelCaps.copy(fontSize = 11.sp),
                    color = PrimaryAccent,
                    modifier = Modifier.padding(top = Dimens.Md)
                )
                Text(
                    summary.workoutName,
                    style = HeadlineLgMobile.copy(fontSize = 26.sp),
                    color = OnSurface,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = Dimens.Sm)
                )
                Text(
                    stringResource(R.string.summary_congrats),
                    style = BodyMd.copy(fontSize = 15.sp),
                    color = OnSurfaceVariant.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = Dimens.Xs)
                )
            }
        }

        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 220) {
            WorkoutSessionTimerRing(
                primaryText = TimeFormatter.formatElapsed(summary.elapsedSeconds),
                secondaryText = stringResource(R.string.session_elapsed_label),
                progress = exerciseProgress.coerceIn(0f, 1f),
                accent = PrimaryAccent,
                modifier = Modifier.padding(vertical = Dimens.Lg)
            )
        }

        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 360) {
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.Md)) {
                SummaryStatTile(
                    icon = Icons.Rounded.Schedule,
                    label = stringResource(R.string.summary_stat_duration),
                    value = "${summary.durationMinutes}",
                    suffix = "min",
                    accent = Primary
                )
                SummaryStatTile(
                    icon = Icons.Rounded.LocalFireDepartment,
                    label = stringResource(R.string.summary_stat_calories),
                    value = "${summary.caloriesBurned}",
                    suffix = "kcal",
                    accent = PrimaryAccent
                )
                SummaryStatTile(
                    icon = Icons.Rounded.SportsScore,
                    label = stringResource(R.string.summary_stat_exercises),
                    value = "${summary.exercisesCompleted}/${summary.totalExercises}",
                    suffix = null,
                    accent = PrimaryContainer,
                    highlight = true
                )
            }
        }

        Spacer(Modifier.height(Dimens.Xl))

        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 500) {
            WorkoutDetailStartCta(
                onClick = onDone,
                modifier = Modifier.fillMaxWidth(),
                labelRes = R.string.session_done,
                showPlayIcon = false
            )
        }

        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 600) {
            Text(
                stringResource(R.string.session_saved_hint),
                style = BodyMd.copy(fontSize = 13.sp),
                color = OnSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = Dimens.Md)
            )
        }
    }
}

@Composable
private fun SummaryStatTile(
    icon: ImageVector,
    label: String,
    value: String,
    suffix: String?,
    accent: androidx.compose.ui.graphics.Color,
    highlight: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        accent.copy(alpha = if (highlight) 0.16f else 0.12f),
                        accent.copy(alpha = 0.03f)
                    )
                )
            )
            .padding(horizontal = Dimens.Md, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = accent, modifier = Modifier.size(20.dp))
        }
        Column(
            Modifier
                .weight(1f)
                .padding(horizontal = Dimens.Md)
        ) {
            Text(label, style = LabelCaps.copy(fontSize = 9.sp), color = OnSurfaceVariant.copy(0.85f))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    value,
                    style = DisplayStat.copy(fontSize = 24.sp, lineHeight = 26.sp),
                    color = if (highlight) accent else OnSurface,
                    fontWeight = FontWeight.Bold
                )
                suffix?.let {
                    Text(
                        it,
                        style = BodyMd.copy(fontSize = 13.sp),
                        color = OnSurfaceVariant.copy(0.65f),
                        modifier = Modifier.padding(start = 4.dp, bottom = 3.dp)
                    )
                }
            }
        }
    }
}
