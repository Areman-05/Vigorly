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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.ui.components.FlatProgressBar
import com.example.vigorly.ui.components.WeeklyGoalCard
import com.example.vigorly.ui.profile.ProfileQuickMetrics
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.HeadlineLgMobile
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer
import com.example.vigorly.util.AthleticStatLabels
import com.example.vigorly.util.HistorySummaryCalculator
import com.example.vigorly.util.WeeklyActivityCalculator

private val StatTeal = Color(0xFF20C997)
private val StatViolet = Color(0xFF9775FA)

@Composable
fun InsightsScreen(
    repository: VigorlyRepository,
    modifier: Modifier = Modifier
) {
    val history by repository.history.collectAsState()
    val profile by repository.profile.collectAsState()
    val stats by repository.athleticStats.collectAsState()
    val weeklyGoal by repository.weeklyGoal.collectAsState()
    val summary = remember(history) { HistorySummaryCalculator.from(history) }
    val weeklyDays = remember(history) { WeeklyActivityCalculator.fromHistory(history) }
    val dominant = remember(stats) { AthleticStatLabels.dominantStat(stats) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.ContainerMargin, vertical = Dimens.Lg)
    ) {
        Text(
            stringResource(R.string.profile_insights),
            style = HeadlineLgMobile.copy(fontSize = 30.sp, lineHeight = 36.sp),
            color = PrimaryAccent,
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
            style = BodyMd.copy(fontSize = 17.sp),
            color = OnSurfaceVariant.copy(alpha = 0.85f),
            modifier = Modifier.padding(top = 6.dp, bottom = Dimens.Lg)
        )

        ProfileQuickMetrics(
            sessions = summary.totalSessions,
            totalMinutes = summary.totalMinutes,
            totalCalories = summary.totalCalories,
            streakDays = profile.activeStreakDays
        )

        WeeklyGoalCard(
            goal = weeklyGoal,
            modifier = Modifier.padding(top = Dimens.Lg)
        )

        InsightsSectionCard(
            modifier = Modifier.padding(top = Dimens.Lg)
        ) {
            WeeklyActivityBars(days = weeklyDays)
        }

        if (stats.isNotEmpty()) {
            InsightsSectionCard(
                modifier = Modifier.padding(top = Dimens.Lg)
            ) {
                Text(
                    stringResource(R.string.insights_athletic_section),
                    style = HeadlineMd.copy(fontSize = 20.sp),
                    color = PrimaryAccent,
                    fontWeight = FontWeight.SemiBold
                )
                dominant?.let { top ->
                    Text(
                        stringResource(
                            R.string.profile_athletic_highlight,
                            AthleticStatLabels.displayLabel(top.label),
                            top.value
                        ),
                        style = BodyMd.copy(fontSize = 16.sp, fontWeight = FontWeight.Medium),
                        color = PrimaryAccent,
                        modifier = Modifier
                            .padding(top = 8.dp, bottom = Dimens.Sm)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PrimaryAccent.copy(alpha = 0.12f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(top = Dimens.Sm)
                ) {
                    stats.forEach { stat ->
                        InsightsAthleticBar(
                            label = AthleticStatLabels.displayLabel(stat.label),
                            value = stat.value,
                            statKey = AthleticStatLabels.normalizeKey(stat.label)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(Dimens.Md))
    }
}

@Composable
private fun InsightsSectionCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Primary.copy(alpha = 0.14f),
                        PrimaryAccent.copy(alpha = 0.06f),
                        PrimaryContainer.copy(alpha = 0.04f)
                    )
                )
            )
            .padding(Dimens.Md)
    ) {
        content()
    }
}

@Composable
private fun InsightsAthleticBar(
    label: String,
    value: Int,
    statKey: String
) {
    val accent = when (statKey) {
        "strength", "power" -> PrimaryAccent
        "endurance", "stamina" -> PrimaryContainer
        "speed" -> StatViolet
        "mobility" -> StatTeal
        else -> Primary
    }
    Column {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text(
                label,
                style = BodyMd.copy(fontSize = 17.sp, fontWeight = FontWeight.Medium),
                color = OnSurface
            )
            Text(
                "$value",
                style = LabelCaps.copy(fontSize = 15.sp),
                color = accent,
                fontWeight = FontWeight.SemiBold
            )
        }
        FlatProgressBar(
            progress = value / 100f,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
                .height(6.dp),
            color = accent,
            trackColor = accent.copy(alpha = 0.15f)
        )
    }
}
