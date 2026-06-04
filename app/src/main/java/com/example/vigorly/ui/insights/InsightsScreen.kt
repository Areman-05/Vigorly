package com.example.vigorly.ui.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.core.testing.VigorlyTestTags
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.ui.components.ActivityMetricTile
import com.example.vigorly.ui.components.WeeklyGoalCard
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.HeadlineLgMobile
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer
import com.example.vigorly.util.HistorySummaryCalculator
import com.example.vigorly.util.WeeklyActivityCalculator

@Composable
fun InsightsScreen(
    repository: VigorlyRepository,
    modifier: Modifier = Modifier
) {
    val history by repository.history.collectAsState()
    val weeklyGoal by repository.weeklyGoal.collectAsState()
    val summary = remember(history) { HistorySummaryCalculator.from(history) }
    val weeklyDays = remember(history) { WeeklyActivityCalculator.fromHistory(history) }
    val weeklyMinutes = remember(weeklyDays) { weeklyDays.sumOf { it.minutes } }
    val avgMinutes = remember(summary) {
        if (summary.totalSessions > 0) summary.totalMinutes / summary.totalSessions else 0
    }
    val bestDay = remember(weeklyDays) { weeklyDays.maxByOrNull { it.minutes } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag(VigorlyTestTags.INSIGHTS)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.ContainerMargin, vertical = Dimens.Lg)
    ) {
        Text(
            stringResource(R.string.insights_screen_title),
            style = HeadlineLgMobile,
            color = OnSurface,
            fontWeight = FontWeight.Bold
        )
        Text(
            if (summary.totalSessions == 0) {
                stringResource(R.string.insights_empty_subtitle)
            } else {
                stringResource(
                    R.string.insights_subtitle_sessions,
                    summary.totalSessions,
                    summary.totalMinutes
                )
            },
            style = BodyMd,
            color = OnSurfaceVariant,
            modifier = Modifier.padding(top = Dimens.Xs, bottom = Dimens.Md)
        )

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.Md)
        ) {
            ActivityMetricTile(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Schedule,
                label = stringResource(R.string.insights_avg_session_short),
                current = avgMinutes,
                goal = 60,
                goalLabel = "60 min",
                accent = PrimaryAccent
            )
            ActivityMetricTile(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Timeline,
                label = stringResource(R.string.insights_weekly_minutes_short),
                current = weeklyMinutes,
                goal = 180,
                goalLabel = "180 min",
                accent = PrimaryContainer
            )
        }

        WeeklyGoalCard(
            goal = weeklyGoal,
            modifier = Modifier.padding(top = Dimens.Md)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.Md)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Primary.copy(alpha = 0.11f),
                            PrimaryAccent.copy(alpha = 0.05f)
                        )
                    )
                )
                .padding(Dimens.Md)
        ) {
            Text(
                stringResource(R.string.insights_weekly_activity),
                style = HeadlineMd.copy(fontSize = 20.sp),
                color = OnSurface,
                fontWeight = FontWeight.Bold
            )
            bestDay?.let { day ->
                if (day.minutes > 0) {
                    Text(
                        stringResource(
                            R.string.insights_best_day_value,
                            day.dayLabel,
                            day.minutes
                        ),
                        style = BodyMd.copy(fontSize = 15.sp),
                        color = PrimaryAccent,
                        modifier = Modifier.padding(top = 4.dp, bottom = Dimens.Sm)
                    )
                }
            }
            WeeklyActivityBars(
                days = weeklyDays,
                showHeader = false,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(Dimens.Md))
    }
}
