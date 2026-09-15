package com.example.vigorly.ui.dashboard

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.data.activity.ActivityMetric
import com.example.vigorly.data.activity.DailyActivityDaySummary
import com.example.vigorly.data.activity.DailyActivityDetail
import com.example.vigorly.data.activity.DailyGoalsCalculator
import com.example.vigorly.data.activity.WeeklyActivityRingDay
import com.example.vigorly.data.activity.WeeklyActivityRingsBuilder
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.ui.components.ActivityHourlyBarChart
import com.example.vigorly.ui.components.CircularStatRing
import com.example.vigorly.ui.components.FrostedGlassCircleButton
import com.example.vigorly.ui.components.GlassSurface
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.DisplayStat
import com.example.vigorly.ui.theme.GlassLabel
import com.example.vigorly.ui.theme.HeadlineLgMobile
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.util.MetricFormatter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private enum class MetricPeriod { Day, Week }

@Composable
fun ActivityMetricDetailScreen(
    metric: ActivityMetric,
    repository: VigorlyRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val detail by repository.displayedActivityDetail.collectAsState()
    val goals by repository.dailyGoals.collectAsState()
    val selectedDate by repository.selectedActivityDate.collectAsState()
    val history by repository.activityDayHistory.collectAsState()
    val weekDays by repository.currentWeekActivityRings.collectAsState()
    val unitsMetric by repository.unitsMetric.collectAsState()
    val locale = Locale.getDefault()
    var period by remember { mutableStateOf(MetricPeriod.Day) }

    val today = LocalDate.now()
    val canGoNext = selectedDate.isBefore(today)
    val dateLabel = remember(selectedDate, locale) {
        val formatter = DateTimeFormatter.ofPattern("EEEE, d MMM", locale)
        selectedDate.format(formatter).replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(locale) else it.toString()
        }
    }
    val dayTitle = if (selectedDate == today) {
        stringResource(R.string.activity_detail_today)
    } else {
        dateLabel
    }
    val weekRangeLabel = remember(weekDays, locale) {
        WeeklyActivityRingsBuilder.formatWeekRange(weekDays, locale)
    }

    val title = when (metric) {
        ActivityMetric.STEPS -> stringResource(R.string.activity_detail_steps)
        ActivityMetric.MOVE -> stringResource(R.string.metric_move)
        ActivityMetric.EXERCISE -> stringResource(R.string.metric_exercise)
        ActivityMetric.STAND -> stringResource(R.string.metric_stand)
    }
    val whatItTracks = when (metric) {
        ActivityMetric.STEPS -> stringResource(R.string.activity_metric_what_steps)
        ActivityMetric.MOVE -> stringResource(R.string.activity_metric_what_move)
        ActivityMetric.EXERCISE -> stringResource(R.string.activity_metric_what_exercise)
        ActivityMetric.STAND -> stringResource(R.string.activity_metric_what_stand)
    }
    val tip = when (metric) {
        ActivityMetric.STEPS -> stringResource(R.string.activity_metric_tip_steps)
        ActivityMetric.MOVE -> stringResource(R.string.activity_metric_tip_move)
        ActivityMetric.EXERCISE -> stringResource(R.string.activity_metric_tip_exercise)
        ActivityMetric.STAND -> stringResource(R.string.activity_metric_tip_stand)
    }

    val dayValue = when (metric) {
        ActivityMetric.STEPS -> detail.steps
        ActivityMetric.MOVE -> detail.moveCalories
        ActivityMetric.EXERCISE -> detail.exerciseMinutes
        ActivityMetric.STAND -> detail.standHours
    }
    val dayGoal = when (metric) {
        ActivityMetric.STEPS -> DailyGoalsCalculator.STEPS_GOAL
        ActivityMetric.MOVE -> goals.moveCaloriesGoal
        ActivityMetric.EXERCISE -> goals.exerciseMinutesGoal
        ActivityMetric.STAND -> goals.standHoursGoal
    }
    val remaining = (dayGoal - dayValue).coerceAtLeast(0)

    val hourlyValues = when (metric) {
        ActivityMetric.STEPS -> {
            val moveSum = detail.moveCaloriesByHour.sum().coerceAtLeast(1)
            detail.moveCaloriesByHour.map { hour ->
                (detail.steps * (hour.toFloat() / moveSum)).coerceAtLeast(0f)
            }
        }
        ActivityMetric.MOVE -> detail.moveCaloriesByHour.map { it.toFloat() }
        ActivityMetric.EXERCISE -> detail.exerciseMinutesByHour.map { it.toFloat() }
        ActivityMetric.STAND -> detail.standByHour.map { if (it) 1f else 0f }
    }
    val peakHour = hourlyValues
        .withIndex()
        .maxByOrNull { it.value }
        ?.takeIf { it.value > 0f }
        ?.index

    val weekPairs = remember(metric, weekDays, history, detail, today) {
        weekDays.map { day ->
            day to if (day.isFuture) {
                0
            } else {
                metricValueForDate(metric, day.date, today, detail, history)
            }
        }
    }
    val weekValues = weekPairs.filterNot { it.first.isFuture }.map { it.second }
    val weekAvg = if (weekValues.isEmpty()) 0 else weekValues.sum() / weekValues.size
    val weekBest = weekValues.maxOrNull() ?: 0
    val weekGoalTotal = dayGoal * weekValues.size.coerceAtLeast(1)
    val weekSum = weekValues.sum()
    val weekPercent = ((weekSum.toFloat() / weekGoalTotal.coerceAtLeast(1)) * 100f)
        .toInt()
        .coerceIn(0, 100)

    val displayValue = if (period == MetricPeriod.Day) dayValue else weekAvg
    val progress = (displayValue.toFloat() / dayGoal.coerceAtLeast(1)).coerceIn(0f, 1f)
    val percent = if (period == MetricPeriod.Day) {
        (progress * 100f).toInt().coerceIn(0, 100)
    } else {
        weekPercent
    }

    val actionText = when {
        percent >= 100 -> stringResource(R.string.activity_metric_action_done)
        percent >= 80 -> stringResource(R.string.activity_metric_action_high)
        percent >= 40 -> stringResource(R.string.activity_metric_action_mid)
        else -> stringResource(R.string.activity_metric_action_low)
    }

    val relatedLeft = when (metric) {
        ActivityMetric.STEPS -> stringResource(R.string.activity_detail_distance) to
            MetricFormatter.formatDistanceKm(detail.distanceKm, unitsMetric)
        ActivityMetric.MOVE -> stringResource(R.string.activity_detail_steps) to
            "%,d".format(locale, detail.steps)
        ActivityMetric.EXERCISE -> stringResource(R.string.metric_move) to
            "${detail.moveCalories} kcal"
        ActivityMetric.STAND -> stringResource(R.string.metric_exercise) to
            "${detail.exerciseMinutes} min"
    }
    val relatedRight = when (metric) {
        ActivityMetric.STEPS -> stringResource(R.string.activity_metric_calories_short) to
            "${detail.moveCalories} kcal"
        ActivityMetric.MOVE -> stringResource(R.string.activity_detail_distance) to
            MetricFormatter.formatDistanceKm(detail.distanceKm, unitsMetric)
        ActivityMetric.EXERCISE -> stringResource(R.string.metric_stand) to
            "${detail.standHours} h"
        ActivityMetric.STAND -> stringResource(R.string.metric_move) to
            "${detail.moveCalories} kcal"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.ContainerMargin)
            .padding(bottom = Dimens.Xl)
    ) {
        // SUELTO: top bar + toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.Sm, bottom = Dimens.Md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FrostedGlassCircleButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.go_back),
                    tint = OnSurface
                )
            }
            Text(
                text = title,
                style = HeadlineLgMobile.copy(
                    fontSize = 24.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.4).sp
                ),
                color = OnSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.size(44.dp))
        }

        PeriodToggle(
            selected = period,
            onSelect = { period = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // CARD: hero del día/semana (fecha + anillo + 2 datos)
        GlassSurface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (period == MetricPeriod.Day) {
                    DateNavRow(
                        title = dayTitle,
                        subtitle = if (selectedDate == today) dateLabel else null,
                        canGoPrev = true,
                        canGoNext = canGoNext,
                        onPrev = { repository.selectActivityDate(selectedDate.minusDays(1)) },
                        onNext = {
                            if (canGoNext) repository.selectActivityDate(selectedDate.plusDays(1))
                        }
                    )
                } else {
                    Text(
                        text = weekRangeLabel.ifBlank { stringResource(R.string.activity_weekly_title) },
                        style = BodyMd.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                        color = GlassLabel,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(Modifier.height(16.dp))

                Box(contentAlignment = Alignment.Center) {
                    CircularStatRing(
                        progress = if (period == MetricPeriod.Day) {
                            progress
                        } else {
                            (weekSum.toFloat() / weekGoalTotal.coerceAtLeast(1)).coerceIn(0f, 1f)
                        },
                        accent = PrimaryAccent,
                        size = 178.dp,
                        strokeWidth = 12.dp
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = formatMetricValue(metric, displayValue, locale),
                            style = DisplayStat.copy(
                                fontSize = 38.sp,
                                lineHeight = 42.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.6).sp
                            ),
                            color = OnSurface
                        )
                        Text(
                            text = stringResource(
                                R.string.activity_from_goal,
                                formatMetricValue(metric, dayGoal, locale)
                            ),
                            style = BodyMd.copy(fontSize = 14.sp, fontWeight = FontWeight.Medium),
                            color = GlassLabel.copy(alpha = 0.8f),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                // 2 datos dentro del mismo hero (sin mini-cards)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    if (period == MetricPeriod.Day) {
                        HeroStat(
                            label = stringResource(R.string.activity_metric_remaining),
                            value = if (remaining == 0) {
                                stringResource(R.string.activity_insight_done)
                            } else {
                                formatMetricValue(metric, remaining, locale)
                            },
                            modifier = Modifier.weight(1f)
                        )
                        HeroStat(
                            label = stringResource(R.string.activity_metric_peak_hour),
                            value = if (peakHour != null) {
                                stringResource(R.string.activity_metric_peak_value, peakHour)
                            } else {
                                stringResource(R.string.activity_metric_peak_none)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        HeroStat(
                            label = stringResource(R.string.activity_metric_avg_week),
                            value = formatMetricValue(metric, weekAvg, locale),
                            modifier = Modifier.weight(1f)
                        )
                        HeroStat(
                            label = stringResource(R.string.activity_metric_best_day),
                            value = formatMetricValue(metric, weekBest, locale),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (period == MetricPeriod.Day) {
                    Spacer(Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color.White.copy(alpha = 0.08f))
                    )
                    Spacer(Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        HeroStat(
                            label = relatedLeft.first,
                            value = relatedLeft.second,
                            modifier = Modifier.weight(1f)
                        )
                        HeroStat(
                            label = relatedRight.first,
                            value = relatedRight.second,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(Dimens.Lg))

        // SUELTO: qué mide
        Text(
            text = stringResource(R.string.activity_metric_what_title),
            style = HeadlineMd.copy(
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.3).sp
            ),
            color = OnSurface
        )
        Text(
            text = whatItTracks,
            style = BodyMd.copy(fontSize = 15.sp, lineHeight = 22.sp),
            color = GlassLabel.copy(alpha = 0.88f),
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(Modifier.height(16.dp))

        // CARD: coaching (estilo WeeklyGoalHero)
        GlassSurface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.activity_summary_section),
                        style = BodyMd.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = (-0.1).sp
                        ),
                        color = OnSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "$percent%",
                        style = DisplayStat.copy(fontSize = 22.sp, lineHeight = 24.sp),
                        color = OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color.White.copy(alpha = 0.14f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth((percent / 100f).coerceIn(0.04f, 1f))
                            .height(7.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(
                                Brush.horizontalGradient(listOf(Primary, PrimaryAccent))
                            )
                    )
                }
                Text(
                    text = actionText,
                    style = BodyMd.copy(fontSize = 14.sp, lineHeight = 19.sp, fontWeight = FontWeight.SemiBold),
                    color = OnSurface
                )
                Text(
                    text = tip,
                    style = BodyMd.copy(fontSize = 13.sp, lineHeight = 18.sp),
                    color = GlassLabel.copy(alpha = 0.88f)
                )
            }
        }

        Spacer(Modifier.height(Dimens.Lg))

        if (period == MetricPeriod.Day) {
            Text(
                text = stringResource(R.string.activity_metric_breakdown_title),
                style = HeadlineMd.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.3).sp
                ),
                color = OnSurface
            )
            Spacer(Modifier.height(12.dp))
            GlassSurface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp)
            ) {
                ActivityHourlyBarChart(
                    values = hourlyValues,
                    barColor = PrimaryAccent.copy(alpha = 0.85f),
                    highlightColor = PrimaryAccent.copy(alpha = 0.18f),
                    height = 120.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 18.dp)
                )
            }
        } else {
            Text(
                text = stringResource(R.string.activity_metric_week_trend),
                style = HeadlineMd.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.3).sp
                ),
                color = OnSurface
            )
            Spacer(Modifier.height(12.dp))
            GlassSurface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp)
            ) {
                MetricWeekBars(
                    days = weekPairs,
                    goal = dayGoal,
                    selectedDate = selectedDate,
                    onDayClick = { repository.selectActivityDate(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 18.dp)
                )
            }
        }
    }
}

@Composable
private fun HeroStat(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = LabelCaps.copy(
                fontSize = 11.sp,
                letterSpacing = 0.08.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = GlassLabel.copy(alpha = 0.65f)
        )
        Text(
            text = value,
            style = BodyMd.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.1).sp
            ),
            color = OnSurface,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun PeriodToggle(
    selected: MetricPeriod,
    onSelect: (MetricPeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        MetricPeriod.entries.forEach { period ->
            val isSelected = period == selected
            val label = when (period) {
                MetricPeriod.Day -> stringResource(R.string.activity_period_day)
                MetricPeriod.Week -> stringResource(R.string.activity_period_week)
            }
            val interaction = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (isSelected) PrimaryAccent.copy(alpha = 0.9f) else Color.Transparent)
                    .clickable(
                        interactionSource = interaction,
                        indication = ripple(bounded = true, color = Color.White.copy(alpha = 0.12f)),
                        onClick = { onSelect(period) }
                    )
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = BodyMd.copy(
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = if (isSelected) OnSurface else GlassLabel.copy(alpha = 0.75f)
                )
            }
        }
    }
}

@Composable
private fun DateNavRow(
    title: String,
    subtitle: String?,
    canGoPrev: Boolean,
    canGoNext: Boolean,
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
    val prevCd = stringResource(R.string.activity_date_prev)
    val nextCd = stringResource(R.string.activity_date_next)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = prevCd,
            tint = if (canGoPrev) OnSurface else GlassLabel.copy(alpha = 0.35f),
            modifier = Modifier
                .size(28.dp)
                .semantics { contentDescription = prevCd }
                .clickable(enabled = canGoPrev, onClick = onPrev)
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                style = HeadlineMd.copy(
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.2).sp
                ),
                color = OnSurface
            )
            if (!subtitle.isNullOrBlank() && subtitle != title) {
                Text(
                    text = subtitle,
                    style = BodyMd.copy(fontSize = 13.sp, fontWeight = FontWeight.Medium),
                    color = GlassLabel.copy(alpha = 0.75f)
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = nextCd,
            tint = if (canGoNext) OnSurface else GlassLabel.copy(alpha = 0.35f),
            modifier = Modifier
                .size(28.dp)
                .semantics { contentDescription = nextCd }
                .clickable(enabled = canGoNext, onClick = onNext)
        )
    }
}

@Composable
private fun MetricWeekBars(
    days: List<Pair<WeeklyActivityRingDay, Int>>,
    goal: Int,
    selectedDate: LocalDate?,
    onDayClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        days.forEach { (day, value) ->
            val isSelected = selectedDate == day.date
            val interaction = remember { MutableInteractionSource() }
            val progress = if (day.isFuture || goal <= 0) {
                0f
            } else {
                (value.toFloat() / goal).coerceIn(0f, 1f)
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
                    }
                )
                Box(
                    modifier = Modifier
                        .width(12.dp)
                        .height(88.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color.White.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    val fraction = when {
                        progress <= 0f -> 0f
                        else -> progress.coerceIn(0.08f, 1f)
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

private fun metricValueForDate(
    metric: ActivityMetric,
    date: LocalDate,
    today: LocalDate,
    liveDetail: DailyActivityDetail,
    history: Map<String, DailyActivityDaySummary>
): Int {
    val summary = if (date == today) {
        DailyActivityDaySummary.fromDetail(date.toString(), liveDetail)
    } else {
        history[date.toString()]
    } ?: return 0
    return when (metric) {
        ActivityMetric.STEPS -> summary.steps
        ActivityMetric.MOVE -> summary.moveCalories
        ActivityMetric.EXERCISE -> summary.exerciseMinutes
        ActivityMetric.STAND -> summary.standHours
    }
}

private fun formatMetricValue(
    metric: ActivityMetric,
    value: Int,
    locale: Locale
): String = when (metric) {
    ActivityMetric.STEPS -> "%,d".format(locale, value)
    ActivityMetric.MOVE -> "$value kcal"
    ActivityMetric.EXERCISE -> "$value min"
    ActivityMetric.STAND -> "$value h"
}
