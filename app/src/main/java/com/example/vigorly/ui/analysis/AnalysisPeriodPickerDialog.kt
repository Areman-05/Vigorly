package com.example.vigorly.ui.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.vigorly.R
import com.example.vigorly.data.activity.WeeklyActivityRingsBuilder
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.GlassLabel
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.SurfaceContainer
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

enum class AnalysisPeriodMode { Day, Week }

private enum class CalendarLevel { Days, Months, Years }

@Composable
fun AnalysisPeriodPickerDialog(
    selectedDate: LocalDate,
    mode: AnalysisPeriodMode,
    onDismiss: () -> Unit,
    onConfirm: (date: LocalDate, mode: AnalysisPeriodMode) -> Unit,
    showModeToggle: Boolean = true,
    titleRes: Int = R.string.analysis_period_picker_title,
    subtitleRes: Int = R.string.analysis_period_picker_subtitle
) {
    val today = remember { LocalDate.now() }
    val locale = Locale.getDefault()
    var draftMode by remember {
        mutableStateOf(if (showModeToggle) mode else AnalysisPeriodMode.Day)
    }
    var draftDate by remember { mutableStateOf(selectedDate.coerceAtMost(today)) }
    var visibleMonth by remember { mutableStateOf(YearMonth.from(draftDate)) }
    var calendarLevel by remember { mutableStateOf(CalendarLevel.Days) }
    var visibleYear by remember { mutableIntStateOf(draftDate.year) }

    val headerTitle = when (calendarLevel) {
        CalendarLevel.Days -> visibleMonth
            .format(DateTimeFormatter.ofPattern("MMMM yyyy", locale))
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
        CalendarLevel.Months -> visibleYear.toString()
        CalendarLevel.Years -> {
            val start = (visibleYear / 12) * 12
            "$start – ${start + 11}"
        }
    }
    val selectedWeek = remember(draftDate, locale) {
        WeeklyActivityRingsBuilder.weekDatesFor(draftDate, locale)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(SurfaceContainer)
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(titleRes),
                style = HeadlineMd.copy(fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
                color = OnSurface
            )
            Text(
                text = stringResource(subtitleRes),
                style = BodyMd.copy(fontSize = 13.sp),
                color = GlassLabel.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            if (showModeToggle) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    PeriodModeChip(
                        label = stringResource(R.string.analysis_period_day),
                        selected = draftMode == AnalysisPeriodMode.Day,
                        onClick = { draftMode = AnalysisPeriodMode.Day },
                        modifier = Modifier.weight(1f)
                    )
                    PeriodModeChip(
                        label = stringResource(R.string.analysis_period_week),
                        selected = draftMode == AnalysisPeriodMode.Week,
                        onClick = { draftMode = AnalysisPeriodMode.Week },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(18.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.08f))
                        .clickable {
                            when (calendarLevel) {
                                CalendarLevel.Days -> {
                                    val prev = visibleMonth.minusMonths(1)
                                    visibleMonth = prev
                                    visibleYear = prev.year
                                }
                                CalendarLevel.Months -> visibleYear -= 1
                                CalendarLevel.Years -> visibleYear -= 12
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = null,
                        tint = OnSurface,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .clickable {
                            calendarLevel = when (calendarLevel) {
                                CalendarLevel.Days -> CalendarLevel.Months
                                CalendarLevel.Months -> CalendarLevel.Years
                                CalendarLevel.Years -> CalendarLevel.Years
                            }
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = headerTitle,
                        style = BodyMd.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                        color = OnSurface
                    )
                    if (calendarLevel != CalendarLevel.Years) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = stringResource(R.string.analysis_period_pick_year),
                            tint = GlassLabel,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                val canGoNext = when (calendarLevel) {
                    CalendarLevel.Days -> !visibleMonth.plusMonths(1).isAfter(YearMonth.from(today))
                    CalendarLevel.Months -> visibleYear < today.year
                    CalendarLevel.Years -> (visibleYear / 12) * 12 + 12 <= today.year
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (canGoNext) Color.White.copy(alpha = 0.08f)
                            else Color.White.copy(alpha = 0.04f)
                        )
                        .then(
                            if (canGoNext) {
                                Modifier.clickable {
                                    when (calendarLevel) {
                                        CalendarLevel.Days -> {
                                            val next = visibleMonth.plusMonths(1)
                                            visibleMonth = next
                                            visibleYear = next.year
                                        }
                                        CalendarLevel.Months -> visibleYear += 1
                                        CalendarLevel.Years -> visibleYear += 12
                                    }
                                }
                            } else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = if (canGoNext) OnSurface else GlassLabel.copy(alpha = 0.35f),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            when (calendarLevel) {
                CalendarLevel.Years -> {
                    val start = (visibleYear / 12) * 12
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        (0 until 3).forEach { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                (0 until 4).forEach { col ->
                                    val year = start + row * 4 + col
                                    val enabled = year <= today.year
                                    val selected = year == draftDate.year
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1.35f)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(
                                                when {
                                                    selected -> PrimaryAccent.copy(alpha = 0.3f)
                                                    enabled -> Color.White.copy(alpha = 0.06f)
                                                    else -> Color.Transparent
                                                }
                                            )
                                            .then(
                                                if (enabled) Modifier.clickable {
                                                    visibleYear = year
                                                    calendarLevel = CalendarLevel.Months
                                                } else Modifier
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = year.toString(),
                                            style = BodyMd.copy(
                                                fontSize = 15.sp,
                                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                            ),
                                            color = when {
                                                !enabled -> GlassLabel.copy(alpha = 0.28f)
                                                selected -> OnSurface
                                                year == today.year -> PrimaryAccent
                                                else -> OnSurface.copy(alpha = 0.9f)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                CalendarLevel.Months -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        (0 until 3).forEach { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                (1..4).forEach { col ->
                                    val monthValue = row * 4 + col
                                    val ym = YearMonth.of(visibleYear, monthValue)
                                    val enabled = !ym.isAfter(YearMonth.from(today))
                                    val selected = ym.year == draftDate.year &&
                                        ym.monthValue == draftDate.monthValue
                                    val label = Month.of(monthValue)
                                        .getDisplayName(TextStyle.SHORT, locale)
                                        .replaceFirstChar {
                                            if (it.isLowerCase()) it.titlecase(locale) else it.toString()
                                        }
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1.35f)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(
                                                when {
                                                    selected -> PrimaryAccent.copy(alpha = 0.3f)
                                                    enabled -> Color.White.copy(alpha = 0.06f)
                                                    else -> Color.Transparent
                                                }
                                            )
                                            .then(
                                                if (enabled) Modifier.clickable {
                                                    visibleMonth = ym
                                                    calendarLevel = CalendarLevel.Days
                                                } else Modifier
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            style = BodyMd.copy(
                                                fontSize = 14.sp,
                                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                            ),
                                            color = when {
                                                !enabled -> GlassLabel.copy(alpha = 0.28f)
                                                selected -> OnSurface
                                                ym == YearMonth.from(today) -> PrimaryAccent
                                                else -> OnSurface.copy(alpha = 0.9f)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                CalendarLevel.Days -> {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        val weekFields = java.time.temporal.WeekFields.of(locale)
                        var day = weekFields.firstDayOfWeek
                        repeat(7) {
                            Text(
                                text = day.getDisplayName(TextStyle.NARROW, locale),
                                style = BodyMd.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium),
                                color = GlassLabel.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f)
                            )
                            day = day.plus(1)
                        }
                    }

                    Spacer(Modifier.height(6.dp))

                    val firstOfMonth = visibleMonth.atDay(1)
                    val weekFields = java.time.temporal.WeekFields.of(locale)
                    val startOffset =
                        (firstOfMonth.dayOfWeek.value - weekFields.firstDayOfWeek.value + 7) % 7
                    val daysInMonth = visibleMonth.lengthOfMonth()
                    val cells = buildList {
                        repeat(startOffset) { add(null as LocalDate?) }
                        for (d in 1..daysInMonth) add(visibleMonth.atDay(d))
                        while (size % 7 != 0) add(null)
                    }

                    cells.chunked(7).forEach { weekRow ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            weekRow.forEach { date ->
                                val enabled = date != null && !date.isAfter(today)
                                val isSelectedDay = date != null &&
                                    draftMode == AnalysisPeriodMode.Day &&
                                    date == draftDate
                                val inSelectedWeek = date != null &&
                                    draftMode == AnalysisPeriodMode.Week &&
                                    date in selectedWeek
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(2.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            when {
                                                isSelectedDay || inSelectedWeek ->
                                                    PrimaryAccent.copy(alpha = 0.3f)
                                                else -> Color.Transparent
                                            }
                                        )
                                        .then(
                                            if (enabled) Modifier.clickable {
                                                draftDate = date!!
                                            } else Modifier
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (date != null) {
                                        Text(
                                            text = date.dayOfMonth.toString(),
                                            style = BodyMd.copy(
                                                fontSize = 14.sp,
                                                fontWeight = if (isSelectedDay || inSelectedWeek) {
                                                    FontWeight.Bold
                                                } else {
                                                    FontWeight.Medium
                                                }
                                            ),
                                            color = when {
                                                !enabled -> GlassLabel.copy(alpha = 0.28f)
                                                isSelectedDay || inSelectedWeek -> OnSurface
                                                date == today -> PrimaryAccent
                                                else -> OnSurface.copy(alpha = 0.9f)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = stringResource(R.string.analysis_dialog_close),
                        color = GlassLabel
                    )
                }
                TextButton(onClick = { onConfirm(draftDate, draftMode) }) {
                    Text(
                        text = stringResource(R.string.analysis_period_apply),
                        color = PrimaryAccent,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun PeriodModeChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) PrimaryAccent else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = BodyMd.copy(
                fontSize = 14.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            ),
            color = if (selected) OnSurface else GlassLabel.copy(alpha = 0.8f)
        )
    }
}
