package com.example.vigorly.ui.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.vigorly.R
import com.example.vigorly.data.model.RecentActivity
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.ui.components.ActivityRings
import com.example.vigorly.ui.components.DailyTipCard
import com.example.vigorly.ui.components.RecommendedWorkoutCard
import com.example.vigorly.ui.components.StreakCard
import com.example.vigorly.ui.components.VigorlyOutlineCard
import com.example.vigorly.ui.components.WeeklyGoalCard
import com.example.vigorly.ui.iconForName
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.ButtonText
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.DisplayStat
import com.example.vigorly.ui.theme.HeadlineLgMobile
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer

@Composable
fun DashboardScreen(
    repository: VigorlyRepository,
    onActivityDetailClick: () -> Unit = {},
    onRecommendedWorkoutClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val goals by repository.dailyGoals.collectAsState()
    val weeklyGoal by repository.weeklyGoal.collectAsState()
    val recent by repository.recentActivity.collectAsState()
    val profile by repository.profile.collectAsState()
    val dailyTip by repository.dailyTip.collectAsState()
    val recommended = repository.getRecommendedWorkout()
    val firstName = profile.displayName.substringBefore(" ").ifBlank { profile.displayName }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.ContainerMargin, vertical = Dimens.Lg)
    ) {
            Column(Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.dashboard_greeting, firstName),
                    style = HeadlineLgMobile,
                    color = OnSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    stringResource(R.string.dashboard_ready),
                    style = BodyMd,
                    color = OnSurfaceVariant,
                    modifier = Modifier.padding(top = Dimens.Xs)
                )
            }

            Spacer(Modifier.height(Dimens.Md))

            ActivityRingsHeroSection(
                moveProgress = goals.moveProgress,
                exerciseProgress = goals.exerciseProgress,
                standProgress = goals.standProgress,
                centerPercent = goals.dailyGoalPercent,
                onClick = onActivityDetailClick
            )

            Spacer(Modifier.height(Dimens.Md))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = stringResource(R.string.daily_goal_label),
                    style = LabelCaps,
                    color = OnSurfaceVariant
                )
            }

            Spacer(Modifier.height(Dimens.Sm))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Dimens.Md)) {
                ActivityMetricTile(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.LocalFireDepartment,
                    label = stringResource(R.string.metric_move),
                    value = "${goals.moveCalories}",
                    suffix = "/ ${goals.moveCaloriesGoal} kcal",
                    accent = PrimaryAccent
                )
                ActivityMetricTile(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.DirectionsWalk,
                    label = stringResource(R.string.metric_steps),
                    value = "%,d".format(goals.steps),
                    suffix = "/ ${goals.stepsGoal / 1000}k",
                    accent = PrimaryContainer
                )
            }

            Spacer(Modifier.height(Dimens.Md))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Dimens.Md)) {
                ActivityMetricTile(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.FitnessCenter,
                    label = stringResource(R.string.metric_exercise),
                    value = "${goals.exerciseMinutes}",
                    suffix = "/ ${goals.exerciseMinutesGoal} min",
                    accent = Primary
                )
                ActivityMetricTile(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.DirectionsWalk,
                    label = stringResource(R.string.metric_stand),
                    value = "${goals.standHours}",
                    suffix = "/ ${goals.standHoursGoal} h",
                    accent = PrimaryAccent.copy(0.85f)
                )
            }

            Spacer(Modifier.height(Dimens.Lg))

            StreakCard(streakDays = profile.activeStreakDays, modifier = Modifier.padding(bottom = Dimens.Md))
            DailyTipCard(tip = dailyTip, modifier = Modifier.padding(bottom = Dimens.Md))
            recommended?.let { workout ->
                RecommendedWorkoutCard(
                    workout = workout,
                    onClick = { onRecommendedWorkoutClick(workout.id) },
                    modifier = Modifier.padding(bottom = Dimens.Md)
                )
            }
            WeeklyGoalCard(goal = weeklyGoal, modifier = Modifier.padding(bottom = Dimens.Md))
            RecentSection(recent)
            Spacer(Modifier.height(Dimens.Md))
        }
}

@Composable
private fun ActivityRingsHeroSection(
    moveProgress: Float,
    exerciseProgress: Float,
    standProgress: Float,
    centerPercent: Int,
    onClick: () -> Unit
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val heroMinHeight = screenHeight * 0.38f
    val interactionSource = remember { MutableInteractionSource() }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = heroMinHeight)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        val ringSize = maxWidth.coerceIn(280.dp, 380.dp)
        ActivityRings(
            moveProgress = moveProgress,
            exerciseProgress = exerciseProgress,
            standProgress = standProgress,
            centerPercent = centerPercent,
            ringSize = ringSize
        )
    }
}

@Composable
private fun ActivityMetricTile(
    modifier: Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    suffix: String,
    accent: androidx.compose.ui.graphics.Color
) {
    VigorlyOutlineCard(modifier = modifier) {
        Column(Modifier.padding(Dimens.Md)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(label, style = LabelCaps, color = OnSurfaceVariant)
                Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(Dimens.Sm))
            Text(value, style = DisplayStat, color = OnSurface, fontWeight = FontWeight.Bold)
            Text(suffix, style = BodyMd, color = OnSurfaceVariant)
        }
    }
}

@Composable
private fun RecentSection(items: List<RecentActivity>) {
    if (items.isEmpty()) return
    VigorlyOutlineCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(Dimens.Md)) {
            Text(stringResource(R.string.recent_section), style = ButtonText, color = OnSurface)
            Spacer(Modifier.height(Dimens.Sm))
            items.forEach { item ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = Dimens.Xs),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            iconForName(item.iconName),
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Column(Modifier.padding(start = Dimens.Sm)) {
                            Text(item.title, style = BodyMd, color = OnSurface)
                            Text(item.timeLabel, style = LabelCaps, color = OnSurfaceVariant)
                        }
                    }
                    Text("${item.durationMinutes}m", style = ButtonText, color = PrimaryAccent)
                }
            }
        }
    }
}
