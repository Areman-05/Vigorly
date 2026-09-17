package com.example.vigorly.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.vigorly.R
import com.example.vigorly.data.activity.DailyActivityDaySummary
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.GlassLabel
import com.example.vigorly.ui.theme.HeadlineLgMobile
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.PrimaryAccent
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale

private data class CalendarMonth(
    val yearMonth: YearMonth,
    val days: List<LocalDate?>
)

private val PickerCellShape = RoundedCornerShape(16.dp)

@Composable
fun ActivityCalendarSheet(
    visible: Boolean,
    selectedDate: LocalDate,
    history: Map<String, DailyActivityDaySummary>,
    liveSummaryProvider: (LocalDate) -> DailyActivityDaySummary?,
    onBack: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier.fillMaxSize()
    ) {
        val today = remember { LocalDate.now() }
        val locale = Locale.getDefault()
        val dateKeyFormatter = remember { DateTimeFormatter.ISO_LOCAL_DATE }
        var visibleMonth by remember { mutableStateOf(YearMonth.from(today)) }
        var showMonthPicker by remember { mutableStateOf(false) }
        val currentMonth = remember(visibleMonth, locale) { buildMonth(visibleMonth) }

        LaunchedEffect(visible) {
            if (visible) {
                visibleMonth = YearMonth.from(today)
                showMonthPicker = false
            }
        }

        if (showMonthPicker) {
            ActivityMonthYearPickerDialog(
                selectedMonth = visibleMonth,
                today = today,
                locale = locale,
                onDismiss = { showMonthPicker = false },
                onConfirm = { month ->
                    visibleMonth = month
                    showMonthPicker = false
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(bottom = 12.dp)
        ) {
            ActivityCalendarTopBar(
                onBack = onBack,
                onOpenPicker = { showMonthPicker = true }
            )
            CalendarMonthSwitcher(
                month = visibleMonth,
                today = today,
                locale = locale,
                onPrev = { visibleMonth = visibleMonth.minusMonths(1) },
                onNext = { visibleMonth = visibleMonth.plusMonths(1) },
                onOpenPicker = { showMonthPicker = true }
            )
            CalendarWeekdayHeader(locale = locale)
            CalendarMonthSection(
                month = currentMonth,
                selectedDate = selectedDate,
                today = today,
                dateKeyFormatter = dateKeyFormatter,
                history = history,
                liveSummaryProvider = liveSummaryProvider,
                onDateSelected = onDateSelected,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .padding(top = 6.dp)
            )
        }
    }
}

@Composable
private fun ActivityCalendarTopBar(
    onBack: () -> Unit,
    onOpenPicker: () -> Unit
) {
    val backLabel = stringResource(R.string.activity_calendar_back)
    val pickLabel = stringResource(R.string.activity_calendar_pick_month)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FrostedGlassCircleButton(onClick = onBack) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = backLabel,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.weight(1f))
        FrostedGlassCircleButton(onClick = onOpenPicker) {
            Icon(
                Icons.Outlined.CalendarMonth,
                contentDescription = pickLabel,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun CalendarMonthSwitcher(
    month: YearMonth,
    today: LocalDate,
    locale: Locale,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onOpenPicker: () -> Unit
) {
    val monthName = month.month
        .getDisplayName(TextStyle.FULL, locale)
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
    val canGoNext = !month.plusMonths(1).isAfter(YearMonth.from(today))
    val interaction = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MonthChevron(
            enabled = true,
            onClick = onPrev,
            contentDescription = stringResource(R.string.activity_calendar_prev_month)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = null,
                tint = OnSurface,
                modifier = Modifier.size(28.dp)
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable(
                    interactionSource = interaction,
                    indication = null,
                    onClick = onOpenPicker
                )
                .padding(vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = monthName,
                style = HeadlineLgMobile.copy(
                    fontSize = 32.sp,
                    lineHeight = 36.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.6).sp
                ),
                color = OnSurface,
                textAlign = TextAlign.Center
            )
            Text(
                text = month.year.toString(),
                style = LabelCaps.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.sp
                ),
                color = GlassLabel.copy(alpha = 0.5f),
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        MonthChevron(
            enabled = canGoNext,
            onClick = onNext,
            contentDescription = stringResource(R.string.activity_calendar_next_month)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = if (canGoNext) OnSurface else GlassLabel.copy(alpha = 0.28f),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun MonthChevron(
    enabled: Boolean,
    onClick: () -> Unit,
    contentDescription: String,
    icon: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .semantics { this.contentDescription = contentDescription }
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        icon()
    }
}

@Composable
private fun CalendarWeekdayHeader(locale: Locale) {
    val weekFields = WeekFields.of(locale)
    val firstDay = weekFields.firstDayOfWeek
    val labels = (0 until 7).map { offset ->
        firstDay.plus(offset.toLong()).getDisplayName(TextStyle.SHORT, locale)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .padding(top = 18.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        labels.forEach { label ->
            Text(
                text = label.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(locale) else it.toString()
                }.take(1),
                style = LabelCaps.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.8.sp
                ),
                color = GlassLabel.copy(alpha = 0.42f),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CalendarMonthSection(
    month: CalendarMonth,
    selectedDate: LocalDate,
    today: LocalDate,
    dateKeyFormatter: DateTimeFormatter,
    history: Map<String, DailyActivityDaySummary>,
    liveSummaryProvider: (LocalDate) -> DailyActivityDaySummary?,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        month.days.chunked(7).forEach { week ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = true),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                week.forEach { date ->
                    CalendarDayCell(
                        date = date,
                        selectedDate = selectedDate,
                        today = today,
                        summary = date?.let { day ->
                            val key = day.format(dateKeyFormatter)
                            history[key] ?: liveSummaryProvider(day)
                        },
                        onClick = { clicked ->
                            if (clicked != null && !clicked.isAfter(today)) {
                                onDateSelected(clicked)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                repeat(7 - week.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    date: LocalDate?,
    selectedDate: LocalDate,
    today: LocalDate,
    summary: DailyActivityDaySummary?,
    onClick: (LocalDate?) -> Unit,
    modifier: Modifier = Modifier
) {
    val isSelected = date == selectedDate
    val isToday = date == today
    val isFuture = date?.isAfter(today) == true

    Column(
        modifier = modifier
            .clickable(enabled = date != null && !isFuture) { onClick(date) }
            .padding(horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (date == null) {
            Spacer(
                Modifier
                    .fillMaxWidth(0.86f)
                    .aspectRatio(1f)
            )
        } else {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth(0.86f)
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                MiniActivityRings(
                    moveProgress = if (isFuture) 0f else summary?.moveProgress ?: 0f,
                    exerciseProgress = if (isFuture) 0f else summary?.exerciseProgress ?: 0f,
                    standProgress = if (isFuture) 0f else summary?.standProgress ?: 0f,
                    size = maxWidth,
                    muted = isFuture
                )
            }
            Text(
                text = date.dayOfMonth.toString(),
                style = BodyMd.copy(
                    fontSize = 13.sp,
                    fontWeight = when {
                        isSelected || isToday -> FontWeight.Bold
                        else -> FontWeight.Medium
                    }
                ),
                color = when {
                    isFuture -> GlassLabel.copy(alpha = 0.28f)
                    isSelected -> PrimaryAccent
                    isToday -> OnSurface
                    else -> OnSurface.copy(alpha = 0.82f)
                },
                modifier = Modifier.padding(top = 4.dp)
            )
            Box(
                modifier = Modifier
                    .padding(top = 3.dp)
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(if (isToday) PrimaryAccent else Color.Transparent)
            )
        }
    }
}

private fun buildMonth(yearMonth: YearMonth): CalendarMonth {
    val locale = Locale.getDefault()
    val weekFields = WeekFields.of(locale)
    val firstDayOfWeek = weekFields.firstDayOfWeek
    val firstOfMonth = yearMonth.atDay(1)
    val offset = ((firstOfMonth.dayOfWeek.value - firstDayOfWeek.value + 7) % 7)
    val leadingBlanks = List(offset) { null as LocalDate? }
    val monthDays = (1..yearMonth.lengthOfMonth()).map { day -> yearMonth.atDay(day) }
    val allDays = leadingBlanks + monthDays
    val trailing = (7 - allDays.size % 7) % 7
    val padded = allDays + List(trailing) { null as LocalDate? }
    return CalendarMonth(yearMonth, padded)
}

private enum class MonthPickerLevel { Months, Years }

@Composable
private fun ActivityMonthYearPickerDialog(
    selectedMonth: YearMonth,
    today: LocalDate,
    locale: Locale,
    onDismiss: () -> Unit,
    onConfirm: (YearMonth) -> Unit
) {
    val todayMonth = YearMonth.from(today)
    var level by remember { mutableStateOf(MonthPickerLevel.Months) }
    var visibleYear by remember { mutableIntStateOf(selectedMonth.year) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        GlassSurface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            shape = RoundedCornerShape(32.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = stringResource(R.string.activity_calendar_pick_month),
                    style = HeadlineMd.copy(fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
                    color = OnSurface
                )
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FrostedGlassCircleButton(
                        onClick = {
                            if (level == MonthPickerLevel.Months) visibleYear -= 1
                            else visibleYear -= 12
                        },
                        size = 40.dp
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = stringResource(R.string.activity_calendar_prev_month),
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = if (level == MonthPickerLevel.Months) {
                            visibleYear.toString()
                        } else {
                            val start = (visibleYear / 12) * 12
                            "$start – ${start + 11}"
                        },
                        style = BodyMd.copy(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
                        color = OnSurface,
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .clickable {
                                level = if (level == MonthPickerLevel.Months) {
                                    MonthPickerLevel.Years
                                } else {
                                    MonthPickerLevel.Months
                                }
                            }
                            .background(Color.White.copy(alpha = 0.08f))
                            .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(999.dp))
                            .padding(horizontal = 18.dp, vertical = 8.dp)
                    )
                    val canGoNext = if (level == MonthPickerLevel.Months) {
                        visibleYear < today.year
                    } else {
                        (visibleYear / 12) * 12 + 12 <= today.year
                    }
                    FrostedGlassCircleButton(
                        onClick = {
                            if (level == MonthPickerLevel.Months) visibleYear += 1
                            else visibleYear += 12
                        },
                        enabled = canGoNext,
                        size = 40.dp
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = stringResource(R.string.activity_calendar_next_month),
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                if (level == MonthPickerLevel.Years) {
                    val start = (visibleYear / 12) * 12
                    PickerCellGrid { row, col ->
                        val year = start + row * 4 + col
                        PickerCell(
                            label = year.toString(),
                            enabled = year <= today.year,
                            selected = year == selectedMonth.year,
                            highlighted = year == today.year,
                            onClick = {
                                visibleYear = year
                                level = MonthPickerLevel.Months
                            }
                        )
                    }
                } else {
                    PickerCellGrid { row, col ->
                        val monthValue = row * 4 + col + 1
                        val ym = YearMonth.of(visibleYear, monthValue)
                        val label = Month.of(monthValue)
                            .getDisplayName(TextStyle.SHORT, locale)
                            .replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(locale) else it.toString()
                            }
                        PickerCell(
                            label = label,
                            enabled = !ym.isAfter(todayMonth),
                            selected = ym == selectedMonth,
                            highlighted = ym == todayMonth,
                            onClick = { onConfirm(ym) }
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
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
                }
            }
        }
    }
}

@Composable
private fun PickerCellGrid(
    cell: @Composable (row: Int, col: Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        (0 until 3).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                (0 until 4).forEach { col ->
                    Box(modifier = Modifier.weight(1f)) {
                        cell(row, col)
                    }
                }
            }
        }
    }
}

@Composable
private fun PickerCell(
    label: String,
    enabled: Boolean,
    selected: Boolean,
    highlighted: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.35f)
            .clip(PickerCellShape)
            .background(
                when {
                    selected -> PrimaryAccent.copy(alpha = 0.28f)
                    enabled -> Color.White.copy(alpha = 0.06f)
                    else -> Color.Transparent
                }
            )
            .then(
                if (selected) {
                    Modifier.border(1.dp, PrimaryAccent.copy(alpha = 0.55f), PickerCellShape)
                } else {
                    Modifier.border(
                        1.dp,
                        Color.White.copy(alpha = if (enabled) 0.08f else 0.03f),
                        PickerCellShape
                    )
                }
            )
            .then(
                if (enabled) Modifier.clickable(onClick = onClick) else Modifier
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
                highlighted -> PrimaryAccent
                else -> OnSurface.copy(alpha = 0.9f)
            }
        )
    }
}
