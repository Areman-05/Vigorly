package com.example.vigorly.ui.dashboard

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vigorly.R
import com.example.vigorly.core.testing.VigorlyTestTags
import com.example.vigorly.data.model.WorkoutDetail
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.di.AppViewModelFactory
import com.example.vigorly.presentation.feature.dashboard.DashboardViewModel
import com.example.vigorly.ui.components.DailyTipCard
import com.example.vigorly.ui.components.GlassSurface
import com.example.vigorly.ui.components.RecommendedWorkoutCard
import com.example.vigorly.ui.components.StreakBannerPopup
import com.example.vigorly.ui.components.TripleActivityRing
import com.example.vigorly.ui.components.WeeklyGoalHeroCard
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.DisplayStat
import com.example.vigorly.ui.theme.GlassBorder
import com.example.vigorly.ui.theme.GlassLabel
import com.example.vigorly.ui.theme.HeadlineLgMobile
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.PrimaryAccent
import kotlinx.coroutines.delay

@Composable
fun DashboardScreen(
    repository: VigorlyRepository,
    onActivityDetailClick: () -> Unit = {},
    onRecommendedWorkoutClick: (String) -> Unit = {},
    onViewAllWorkoutsClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = viewModel(factory = AppViewModelFactory(repository))
) {
    val goals by viewModel.dailyGoals.collectAsState()
    val weeklyGoal by viewModel.weeklyGoal.collectAsState()
    val profile by viewModel.profile.collectAsState()
    val dailyTip by viewModel.dailyTip.collectAsState()
    val showStreakBanner by viewModel.showStreakBanner.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val history by viewModel.history.collectAsState()
    val recommendedWorkouts = viewModel.getRecommendedWorkouts(5)
    val tipCards = remember(dailyTip) { viewModel.tipCards() }
    val workoutsToday = remember(history) { viewModel.todaysWorkoutCount() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .testTag(VigorlyTestTags.DASHBOARD)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.ContainerMargin)
                .padding(top = Dimens.Sm, bottom = Dimens.FloatingNavClearance)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Dimens.Md),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.dashboard_title),
                    style = HeadlineLgMobile,
                    color = OnSurface
                )
                ResumenChip(onClick = onActivityDetailClick)
            }

            HomeStatsGrid(
                moveProgress = goals.moveProgress,
                exerciseProgress = goals.exerciseProgress,
                standProgress = goals.standProgress,
                exerciseMinutes = goals.exerciseMinutes,
                workoutsToday = workoutsToday
            )

            Spacer(Modifier.height(Dimens.Lg))

            SectionTitleRow(
                title = stringResource(R.string.dashboard_recommended_title),
                actionLabel = stringResource(R.string.dashboard_view_all),
                onActionClick = onViewAllWorkoutsClick
            )

            Spacer(Modifier.height(Dimens.Sm))

            if (recommendedWorkouts.isNotEmpty()) {
                RecommendedWorkoutCarousel(
                    workouts = recommendedWorkouts,
                    favorites = favorites,
                    onFavoriteToggle = { viewModel.toggleFavorite(it) },
                    onWorkoutClick = onRecommendedWorkoutClick,
                    modifier = Modifier.padding(bottom = Dimens.Md)
                )
            }

            Spacer(Modifier.height(Dimens.Sm))

            Text(
                text = stringResource(R.string.dashboard_tips_title),
                style = HeadlineMd.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.2).sp
                ),
                color = OnSurface
            )

            Spacer(Modifier.height(Dimens.Sm))

            tipCards.forEachIndexed { index, tip ->
                DailyTipCard(
                    tip = tip,
                    coverIndex = index,
                    showPersonalizedHint = tip.id.startsWith("personalized"),
                    modifier = Modifier.padding(bottom = Dimens.Md)
                )
            }

            WeeklyGoalHeroCard(
                goal = weeklyGoal,
                modifier = Modifier.padding(bottom = Dimens.Md)
            )

            HomeSnapshotGrid(
                streakDays = profile.activeStreakDays,
                totalWorkouts = profile.totalWorkouts,
                level = profile.level,
                favoritesCount = favorites.size
            )
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
private fun ResumenChip(onClick: () -> Unit) {
    val shape = RoundedCornerShape(20.dp)
    Row(
        modifier = Modifier
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFFFB347).copy(alpha = 0.28f),
                        Color(0xFFFF6B4A).copy(alpha = 0.22f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFD089).copy(alpha = 0.55f),
                        GlassBorder
                    )
                ),
                shape = shape
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.Insights,
            contentDescription = null,
            tint = Color(0xFFFFC875),
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = stringResource(R.string.dashboard_summary_action),
            style = BodyMd.copy(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
            color = Color(0xFFFFE1B0)
        )
    }
}

@Composable
private fun SectionTitleRow(
    title: String,
    actionLabel: String,
    onActionClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = HeadlineMd,
            color = OnSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 6.dp)
                .clickable(onClick = onActionClick)
        ) {
            Text(
                text = actionLabel,
                style = BodyMd.copy(fontSize = 14.sp, fontWeight = FontWeight.Medium),
                color = GlassLabel.copy(alpha = 0.85f),
                maxLines = 1
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = GlassLabel.copy(alpha = 0.85f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun RecommendedWorkoutCarousel(
    workouts: List<WorkoutDetail>,
    favorites: Set<String>,
    onFavoriteToggle: (String) -> Unit,
    onWorkoutClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (workouts.isEmpty()) return

    val count = workouts.size
    // Infinite pager: always advance +1 so wrap never reverse-scrolls (the jump).
    val startPage = remember(count) {
        val mid = Int.MAX_VALUE / 2
        mid - (mid % count)
    }
    val pagerState = rememberPagerState(
        initialPage = startPage,
        pageCount = { if (count <= 1) 1 else Int.MAX_VALUE }
    )

    LaunchedEffect(count, workouts.map { it.id }) {
        if (count <= 1) return@LaunchedEffect
        while (true) {
            delay(4800)
            pagerState.animateScrollToPage(
                page = pagerState.currentPage + 1,
                animationSpec = tween(durationMillis = 520, easing = FastOutSlowInEasing)
            )
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxWidth(),
        beyondViewportPageCount = 1,
        userScrollEnabled = count > 1
    ) { page ->
        val workout = workouts[page % count]
        RecommendedWorkoutCard(
            workout = workout,
            isFavorite = workout.id in favorites,
            onFavoriteToggle = { onFavoriteToggle(workout.id) },
            onClick = { onWorkoutClick(workout.id) }
        )
    }
}

@Composable
private fun HomeStatsGrid(
    moveProgress: Float,
    exerciseProgress: Float,
    standProgress: Float,
    exerciseMinutes: Int,
    workoutsToday: Int
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.dashboard_today_label).uppercase(),
            style = LabelCaps,
            color = PrimaryAccent,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GlassSurface(
                modifier = Modifier
                    .weight(1.15f)
                    .fillMaxHeight()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    TripleActivityRing(
                        moveProgress = moveProgress,
                        exerciseProgress = exerciseProgress,
                        standProgress = standProgress,
                        size = 158.dp,
                        strokeWidth = 12.dp,
                        gap = 7.dp
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(0.95f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                NumberStatCard(
                    value = formatExerciseTime(exerciseMinutes),
                    label = stringResource(R.string.dashboard_exercise_today),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
                NumberStatCard(
                    value = workoutsToday.toString(),
                    label = stringResource(R.string.dashboard_workouts_today),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun NumberStatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    GlassSurface(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                style = DisplayStat.copy(fontSize = 28.sp, lineHeight = 30.sp),
                color = OnSurface,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = label,
                style = BodyMd.copy(
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = GlassLabel,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

@Composable
private fun HomeSnapshotGrid(
    streakDays: Int,
    totalWorkouts: Int,
    level: Int,
    favoritesCount: Int
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = stringResource(R.string.dashboard_progress_label),
            style = BodyMd.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.0.sp
            ),
            color = PrimaryAccent,
            modifier = Modifier.padding(bottom = 2.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SnapshotMiniCard(
                value = stringResource(R.string.dashboard_snap_streak_value, streakDays),
                label = stringResource(R.string.dashboard_snap_streak),
                icon = Icons.Outlined.LocalFireDepartment,
                modifier = Modifier.weight(1f)
            )
            SnapshotMiniCard(
                value = totalWorkouts.toString(),
                label = stringResource(R.string.dashboard_snap_total),
                icon = Icons.Outlined.Flag,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SnapshotMiniCard(
                value = level.toString(),
                label = stringResource(R.string.dashboard_snap_level),
                icon = Icons.Outlined.WorkspacePremium,
                modifier = Modifier.weight(1f)
            )
            SnapshotMiniCard(
                value = favoritesCount.toString(),
                label = stringResource(R.string.dashboard_snap_favorites),
                icon = Icons.Outlined.FavoriteBorder,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SnapshotMiniCard(
    value: String,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    GlassSurface(
        modifier = modifier.height(96.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = OnSurface,
                modifier = Modifier.size(18.dp)
            )
            Column {
                Text(
                    text = value,
                    style = DisplayStat.copy(fontSize = 22.sp, lineHeight = 24.sp),
                    color = OnSurface,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = label,
                    style = BodyMd.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.1.sp
                    ),
                    color = GlassLabel.copy(alpha = 0.82f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 3.dp)
                )
            }
        }
    }
}

private fun formatExerciseTime(minutes: Int): String {
    if (minutes >= 60) {
        val hours = minutes / 60
        val rem = minutes % 60
        return if (rem == 0) "${hours}h" else "${hours}h ${rem}m"
    }
    return "${minutes}m"
}
