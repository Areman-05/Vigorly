package com.example.vigorly.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.data.model.WeeklyGoal
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer
import com.example.vigorly.ui.workout.WorkoutDetailStartCta
import com.example.vigorly.util.HistorySummary

@Composable
fun ProfileStatsSection(
    summary: HistorySummary,
    weeklyGoal: WeeklyGoal,
    onOpenInsights: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            stringResource(R.string.profile_insights),
            style = LabelCaps.copy(fontSize = 11.sp),
            color = PrimaryAccent.copy(alpha = 0.85f),
            modifier = Modifier.padding(bottom = Dimens.Sm)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Dimens.Md),
            verticalArrangement = Arrangement.spacedBy(Dimens.Sm)
        ) {
            StatsInsightRow(
                icon = Icons.Default.Insights,
                label = stringResource(R.string.profile_stats_sessions),
                value = "${summary.totalSessions}",
                accent = PrimaryAccent
            )
            StatsInsightRow(
                icon = Icons.Default.Schedule,
                label = stringResource(R.string.profile_stat_minutes),
                value = "${summary.totalMinutes} min",
                accent = Primary
            )
            StatsInsightRow(
                icon = Icons.Default.LocalFireDepartment,
                label = stringResource(R.string.profile_stat_calories),
                value = "%,d kcal".format(summary.totalCalories),
                accent = PrimaryContainer
            )
            StatsInsightRow(
                icon = Icons.Default.Insights,
                label = stringResource(R.string.weekly_goal_title),
                value = stringResource(
                    R.string.profile_weekly_progress,
                    weeklyGoal.completedSessions,
                    weeklyGoal.targetSessions
                ),
                accent = PrimaryAccent.copy(alpha = 0.9f)
            )
        }
        WorkoutDetailStartCta(
            onClick = onOpenInsights,
            modifier = Modifier.fillMaxWidth(),
            labelRes = R.string.profile_view_stats,
            showPlayIcon = false
        )
    }
}

@Composable
private fun StatsInsightRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    accent: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = accent,
            modifier = Modifier.size(22.dp)
        )
        Text(
            label,
            style = BodyMd.copy(fontSize = 14.sp),
            color = OnSurfaceVariant.copy(alpha = 0.85f),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = Dimens.Sm)
        )
        Text(
            value,
            style = BodyMd.copy(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
            color = accent
        )
    }
}
