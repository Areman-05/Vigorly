package com.example.vigorly.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import com.example.vigorly.ui.components.FlatProgressBar
import com.example.vigorly.util.LevelCalculator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.data.model.Milestone
import com.example.vigorly.data.model.WorkoutHistoryItem
import com.example.vigorly.ui.iconForName
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.ButtonText
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.DisplayStat
import com.example.vigorly.ui.theme.HeadlineLgMobile
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer
import com.example.vigorly.util.HistoryLabels

@Composable
fun ProfileSummaryCard(
    sessions: Int,
    totalMinutes: Int,
    totalCalories: Int,
    streakDays: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Primary.copy(alpha = 0.12f),
                        PrimaryAccent.copy(alpha = 0.05f)
                    )
                )
            )
            .padding(Dimens.Md)
    ) {
        Text(
            stringResource(R.string.profile_summary_title),
            style = LabelCaps.copy(fontSize = 10.sp),
            color = PrimaryAccent.copy(alpha = 0.85f),
            modifier = Modifier.padding(bottom = Dimens.Sm)
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            ProfileSummaryMetric(
                label = stringResource(R.string.profile_workouts_stat),
                value = "$sessions",
                accent = PrimaryAccent,
                modifier = Modifier.weight(1f)
            )
            ProfileSummaryMetric(
                label = stringResource(R.string.profile_stat_minutes),
                value = "$totalMinutes",
                suffix = "min",
                accent = Primary,
                modifier = Modifier.weight(1f)
            )
            ProfileSummaryMetric(
                label = stringResource(R.string.profile_stat_calories),
                value = "%,d".format(totalCalories),
                suffix = "kcal",
                accent = PrimaryContainer,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(Dimens.Sm))
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(PrimaryAccent.copy(alpha = 0.08f))
                .padding(horizontal = Dimens.Md, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(R.string.profile_streak_stat),
                style = LabelCaps.copy(fontSize = 9.sp),
                color = OnSurfaceVariant.copy(0.75f)
            )
            Text(
                "$streakDays ${stringResource(R.string.profile_stat_days)}",
                style = BodyMd.copy(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
                color = PrimaryAccent
            )
        }
    }
}

@Composable
private fun ProfileSummaryMetric(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier,
    suffix: String? = null
) {
    Column(modifier) {
        Text(label, style = LabelCaps.copy(fontSize = 9.sp), color = OnSurfaceVariant.copy(0.8f))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                value,
                style = DisplayStat.copy(fontSize = 22.sp, lineHeight = 24.sp),
                color = accent,
                fontWeight = FontWeight.Bold
            )
            suffix?.let {
                Text(
                    it,
                    style = BodyMd.copy(fontSize = 11.sp),
                    color = OnSurfaceVariant.copy(0.65f),
                    modifier = Modifier.padding(start = 2.dp, bottom = 2.dp)
                )
            }
        }
    }
}

@Composable
fun ProfileLevelCard(
    level: Int,
    progress: Float,
    workoutsUntilNext: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(R.string.profile_level_section),
                style = HeadlineMd.copy(fontSize = 16.sp),
                color = OnSurface,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                stringResource(R.string.profile_level_badge, level),
                style = LabelCaps.copy(fontSize = 10.sp),
                color = PrimaryAccent,
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(PrimaryAccent.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
        FlatProgressBar(
            progress = progress.coerceIn(0f, 1f),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.Md, bottom = Dimens.Sm)
                .height(6.dp),
            color = PrimaryAccent,
            trackColor = OnSurfaceVariant.copy(alpha = 0.15f)
        )
        Text(
            if (level >= LevelCalculator.MAX_LEVEL) {
                stringResource(R.string.level_max_reached)
            } else {
                stringResource(R.string.level_progress_hint, workoutsUntilNext, level + 1)
            },
            style = BodyMd.copy(fontSize = 13.sp),
            color = OnSurfaceVariant.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun ProfileShortcutTile(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accent: Color = PrimaryAccent
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
            .clickable(onClick = onClick)
            .padding(vertical = Dimens.Md, horizontal = Dimens.Sm),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(22.dp))
        }
        Text(
            label,
            style = BodyMd.copy(fontSize = 13.sp, fontWeight = FontWeight.Medium),
            color = OnSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = Dimens.Sm),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ProfileSectionHeader(
    title: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = HeadlineMd.copy(fontSize = 18.sp), color = OnSurface, fontWeight = FontWeight.SemiBold)
        if (actionLabel != null && onAction != null) {
            TextButton(onClick = onAction) {
                Text(actionLabel, style = ButtonText, color = PrimaryAccent)
            }
        }
    }
}

@Composable
fun ProfileMilestoneChip(
    milestone: Milestone,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .alpha(if (milestone.unlocked) 1f else 0.55f)
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(
                    if (milestone.unlocked) PrimaryAccent.copy(alpha = 0.14f)
                    else OnSurfaceVariant.copy(alpha = 0.08f)
                )
                .border(
                    1.dp,
                    if (milestone.unlocked) PrimaryAccent.copy(0.35f) else OnSurfaceVariant.copy(0.15f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                iconForName(milestone.iconName),
                contentDescription = null,
                tint = if (milestone.unlocked) PrimaryAccent else OnSurfaceVariant,
                modifier = Modifier.size(26.dp)
            )
        }
        Text(
            milestone.title,
            style = LabelCaps.copy(fontSize = 9.sp),
            color = if (milestone.unlocked) OnSurface else OnSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ProfileRecentSessionRow(
    item: WorkoutHistoryItem,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        accent.copy(alpha = 0.1f),
                        Primary.copy(alpha = 0.04f)
                    )
                )
            )
            .clickable(onClick = onClick)
            .padding(horizontal = Dimens.Md, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            iconForName(item.iconName),
            contentDescription = null,
            tint = accent,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.14f))
                .padding(9.dp)
        )
        Column(
            Modifier
                .weight(1f)
                .padding(horizontal = Dimens.Md)
        ) {
            Text(
                item.title,
                style = BodyMd.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                color = OnSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                HistoryLabels.displayTimestamp(item),
                style = BodyMd.copy(fontSize = 12.sp),
                color = OnSurfaceVariant.copy(alpha = 0.75f),
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                stringResource(R.string.history_duration_chip, item.durationMinutes).uppercase(),
                style = LabelCaps.copy(fontSize = 9.sp),
                color = accent
            )
            Text(
                stringResource(R.string.history_calories_chip, item.calories).uppercase(),
                style = LabelCaps.copy(fontSize = 8.sp),
                color = OnSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 3.dp)
            )
        }
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = OnSurfaceVariant.copy(alpha = 0.35f),
            modifier = Modifier
                .padding(start = Dimens.Xs)
                .size(18.dp)
        )
    }
}
