package com.example.vigorly.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.data.activity.DailyActivityDaySummary
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale

private data class CalendarMonth(
    val yearMonth: YearMonth,
    val days: List<LocalDate?>
)

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
        val monthTitleFormatter = remember {
            DateTimeFormatter.ofPattern("MMMM yyyy", locale)
        }
        val months = remember(today) { buildCalendarMonths(today, monthsBack = 14) }
        val listState = rememberLazyListState()
        val todayMonthIndex = remember(months, today) {
            months.indexOfFirst { it.yearMonth == YearMonth.from(today) }.coerceAtLeast(0)
        }

        LaunchedEffect(Unit) {
            listState.scrollToItem(todayMonthIndex)
        }

        AuthGradientBackground(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                ActivityCalendarTopBar(onBack = onBack)

                CalendarWeekdayHeader(locale = locale)

                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        horizontal = Dimens.ContainerMargin,
                        vertical = Dimens.Md
                    ),
                    verticalArrangement = Arrangement.spacedBy(Dimens.Xl)
                ) {
                    itemsIndexed(months, key = { _, month -> month.yearMonth.toString() }) { _, month ->
                        CalendarMonthSection(
                            month = month,
                            selectedDate = selectedDate,
                            today = today,
                            locale = locale,
                            monthTitleFormatter = monthTitleFormatter,
                            dateKeyFormatter = dateKeyFormatter,
                            history = history,
                            liveSummaryProvider = liveSummaryProvider,
                            onDateSelected = onDateSelected
                        )
                    }
                    item { Spacer(Modifier.height(Dimens.Xl)) }
                }
            }
        }
    }
}

@Composable
private fun ActivityCalendarTopBar(onBack: () -> Unit) {
    val title = stringResource(R.string.activity_calendar_title)
    val backLabel = stringResource(R.string.activity_calendar_back)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.Xs, vertical = Dimens.Xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = backLabel,
                tint = OnSurfaceVariant
            )
        }
        Text(
            text = title,
            style = HeadlineMd.copy(fontWeight = FontWeight.Bold),
            color = OnSurface,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.size(48.dp))
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
            .padding(horizontal = Dimens.ContainerMargin, vertical = Dimens.Sm),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        labels.forEach { label ->
            Text(
                text = label.replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() },
                style = LabelCaps.copy(fontSize = 11.sp, letterSpacing = 0.05.sp),
                color = OnSurfaceVariant,
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
    locale: Locale,
    monthTitleFormatter: DateTimeFormatter,
    dateKeyFormatter: DateTimeFormatter,
    history: Map<String, DailyActivityDaySummary>,
    liveSummaryProvider: (LocalDate) -> DailyActivityDaySummary?,
    onDateSelected: (LocalDate) -> Unit
) {
    val title = month.yearMonth.atDay(1)
        .format(monthTitleFormatter)
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }

    Column {
        Text(
            text = title,
            style = HeadlineMd.copy(fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
            color = Primary,
            modifier = Modifier.padding(bottom = Dimens.Md)
        )
        month.days.chunked(7).forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
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
            Spacer(Modifier.height(Dimens.Xs))
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
            .padding(vertical = 3.dp)
            .clip(RoundedCornerShape(12.dp))
            .then(
                when {
                    isSelected -> Modifier.background(Primary.copy(alpha = 0.22f))
                    else -> Modifier
                }
            )
            .clickable(enabled = date != null && !isFuture) { onClick(date) }
            .padding(vertical = 6.dp, horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        if (date == null) {
            Spacer(Modifier.size(40.dp))
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(38.dp)
                    .then(
                        if (isToday && !isSelected) {
                            Modifier.border(1.5.dp, PrimaryAccent.copy(alpha = 0.7f), CircleShape)
                        } else {
                            Modifier
                        }
                    )
            ) {
                if (!isFuture) {
                    MiniActivityRings(
                        moveProgress = summary?.moveProgress ?: 0f,
                        exerciseProgress = summary?.exerciseProgress ?: 0f,
                        standProgress = summary?.standProgress ?: 0f,
                        size = 32.dp
                    )
                }
            }
            Text(
                text = date.dayOfMonth.toString(),
                style = BodyMd.copy(
                    fontSize = 12.sp,
                    fontWeight = when {
                        isSelected || isToday -> FontWeight.Bold
                        else -> FontWeight.Medium
                    }
                ),
                color = when {
                    isFuture -> OnSurfaceVariant.copy(alpha = 0.28f)
                    isSelected -> PrimaryAccent
                    isToday -> Primary
                    else -> OnSurface.copy(alpha = 0.85f)
                }
            )
        }
    }
}

private fun buildCalendarMonths(today: LocalDate, monthsBack: Int): List<CalendarMonth> {
    val end = YearMonth.from(today)
    val start = end.minusMonths(monthsBack.toLong())
    val locale = Locale.getDefault()
    val weekFields = WeekFields.of(locale)
    val firstDayOfWeek = weekFields.firstDayOfWeek

    val yearMonths = mutableListOf<YearMonth>()
    var cursor = start
    while (!cursor.isAfter(end)) {
        yearMonths.add(cursor)
        cursor = cursor.plusMonths(1)
    }

    return yearMonths.map { yearMonth ->
        val firstOfMonth = yearMonth.atDay(1)
        val offset = ((firstOfMonth.dayOfWeek.value - firstDayOfWeek.value + 7) % 7)
        val leadingBlanks = List(offset) { null as LocalDate? }
        val monthDays = (1..yearMonth.lengthOfMonth()).map { day ->
            yearMonth.atDay(day)
        }
        val allDays = leadingBlanks + monthDays
        val trailing = (7 - allDays.size % 7) % 7
        val padded = allDays + List(trailing) { null as LocalDate? }
        CalendarMonth(yearMonth, padded)
    }
}
