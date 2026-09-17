package com.example.vigorly.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.example.vigorly.data.model.WorkoutHistoryItem
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.ui.analysis.AnalysisPeriodMode
import com.example.vigorly.ui.analysis.AnalysisPeriodPickerDialog
import com.example.vigorly.ui.components.EmptyState
import com.example.vigorly.ui.components.FrostedGlassCircleButton
import com.example.vigorly.ui.components.GlassSurface
import com.example.vigorly.ui.iconForName
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.DisplayStat
import com.example.vigorly.ui.theme.GlassLabel
import com.example.vigorly.ui.theme.HeadlineLgMobile
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.workout.WorkoutDetailSectionEnter
import com.example.vigorly.ui.workout.rememberWorkoutDetailVisible
import com.example.vigorly.util.HistoryGrouper
import com.example.vigorly.util.HistoryLabels
import com.example.vigorly.util.HistorySummaryCalculator
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HistoryScreen(
    repository: VigorlyRepository,
    onHistoryItemClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val history by repository.history.collectAsState()
    val today = remember { LocalDate.now() }
    var filterDate by remember { mutableStateOf(today) }
    var showDatePicker by remember { mutableStateOf(false) }
    val locale = remember { Locale.getDefault() }
    val filterDateFormatter = remember(locale) {
        DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM yyyy", locale)
    }

    val sortedHistory = remember(history) {
        history.sortedByDescending { it.completedAtMillis }
    }

    val filteredHistory = remember(sortedHistory, filterDate) {
        sortedHistory.filter { HistoryLabels.itemLocalDate(it) == filterDate }
    }

    val sections = remember(filteredHistory) { HistoryGrouper.group(filteredHistory) }
    val summary = remember(filteredHistory) { HistorySummaryCalculator.from(filteredHistory) }
    val viewingToday = filterDate == today
    val contentVisible = rememberWorkoutDetailVisible()

    if (showDatePicker) {
        AnalysisPeriodPickerDialog(
            selectedDate = filterDate,
            mode = AnalysisPeriodMode.Day,
            showModeToggle = false,
            titleRes = R.string.history_date_picker_title,
            subtitleRes = R.string.history_date_picker_subtitle,
            onDismiss = { showDatePicker = false },
            onConfirm = { date, _ ->
                filterDate = date
                showDatePicker = false
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag(VigorlyTestTags.HISTORY)
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
                    stringResource(R.string.history_title),
                    style = HeadlineLgMobile,
                    color = OnSurface,
                    modifier = Modifier.weight(1f)
                )
                FrostedGlassCircleButton(
                    onClick = { showDatePicker = true }
                ) {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = stringResource(R.string.history_calendar_hint),
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        if (history.isEmpty()) {
            Spacer(Modifier.height(Dimens.Lg))
            EmptyState(
                title = stringResource(R.string.history_empty_title),
                message = stringResource(R.string.history_empty_message)
            )
        } else {
            if (!viewingToday) {
                WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 80) {
                    HistoryFilterBanner(
                        label = stringResource(
                            R.string.history_filter_banner,
                            filterDateFormatter.format(filterDate).replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(locale) else it.toString()
                            }
                        ),
                        actionLabel = stringResource(R.string.history_back_to_today),
                        onClear = { filterDate = today },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = Dimens.Md)
                    )
                }
            }

            if (filteredHistory.isEmpty()) {
                Spacer(Modifier.height(Dimens.Lg))
                EmptyState(
                    title = stringResource(R.string.history_no_sessions_date),
                    message = stringResource(R.string.history_calendar_hint)
                )
            } else {
                WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 120) {
                    HistorySummaryCard(
                        sessions = summary.totalSessions,
                        totalMinutes = summary.totalMinutes,
                        totalCalories = summary.totalCalories,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = Dimens.Lg, bottom = Dimens.Md)
                    )
                }

                var delay = 220
                sections.forEach { section ->
                    section.items.forEach { item ->
                        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = delay) {
                            HistoryItemCard(
                                item = item,
                                onClick = { onHistoryItemClick(item.id) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 10.dp)
                            )
                        }
                        delay += 50
                    }
                }
            }
        }

        Spacer(Modifier.height(Dimens.FloatingNavClearance))
    }
}

@Composable
private fun HistoryFilterBanner(
    label: String,
    actionLabel: String,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassSurface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                label,
                style = BodyMd.copy(fontSize = 14.sp, fontWeight = FontWeight.Medium),
                color = OnSurface,
                modifier = Modifier.weight(1f)
            )
            Text(
                actionLabel,
                style = BodyMd.copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                color = GlassLabel.copy(alpha = 0.9f),
                modifier = Modifier.clickable(onClick = onClear)
            )
        }
    }
}

@Composable
private fun HistorySummaryCard(
    sessions: Int,
    totalMinutes: Int,
    totalCalories: Int,
    modifier: Modifier = Modifier
) {
    GlassSurface(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SummaryMetric(
                label = stringResource(R.string.history_sessions),
                value = "$sessions",
                modifier = Modifier.weight(1f)
            )
            SummaryMetric(
                label = stringResource(R.string.history_total_time),
                value = "$totalMinutes",
                suffix = "min",
                modifier = Modifier.weight(1f)
            )
            SummaryMetric(
                label = stringResource(R.string.history_calories),
                value = "%,d".format(totalCalories),
                suffix = "kcal",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SummaryMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    suffix: String? = null
) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            label.uppercase(),
            style = LabelCaps.copy(fontSize = 10.sp),
            color = GlassLabel.copy(alpha = 0.55f)
        )
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.padding(top = 6.dp)
        ) {
            Text(
                value,
                style = DisplayStat.copy(fontSize = 26.sp, lineHeight = 28.sp),
                color = OnSurface
            )
            suffix?.let {
                Text(
                    it,
                    style = BodyMd.copy(fontSize = 12.sp),
                    color = GlassLabel.copy(alpha = 0.55f),
                    modifier = Modifier.padding(start = 2.dp, bottom = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun HistoryItemCard(
    item: WorkoutHistoryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sessionDate = HistoryLabels.formatItemDate(item.completedAtMillis)

    GlassSurface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconForName(item.iconName),
                    contentDescription = null,
                    tint = OnSurface.copy(alpha = 0.92f),
                    modifier = Modifier.size(22.dp)
                )
            }
            Column(
                Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                if (sessionDate.isNotBlank()) {
                    Text(
                        sessionDate.uppercase(),
                        style = LabelCaps.copy(fontSize = 10.sp),
                        color = GlassLabel.copy(alpha = 0.55f)
                    )
                }
                Text(
                    item.title,
                    style = BodyMd.copy(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
                    color = OnSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = if (sessionDate.isNotBlank()) 4.dp else 0.dp)
                )
                Text(
                    HistoryLabels.displayTimestamp(item),
                    style = BodyMd.copy(fontSize = 13.sp),
                    color = GlassLabel.copy(alpha = 0.65f),
                    modifier = Modifier.padding(top = 3.dp)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    stringResource(R.string.history_duration_chip, item.durationMinutes),
                    style = BodyMd.copy(fontSize = 13.sp, fontWeight = FontWeight.Medium),
                    color = OnSurface.copy(alpha = 0.9f)
                )
                Text(
                    stringResource(R.string.history_calories_chip, item.calories),
                    style = BodyMd.copy(fontSize = 12.sp),
                    color = GlassLabel.copy(alpha = 0.55f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = GlassLabel.copy(alpha = 0.4f),
                modifier = Modifier
                    .padding(start = 6.dp)
                    .size(20.dp)
            )
        }
    }
}
