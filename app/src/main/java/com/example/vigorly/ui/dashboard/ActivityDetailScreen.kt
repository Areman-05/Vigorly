package com.example.vigorly.ui.dashboard

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.core.testing.VigorlyTestTags
import com.example.vigorly.data.activity.ActivityMetric
import com.example.vigorly.data.activity.DailyActivityDetail
import com.example.vigorly.data.activity.WeeklyActivityRingDay
import com.example.vigorly.data.activity.WeeklyActivityRingsBuilder
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.ui.components.ActivityCalendarSheet
import com.example.vigorly.ui.components.ActivityHourlyBarChart
import com.example.vigorly.ui.components.CircularStatRing
import com.example.vigorly.ui.components.GlassSurface
import com.example.vigorly.ui.components.WeeklyActivityDayRail
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.DisplayStat
import com.example.vigorly.ui.theme.GlassLabel
import com.example.vigorly.ui.theme.HeadlineLgMobile
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer
import com.example.vigorly.util.MetricFormatter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private enum class HourlyMetricTab {
    Move,
    Exercise,
    Stand;

    val accent: Color
        get() = when (this) {
            Move -> PrimaryAccent
            Exercise -> PrimaryContainer
            Stand -> Primary
        }
}

private fun hourlyValues(tab: HourlyMetricTab, detail: DailyActivityDetail): List<Float> {
    return when (tab) {
        HourlyMetricTab.Move -> detail.moveCaloriesByHour.map { it.toFloat() }
        HourlyMetricTab.Exercise -> detail.exerciseMinutesByHour.map { it.toFloat() }
        HourlyMetricTab.Stand -> detail.standByHour.map { if (it) 1f else 0f }
    }
}

@Composable
fun ActivityDetailScreen(
    repository: VigorlyRepository,
    showCalendar: Boolean,
    onDismissCalendar: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onOpenMetric: (ActivityMetric) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val detail by repository.displayedActivityDetail.collectAsState()
    val goals by repository.dailyGoals.collectAsState()
    val selectedDate by repository.selectedActivityDate.collectAsState()
    val history by repository.activityDayHistory.collectAsState()
    val weekDays by repository.currentWeekActivityRings.collectAsState()
    val unitsMetric by repository.unitsMetric.collectAsState()
    val locale = Locale.getDefault()
    val today = remember { LocalDate.now() }

    val weekRangeLabel = remember(weekDays, locale) {
        WeeklyActivityRingsBuilder.formatWeekRange(weekDays, locale)
    }
    val dateLabel = remember(selectedDate, locale) {
        val formatter = DateTimeFormatter.ofPattern("EEEE, d MMM", locale)
        selectedDate.format(formatter).replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(locale) else it.toString()
        }
    }

    val moveProgress = (detail.moveCalories.toFloat() / goals.moveCaloriesGoal.coerceAtLeast(1))
        .coerceIn(0f, 1f)
    val exerciseProgress = (detail.exerciseMinutes.toFloat() / goals.exerciseMinutesGoal.coerceAtLeast(1))
        .coerceIn(0f, 1f)
    val standProgress = (detail.standHours.toFloat() / goals.standHoursGoal.coerceAtLeast(1))
        .coerceIn(0f, 1f)
    val dayPercent = ((moveProgress + exerciseProgress + standProgress) / 3f * 100f)
        .toInt()
        .coerceIn(0, 100)
    val insightText = when {
        dayPercent >= 80 -> stringResource(R.string.activity_insight_body_high, dayPercent)
        dayPercent >= 40 -> stringResource(R.string.activity_insight_body_mid, dayPercent)
        else -> stringResource(R.string.activity_insight_body_low, dayPercent)
    }

    val activeDays = remember(weekDays) { weekDays.count { !it.isFuture && it.hasActivity } }
    val pastOrToday = remember(weekDays) { weekDays.count { !it.isFuture }.coerceAtLeast(1) }

    var hourlyTab by remember { mutableIntStateOf(0) }
    val selectedTab = HourlyMetricTab.entries[hourlyTab.coerceIn(0, 2)]

    LaunchedEffect(Unit) {
        repository.refreshActivityDayHistory()
    }
    LaunchedEffect(showCalendar) {
        if (showCalendar) repository.refreshActivityDayHistory()
    }

    Box(
        modifier
            .fillMaxSize()
            .testTag(VigorlyTestTags.ACTIVITY_DETAIL)
    ) {
        if (!showCalendar) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Dimens.ContainerMargin)
                    .padding(top = Dimens.Md, bottom = Dimens.Xl)
            ) {
                Text(
                    text = stringResource(R.string.dashboard_summary_action),
                    style = HeadlineLgMobile,
                    color = OnSurface
                )
                Text(
                    text = dateLabel,
                    style = BodyMd.copy(fontSize = 15.sp, fontWeight = FontWeight.Medium),
                    color = GlassLabel.copy(alpha = 0.82f),
                    modifier = Modifier.padding(top = 4.dp, bottom = Dimens.Lg)
                )

                if (selectedDate == today) {
                    Text(
                        text = stringResource(R.string.activity_detail_today),
                        style = HeadlineMd.copy(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = (-0.2).sp
                        ),
                        color = OnSurface,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
                val moveLeft = (goals.moveCaloriesGoal - detail.moveCalories).coerceAtLeast(0)
                val exerciseLeft = (goals.exerciseMinutesGoal - detail.exerciseMinutes).coerceAtLeast(0)
                val standLeft = (goals.standHoursGoal - detail.standHours).coerceAtLeast(0)
                TodayRingsCard(
                    moveValue = stringResource(
                        R.string.activity_day_snapshot_move,
                        detail.moveCalories
                    ),
                    moveGoal = stringResource(
                        R.string.activity_day_snapshot_move,
                        goals.moveCaloriesGoal
                    ),
                    moveRemaining = remainingCaption(
                        remaining = moveLeft,
                        remainingLabel = stringResource(
                            R.string.activity_day_snapshot_move,
                            moveLeft
                        )
                    ),
                    moveProgress = moveProgress,
                    exerciseValue = stringResource(
                        R.string.activity_day_snapshot_exercise,
                        detail.exerciseMinutes
                    ),
                    exerciseGoal = stringResource(
                        R.string.activity_day_snapshot_exercise,
                        goals.exerciseMinutesGoal
                    ),
                    exerciseRemaining = remainingCaption(
                        remaining = exerciseLeft,
                        remainingLabel = stringResource(
                            R.string.activity_day_snapshot_exercise,
                            exerciseLeft
                        )
                    ),
                    exerciseProgress = exerciseProgress,
                    standValue = stringResource(
                        R.string.activity_day_snapshot_stand,
                        detail.standHours
                    ),
                    standGoal = stringResource(
                        R.string.activity_day_snapshot_stand,
                        goals.standHoursGoal
                    ),
                    standRemaining = remainingCaption(
                        remaining = standLeft,
                        remainingLabel = stringResource(
                            R.string.activity_day_snapshot_stand,
                            standLeft
                        )
                    ),
                    standProgress = standProgress,
                    onOpenMetric = onOpenMetric
                )

                Spacer(Modifier.height(Dimens.Lg))

                Text(
                    text = stringResource(R.string.activity_day_summary_label),
                    style = HeadlineMd.copy(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.2).sp
                    ),
                    color = OnSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                DaySummaryBoard(
                    stepsValue = "%,d".format(locale, detail.steps),
                    stepsLabel = stringResource(R.string.activity_detail_steps),
                    distanceValue = MetricFormatter.formatDistanceKm(detail.distanceKm, unitsMetric),
                    distanceLabel = stringResource(R.string.activity_detail_distance),
                    insightText = insightText,
                    onStepsClick = { onOpenMetric(ActivityMetric.STEPS) }
                )

                Spacer(Modifier.height(Dimens.Lg))

                Text(
                    text = stringResource(R.string.activity_hourly_title),
                    style = HeadlineMd.copy(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.2).sp
                    ),
                    color = OnSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                DayBreakdownCard(
                    detail = detail,
                    selectedTab = selectedTab,
                    onSelectTab = { hourlyTab = it.ordinal }
                )

                if (weekDays.isNotEmpty()) {
                    Spacer(Modifier.height(Dimens.Lg))
                    Text(
                        text = stringResource(R.string.activity_weekly_title),
                        style = HeadlineMd.copy(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = (-0.2).sp
                        ),
                        color = OnSurface
                    )
                    Text(
                        text = weekRangeLabel.ifBlank {
                            stringResource(R.string.activity_weekly_subtitle)
                        },
                        style = BodyMd.copy(fontSize = 14.sp, lineHeight = 19.sp),
                        color = GlassLabel.copy(alpha = 0.75f),
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )
                    WeekStripCard(
                        days = weekDays,
                        selectedDate = selectedDate,
                        summary = stringResource(
                            R.string.activity_weekly_summary,
                            activeDays,
                            pastOrToday
                        ),
                        hint = stringResource(R.string.activity_weekly_subtitle),
                        onDayClick = onDateSelected
                    )
                }
            }
        } else {
            ActivityCalendarSheet(
                visible = true,
                selectedDate = selectedDate,
                history = history,
                liveSummaryProvider = { date -> repository.summaryForDate(date) },
                onBack = onDismissCalendar,
                onDateSelected = onDateSelected
            )
        }
    }
}

@Composable
private fun remainingCaption(remaining: Int, remainingLabel: String): String {
    return if (remaining == 0) {
        stringResource(R.string.activity_insight_done)
    } else {
        stringResource(R.string.activity_insight_remaining, remainingLabel)
    }
}

@Composable
private fun TodayRingsCard(
    moveValue: String,
    moveGoal: String,
    moveRemaining: String,
    moveProgress: Float,
    exerciseValue: String,
    exerciseGoal: String,
    exerciseRemaining: String,
    exerciseProgress: Float,
    standValue: String,
    standGoal: String,
    standRemaining: String,
    standProgress: Float,
    onOpenMetric: (ActivityMetric) -> Unit
) {
    GlassSurface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            RingMetricRow(
                label = stringResource(R.string.metric_move),
                value = moveValue,
                goal = moveGoal,
                remaining = moveRemaining,
                progress = moveProgress,
                accent = PrimaryAccent,
                onClick = { onOpenMetric(ActivityMetric.MOVE) }
            )
            RingRowDivider()
            RingMetricRow(
                label = stringResource(R.string.metric_exercise),
                value = exerciseValue,
                goal = exerciseGoal,
                remaining = exerciseRemaining,
                progress = exerciseProgress,
                accent = PrimaryContainer,
                onClick = { onOpenMetric(ActivityMetric.EXERCISE) }
            )
            RingRowDivider()
            RingMetricRow(
                label = stringResource(R.string.metric_stand),
                value = standValue,
                goal = standGoal,
                remaining = standRemaining,
                progress = standProgress,
                accent = Primary,
                onClick = { onOpenMetric(ActivityMetric.STAND) }
            )
        }
    }
}

@Composable
private fun RingRowDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(1.dp)
            .background(Color.White.copy(alpha = 0.08f))
    )
}

@Composable
private fun RingMetricRow(
    label: String,
    value: String,
    goal: String,
    remaining: String,
    progress: Float,
    accent: Color,
    onClick: () -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interaction,
                indication = ripple(bounded = true, color = Color.White.copy(alpha = 0.12f)),
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularStatRing(
            progress = progress,
            accent = accent,
            size = 52.dp,
            strokeWidth = 5.dp
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 14.dp, end = 8.dp)
        ) {
            Text(
                text = label,
                style = BodyMd.copy(fontSize = 13.sp, fontWeight = FontWeight.Medium),
                color = GlassLabel.copy(alpha = 0.8f)
            )
            Text(
                text = value,
                style = DisplayStat.copy(
                    fontSize = 26.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp
                ),
                color = OnSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp)
            )
            Text(
                text = stringResource(R.string.activity_from_goal, goal) + " · " + remaining,
                style = BodyMd.copy(fontSize = 12.sp, lineHeight = 16.sp),
                color = GlassLabel.copy(alpha = 0.7f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 3.dp)
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = GlassLabel.copy(alpha = 0.45f),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun DaySummaryBoard(
    stepsValue: String,
    stepsLabel: String,
    distanceValue: String,
    distanceLabel: String,
    insightText: String,
    onStepsClick: () -> Unit
) {
    GlassSurface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SummaryStat(
                    value = stepsValue,
                    label = stepsLabel,
                    onClick = onStepsClick,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .width(1.dp)
                        .height(44.dp)
                        .background(Color.White.copy(alpha = 0.1f))
                )
                SummaryStat(
                    value = distanceValue,
                    label = distanceLabel,
                    onClick = onStepsClick,
                    modifier = Modifier.weight(1f)
                )
            }
            Text(
                text = insightText,
                style = BodyMd.copy(fontSize = 14.sp, lineHeight = 20.sp),
                color = GlassLabel.copy(alpha = 0.85f),
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Composable
private fun SummaryStat(
    value: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interaction = remember { MutableInteractionSource() }
    Column(
        modifier = modifier.clickable(
            interactionSource = interaction,
            indication = ripple(bounded = true, color = Color.White.copy(alpha = 0.1f)),
            onClick = onClick
        )
    ) {
        Text(
            text = value,
            style = DisplayStat.copy(
                fontSize = 32.sp,
                lineHeight = 34.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.4).sp
            ),
            color = OnSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = label,
            style = BodyMd.copy(fontSize = 13.sp, fontWeight = FontWeight.Medium),
            color = GlassLabel.copy(alpha = 0.78f),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun DayBreakdownCard(
    detail: DailyActivityDetail,
    selectedTab: HourlyMetricTab,
    onSelectTab: (HourlyMetricTab) -> Unit
) {
    GlassSurface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 18.dp)
        ) {
            BreakdownMetricLegend(
                selected = selectedTab,
                onSelect = onSelectTab
            )
            Spacer(Modifier.height(16.dp))
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    fadeIn(tween(220, easing = FastOutSlowInEasing)) togetherWith
                        fadeOut(tween(140))
                },
                label = "periodStrip"
            ) { tab ->
                val hourly = hourlyValues(tab, detail)
                val peakHour = hourly
                    .withIndex()
                    .maxByOrNull { it.value }
                    ?.takeIf { it.value > 0f }
                    ?.index
                val headline = if (peakHour != null) {
                    stringResource(R.string.activity_hourly_peak, peakHour)
                } else {
                    stringResource(R.string.activity_hourly_quiet)
                }

                Column {
                    Text(
                        text = headline,
                        style = BodyMd.copy(fontSize = 13.sp, fontWeight = FontWeight.Medium),
                        color = GlassLabel.copy(alpha = 0.78f)
                    )
                    Spacer(Modifier.height(14.dp))
                    ActivityHourlyBarChart(
                        values = hourly,
                        barColor = tab.accent,
                        highlightColor = tab.accent.copy(alpha = 0.18f),
                        height = 168.dp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun BreakdownMetricLegend(
    selected: HourlyMetricTab,
    onSelect: (HourlyMetricTab) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HourlyMetricTab.entries.forEach { tab ->
            val isSelected = tab == selected
            val label = when (tab) {
                HourlyMetricTab.Move -> stringResource(R.string.metric_move)
                HourlyMetricTab.Exercise -> stringResource(R.string.metric_exercise)
                HourlyMetricTab.Stand -> stringResource(R.string.metric_stand)
            }
            val interaction = remember { MutableInteractionSource() }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(
                        interactionSource = interaction,
                        indication = ripple(bounded = true, color = Color.White.copy(alpha = 0.1f)),
                        onClick = { onSelect(tab) }
                    )
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(tab.accent)
                    )
                    Text(
                        text = label,
                        style = BodyMd.copy(
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                        ),
                        color = if (isSelected) OnSurface else GlassLabel.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .fillMaxWidth()
                        .height(2.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (isSelected) tab.accent else Color.Transparent)
                )
            }
        }
    }
}

@Composable
private fun WeekStripCard(
    days: List<WeeklyActivityRingDay>,
    selectedDate: LocalDate,
    summary: String,
    hint: String,
    onDayClick: (LocalDate) -> Unit
) {
    GlassSurface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 16.dp)
        ) {
            WeeklyActivityDayRail(
                days = days,
                selectedDate = selectedDate,
                onDayClick = onDayClick
            )
            Text(
                text = summary,
                style = BodyMd.copy(fontSize = 13.sp, fontWeight = FontWeight.Medium),
                color = GlassLabel.copy(alpha = 0.78f),
                modifier = Modifier.padding(start = 4.dp, top = 14.dp)
            )
            Text(
                text = hint,
                style = BodyMd.copy(fontSize = 12.sp),
                color = GlassLabel.copy(alpha = 0.55f),
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
            )
        }
    }
}
