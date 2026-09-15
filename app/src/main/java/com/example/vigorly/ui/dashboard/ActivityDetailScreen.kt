package com.example.vigorly.ui.dashboard

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.data.activity.DailyGoalsCalculator
import com.example.vigorly.data.activity.WeeklyActivityRingDay
import com.example.vigorly.data.activity.WeeklyActivityRingsBuilder
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.ui.components.ActivityCalendarSheet
import com.example.vigorly.ui.components.ActivityHourlyBarChart
import com.example.vigorly.ui.components.GlassSurface
import com.example.vigorly.ui.components.TripleActivityRing
import com.example.vigorly.ui.performance.UiPerformance
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.DisplayStat
import com.example.vigorly.ui.theme.GlassLabel
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.util.MetricFormatter
import java.time.LocalDate
import java.util.Locale

private enum class HourlyMetricTab { Move, Exercise, Stand }

@Composable
fun ActivityDetailScreen(
    repository: VigorlyRepository,
    showCalendar: Boolean,
    onDismissCalendar: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val detail by repository.displayedActivityDetail.collectAsState()
    val goals by repository.dailyGoals.collectAsState()
    val selectedDate by repository.selectedActivityDate.collectAsState()
    val history by repository.activityDayHistory.collectAsState()
    val weekDays by repository.currentWeekActivityRings.collectAsState()
    val unitsMetric by repository.unitsMetric.collectAsState()
    val locale = Locale.getDefault()
    val weekRangeLabel = remember(weekDays, locale) {
        WeeklyActivityRingsBuilder.formatWeekRange(weekDays, locale)
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

    val insightBody = when {
        dayPercent < 40 -> stringResource(R.string.activity_insight_body_low, dayPercent)
        dayPercent < 80 -> stringResource(R.string.activity_insight_body_mid, dayPercent)
        else -> stringResource(R.string.activity_insight_body_high, dayPercent)
    }

    var hourlyTab by remember { mutableIntStateOf(0) }
    val selectedTab = HourlyMetricTab.entries[hourlyTab.coerceIn(0, 2)]

    LaunchedEffect(Unit) {
        repository.refreshActivityDayHistory()
    }
    LaunchedEffect(showCalendar) {
        if (showCalendar) repository.refreshActivityDayHistory()
    }

    Box(modifier.fillMaxSize()) {
        if (!showCalendar) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Dimens.ContainerMargin)
                    .padding(top = Dimens.Sm, bottom = Dimens.Xl)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TripleActivityRing(
                        moveProgress = moveProgress,
                        exerciseProgress = exerciseProgress,
                        standProgress = standProgress,
                        size = 176.dp,
                        strokeWidth = 14.dp,
                        gap = 8.dp
                    )
                    Spacer(Modifier.height(20.dp))
                    Text(
                        text = "$dayPercent%",
                        style = DisplayStat.copy(
                            fontSize = 44.sp,
                            lineHeight = 46.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.8).sp
                        ),
                        color = OnSurface
                    )
                    Text(
                        text = stringResource(R.string.activity_day_progress_caption),
                        style = BodyMd.copy(fontSize = 14.sp, fontWeight = FontWeight.Medium),
                        color = GlassLabel.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(Color.White.copy(alpha = 0.12f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth((dayPercent / 100f).coerceIn(0.04f, 1f))
                                .height(6.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(
                                    Brush.horizontalGradient(listOf(Primary, PrimaryAccent))
                                )
                        )
                    }
                    Text(
                        text = insightBody,
                        style = BodyMd.copy(fontSize = 14.sp, lineHeight = 20.sp),
                        color = GlassLabel.copy(alpha = 0.85f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                    Text(
                        text = stringResource(
                            R.string.activity_steps_of_goal,
                            "%,d".format(locale, detail.steps),
                            "%,d".format(locale, DailyGoalsCalculator.STEPS_GOAL)
                        ) + " · " + MetricFormatter.formatDistanceKm(
                            detail.distanceKm,
                            unitsMetric
                        ),
                        style = BodyMd.copy(fontSize = 13.sp, fontWeight = FontWeight.Medium),
                        color = GlassLabel.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(Modifier.height(Dimens.Xl))

                Text(
                    text = stringResource(R.string.activity_hourly_title),
                    style = HeadlineMd.copy(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.3).sp
                    ),
                    color = OnSurface
                )
                Text(
                    text = stringResource(R.string.activity_hourly_subtitle),
                    style = BodyMd.copy(fontSize = 13.sp, lineHeight = 18.sp),
                    color = GlassLabel.copy(alpha = 0.72f),
                    modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                )

                HourlyMetricTabs(
                    selected = selectedTab,
                    onSelect = { hourlyTab = it.ordinal }
                )
                Spacer(Modifier.height(16.dp))

                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = {
                        fadeIn(tween(280)) togetherWith fadeOut(tween(180))
                    },
                    label = "hourlyChart"
                ) { tab ->
                    val values = when (tab) {
                        HourlyMetricTab.Move ->
                            detail.moveCaloriesByHour.map { it.toFloat() }
                        HourlyMetricTab.Exercise ->
                            detail.exerciseMinutesByHour.map { it.toFloat() }
                        HourlyMetricTab.Stand ->
                            detail.standByHour.map { if (it) 1f else 0f }
                    }
                    val valueLabel = when (tab) {
                        HourlyMetricTab.Move -> stringResource(
                            R.string.activity_detail_move_value,
                            detail.moveCalories
                        )
                        HourlyMetricTab.Exercise -> stringResource(
                            R.string.activity_detail_exercise_value,
                            detail.exerciseMinutes
                        )
                        HourlyMetricTab.Stand -> stringResource(
                            R.string.activity_detail_stand_value,
                            detail.standHours
                        )
                    }
                    val peakHour = values
                        .withIndex()
                        .maxByOrNull { it.value }
                        ?.takeIf { it.value > 0f }
                        ?.index
                    val periodLabel = when (peakHour) {
                        null -> stringResource(R.string.activity_hourly_quiet)
                        in 0..5 -> stringResource(R.string.activity_chart_period_dawn)
                        in 6..11 -> stringResource(R.string.activity_chart_period_morning)
                        in 12..14 -> stringResource(R.string.activity_chart_period_noon)
                        in 15..19 -> stringResource(R.string.activity_chart_period_afternoon)
                        else -> stringResource(R.string.activity_chart_period_night)
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = valueLabel,
                            style = DisplayStat.copy(
                                fontSize = 30.sp,
                                lineHeight = 32.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp
                            ),
                            color = OnSurface
                        )
                        Text(
                            text = if (peakHour != null) {
                                stringResource(R.string.activity_hourly_peak, peakHour) +
                                    " · " + periodLabel
                            } else {
                                periodLabel
                            },
                            style = BodyMd.copy(fontSize = 14.sp, fontWeight = FontWeight.Medium),
                            color = GlassLabel.copy(alpha = 0.82f),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        ActivityHourlyBarChart(
                            values = values,
                            barColor = PrimaryAccent,
                            highlightColor = Primary.copy(alpha = 0.25f),
                            height = 120.dp
                        )
                    }
                }

                AnimatedVisibility(
                    visible = weekDays.isNotEmpty(),
                    enter = fadeIn(detailFadeTween(220)) +
                        slideInVertically(
                            animationSpec = detailSlideTween(220),
                            initialOffsetY = { it / 6 }
                        )
                ) {
                    Column {
                        Spacer(Modifier.height(Dimens.Xl))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color.White.copy(alpha = 0.10f))
                        )
                        Spacer(Modifier.height(Dimens.Lg))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.activity_weekly_title),
                                    style = HeadlineMd.copy(
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        letterSpacing = (-0.3).sp
                                    ),
                                    color = OnSurface
                                )
                                Text(
                                    text = stringResource(R.string.activity_weekly_subtitle),
                                    style = BodyMd.copy(fontSize = 13.sp, lineHeight = 18.sp),
                                    color = GlassLabel.copy(alpha = 0.72f),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            if (weekRangeLabel.isNotBlank()) {
                                Text(
                                    text = weekRangeLabel,
                                    style = BodyMd.copy(fontSize = 13.sp, fontWeight = FontWeight.Medium),
                                    color = GlassLabel.copy(alpha = 0.7f)
                                )
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        GlassSurface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            WeekProgressStrip(
                                days = weekDays,
                                selectedDate = selectedDate,
                                onDayClick = onDateSelected,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 16.dp)
                            )
                        }
                    }
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
private fun HourlyMetricTabs(
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
            GlassSurface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                onClick = { onSelect(tab) }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = BodyMd.copy(
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                        ),
                        color = if (isSelected) OnSurface else GlassLabel.copy(alpha = 0.72f),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun WeekProgressStrip(
    days: List<WeeklyActivityRingDay>,
    selectedDate: LocalDate?,
    onDayClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        days.forEach { day ->
            val isSelected = selectedDate == day.date
            val interaction = remember { MutableInteractionSource() }
            val avgProgress = if (day.isFuture) {
                0f
            } else {
                ((day.moveProgress + day.exerciseProgress + day.standProgress) / 3f)
                    .coerceIn(0f, 1f)
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        enabled = !day.isFuture,
                        interactionSource = interaction,
                        indication = null,
                        onClick = { onDayClick(day.date) }
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = day.dayLabel.take(2),
                    style = BodyMd.copy(
                        fontSize = 12.sp,
                        fontWeight = if (isSelected || day.isToday) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = when {
                        isSelected || day.isToday -> OnSurface
                        day.isFuture -> GlassLabel.copy(alpha = 0.35f)
                        else -> GlassLabel.copy(alpha = 0.7f)
                    },
                    maxLines = 1
                )

                Box(
                    modifier = Modifier
                        .width(11.dp)
                        .height(76.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color.White.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    val fraction = when {
                        avgProgress <= 0f -> 0f
                        else -> avgProgress.coerceIn(0.08f, 1f)
                    }
                    if (fraction > 0f) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(fraction)
                                .clip(RoundedCornerShape(999.dp))
                                .background(
                                    if (isSelected || day.isToday) {
                                        Brush.verticalGradient(listOf(PrimaryAccent, Primary))
                                    } else {
                                        Brush.verticalGradient(
                                            listOf(
                                                Color.White.copy(alpha = 0.55f),
                                                Color.White.copy(alpha = 0.35f)
                                            )
                                        )
                                    }
                                )
                        )
                    }
                }

                Text(
                    text = day.date.dayOfMonth.toString(),
                    style = BodyMd.copy(
                        fontSize = 13.sp,
                        fontWeight = if (isSelected || day.isToday) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = when {
                        isSelected || day.isToday -> OnSurface
                        day.isFuture -> GlassLabel.copy(alpha = 0.3f)
                        else -> GlassLabel.copy(alpha = 0.65f)
                    }
                )
            }
        }
    }
}

private val detailEnterEnabled: Boolean get() = UiPerformance.decorativeMotionEnabled

private fun detailFadeTween(delayMillis: Int) = tween<Float>(
    durationMillis = if (detailEnterEnabled) 400 else 0,
    delayMillis = if (detailEnterEnabled) delayMillis else 0,
    easing = FastOutSlowInEasing
)

private fun detailSlideTween(delayMillis: Int): androidx.compose.animation.core.FiniteAnimationSpec<IntOffset> =
    tween(
        durationMillis = if (detailEnterEnabled) 400 else 0,
        delayMillis = if (detailEnterEnabled) delayMillis else 0,
        easing = FastOutSlowInEasing
    )
