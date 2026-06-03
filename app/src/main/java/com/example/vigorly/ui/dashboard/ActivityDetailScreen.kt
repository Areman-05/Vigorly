package com.example.vigorly.ui.dashboard

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.data.activity.WeeklyActivityRingsBuilder
import com.example.vigorly.ui.components.ActivityCalendarSheet
import com.example.vigorly.ui.components.ActivityHourlyBarChart
import com.example.vigorly.ui.components.WeeklyActivityRingsSection
import com.example.vigorly.util.MetricFormatter
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.DisplayStat
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ActivityDetailScreen(
    repository: VigorlyRepository,
    showCalendar: Boolean,
    onDismissCalendar: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val detail by repository.displayedActivityDetail.collectAsState()
    val selectedDate by repository.selectedActivityDate.collectAsState()
    val history by repository.activityDayHistory.collectAsState()
    val weekDays by repository.currentWeekActivityRings.collectAsState()
    val unitsMetric by repository.unitsMetric.collectAsState()
    val statStyle = DisplayStat.copy(fontSize = 36.sp, lineHeight = 36.sp)
    val today = remember { LocalDate.now() }
    val locale = Locale.getDefault()
    val dateFormatter = remember {
        DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", locale)
    }
    val headerLabel = if (selectedDate == today) {
        stringResource(R.string.activity_detail_today)
    } else {
        selectedDate.format(dateFormatter).replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(locale) else it.toString()
        }
    }
    val weekRangeLabel = remember(weekDays, locale) {
        WeeklyActivityRingsBuilder.formatWeekRange(weekDays, locale)
    }

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
                    .padding(horizontal = Dimens.ContainerMargin, vertical = Dimens.Md)
            ) {
                AnimatedContent(
                    targetState = headerLabel,
                    transitionSpec = {
                        fadeIn(tween(280)) togetherWith fadeOut(tween(180))
                    },
                    label = "detailHeader"
                ) { label ->
                    Text(
                        text = label,
                        style = HeadlineMd,
                        color = OnSurface,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = Dimens.Xl)
                    )
                }

                ActivityDetailMetricSection(
                    visible = true,
                    enterDelayMillis = 0,
                    icon = Icons.Default.LocalFireDepartment,
                    iconTint = PrimaryAccent,
                    label = stringResource(R.string.metric_move),
                    value = stringResource(R.string.activity_detail_move_value, detail.moveCalories),
                    values = detail.moveCaloriesByHour.map { it.toFloat() },
                    barColor = PrimaryAccent,
                    valueStyle = statStyle
                )

                ActivityDetailMetricSection(
                    visible = true,
                    enterDelayMillis = 80,
                    icon = Icons.Default.FitnessCenter,
                    iconTint = PrimaryContainer,
                    label = stringResource(R.string.metric_exercise),
                    value = stringResource(R.string.activity_detail_exercise_value, detail.exerciseMinutes),
                    values = detail.exerciseMinutesByHour.map { it.toFloat() },
                    barColor = PrimaryContainer,
                    valueStyle = statStyle
                )

                ActivityDetailMetricSection(
                    visible = true,
                    enterDelayMillis = 160,
                    icon = Icons.Default.AccessibilityNew,
                    iconTint = Primary,
                    label = stringResource(R.string.metric_stand),
                    value = stringResource(R.string.activity_detail_stand_value, detail.standHours),
                    values = detail.standByHour.map { if (it) 1f else 0f },
                    barColor = Primary,
                    valueStyle = statStyle
                )

                Spacer(Modifier.height(Dimens.Xl))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.Xl)
                ) {
                    ActivitySummaryTile(
                        modifier = Modifier.weight(1f),
                        visible = true,
                        enterDelayMillis = 240,
                        icon = Icons.Default.DirectionsWalk,
                        iconTint = PrimaryContainer,
                        label = stringResource(R.string.activity_detail_steps),
                        value = "%,d".format(Locale.getDefault(), detail.steps),
                        valueStyle = statStyle.copy(fontSize = 28.sp, lineHeight = 28.sp)
                    )
                    ActivitySummaryTile(
                        modifier = Modifier.weight(1f),
                        visible = true,
                        enterDelayMillis = 320,
                        icon = Icons.Default.Route,
                        iconTint = PrimaryAccent,
                        label = stringResource(R.string.activity_detail_distance),
                        value = MetricFormatter.formatDistanceKm(detail.distanceKm, unitsMetric),
                        valueStyle = statStyle.copy(fontSize = 28.sp, lineHeight = 28.sp)
                    )
                }

                Spacer(Modifier.height(Dimens.Xl))

                AnimatedVisibility(
                    visible = weekDays.isNotEmpty(),
                    enter = fadeIn(tween(420, delayMillis = 400, easing = FastOutSlowInEasing)) +
                        slideInVertically(
                            animationSpec = tween(420, delayMillis = 400, easing = FastOutSlowInEasing),
                            initialOffsetY = { it / 5 }
                        )
                ) {
                    WeeklyActivityRingsSection(
                        days = weekDays,
                        weekRangeLabel = weekRangeLabel,
                        selectedDate = selectedDate,
                        onDayClick = onDateSelected
                    )
                }

                Spacer(Modifier.height(Dimens.Xl))
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
private fun ActivityDetailMetricSection(
    visible: Boolean,
    enterDelayMillis: Int,
    icon: ImageVector,
    iconTint: Color,
    label: String,
    value: String,
    values: List<Float>,
    barColor: Color,
    valueStyle: androidx.compose.ui.text.TextStyle
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(420, delayMillis = enterDelayMillis, easing = FastOutSlowInEasing)) +
            slideInVertically(
                animationSpec = tween(420, delayMillis = enterDelayMillis, easing = FastOutSlowInEasing),
                initialOffsetY = { it / 4 }
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Dimens.Xl)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.Sm)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
                Text(label, style = LabelCaps, color = OnSurfaceVariant)
            }
            Spacer(Modifier.height(Dimens.Sm))
            AnimatedContent(
                targetState = value,
                transitionSpec = {
                    fadeIn(tween(260)) togetherWith fadeOut(tween(180))
                },
                label = "metricValue_$label"
            ) { animatedValue ->
                Text(
                    text = animatedValue,
                    style = valueStyle,
                    color = OnSurface,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(Dimens.Md))
            ActivityHourlyBarChart(
                values = values,
                barColor = barColor
            )
        }
    }
}

@Composable
private fun ActivitySummaryTile(
    modifier: Modifier,
    visible: Boolean,
    enterDelayMillis: Int,
    icon: ImageVector,
    iconTint: Color,
    label: String,
    value: String,
    valueStyle: androidx.compose.ui.text.TextStyle
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(tween(420, delayMillis = enterDelayMillis, easing = FastOutSlowInEasing)) +
            slideInVertically(
                animationSpec = tween(420, delayMillis = enterDelayMillis, easing = FastOutSlowInEasing),
                initialOffsetY = { it / 5 }
            )
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.Xs)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
                Text(label, style = LabelCaps, color = OnSurfaceVariant)
            }
            Spacer(Modifier.height(Dimens.Sm))
            AnimatedContent(
                targetState = value,
                transitionSpec = {
                    fadeIn(tween(260)) togetherWith fadeOut(tween(180))
                },
                label = "summaryValue_$label"
            ) { animatedValue ->
                Text(
                    text = animatedValue,
                    style = valueStyle,
                    color = OnSurface,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
