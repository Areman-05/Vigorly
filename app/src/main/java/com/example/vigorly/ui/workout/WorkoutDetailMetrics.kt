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
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Schedule
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
import com.example.vigorly.ui.components.IntensityBadge
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.DisplayStat
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant

@Composable
fun WorkoutDetailMetricsRow(
    durationMinutes: Int,
    intensity: String,
    calories: Int,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.Sm)
    ) {
        DetailMetricTile(
            modifier = Modifier.weight(1f),
            icon = Icons.Rounded.Schedule,
            label = stringResource(R.string.workout_detail_duration),
            value = stringResource(R.string.workout_duration_chip, durationMinutes),
            accent = accent
        )
        DetailMetricTile(
            modifier = Modifier.weight(1f),
            icon = Icons.Rounded.LocalFireDepartment,
            label = stringResource(R.string.workout_intensity),
            value = null,
            accent = accent,
            intensity = intensity
        )
        DetailMetricTile(
            modifier = Modifier.weight(1f),
            icon = Icons.Rounded.Flag,
            label = stringResource(R.string.workout_est_cal),
            value = "$calories",
            accent = accent,
            valueSuffix = "kcal"
        )
    }
}

@Composable
fun WorkoutDetailTargetStrip(
    targetMuscles: String,
    targetDescription: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        accent.copy(alpha = 0.14f),
                        accent.copy(alpha = 0.03f)
                    )
                )
            )
            .padding(horizontal = Dimens.Md, vertical = Dimens.Md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                stringResource(R.string.workout_target),
                style = LabelCaps.copy(fontSize = 10.sp),
                color = OnSurfaceVariant.copy(alpha = 0.75f)
            )
            Text(
                targetMuscles,
                style = BodyMd.copy(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
                color = OnSurface,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                targetDescription,
                style = BodyMd.copy(fontSize = 13.sp),
                color = OnSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
private fun DetailMetricTile(
    icon: ImageVector,
    label: String,
    value: String?,
    accent: Color,
    modifier: Modifier = Modifier,
    intensity: String? = null,
    valueSuffix: String? = null
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        accent.copy(alpha = 0.1f),
                        accent.copy(alpha = 0.02f)
                    )
                )
            )
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = LabelCaps.copy(fontSize = 9.sp), color = OnSurfaceVariant.copy(0.8f))
            Box(
                Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = accent, modifier = Modifier.size(14.dp))
            }
        }
        Spacer(Modifier.height(8.dp))
        if (intensity != null) {
            IntensityBadge(intensity = intensity)
        } else {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    value.orEmpty(),
                    style = DisplayStat.copy(fontSize = 22.sp, lineHeight = 24.sp),
                    color = OnSurface,
                    fontWeight = FontWeight.Bold
                )
                valueSuffix?.let {
                    Text(
                        it,
                        style = BodyMd.copy(fontSize = 12.sp),
                        color = OnSurfaceVariant.copy(0.65f),
                        modifier = Modifier.padding(start = 2.dp, bottom = 2.dp)
                    )
                }
            }
        }
    }
}
