package com.example.vigorly.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.ui.components.AthleticRadarChart
import com.example.vigorly.ui.components.EmptyState
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.HeadlineLgMobile
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.workout.WorkoutDetailSectionEnter
import com.example.vigorly.ui.workout.WorkoutTypeTheme
import com.example.vigorly.ui.workout.rememberWorkoutDetailVisible
import com.example.vigorly.util.HistoryLabels
import com.example.vigorly.util.HistorySummaryCalculator
import com.example.vigorly.util.LevelCalculator

private const val RECENT_SESSIONS_LIMIT = 3
private const val MILESTONE_PREVIEW_LIMIT = 4

@Composable
fun ProfileScreen(
    repository: VigorlyRepository,
    onViewAllMilestones: () -> Unit = {},
    onOpenWorkouts: () -> Unit = {},
    onOpenInsights: () -> Unit = {},
    onOpenHistory: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onHistoryItemClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val profile by repository.profile.collectAsState()
    val stats by repository.athleticStats.collectAsState()
    val milestones by repository.milestones.collectAsState()
    val history by repository.history.collectAsState()
    val summary = remember(history) { HistorySummaryCalculator.from(history) }
    val recentSessions = remember(history) {
        history.sortedByDescending { it.completedAtMillis }.take(RECENT_SESSIONS_LIMIT)
    }
    val previewMilestones = remember(milestones) { milestones.take(MILESTONE_PREVIEW_LIMIT) }
    val unlockedCount = milestones.count { it.unlocked }
    val contentVisible = rememberWorkoutDetailVisible()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.ContainerMargin, vertical = Dimens.Lg)
    ) {
        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 0) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    stringResource(R.string.profile_title),
                    style = HeadlineLgMobile.copy(fontSize = 28.sp),
                    color = OnSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onOpenSettings, modifier = Modifier.size(44.dp)) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = stringResource(R.string.profile_open_settings),
                        tint = PrimaryAccent,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }

        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 80) {
            ProfileHeroCard(
                displayName = profile.displayName,
                avatarUrl = profile.avatarUrl,
                level = profile.level,
                isProMember = profile.isProMember,
                modifier = Modifier.padding(top = Dimens.Md)
            )
        }

        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 160) {
            ProfileSummaryCard(
                sessions = profile.totalWorkouts,
                totalMinutes = summary.totalMinutes,
                totalCalories = summary.totalCalories,
                streakDays = profile.activeStreakDays,
                modifier = Modifier.padding(top = Dimens.Md)
            )
        }

        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 240) {
            ProfileLevelCard(
                level = profile.level,
                progress = LevelCalculator.progressToNextLevel(profile.totalWorkouts),
                workoutsUntilNext = LevelCalculator.workoutsUntilNextLevel(profile.totalWorkouts),
                modifier = Modifier.padding(top = Dimens.Md)
            )
        }

        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 320) {
            Column(Modifier.padding(top = Dimens.Lg)) {
                Text(
                    stringResource(R.string.profile_shortcuts),
                    style = LabelCaps.copy(fontSize = 11.sp),
                    color = PrimaryAccent.copy(alpha = 0.85f),
                    modifier = Modifier.padding(bottom = Dimens.Sm)
                )
                ProfileQuickActions(
                    onOpenWorkouts = onOpenWorkouts,
                    onOpenInsights = onOpenInsights,
                    onOpenHistory = onOpenHistory,
                    onViewAllMilestones = onViewAllMilestones
                )
            }
        }

        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 400) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.Lg)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Primary.copy(alpha = 0.08f),
                                PrimaryAccent.copy(alpha = 0.03f)
                            )
                        )
                    )
                    .padding(Dimens.Md)
            ) {
                ProfileSectionHeader(
                    title = stringResource(R.string.profile_athletic),
                    actionLabel = null,
                    onAction = null
                )
                AthleticRadarChart(stats, Modifier.padding(top = Dimens.Sm))
            }
        }

        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 480) {
            Column(Modifier.padding(top = Dimens.Lg)) {
                ProfileSectionHeader(
                    title = stringResource(R.string.profile_milestones),
                    actionLabel = stringResource(R.string.profile_view_all),
                    onAction = onViewAllMilestones
                )
                Text(
                    stringResource(
                        R.string.profile_milestones_unlocked,
                        unlockedCount,
                        milestones.size
                    ),
                    style = LabelCaps.copy(fontSize = 10.sp),
                    color = OnSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 2.dp, bottom = Dimens.Sm)
                )
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    previewMilestones.forEach { milestone ->
                        ProfileMilestoneChip(
                            milestone = milestone,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 560) {
            Column(Modifier.padding(top = Dimens.Lg)) {
                ProfileSectionHeader(
                    title = stringResource(R.string.profile_recent_history),
                    actionLabel = if (history.isNotEmpty()) stringResource(R.string.profile_open_history) else null,
                    onAction = if (history.isNotEmpty()) onOpenHistory else null
                )
                if (recentSessions.isEmpty()) {
                    EmptyState(
                        title = stringResource(R.string.profile_no_recent_sessions),
                        message = stringResource(R.string.profile_no_recent_sessions_hint),
                        modifier = Modifier.padding(top = Dimens.Sm)
                    )
                } else {
                    recentSessions.forEach { item ->
                        val type = HistoryLabels.parseWorkoutType(item.workoutType)
                        val accent = type?.let { WorkoutTypeTheme.accent(it) } ?: PrimaryAccent
                        ProfileRecentSessionRow(
                            item = item,
                            accent = accent,
                            onClick = { onHistoryItemClick(item.id) },
                            modifier = Modifier.padding(top = Dimens.Sm)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(Dimens.Md))
    }
}
