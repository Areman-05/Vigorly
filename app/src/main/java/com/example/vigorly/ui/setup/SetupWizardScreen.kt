package com.example.vigorly.ui.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.vigorly.R
import com.example.vigorly.core.testing.VigorlyTestTags
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.ui.components.SetupActivityRingsGuide
import com.example.vigorly.ui.components.SetupRhythmVisual
import com.example.vigorly.ui.theme.BodyLg
import com.example.vigorly.ui.components.AuthGradientBackground
import com.example.vigorly.ui.components.SetupIconBadge
import com.example.vigorly.ui.components.SetupOptionCard
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.ButtonText
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.DisplayStat
import com.example.vigorly.ui.theme.HeadlineLg
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnPrimaryContainer
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer

private enum class IntroVisual { WELCOME, RHYTHM, READY }

private data class IntroStep(val titleRes: Int, val bodyRes: Int, val visual: IntroVisual)
private data class SelectOption(val key: String, val titleRes: Int, val subtitleRes: Int, val icon: (String) -> androidx.compose.ui.graphics.vector.ImageVector)

private fun toggleInSet(current: Set<String>, key: String): Set<String> =
    if (key in current) current - key else current + key

@Composable
fun SetupWizardScreen(
    repository: VigorlyRepository,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableIntStateOf(0) }
    var fitnessGoals by remember { mutableStateOf(emptySet<String>()) }
    var activityLevels by remember { mutableStateOf(emptySet<String>()) }
    var workoutLocations by remember { mutableStateOf(emptySet<String>()) }
    var preferredTimes by remember { mutableStateOf(emptySet<String>()) }
    var weeklySessions by remember { mutableStateOf<Int?>(null) }

    val introSteps = listOf(
        IntroStep(R.string.onboarding_welcome_title, R.string.onboarding_welcome_body, IntroVisual.WELCOME),
        IntroStep(R.string.onboarding_goals_title, R.string.onboarding_goals_body, IntroVisual.RHYTHM),
        IntroStep(R.string.onboarding_ready_title, R.string.onboarding_ready_body, IntroVisual.READY)
    )
    val goalOptions = listOf(
        SelectOption("strength", R.string.setup_goal_strength, R.string.setup_goal_strength_desc, SetupStepIcons::goal),
        SelectOption("cardio", R.string.setup_goal_cardio, R.string.setup_goal_cardio_desc, SetupStepIcons::goal),
        SelectOption("weight", R.string.setup_goal_weight, R.string.setup_goal_weight_desc, SetupStepIcons::goal),
        SelectOption("muscle", R.string.setup_goal_muscle, R.string.setup_goal_muscle_desc, SetupStepIcons::goal),
        SelectOption("endurance", R.string.setup_goal_endurance, R.string.setup_goal_endurance_desc, SetupStepIcons::goal),
        SelectOption("flexibility", R.string.setup_goal_flexibility, R.string.setup_goal_flexibility_desc, SetupStepIcons::goal),
        SelectOption("wellness", R.string.setup_goal_wellness, R.string.setup_goal_wellness_desc, SetupStepIcons::goal)
    )
    val activityOptions = listOf(
        SelectOption("sedentary", R.string.setup_activity_sedentary, R.string.setup_activity_sedentary_desc, SetupStepIcons::activity),
        SelectOption("light", R.string.setup_activity_light, R.string.setup_activity_light_desc, SetupStepIcons::activity),
        SelectOption("moderate", R.string.setup_activity_moderate, R.string.setup_activity_moderate_desc, SetupStepIcons::activity),
        SelectOption("active", R.string.setup_activity_active, R.string.setup_activity_active_desc, SetupStepIcons::activity),
        SelectOption("athlete", R.string.setup_activity_athlete, R.string.setup_activity_athlete_desc, SetupStepIcons::activity)
    )
    val locationOptions = listOf(
        SelectOption("gym", R.string.setup_location_gym, R.string.setup_location_gym_desc, SetupStepIcons::location),
        SelectOption("home", R.string.setup_location_home, R.string.setup_location_home_desc, SetupStepIcons::location),
        SelectOption("outdoor", R.string.setup_location_outdoor, R.string.setup_location_outdoor_desc, SetupStepIcons::location),
        SelectOption("mixed", R.string.setup_location_mixed, R.string.setup_location_mixed_desc, SetupStepIcons::location)
    )
    val timeOptions = listOf(
        SelectOption("morning", R.string.setup_time_morning, R.string.setup_time_morning_desc, SetupStepIcons::time),
        SelectOption("midday", R.string.setup_time_midday, R.string.setup_time_midday_desc, SetupStepIcons::time),
        SelectOption("afternoon", R.string.setup_time_afternoon, R.string.setup_time_afternoon_desc, SetupStepIcons::time),
        SelectOption("evening", R.string.setup_time_evening, R.string.setup_time_evening_desc, SetupStepIcons::time),
        SelectOption("flexible", R.string.setup_time_flexible, R.string.setup_time_flexible_desc, SetupStepIcons::time)
    )
    val sessionOptions = listOf(2, 3, 4, 5, 6, 7)

    val totalSteps = introSteps.size + 7
    val progress = (step + 1) / totalSteps.toFloat()
    val introCount = introSteps.size
    val isSelectionStep = step in introCount..introCount + 3
    val isWeeklyStep = step == introCount + 4
    val isRingsStep = step == introCount + 5
    val isWideContentStep = isSelectionStep || isWeeklyStep || isRingsStep
    val canContinue = when {
        step < introCount -> true
        step == introCount -> fitnessGoals.isNotEmpty()
        step == introCount + 1 -> activityLevels.isNotEmpty()
        step == introCount + 2 -> workoutLocations.isNotEmpty()
        step == introCount + 3 -> preferredTimes.isNotEmpty()
        step == introCount + 4 -> weeklySessions != null
        else -> true
    }

    AuthGradientBackground(modifier) {
        Column(
            Modifier
                .fillMaxSize()
                .testTag(VigorlyTestTags.SETUP)
                .statusBarsPadding()
                .padding(top = Dimens.Lg)
                .padding(horizontal = if (isWideContentStep) Dimens.Sm else Dimens.ContainerMargin)
                .padding(bottom = Dimens.ContainerMargin)
        ) {
            SetupProgressBar(
                progress = progress,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                stringResource(R.string.setup_progress, step + 1, totalSteps),
                style = LabelCaps,
                color = OnSurfaceVariant,
                modifier = Modifier.padding(top = Dimens.Md)
            )

            Box(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                val scrollState = rememberScrollState()
                Column(
                    Modifier
                        .fillMaxSize()
                        .then(if (isRingsStep) Modifier else Modifier.verticalScroll(scrollState))
                        .padding(
                            top = if (isWideContentStep) Dimens.Sm else Dimens.Lg,
                            bottom = Dimens.Lg
                        ),
                    verticalArrangement = if (isWideContentStep) Arrangement.Top else Arrangement.Center,
                    horizontalAlignment = if (isWideContentStep) Alignment.Start else Alignment.CenterHorizontally
                ) {
                    when {
                        step < introCount -> IntroStepContent(introSteps[step])
                        step == introCount -> MultiSelectionStep(
                            titleRes = R.string.setup_step_goal_title,
                            bodyRes = R.string.setup_step_goal_body,
                            options = goalOptions,
                            selected = fitnessGoals,
                            onToggle = { fitnessGoals = toggleInSet(fitnessGoals, it) }
                        )
                        step == introCount + 1 -> MultiSelectionStep(
                            titleRes = R.string.setup_step_activity_title,
                            bodyRes = R.string.setup_step_activity_body,
                            options = activityOptions,
                            selected = activityLevels,
                            onToggle = { activityLevels = toggleInSet(activityLevels, it) }
                        )
                        step == introCount + 2 -> MultiSelectionStep(
                            titleRes = R.string.setup_step_location_title,
                            bodyRes = R.string.setup_step_location_body,
                            options = locationOptions,
                            selected = workoutLocations,
                            onToggle = { workoutLocations = toggleInSet(workoutLocations, it) }
                        )
                        step == introCount + 3 -> MultiSelectionStep(
                            titleRes = R.string.setup_step_time_title,
                            bodyRes = R.string.setup_step_time_body,
                            options = timeOptions,
                            selected = preferredTimes,
                            onToggle = { preferredTimes = toggleInSet(preferredTimes, it) }
                        )
                        step == introCount + 4 -> WeeklyStep(weeklySessions, sessionOptions) { weeklySessions = it }
                        step == introCount + 5 -> RingsStep()
                        else -> CompleteStepContent()
                    }
                }
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                horizontalArrangement = Arrangement.spacedBy(Dimens.Md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (step > 0) {
                    OutlinedButton(
                        onClick = { step-- },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = OnSurface)
                    ) {
                        Text(stringResource(R.string.setup_back), style = ButtonText)
                    }
                } else {
                    Spacer(Modifier.weight(1f))
                }
                Button(
                    onClick = {
                        if (step < totalSteps - 1) {
                            step++
                        } else {
                            val sessions = weeklySessions ?: return@Button
                            repository.saveSetupPreferences(
                                fitnessGoals.joinToString(","),
                                activityLevels.joinToString(","),
                                sessions,
                                notifications = false,
                                workoutLocations.joinToString(","),
                                preferredTimes.joinToString(",")
                            )
                            onComplete()
                        }
                    },
                    enabled = canContinue,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryContainer,
                        contentColor = OnPrimaryContainer
                    )
                ) {
                    Text(
                        if (step < totalSteps - 1) stringResource(R.string.setup_continue)
                        else stringResource(R.string.setup_finish),
                        style = ButtonText
                    )
                }
            }
        }
    }
}

@Composable
private fun SetupProgressBar(progress: Float, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(5.dp)
            .clip(RoundedCornerShape(2.5.dp))
            .background(OnSurfaceVariant.copy(alpha = 0.2f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .fillMaxHeight()
                .background(PrimaryAccent)
        )
    }
}

@Composable
private fun IntroStepContent(step: IntroStep) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        when (step.visual) {
            IntroVisual.WELCOME -> SetupIconBadge(
                icon = SetupStepIcons.introWelcome,
                selected = true,
                modifier = Modifier.padding(bottom = Dimens.Md),
                size = 72.dp
            )
            IntroVisual.RHYTHM -> SetupRhythmVisual(
                modifier = Modifier.padding(bottom = Dimens.Md),
                size = 220.dp
            )
            IntroVisual.READY -> SetupIconBadge(
                icon = SetupStepIcons.introReady,
                selected = true,
                modifier = Modifier.padding(bottom = Dimens.Md),
                size = 72.dp
            )
        }
        Text(
            stringResource(step.titleRes),
            style = HeadlineLg.copy(fontWeight = FontWeight.Bold),
            color = OnSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = Dimens.Md)
        )
        Text(
            stringResource(step.bodyRes),
            style = BodyLg,
            color = OnSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = Dimens.Md, start = Dimens.Sm, end = Dimens.Sm)
        )
    }
}

@Composable
private fun MultiSelectionStep(
    titleRes: Int,
    bodyRes: Int,
    options: List<SelectOption>,
    selected: Set<String>,
    onToggle: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.Md)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.Sm)) {
            Text(
                stringResource(titleRes),
                style = HeadlineLg.copy(fontWeight = FontWeight.Bold),
                color = OnSurface
            )
            Text(stringResource(bodyRes), style = BodyLg, color = OnSurfaceVariant)
            Text(
                stringResource(R.string.setup_multi_select_hint),
                style = LabelCaps,
                color = PrimaryAccent.copy(0.8f),
                modifier = Modifier.padding(top = Dimens.Xs)
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Dimens.Md)
        ) {
            options.forEach { option ->
                SetupOptionCard(
                    title = stringResource(option.titleRes),
                    subtitle = stringResource(option.subtitleRes),
                    icon = option.icon(option.key),
                    selected = option.key in selected,
                    onClick = { onToggle(option.key) },
                    modifier = Modifier.fillMaxWidth(),
                    badgeSize = 56.dp,
                    iconSize = 30.dp,
                    contentPadding = Dimens.Lg,
                    minHeight = 80.dp
                )
            }
        }
    }
}

@Composable
private fun WeeklyStep(sessions: Int?, options: List<Int>, onChange: (Int) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.Md)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.Sm)) {
            Text(
                stringResource(R.string.setup_step_weekly_title),
                style = HeadlineLg.copy(fontWeight = FontWeight.Bold),
                color = OnSurface
            )
            Text(
                stringResource(R.string.setup_step_weekly_body),
                style = BodyLg,
                color = OnSurfaceVariant
            )
            Text(
                stringResource(R.string.setup_weekly_select_hint),
                style = LabelCaps,
                color = PrimaryAccent.copy(0.8f),
                modifier = Modifier.padding(top = Dimens.Xs)
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Dimens.Md)
        ) {
            options.forEach { count ->
                SetupOptionCard(
                    title = stringResource(R.string.setup_weekly_option_title, count),
                    subtitle = weeklySessionDescription(count),
                    icon = SetupStepIcons.weeklySessions(count),
                    selected = sessions == count,
                    onClick = { onChange(count) },
                    modifier = Modifier.fillMaxWidth(),
                    badgeSize = 56.dp,
                    iconSize = 30.dp,
                    contentPadding = Dimens.Lg,
                    minHeight = 80.dp
                )
            }
        }
    }
}

@Composable
private fun weeklySessionDescription(count: Int): String {
    val descRes = when (count) {
        2 -> R.string.setup_weekly_2_desc
        3 -> R.string.setup_weekly_3_desc
        4 -> R.string.setup_weekly_4_desc
        5 -> R.string.setup_weekly_5_desc
        6 -> R.string.setup_weekly_6_desc
        else -> R.string.setup_weekly_7_desc
    }
    return stringResource(descRes)
}

@Composable
private fun RingsStep() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(Dimens.Lg)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.Sm)) {
            Text(
                stringResource(R.string.setup_step_rings_title),
                style = HeadlineLg.copy(fontWeight = FontWeight.Bold),
                color = OnSurface
            )
            Text(
                stringResource(R.string.setup_step_rings_body),
                style = BodyLg,
                color = OnSurfaceVariant
            )
        }
        SetupActivityRingsGuide(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )
        Text(
            stringResource(R.string.setup_rings_how_it_works),
            style = BodyLg,
            color = OnSurfaceVariant,
            modifier = Modifier.padding(bottom = Dimens.Sm)
        )
    }
}

@Composable
private fun CompleteStepContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        SetupIconBadge(
            icon = SetupStepIcons.introComplete,
            selected = true,
            modifier = Modifier.padding(bottom = Dimens.Md)
        )
        Text(
            stringResource(R.string.setup_step_ready_title),
            style = DisplayStat.copy(fontWeight = FontWeight.Bold),
            color = PrimaryAccent,
            textAlign = TextAlign.Center
        )
        Text(
            stringResource(R.string.setup_step_ready_body),
            style = BodyMd,
            color = OnSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = Dimens.Md, start = Dimens.Md, end = Dimens.Md)
        )
    }
}
