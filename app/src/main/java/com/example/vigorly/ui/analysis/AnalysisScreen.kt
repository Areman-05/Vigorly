package com.example.vigorly.ui.analysis

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Straighten
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.vigorly.R
import com.example.vigorly.core.testing.VigorlyTestTags
import com.example.vigorly.data.MilestoneHints
import com.example.vigorly.data.activity.ActivityMetric
import com.example.vigorly.data.activity.WeeklyActivityRingsBuilder
import com.example.vigorly.data.model.Milestone
import com.example.vigorly.data.model.WeightLogEntry
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.ui.components.GlassSurface
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.DisplayStat
import com.example.vigorly.ui.theme.GlassLabel
import com.example.vigorly.ui.theme.HeadlineLgMobile
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.util.MetricFormatter
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private enum class AnalysisTab { Progress, Achievements }

@Composable
fun AnalysisScreen(
    repository: VigorlyRepository,
    onOpenMetric: (ActivityMetric) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var tab by remember { mutableStateOf(AnalysisTab.Progress) }
    var showPeriodPicker by remember { mutableStateOf(false) }
    var periodMode by remember { mutableStateOf(AnalysisPeriodMode.Day) }
    val milestones by repository.milestones.collectAsState()
    val weightLog by repository.weightLog.collectAsState()
    val weightGoal by repository.weightGoalKg.collectAsState()
    val detail by repository.displayedActivityDetail.collectAsState()
    val selectedDate by repository.selectedActivityDate.collectAsState()
    val history by repository.activityDayHistory.collectAsState()
    val unitsMetric by repository.unitsMetric.collectAsState()
    val locale = Locale.getDefault()
    val today = remember { LocalDate.now() }
    val dateKeyFormatter = remember { DateTimeFormatter.ISO_LOCAL_DATE }
    val dayLabelFormatter = remember {
        DateTimeFormatter.ofPattern("d MMM", locale)
    }

    val weekDates = remember(selectedDate, locale) {
        WeeklyActivityRingsBuilder.weekDatesFor(selectedDate, locale)
    }
    val weekRangeLabel = remember(weekDates, locale) {
        if (weekDates.isEmpty()) ""
        else {
            val start = weekDates.first().format(dayLabelFormatter)
            val end = weekDates.last().format(dayLabelFormatter)
            "$start – $end"
        }
    }
    val periodLabel = when (periodMode) {
        AnalysisPeriodMode.Day -> stringResource(
            R.string.analysis_period_day_label,
            selectedDate.format(dayLabelFormatter)
        )
        AnalysisPeriodMode.Week -> stringResource(
            R.string.analysis_period_week_label,
            weekRangeLabel
        )
    }

    val indicatorSteps: Int
    val indicatorCalories: Int
    val indicatorDistance: Float
    val indicatorExercise: Int
    val previousSteps: Int
    val previousExercise: Int
    if (periodMode == AnalysisPeriodMode.Day) {
        indicatorSteps = detail.steps
        indicatorCalories = detail.moveCalories
        indicatorDistance = detail.distanceKm
        indicatorExercise = detail.exerciseMinutes
        val prevDate = selectedDate.minusDays(1)
        val prevDetail = if (prevDate == today) {
            detail
        } else {
            history[prevDate.format(dateKeyFormatter)]?.toDetail()
        }
        previousSteps = prevDetail?.steps ?: 0
        previousExercise = prevDetail?.exerciseMinutes ?: 0
    } else {
        var stepsSum = 0
        var calSum = 0
        var exerciseSum = 0
        weekDates.filter { !it.isAfter(today) }.forEach { date ->
            val d = if (date == today) {
                detail
            } else {
                history[date.format(dateKeyFormatter)]?.toDetail()
            }
            if (d != null) {
                stepsSum += d.steps
                calSum += d.moveCalories
                exerciseSum += d.exerciseMinutes
            }
        }
        indicatorSteps = stepsSum
        indicatorCalories = calSum
        indicatorDistance = com.example.vigorly.data.activity.DailyActivityDetail
            .distanceKmFromSteps(stepsSum)
        indicatorExercise = exerciseSum

        val prevWeekAnchor = WeeklyActivityRingsBuilder.shiftWeek(selectedDate, -1)
        val prevWeekDates = WeeklyActivityRingsBuilder.weekDatesFor(prevWeekAnchor, locale)
        var prevStepsSum = 0
        var prevExerciseSum = 0
        prevWeekDates.filter { !it.isAfter(today) }.forEach { date ->
            val d = if (date == today) detail else history[date.format(dateKeyFormatter)]?.toDetail()
            if (d != null) {
                prevStepsSum += d.steps
                prevExerciseSum += d.exerciseMinutes
            }
        }
        previousSteps = prevStepsSum
        previousExercise = prevExerciseSum
    }

    LaunchedEffect(Unit) {
        repository.refreshActivityDayHistory()
    }

    if (showPeriodPicker) {
        AnalysisPeriodPickerDialog(
            selectedDate = selectedDate,
            mode = periodMode,
            onDismiss = { showPeriodPicker = false },
            onConfirm = { date, mode ->
                repository.selectActivityDate(date)
                periodMode = mode
                showPeriodPicker = false
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .testTag(VigorlyTestTags.ANALYSIS)
            .padding(horizontal = Dimens.ContainerMargin)
    ) {
        Text(
            text = stringResource(R.string.analysis_title),
            style = HeadlineLgMobile.copy(
                fontSize = 34.sp,
                lineHeight = 40.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp
            ),
            color = OnSurface,
            modifier = Modifier.padding(top = Dimens.Sm, bottom = Dimens.Md)
        )

        AnalysisSegmentedControl(
            selected = tab,
            onSelect = { tab = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Dimens.Md)
        )

        when (tab) {
            AnalysisTab.Progress -> ProgressTab(
                weightLog = weightLog,
                weightGoalKg = weightGoal,
                unitsMetric = unitsMetric,
                steps = indicatorSteps,
                calories = indicatorCalories,
                distanceKm = indicatorDistance,
                exerciseMinutes = indicatorExercise,
                previousSteps = previousSteps,
                previousExercise = previousExercise,
                periodLabel = periodLabel,
                periodMode = periodMode,
                canGoNext = when (periodMode) {
                    AnalysisPeriodMode.Day -> selectedDate.isBefore(today)
                    AnalysisPeriodMode.Week ->
                        !WeeklyActivityRingsBuilder.shiftWeek(selectedDate, 1).isAfter(today)
                },
                onPrev = {
                    val next = when (periodMode) {
                        AnalysisPeriodMode.Day -> selectedDate.minusDays(1)
                        AnalysisPeriodMode.Week ->
                            WeeklyActivityRingsBuilder.shiftWeek(selectedDate, -1)
                    }
                    repository.selectActivityDate(next)
                },
                onNext = {
                    val next = when (periodMode) {
                        AnalysisPeriodMode.Day -> selectedDate.plusDays(1)
                        AnalysisPeriodMode.Week ->
                            WeeklyActivityRingsBuilder.shiftWeek(selectedDate, 1)
                    }
                    if (!next.isAfter(today)) repository.selectActivityDate(next)
                },
                onOpenCalendar = { showPeriodPicker = true },
                onAddOrEditWeight = { id, kg, goal ->
                    if (id == null) repository.addWeightEntry(kg)
                    else repository.updateWeightEntry(id, kg)
                    if (goal != null) repository.setWeightGoalKg(goal)
                },
                onDeleteWeight = { repository.deleteWeightEntry(it) },
                onOpenMetric = onOpenMetric,
                locale = locale,
                modifier = Modifier.fillMaxSize()
            )
            AnalysisTab.Achievements -> AchievementsTab(
                milestones = milestones,
                locale = locale,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun AnalysisSegmentedControl(
    selected: AnalysisTab,
    onSelect: (AnalysisTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        AnalysisTab.entries.forEach { item ->
            val isSelected = item == selected
            val label = when (item) {
                AnalysisTab.Progress -> stringResource(R.string.analysis_tab_progress)
                AnalysisTab.Achievements -> stringResource(R.string.analysis_tab_achievements)
            }
            val interaction = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (isSelected) PrimaryAccent else Color.Transparent)
                    .clickable(
                        interactionSource = interaction,
                        indication = ripple(bounded = true, color = Color.White.copy(alpha = 0.12f)),
                        onClick = { onSelect(item) }
                    )
                    .padding(vertical = 11.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = BodyMd.copy(
                        fontSize = 15.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = if (isSelected) OnSurface else GlassLabel.copy(alpha = 0.75f)
                )
            }
        }
    }
}

@Composable
private fun ProgressTab(
    weightLog: List<WeightLogEntry>,
    weightGoalKg: Float?,
    unitsMetric: Boolean,
    steps: Int,
    calories: Int,
    distanceKm: Float,
    exerciseMinutes: Int,
    previousSteps: Int,
    previousExercise: Int,
    periodLabel: String,
    periodMode: AnalysisPeriodMode,
    canGoNext: Boolean,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onOpenCalendar: () -> Unit,
    onAddOrEditWeight: (id: String?, kg: Float, goal: Float?) -> Unit,
    onDeleteWeight: (String) -> Unit,
    onOpenMetric: (ActivityMetric) -> Unit,
    locale: Locale,
    modifier: Modifier = Modifier
) {
    var showWeightDialog by remember { mutableStateOf(false) }
    var editingEntry by remember { mutableStateOf<WeightLogEntry?>(null) }

    if (showWeightDialog) {
        WeightEntryDialog(
            initialKg = editingEntry?.weightKg,
            initialGoalKg = weightGoalKg,
            unitsMetric = unitsMetric,
            isEdit = editingEntry != null,
            onDismiss = {
                showWeightDialog = false
                editingEntry = null
            },
            onConfirm = { kg, goal ->
                onAddOrEditWeight(editingEntry?.id, kg, goal)
                showWeightDialog = false
                editingEntry = null
            },
            onDelete = editingEntry?.let { entry ->
                {
                    onDeleteWeight(entry.id)
                    showWeightDialog = false
                    editingEntry = null
                }
            }
        )
    }

    val currentKg = weightLog.lastOrNull()?.weightKg

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        AnalysisPeriodCard(
            periodLabel = periodLabel,
            periodMode = periodMode,
            canGoNext = canGoNext,
            onPrev = onPrev,
            onNext = onNext,
            onOpenCalendar = onOpenCalendar
        )

        Text(
            text = stringResource(R.string.analysis_role_subtitle),
            style = BodyMd.copy(fontSize = 14.sp, lineHeight = 20.sp),
            color = GlassLabel.copy(alpha = 0.88f)
        )

        PeriodInsightCard(
            steps = steps,
            previousSteps = previousSteps,
            exerciseMinutes = exerciseMinutes,
            previousExercise = previousExercise,
            periodMode = periodMode
        )

        GlassSurface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.analysis_weight_title),
                    style = BodyMd.copy(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
                    color = OnSurface
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.analysis_weight_current),
                            style = BodyMd.copy(fontSize = 13.sp),
                            color = GlassLabel.copy(alpha = 0.75f)
                        )
                        Text(
                            text = currentKg?.let { formatWeight(it, unitsMetric, locale) }
                                ?: stringResource(R.string.analysis_weight_empty),
                            style = DisplayStat.copy(fontSize = 22.sp, lineHeight = 24.sp),
                            color = OnSurface,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = stringResource(R.string.analysis_weight_goal),
                            style = BodyMd.copy(fontSize = 13.sp),
                            color = GlassLabel.copy(alpha = 0.75f)
                        )
                        Text(
                            text = weightGoalKg?.let { formatWeight(it, unitsMetric, locale) }
                                ?: "—",
                            style = DisplayStat.copy(fontSize = 22.sp, lineHeight = 24.sp),
                            color = OnSurface,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (weightLog.size >= 2) {
                    WeightTrendChart(
                        entries = weightLog,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    )
                } else {
                    Text(
                        text = stringResource(R.string.analysis_weight_chart_hint),
                        style = BodyMd.copy(fontSize = 13.sp, lineHeight = 18.sp),
                        color = GlassLabel.copy(alpha = 0.8f),
                        modifier = Modifier.padding(vertical = 18.dp)
                    )
                }

                if (weightLog.isNotEmpty()) {
                    val latest = weightLog.last()
                    val dateLabel = formatUnlockDate(latest.recordedAtMillis, locale)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                editingEntry = latest
                                showWeightDialog = true
                            }
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.analysis_weight_last_entry, dateLabel),
                            style = BodyMd.copy(fontSize = 13.sp),
                            color = GlassLabel.copy(alpha = 0.8f)
                        )
                        Text(
                            text = stringResource(R.string.analysis_weight_edit),
                            style = BodyMd.copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                            color = PrimaryAccent
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(PrimaryAccent)
                        .clickable {
                            editingEntry = null
                            showWeightDialog = true
                        }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.analysis_weight_add),
                        style = BodyMd.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                        color = OnSurface
                    )
                }
            }
        }

        Text(
            text = stringResource(R.string.analysis_indicators_title),
            style = HeadlineMd.copy(
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.3).sp
            ),
            color = OnSurface,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = stringResource(R.string.analysis_indicators_subtitle),
            style = BodyMd.copy(fontSize = 13.sp, lineHeight = 18.sp),
            color = GlassLabel.copy(alpha = 0.8f),
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IndicatorCard(
                    title = stringResource(R.string.analysis_indicator_steps),
                    value = "%,d".format(locale, steps),
                    icon = Icons.Outlined.DirectionsWalk,
                    onClick = { onOpenMetric(ActivityMetric.STEPS) },
                    modifier = Modifier.weight(1f)
                )
                IndicatorCard(
                    title = stringResource(R.string.analysis_indicator_calories),
                    value = "$calories kcal",
                    icon = Icons.Outlined.LocalFireDepartment,
                    onClick = { onOpenMetric(ActivityMetric.MOVE) },
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IndicatorCard(
                    title = stringResource(R.string.analysis_indicator_distance),
                    value = MetricFormatter.formatDistanceKm(distanceKm, unitsMetric),
                    icon = Icons.Outlined.Straighten,
                    onClick = { onOpenMetric(ActivityMetric.STEPS) },
                    modifier = Modifier.weight(1f)
                )
                IndicatorCard(
                    title = stringResource(R.string.analysis_indicator_exercise),
                    value = stringResource(
                        R.string.analysis_indicator_exercise_value,
                        exerciseMinutes
                    ),
                    icon = Icons.Outlined.Timer,
                    onClick = { onOpenMetric(ActivityMetric.EXERCISE) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Deja que las cards pasen detrás de la nav flotante
        Spacer(Modifier.height(Dimens.FloatingNavClearance + 24.dp))
    }
}

@Composable
private fun PeriodInsightCard(
    steps: Int,
    previousSteps: Int,
    exerciseMinutes: Int,
    previousExercise: Int,
    periodMode: AnalysisPeriodMode
) {
    val stepsDelta = steps - previousSteps
    val exerciseDelta = exerciseMinutes - previousExercise
    val compareLabel = if (periodMode == AnalysisPeriodMode.Day) {
        stringResource(R.string.analysis_insight_vs_day)
    } else {
        stringResource(R.string.analysis_insight_vs_week)
    }
    val insightText = when {
        previousSteps == 0 && previousExercise == 0 && steps == 0 && exerciseMinutes == 0 ->
            stringResource(R.string.analysis_insight_empty)
        stepsDelta > 0 && exerciseDelta >= 0 ->
            stringResource(R.string.analysis_insight_up_both, stepsDelta, exerciseDelta, compareLabel)
        stepsDelta < 0 && exerciseDelta <= 0 ->
            stringResource(
                R.string.analysis_insight_down_both,
                kotlin.math.abs(stepsDelta),
                kotlin.math.abs(exerciseDelta),
                compareLabel
            )
        stepsDelta != 0 ->
            stringResource(
                if (stepsDelta > 0) R.string.analysis_insight_steps_up else R.string.analysis_insight_steps_down,
                kotlin.math.abs(stepsDelta),
                compareLabel
            )
        exerciseDelta != 0 ->
            stringResource(
                if (exerciseDelta > 0) R.string.analysis_insight_exercise_up else R.string.analysis_insight_exercise_down,
                kotlin.math.abs(exerciseDelta),
                compareLabel
            )
        else -> stringResource(R.string.analysis_insight_stable, compareLabel)
    }

    GlassSurface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.analysis_insight_title),
                style = BodyMd.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp),
                color = PrimaryAccent
            )
            Text(
                text = insightText,
                style = BodyMd.copy(fontSize = 15.sp, lineHeight = 21.sp, fontWeight = FontWeight.Medium),
                color = OnSurface,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun AchievementsTab(
    milestones: List<Milestone>,
    locale: Locale,
    modifier: Modifier = Modifier
) {
    var selected by remember { mutableStateOf<Milestone?>(null) }

    selected?.let { milestone ->
        AchievementDetailDialog(
            milestone = milestone,
            locale = locale,
            onDismiss = { selected = null }
        )
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier,
        contentPadding = PaddingValues(bottom = Dimens.FloatingNavClearance + 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        items(milestones, key = { it.id }) { milestone ->
            AchievementBadge(
                milestone = milestone,
                onClick = { selected = milestone }
            )
        }
    }
}

@Composable
private fun AchievementBadge(
    milestone: Milestone,
    onClick: () -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interaction,
                indication = ripple(bounded = false),
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(84.dp)
                .clip(CircleShape)
                .background(
                    if (milestone.unlocked) {
                        Brush.radialGradient(
                            listOf(
                                PrimaryAccent.copy(alpha = 0.35f),
                                Color.White.copy(alpha = 0.08f)
                            )
                        )
                    } else {
                        Brush.radialGradient(
                            listOf(
                                Color.White.copy(alpha = 0.08f),
                                Color.White.copy(alpha = 0.04f)
                            )
                        )
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (milestone.unlocked) {
                Icon(
                    imageVector = Icons.Filled.EmojiEvents,
                    contentDescription = null,
                    tint = Color(0xFFFFC857),
                    modifier = Modifier.size(36.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = PrimaryAccent.copy(alpha = 0.75f),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = "${milestone.title} ${milestone.subtitle}".trim(),
            style = BodyMd.copy(
                fontSize = 12.sp,
                fontWeight = if (milestone.unlocked) FontWeight.SemiBold else FontWeight.Medium,
                lineHeight = 15.sp
            ),
            color = if (milestone.unlocked) OnSurface else GlassLabel.copy(alpha = 0.45f),
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun AchievementDetailDialog(
    milestone: Milestone,
    locale: Locale,
    onDismiss: () -> Unit
) {
    val hint = MilestoneHints.hint(milestone.id)
        ?: stringResource(R.string.analysis_achievement_default_hint)
    val dateText = milestone.unlockedAtMillis?.let { formatUnlockDate(it, locale) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.analysis_dialog_close), color = PrimaryAccent)
            }
        },
        title = {
            Text(
                text = "${milestone.title} ${milestone.subtitle}".trim(),
                style = HeadlineMd.copy(fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
                color = OnSurface
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (milestone.unlocked) {
                    Text(
                        text = stringResource(R.string.analysis_achievement_unlocked),
                        style = BodyMd.copy(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
                        color = PrimaryAccent
                    )
                    if (dateText != null) {
                        Text(
                            text = stringResource(R.string.analysis_achievement_date, dateText),
                            style = BodyMd.copy(fontSize = 14.sp),
                            color = GlassLabel
                        )
                    }
                } else {
                    Text(
                        text = stringResource(R.string.analysis_achievement_locked_title),
                        style = BodyMd.copy(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
                        color = OnSurface
                    )
                    Text(
                        text = hint,
                        style = BodyMd.copy(fontSize = 14.sp, lineHeight = 20.sp),
                        color = GlassLabel
                    )
                }
            }
        },
        containerColor = Color(0xFF1E1F23),
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun WeightEntryDialog(
    initialKg: Float?,
    initialGoalKg: Float?,
    unitsMetric: Boolean,
    isEdit: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (kg: Float, goalKg: Float?) -> Unit,
    onDelete: (() -> Unit)?
) {
    var weightText by remember {
        mutableStateOf(
            initialKg?.let { if (unitsMetric) "%.1f".format(it) else "%.1f".format(it * 2.20462f) }
                .orEmpty()
        )
    }
    var goalText by remember {
        mutableStateOf(
            initialGoalKg?.let { if (unitsMetric) "%.1f".format(it) else "%.1f".format(it * 2.20462f) }
                .orEmpty()
        )
    }
    val unit = if (unitsMetric) "kg" else "lb"
    val sheetInteraction = remember { MutableInteractionSource() }

    fun submit() {
        val raw = weightText.replace(',', '.').toFloatOrNull() ?: return
        val kg = if (unitsMetric) raw else raw / 2.20462f
        val goalRaw = goalText.replace(',', '.').toFloatOrNull()
        val goalKg = goalRaw?.let { if (unitsMetric) it else it / 2.20462f }
        onConfirm(kg, goalKg)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            GlassSurface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.ContainerMargin)
                    .clickable(
                        interactionSource = sheetInteraction,
                        indication = null,
                        onClick = {}
                    ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = if (isEdit) {
                            stringResource(R.string.analysis_weight_edit_title)
                        } else {
                            stringResource(R.string.analysis_weight_add)
                        },
                        style = HeadlineMd.copy(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = (-0.3).sp
                        ),
                        color = OnSurface
                    )
                    Text(
                        text = stringResource(R.string.analysis_weight_dialog_hint),
                        style = BodyMd.copy(fontSize = 14.sp, lineHeight = 20.sp),
                        color = GlassLabel.copy(alpha = 0.85f)
                    )

                    WeightGlassField(
                        label = stringResource(R.string.analysis_weight_field, unit),
                        value = weightText,
                        onValueChange = {
                            weightText = it.filter { ch -> ch.isDigit() || ch == '.' || ch == ',' }
                        }
                    )
                    WeightGlassField(
                        label = stringResource(R.string.analysis_weight_goal_field, unit),
                        value = goalText,
                        onValueChange = {
                            goalText = it.filter { ch -> ch.isDigit() || ch == '.' || ch == ',' }
                        }
                    )

                    Spacer(Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(PrimaryAccent)
                            .clickable(onClick = ::submit)
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.analysis_weight_save),
                            style = BodyMd.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                            color = OnSurface
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (onDelete != null) {
                            Text(
                                text = stringResource(R.string.analysis_weight_delete),
                                style = BodyMd.copy(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
                                color = GlassLabel.copy(alpha = 0.75f),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable(onClick = onDelete)
                                    .padding(vertical = 8.dp, horizontal = 4.dp)
                            )
                        } else {
                            Spacer(Modifier.width(1.dp))
                        }
                        Text(
                            text = stringResource(R.string.analysis_dialog_close),
                            style = BodyMd.copy(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
                            color = GlassLabel.copy(alpha = 0.85f),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable(onClick = onDismiss)
                                .padding(vertical = 8.dp, horizontal = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WeightGlassField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = BodyMd.copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
            color = GlassLabel.copy(alpha = 0.8f)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White.copy(alpha = 0.08f))
                .padding(horizontal = 14.dp, vertical = 14.dp)
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                textStyle = BodyMd.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = OnSurface
                ),
                cursorBrush = SolidColor(PrimaryAccent),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { inner ->
                    if (value.isEmpty()) {
                        Text(
                            text = "0.0",
                            style = BodyMd.copy(fontSize = 18.sp),
                            color = GlassLabel.copy(alpha = 0.35f)
                        )
                    }
                    inner()
                }
            )
        }
    }
}

@Composable
private fun AnalysisPeriodCard(
    periodLabel: String,
    periodMode: AnalysisPeriodMode,
    canGoNext: Boolean,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onOpenCalendar: () -> Unit
) {
    GlassSurface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.analysis_week_title),
                        style = BodyMd.copy(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
                        color = OnSurface
                    )
                    Text(
                        text = stringResource(R.string.analysis_week_subtitle),
                        style = BodyMd.copy(fontSize = 13.sp),
                        color = GlassLabel.copy(alpha = 0.75f),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.08f))
                        .clickable(onClick = onOpenCalendar),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarMonth,
                        contentDescription = stringResource(R.string.analysis_week_calendar),
                        tint = OnSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.08f))
                        .clickable(onClick = onPrev),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = stringResource(R.string.analysis_week_prev),
                        tint = OnSurface,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (periodMode == AnalysisPeriodMode.Day) {
                            stringResource(R.string.analysis_period_day)
                        } else {
                            stringResource(R.string.analysis_period_week)
                        },
                        style = BodyMd.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium),
                        color = GlassLabel.copy(alpha = 0.75f)
                    )
                    Text(
                        text = periodLabel.substringAfter("· ").ifBlank { periodLabel },
                        style = BodyMd.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                        color = OnSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (canGoNext) Color.White.copy(alpha = 0.08f)
                            else Color.White.copy(alpha = 0.04f)
                        )
                        .then(
                            if (canGoNext) Modifier.clickable(onClick = onNext)
                            else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = stringResource(R.string.analysis_week_next),
                        tint = if (canGoNext) OnSurface else GlassLabel.copy(alpha = 0.35f),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun IndicatorCard(
    title: String,
    value: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassSurface(
        modifier = modifier.height(92.dp),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 11.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = OnSurface,
                modifier = Modifier.size(18.dp)
            )
            Column {
                Text(
                    text = value,
                    style = DisplayStat.copy(
                        fontSize = 18.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = OnSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = title,
                    style = BodyMd.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium),
                    color = GlassLabel.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun WeightTrendChart(
    entries: List<WeightLogEntry>,
    modifier: Modifier = Modifier
) {
    val points = entries.takeLast(12)
    val min = points.minOf { it.weightKg }
    val max = points.maxOf { it.weightKg }
    val range = (max - min).coerceAtLeast(0.5f)
    val latest = points.last()

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val padX = 8.dp.toPx()
            val padY = 12.dp.toPx()
            val w = size.width - padX * 2
            val h = size.height - padY * 2
            // grid
            repeat(4) { i ->
                val y = padY + h * (i / 3f)
                drawLine(
                    color = Color.White.copy(alpha = 0.08f),
                    start = Offset(padX, y),
                    end = Offset(size.width - padX, y),
                    strokeWidth = 1.5f
                )
            }
            if (points.size < 2) return@Canvas
            val path = Path()
            points.forEachIndexed { index, entry ->
                val x = padX + w * (index / (points.size - 1).toFloat())
                val y = padY + h * (1f - ((entry.weightKg - min) / range))
                if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            drawPath(
                path = path,
                color = PrimaryAccent,
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
            )
            val lastX = padX + w
            val lastY = padY + h * (1f - ((latest.weightKg - min) / range))
            drawCircle(color = PrimaryAccent, radius = 6.dp.toPx(), center = Offset(lastX, lastY))
        }
        Text(
            text = "%.1f kg".format(latest.weightKg),
            style = BodyMd.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
            color = OnSurface,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
        )
    }
}

private fun formatWeight(kg: Float, unitsMetric: Boolean, locale: Locale): String {
    return if (unitsMetric) {
        "%.1f kg".format(locale, kg)
    } else {
        "%.1f lb".format(locale, kg * 2.20462f)
    }
}

private fun formatUnlockDate(millis: Long, locale: Locale): String {
    val formatter = DateTimeFormatter.ofPattern("d MMM yyyy", locale)
    return Instant.ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(formatter)
}
