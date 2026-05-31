package com.example.vigorly.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.ui.components.ActivityCalendarSheet
import com.example.vigorly.ui.components.ActivityHourlyBarChart
import com.example.vigorly.ui.components.VigorlyOutlineCard
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
                Text(
                    text = headerLabel,
                    style = HeadlineMd,
                    color = OnSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = Dimens.Lg)
                )

                ActivityDetailMetricSection(
                    label = stringResource(R.string.metric_move),
                    value = stringResource(R.string.activity_detail_move_value, detail.moveCalories),
                    values = detail.moveCaloriesByHour.map { it.toFloat() },
                    barColor = PrimaryAccent,
                    valueStyle = statStyle
                )

                Spacer(Modifier.height(Dimens.Lg))

                ActivityDetailMetricSection(
                    label = stringResource(R.string.metric_exercise),
                    value = stringResource(R.string.activity_detail_exercise_value, detail.exerciseMinutes),
                    values = detail.exerciseMinutesByHour.map { it.toFloat() },
                    barColor = PrimaryContainer,
                    valueStyle = statStyle
                )

                Spacer(Modifier.height(Dimens.Lg))

                ActivityDetailMetricSection(
                    label = stringResource(R.string.metric_stand),
                    value = stringResource(R.string.activity_detail_stand_value, detail.standHours),
                    values = detail.standByHour.map { if (it) 1f else 0f },
                    barColor = Primary,
                    maxChartValue = 1f,
                    valueStyle = statStyle
                )

                Spacer(Modifier.height(Dimens.Lg))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.Md)
                ) {
                    ActivitySummaryTile(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.activity_detail_steps),
                        value = "%,d".format(Locale.getDefault(), detail.steps),
                        valueStyle = statStyle.copy(fontSize = 28.sp, lineHeight = 28.sp)
                    )
                    ActivitySummaryTile(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.activity_detail_distance),
                        value = MetricFormatter.formatDistanceKm(detail.distanceKm, unitsMetric),
                        valueStyle = statStyle.copy(fontSize = 28.sp, lineHeight = 28.sp)
                    )
                }

                Spacer(Modifier.height(Dimens.Lg))
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
    label: String,
    value: String,
    values: List<Float>,
    barColor: Color,
    valueStyle: androidx.compose.ui.text.TextStyle,
    maxChartValue: Float? = null
) {
    VigorlyOutlineCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(Dimens.Md)) {
            Text(label, style = LabelCaps, color = OnSurfaceVariant)
            Spacer(Modifier.height(Dimens.Xs))
            Text(
                text = value,
                style = valueStyle,
                color = OnSurface,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(Dimens.Md))
            ActivityHourlyBarChart(
                values = values,
                barColor = barColor,
                maxValue = maxChartValue
            )
        }
    }
}

@Composable
private fun ActivitySummaryTile(
    modifier: Modifier,
    label: String,
    value: String,
    valueStyle: androidx.compose.ui.text.TextStyle
) {
    VigorlyOutlineCard(modifier = modifier) {
        Column(Modifier.padding(Dimens.Md)) {
            Text(label, style = LabelCaps, color = OnSurfaceVariant)
            Spacer(Modifier.height(Dimens.Sm))
            Text(
                text = value,
                style = valueStyle,
                color = OnSurface,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
