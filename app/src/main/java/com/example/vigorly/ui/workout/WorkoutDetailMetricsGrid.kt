package com.example.vigorly.ui.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.FormatListNumbered
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.DisplayStat
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.util.WorkoutLabels

@Composable
fun WorkoutDetailMetricsGrid(
    durationMinutes: Int,
    intensity: String,
    calories: Int,
    exerciseCount: Int,
    accent: Color,
    modifier: Modifier = Modifier
) {
    val intensityProgress = WorkoutLabels.intensityProgress(intensity)
    val intensityLabel = WorkoutLabels.intensityLabel(intensity)

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Dimens.Md)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Dimens.Md)) {
            WorkoutMetricTile(
                modifier = Modifier.weight(1f),
                icon = Icons.Rounded.Schedule,
                label = stringResource(R.string.workout_detail_duration),
                value = "$durationMinutes",
                subLabel = "min",
                progress = (durationMinutes / 60f).coerceIn(0f, 1f),
                accent = accent
            )
            WorkoutMetricTile(
                modifier = Modifier.weight(1f),
                icon = Icons.Rounded.Whatshot,
                label = stringResource(R.string.workout_est_cal),
                value = "$calories",
                subLabel = "kcal",
                progress = (calories / 520f).coerceIn(0f, 1f),
                accent = PrimaryAccent
            )
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Dimens.Md)) {
            WorkoutMetricTile(
                modifier = Modifier.weight(1f),
                icon = Icons.Rounded.LocalFireDepartment,
                label = stringResource(R.string.workout_intensity),
                value = intensityLabel,
                subLabel = null,
                progress = intensityProgress,
                accent = accent,
                compactValue = true
            )
            WorkoutMetricTile(
                modifier = Modifier.weight(1f),
                icon = Icons.Rounded.FormatListNumbered,
                label = stringResource(R.string.workout_detail_session),
                value = "$exerciseCount",
                subLabel = stringResource(R.string.workout_exercises_short),
                progress = (exerciseCount / 8f).coerceIn(0.15f, 1f),
                accent = accent.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
private fun WorkoutMetricTile(
    icon: ImageVector,
    label: String,
    value: String,
    subLabel: String?,
    progress: Float,
    accent: Color,
    modifier: Modifier = Modifier,
    compactValue: Boolean = false
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        accent.copy(alpha = 0.14f),
                        accent.copy(alpha = 0.04f)
                    )
                )
            )
            .padding(horizontal = Dimens.Md, vertical = 14.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = LabelCaps.copy(fontSize = 9.sp), color = OnSurfaceVariant.copy(0.85f))
            Box(
                Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = accent, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                value,
                style = if (compactValue) {
                    DisplayStat.copy(fontSize = 20.sp, lineHeight = 22.sp)
                } else {
                    DisplayStat.copy(fontSize = 28.sp, lineHeight = 30.sp)
                },
                color = OnSurface,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            subLabel?.let {
                Text(
                    it,
                    style = BodyMd.copy(fontSize = 13.sp),
                    color = OnSurfaceVariant.copy(0.65f),
                    modifier = Modifier.padding(start = 4.dp, bottom = if (compactValue) 2.dp else 4.dp)
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height(3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(accent.copy(alpha = 0.12f))
        ) {
            Box(
                Modifier
                    .fillMaxWidth(progress.coerceIn(0.08f, 1f))
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(accent.copy(0.55f), accent)
                        )
                    )
            )
        }
    }
}
