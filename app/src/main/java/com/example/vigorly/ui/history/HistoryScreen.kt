package com.example.vigorly.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.data.model.WorkoutHistoryItem
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.ui.components.EmptyState
import com.example.vigorly.ui.iconForName
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
import com.example.vigorly.ui.workout.WorkoutDetailSectionEnter
import com.example.vigorly.ui.workout.WorkoutTypeTheme
import com.example.vigorly.ui.workout.rememberWorkoutDetailVisible
import com.example.vigorly.util.HistoryGrouper
import com.example.vigorly.util.HistoryLabels
import com.example.vigorly.util.HistorySectionKind
import com.example.vigorly.util.HistorySummaryCalculator
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val HISTORY_INITIAL_PAGE = 8
private const val HISTORY_PAGE_INCREMENT = 8

@Composable
fun HistoryScreen(
    repository: VigorlyRepository,
    onHistoryItemClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val history by repository.history.collectAsState()
    var visibleCount by remember { mutableIntStateOf(HISTORY_INITIAL_PAGE) }
    var filterDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    val locale = remember { Locale.getDefault() }
    val filterDateFormatter = remember(locale) {
        DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM yyyy", locale)
    }

    val sortedHistory = remember(history) {
        history.sortedByDescending { it.completedAtMillis }
    }

    val filteredHistory = remember(sortedHistory, filterDate) {
        val date = filterDate ?: return@remember sortedHistory
        sortedHistory.filter { HistoryLabels.itemLocalDate(it) == date }
    }

    LaunchedEffect(filterDate) {
        visibleCount = if (filterDate != null) filteredHistory.size.coerceAtLeast(HISTORY_INITIAL_PAGE)
        else HISTORY_INITIAL_PAGE
    }

    val visibleHistory = remember(filteredHistory, visibleCount, filterDate) {
        if (filterDate != null) filteredHistory
        else filteredHistory.take(visibleCount)
    }

    val sections = remember(visibleHistory) { HistoryGrouper.group(visibleHistory) }
    val summary = remember(visibleHistory) { HistorySummaryCalculator.from(visibleHistory) }
    val hasMore = filterDate == null && visibleCount < filteredHistory.size
    val contentVisible = rememberWorkoutDetailVisible()

    if (showDatePicker) {
        HistoryDatePickerDialog(
            onDismiss = { showDatePicker = false },
            onDateSelected = { date ->
                filterDate = date
                showDatePicker = false
            },
            initialDate = filterDate ?: LocalDate.now()
        )
    }

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
                    stringResource(R.string.history_title),
                    style = HeadlineLgMobile.copy(fontSize = 28.sp),
                    color = OnSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = stringResource(R.string.history_calendar_hint),
                        tint = PrimaryAccent,
                        modifier = Modifier.size(26.dp)
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
            filterDate?.let { date ->
                WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 80) {
                    HistoryFilterBanner(
                        label = stringResource(
                            R.string.history_filter_banner,
                            filterDateFormatter.format(date).replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(locale) else it.toString()
                            }
                        ),
                        onClear = {
                            filterDate = null
                            visibleCount = HISTORY_INITIAL_PAGE
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = Dimens.Md)
                    )
                }
            }

            if (filterDate != null && filteredHistory.isEmpty()) {
                Spacer(Modifier.height(Dimens.Lg))
                EmptyState(
                    title = stringResource(R.string.history_no_sessions_date),
                    message = stringResource(R.string.history_clear_filter)
                )
                Text(
                    stringResource(R.string.history_clear_filter),
                    style = BodyMd.copy(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
                    color = PrimaryAccent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimens.Sm)
                        .clickable {
                            filterDate = null
                            visibleCount = HISTORY_INITIAL_PAGE
                        },
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
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
                    WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = delay) {
                        Text(
                            sectionTitle(section.kind),
                            style = LabelCaps.copy(fontSize = 11.sp),
                            color = PrimaryAccent.copy(alpha = 0.85f),
                            modifier = Modifier.padding(top = Dimens.Sm, bottom = Dimens.Sm)
                        )
                    }
                    delay += 60
                    section.items.forEach { item ->
                        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = delay) {
                            HistoryItemCard(
                                item = item,
                                onClick = { onHistoryItemClick(item.id) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                            )
                        }
                        delay += 50
                    }
                }

                if (hasMore) {
                    WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = delay) {
                        HistoryLoadMoreRow(
                            onClick = { visibleCount += HISTORY_PAGE_INCREMENT },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = Dimens.Md, bottom = Dimens.Sm)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(Dimens.Md))
    }
}

@Composable
private fun HistoryFilterBanner(
    label: String,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Primary.copy(alpha = 0.1f))
            .padding(horizontal = Dimens.Md, vertical = 12.dp),
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
            stringResource(R.string.history_clear_filter),
            style = BodyMd.copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
            color = PrimaryAccent,
            modifier = Modifier.clickable(onClick = onClear)
        )
    }
}

@Composable
private fun HistoryLoadMoreRow(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            stringResource(R.string.history_load_more),
            style = BodyMd.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
            color = PrimaryAccent
        )
        Icon(
            Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = PrimaryAccent,
            modifier = Modifier
                .padding(start = 4.dp)
                .size(22.dp)
        )
    }
}

@Composable
private fun sectionTitle(kind: HistorySectionKind): String = when (kind) {
    HistorySectionKind.TODAY -> stringResource(R.string.history_section_today)
    HistorySectionKind.YESTERDAY -> stringResource(R.string.history_section_yesterday)
    HistorySectionKind.EARLIER -> stringResource(R.string.history_section_earlier)
}

@Composable
private fun HistorySummaryCard(
    sessions: Int,
    totalMinutes: Int,
    totalCalories: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Primary.copy(alpha = 0.12f),
                        PrimaryAccent.copy(alpha = 0.05f)
                    )
                )
            )
            .padding(Dimens.Md)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            SummaryMetric(
                label = stringResource(R.string.history_sessions),
                value = "$sessions",
                accent = PrimaryAccent,
                modifier = Modifier.weight(1f)
            )
            SummaryMetric(
                label = stringResource(R.string.history_total_time),
                value = "$totalMinutes",
                suffix = "min",
                accent = Primary,
                modifier = Modifier.weight(1f)
            )
            SummaryMetric(
                label = stringResource(R.string.history_calories),
                value = "%,d".format(totalCalories),
                suffix = "kcal",
                accent = PrimaryContainer,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SummaryMetric(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier,
    suffix: String? = null
) {
    Column(modifier) {
        Text(label, style = LabelCaps.copy(fontSize = 9.sp), color = OnSurfaceVariant.copy(0.8f))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                value,
                style = DisplayStat.copy(fontSize = 24.sp, lineHeight = 26.sp),
                color = accent,
                fontWeight = FontWeight.Bold
            )
            suffix?.let {
                Text(
                    it,
                    style = BodyMd.copy(fontSize = 12.sp),
                    color = OnSurfaceVariant.copy(0.65f),
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
    val type = HistoryLabels.parseWorkoutType(item.workoutType)
    val accent = type?.let { WorkoutTypeTheme.accent(it) } ?: PrimaryAccent
    val sessionDate = HistoryLabels.formatItemDate(item.completedAtMillis)

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        accent.copy(alpha = 0.12f),
                        Primary.copy(alpha = 0.05f),
                        PrimaryAccent.copy(alpha = 0.02f)
                    )
                )
            )
            .clickable(onClick = onClick)
            .padding(horizontal = Dimens.Md, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = iconForName(item.iconName),
            contentDescription = null,
            tint = accent,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.16f))
                .padding(10.dp)
        )
        Column(
            Modifier
                .weight(1f)
                .padding(horizontal = Dimens.Md)
        ) {
            if (sessionDate.isNotBlank()) {
                Text(
                    sessionDate,
                    style = LabelCaps.copy(fontSize = 10.sp),
                    color = PrimaryAccent.copy(alpha = 0.85f)
                )
            }
            Text(
                item.title,
                style = BodyMd.copy(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
                color = OnSurface,
                modifier = Modifier.padding(top = if (sessionDate.isNotBlank()) 4.dp else 0.dp)
            )
            Text(
                HistoryLabels.displayTimestamp(item),
                style = BodyMd.copy(fontSize = 13.sp),
                color = OnSurfaceVariant.copy(alpha = 0.75f),
                modifier = Modifier.padding(top = 3.dp)
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                stringResource(R.string.history_duration_chip, item.durationMinutes).uppercase(),
                style = LabelCaps.copy(fontSize = 10.sp),
                color = accent
            )
            Text(
                stringResource(R.string.history_calories_chip, item.calories).uppercase(),
                style = LabelCaps.copy(fontSize = 9.sp),
                color = OnSurfaceVariant.copy(alpha = 0.65f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = OnSurfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier
                .padding(start = Dimens.Sm)
                .size(20.dp)
        )
    }
}
