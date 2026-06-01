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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

@Composable
fun HistoryScreen(
    repository: VigorlyRepository,
    onHistoryItemClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val history by repository.history.collectAsState()
    val sections = HistoryGrouper.group(history)
    val summary = HistorySummaryCalculator.from(history)
    val contentVisible = rememberWorkoutDetailVisible()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.ContainerMargin, vertical = Dimens.Lg)
    ) {
        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 0) {
            Text(
                stringResource(R.string.history_title),
                style = HeadlineLgMobile.copy(fontSize = 28.sp),
                color = OnSurface,
                fontWeight = FontWeight.Bold
            )
        }

        if (history.isEmpty()) {
            Spacer(Modifier.height(Dimens.Lg))
            EmptyState(
                title = stringResource(R.string.history_empty_title),
                message = stringResource(R.string.history_empty_message)
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
        }

        Spacer(Modifier.height(Dimens.Md))
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
            Text(
                item.title,
                style = BodyMd.copy(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
                color = OnSurface
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
