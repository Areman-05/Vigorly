package com.example.vigorly.data.activity

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale

data class WeeklyActivityRingDay(
    val date: LocalDate,
    val dayLabel: String,
    val moveProgress: Float,
    val exerciseProgress: Float,
    val standProgress: Float,
    val isToday: Boolean,
    val isFuture: Boolean
) {
    val hasActivity: Boolean
        get() = moveProgress > 0f || exerciseProgress > 0f || standProgress > 0f
}

object WeeklyActivityRingsBuilder {
    private val dateKeyFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun build(
        history: Map<String, DailyActivityDaySummary>,
        liveTodayDetail: DailyActivityDetail,
        today: LocalDate = LocalDate.now(),
        locale: Locale = Locale.getDefault()
    ): List<WeeklyActivityRingDay> {
        return weekDatesFor(today, locale).map { date ->
            toRingDay(date, today, history, liveTodayDetail, locale)
        }
    }

    fun weekDatesFor(reference: LocalDate, locale: Locale): List<LocalDate> {
        val weekFields = WeekFields.of(locale)
        val firstDayOfWeek = weekFields.firstDayOfWeek
        val daysFromStart = (reference.dayOfWeek.value - firstDayOfWeek.value + 7) % 7
        val weekStart = reference.minusDays(daysFromStart.toLong())
        return (0..6).map { weekStart.plusDays(it.toLong()) }
    }

    fun formatWeekRange(days: List<WeeklyActivityRingDay>, locale: Locale): String {
        if (days.isEmpty()) return ""
        val start = days.first().date
        val end = days.last().date
        val formatter = DateTimeFormatter.ofPattern("d MMM", locale)
        val startLabel = start.format(formatter)
        val endLabel = end.format(formatter)
        return "$startLabel – $endLabel"
    }

    private fun toRingDay(
        date: LocalDate,
        today: LocalDate,
        history: Map<String, DailyActivityDaySummary>,
        liveTodayDetail: DailyActivityDetail,
        locale: Locale
    ): WeeklyActivityRingDay {
        val isFuture = date.isAfter(today)
        val isToday = date == today
        val summary = when {
            isFuture -> null
            isToday -> DailyActivityDaySummary.fromDetail(date.format(dateKeyFormatter), liveTodayDetail)
            else -> history[date.format(dateKeyFormatter)]
        }
        val dayLabel = date.dayOfWeek
            .getDisplayName(TextStyle.SHORT, locale)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
        return WeeklyActivityRingDay(
            date = date,
            dayLabel = dayLabel,
            moveProgress = summary?.moveProgress ?: 0f,
            exerciseProgress = summary?.exerciseProgress ?: 0f,
            standProgress = summary?.standProgress ?: 0f,
            isToday = isToday,
            isFuture = isFuture
        )
    }
}
