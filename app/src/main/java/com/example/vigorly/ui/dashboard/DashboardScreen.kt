package com.example.vigorly.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.core.testing.VigorlyTestTags
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.di.AppViewModelFactory
import com.example.vigorly.presentation.feature.dashboard.DashboardViewModel
import com.example.vigorly.ui.components.ActivityRings
import com.example.vigorly.ui.components.DailyTipCard
import com.example.vigorly.ui.components.RecommendedWorkoutCard
import com.example.vigorly.ui.components.StreakBannerPopup
import com.example.vigorly.ui.components.WeeklyGoalCard
import com.example.vigorly.ui.workout.WorkoutDetailStartCta
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

@Composable
fun DashboardScreen(
    repository: VigorlyRepository,
    onActivityDetailClick: () -> Unit = {},
    onRecommendedWorkoutClick: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = viewModel(factory = AppViewModelFactory(repository))
) {
    val goals by viewModel.dailyGoals.collectAsState()
    val weeklyGoal by viewModel.weeklyGoal.collectAsState()
    val profile by viewModel.profile.collectAsState()
    val dailyTip by viewModel.dailyTip.collectAsState()
    val showStreakBanner by viewModel.showStreakBanner.collectAsState()
    val recommended = viewModel.getRecommendedWorkout()
    val firstName = profile.displayName.substringBefore(" ").ifBlank { profile.displayName }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag(VigorlyTestTags.DASHBOARD)
    ) {
        Column(
            modifier = Modifier
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
                onViewActivityClick = onActivityDetailClick
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
                    current = goals.moveCalories,
                    goal = goals.moveCaloriesGoal,
                    goalLabel = "${goals.moveCaloriesGoal} kcal",
                    accent = PrimaryAccent
                )
                ActivityMetricTile(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.DirectionsWalk,
                    label = stringResource(R.string.metric_steps),
                    current = goals.steps,
                    goal = goals.stepsGoal,
                    goalLabel = "${goals.stepsGoal / 1000}k",
                    accent = PrimaryContainer,
                    valueFormatter = { "%,d".format(it) }
                )
            }

            Spacer(Modifier.height(Dimens.Md))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Dimens.Md)) {
                ActivityMetricTile(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.FitnessCenter,
                    label = stringResource(R.string.metric_exercise),
                    current = goals.exerciseMinutes,
                    goal = goals.exerciseMinutesGoal,
                    goalLabel = "${goals.exerciseMinutesGoal} min",
                    accent = Primary
                )
                ActivityMetricTile(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.AccessibilityNew,
                    label = stringResource(R.string.metric_stand),
                    current = goals.standHours,
                    goal = goals.standHoursGoal,
                    goalLabel = "${goals.standHoursGoal} h",
                    accent = PrimaryAccent.copy(0.85f)
                )
            }

            Spacer(Modifier.height(Dimens.Lg))

            DailyTipCard(tip = dailyTip, modifier = Modifier.padding(bottom = Dimens.Md))
            recommended?.let { workout ->
                RecommendedWorkoutCard(
                    workout = workout,
                    onClick = { onRecommendedWorkoutClick(workout.id) },
                    modifier = Modifier.padding(bottom = Dimens.Md)
                )
            }
            WeeklyGoalCard(goal = weeklyGoal, modifier = Modifier.padding(bottom = Dimens.Md))
            Spacer(Modifier.height(Dimens.Md))
        }

        StreakBannerPopup(
            visible = showStreakBanner,
            streakDays = profile.activeStreakDays,
            onDismiss = viewModel::dismissStreakBanner,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = Dimens.Sm)
                .padding(horizontal = Dimens.ContainerMargin)
        )
    }
}

@Composable
private fun ActivityRingsHeroSection(
    moveProgress: Float,
    exerciseProgress: Float,
    standProgress: Float,
    centerPercent: Int,
    onViewActivityClick: () -> Unit
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val heroMinHeight = screenHeight * 0.38f

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = heroMinHeight),
        contentAlignment = Alignment.Center
    ) {
        val ringSize = maxWidth.coerceIn(260.dp, 360.dp)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            ActivityRings(
                moveProgress = moveProgress,
                exerciseProgress = exerciseProgress,
                standProgress = standProgress,
                centerPercent = centerPercent,
                ringSize = ringSize
            )
            WorkoutDetailStartCta(
                onClick = onViewActivityClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.Lg),
                labelRes = R.string.dashboard_view_activity,
                showPlayIcon = false
            )
        }
    }
}

@Composable
private fun ActivityMetricTile(
    modifier: Modifier,
    icon: ImageVector,
    label: String,
    current: Int,
    goal: Int,
    goalLabel: String,
    accent: Color,
    valueFormatter: (Int) -> String = { it.toString() }
) {
    val displayValue = current.coerceAtMost(goal)
    val progress = (displayValue.toFloat() / goal.coerceAtLeast(1)).coerceIn(0f, 1f)
    val goalReached = current >= goal

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        accent.copy(alpha = 0.12f),
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
            Text(label, style = LabelCaps, color = OnSurfaceVariant.copy(alpha = 0.85f))
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = valueFormatter(displayValue),
            style = DisplayStat.copy(fontSize = 28.sp, lineHeight = 30.sp),
            color = if (goalReached) accent else OnSurface,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "/ $goalLabel",
            style = BodyMd.copy(fontSize = 13.sp),
            color = OnSurfaceVariant.copy(alpha = 0.65f)
        )
        Spacer(Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(accent.copy(alpha = 0.12f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accent.copy(alpha = if (goalReached) 0.95f else 0.55f))
            )
        }
    }
}
