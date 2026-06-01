package com.example.vigorly.ui.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Whatshot
import androidx.compose.material3.HorizontalDivider
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
import com.example.vigorly.ui.theme.HeadlineLgMobile
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.util.WorkoutLabels

@Composable
fun WorkoutDetailOverview(
    durationMinutes: Int,
    intensity: String,
    calories: Int,
    targetMuscles: String,
    targetDescription: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Primary.copy(alpha = 0.1f),
                        PrimaryAccent.copy(alpha = 0.04f)
                    )
                )
            )
            .padding(Dimens.Md)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.Lg)
        ) {
            OverviewStat(
                modifier = Modifier.weight(1f),
                icon = Icons.Rounded.Schedule,
                iconTint = accent,
                label = stringResource(R.string.workout_detail_duration),
                value = stringResource(R.string.workout_duration_chip, durationMinutes)
            )
            OverviewStat(
                modifier = Modifier.weight(1f),
                icon = Icons.Rounded.Whatshot,
                iconTint = PrimaryAccent,
                label = stringResource(R.string.workout_est_cal),
                value = "$calories",
                valueSuffix = "kcal"
            )
        }

        Spacer(Modifier.height(Dimens.Md))

        WorkoutDetailStatRow(
            icon = Icons.Rounded.LocalFireDepartment,
            iconTint = accent,
            label = stringResource(R.string.workout_intensity),
            value = WorkoutLabels.intensityLabel(intensity),
            emphasizeValue = true
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = Dimens.Md),
            color = OnSurfaceVariant.copy(alpha = 0.12f)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.Sm)
        ) {
            Icon(
                Icons.Rounded.FitnessCenter,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(20.dp)
            )
            Text(
                stringResource(R.string.workout_target).uppercase(),
                style = LabelCaps.copy(fontSize = 10.sp),
                color = OnSurfaceVariant.copy(alpha = 0.75f)
            )
        }
        Text(
            targetMuscles,
            style = DisplayStat.copy(fontSize = 28.sp, lineHeight = 30.sp),
            color = OnSurface,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 6.dp)
        )
        Text(
            targetDescription,
            style = BodyMd.copy(fontSize = 14.sp),
            color = OnSurfaceVariant.copy(alpha = 0.72f),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun OverviewStat(
    icon: ImageVector,
    iconTint: Color,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueSuffix: String? = null
) {
    Column(modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(18.dp))
            Text(label, style = LabelCaps.copy(fontSize = 10.sp), color = OnSurfaceVariant.copy(0.8f))
        }
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(
                value,
                style = DisplayStat.copy(fontSize = 26.sp, lineHeight = 28.sp),
                color = OnSurface,
                fontWeight = FontWeight.Bold
            )
            valueSuffix?.let {
                Text(
                    it,
                    style = BodyMd.copy(fontSize = 13.sp),
                    color = OnSurfaceVariant.copy(0.65f),
                    modifier = Modifier.padding(start = 3.dp, bottom = 3.dp)
                )
            }
        }
    }
}

@Composable
fun WorkoutDetailStatRow(
    icon: ImageVector,
    iconTint: Color,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    emphasizeValue: Boolean = false
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.Sm)
        ) {
            Box(
                Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconTint, modifier = Modifier.size(16.dp))
            }
            Text(label, style = LabelCaps, color = OnSurfaceVariant)
        }
        Text(
            value,
            style = if (emphasizeValue) {
                HeadlineLgMobile.copy(fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            } else {
                BodyMd.copy(fontSize = 15.sp, fontWeight = FontWeight.Medium)
            },
            color = if (emphasizeValue) iconTint else OnSurface
        )
    }
}
