package com.example.vigorly.ui.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.core.testing.VigorlyTestTags
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.ui.auth.AuthFieldShape
import com.example.vigorly.ui.components.AuthGradientBackground
import com.example.vigorly.ui.components.GlassSurface
import com.example.vigorly.ui.components.SetupActivityRingsGuide
import com.example.vigorly.ui.components.SetupOptionCard
import com.example.vigorly.ui.components.VigorlyBrandMark
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.ButtonText
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.DisplayStat
import com.example.vigorly.ui.theme.GlassLabel
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.VigorlyDisplayFamily

private enum class IntroVisual { WELCOME, RHYTHM, READY }

private data class IntroStep(val titleRes: Int, val bodyRes: Int, val visual: IntroVisual)
private data class IntroFeature(val titleRes: Int, val bodyRes: Int)
private data class SelectOption(
    val key: String,
    val titleRes: Int,
    val subtitleRes: Int,
    val icon: (String) -> ImageVector
)

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
        SelectOption("strength", R.string.workout_type_strength, R.string.profile_pref_type_strength_desc, SetupStepIcons::category),
        SelectOption("hiit", R.string.workout_type_hiit, R.string.profile_pref_type_hiit_desc, SetupStepIcons::category),
        SelectOption("cardio", R.string.workout_type_cardio, R.string.profile_pref_type_cardio_desc, SetupStepIcons::category),
        SelectOption("recovery", R.string.workout_type_yoga, R.string.profile_pref_type_recovery_desc, SetupStepIcons::category),
        SelectOption("pilates", R.string.workout_type_pilates, R.string.profile_pref_type_pilates_desc, SetupStepIcons::category),
        SelectOption("mobility", R.string.workout_type_mobility, R.string.profile_pref_type_mobility_desc, SetupStepIcons::category),
        SelectOption("swim", R.string.workout_type_swim, R.string.profile_pref_type_swim_desc, SetupStepIcons::category)
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
    val isIntroStep = step < introCount
    val isSelectionStep = step in introCount..introCount + 3
    val isWeeklyStep = step == introCount + 4
    val isRingsStep = step == introCount + 5
    val isCompleteStep = step == totalSteps - 1
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
                .navigationBarsPadding()
                .padding(top = Dimens.Md)
                .padding(horizontal = Dimens.ContainerMargin)
                .padding(bottom = 44.dp)
        ) {
            SetupProgressBar(
                progress = progress,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                stringResource(R.string.setup_progress, step + 1, totalSteps),
                style = LabelCaps.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp),
                color = GlassLabel.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = Dimens.Sm)
            )

            Box(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                val scrollState = rememberScrollState()
                val usesFillLayout = isSelectionStep || isWeeklyStep || isRingsStep
                Column(
                    Modifier
                        .fillMaxSize()
                        .then(if (usesFillLayout) Modifier else Modifier.verticalScroll(scrollState))
                        .padding(
                            top = if (usesFillLayout) Dimens.Sm else 12.dp,
                            bottom = Dimens.Md
                        ),
                    verticalArrangement = if (usesFillLayout) Arrangement.Top else Arrangement.Center,
                    horizontalAlignment = if (usesFillLayout) Alignment.Start else Alignment.CenterHorizontally
                ) {
                    when {
                        isIntroStep -> IntroStepContent(introSteps[step])
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
                            onToggle = { workoutLocations = toggleInSet(workoutLocations, it) },
                            fillRemaining = false
                        )
                        step == introCount + 3 -> MultiSelectionStep(
                            titleRes = R.string.setup_step_time_title,
                            bodyRes = R.string.setup_step_time_body,
                            options = timeOptions,
                            selected = preferredTimes,
                            onToggle = { preferredTimes = toggleInSet(preferredTimes, it) }
                        )
                        isWeeklyStep -> WeeklyStep(weeklySessions, sessionOptions) {
                            weeklySessions = it
                        }
                        isRingsStep -> RingsStep()
                        isCompleteStep -> CompleteStepContent()
                    }
                }
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (step > 0) {
                    OutlinedButton(
                        onClick = { step-- },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        shape = AuthFieldShape,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = OnSurface)
                    ) {
                        Text(stringResource(R.string.setup_back), style = ButtonText)
                    }
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
                        .then(if (step == 0) Modifier.fillMaxWidth() else Modifier.weight(1.15f))
                        .fillMaxHeight(),
                    shape = AuthFieldShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryAccent,
                        contentColor = OnSurface
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
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(OnSurface.copy(alpha = 0.12f))
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
    val features = when (step.visual) {
        IntroVisual.WELCOME -> listOf(
            IntroFeature(R.string.onboarding_feature_home_title, R.string.onboarding_feature_home_body),
            IntroFeature(R.string.onboarding_feature_workouts_title, R.string.onboarding_feature_workouts_body),
            IntroFeature(R.string.onboarding_feature_session_title, R.string.onboarding_feature_session_body)
        )
        IntroVisual.RHYTHM -> listOf(
            IntroFeature(R.string.onboarding_feature_home_title, R.string.onboarding_feature_home_body),
            IntroFeature(R.string.onboarding_feature_workouts_title, R.string.onboarding_feature_workouts_body),
            IntroFeature(R.string.onboarding_feature_analysis_title, R.string.onboarding_feature_analysis_body),
            IntroFeature(R.string.onboarding_feature_history_title, R.string.onboarding_feature_history_body),
            IntroFeature(R.string.onboarding_feature_profile_title, R.string.onboarding_feature_profile_body)
        )
        IntroVisual.READY -> listOf(
            IntroFeature(R.string.onboarding_ready_next_title, R.string.onboarding_ready_next_body),
            IntroFeature(R.string.onboarding_feature_home_title, R.string.onboarding_ready_home_body),
            IntroFeature(R.string.onboarding_feature_workouts_title, R.string.onboarding_ready_workouts_body)
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (step.visual == IntroVisual.WELCOME) {
            VigorlyBrandMark(compact = true)
        }
        Text(
            stringResource(step.titleRes),
            style = DisplayStat.copy(
                fontFamily = VigorlyDisplayFamily,
                fontSize = 40.sp,
                lineHeight = 42.sp,
                letterSpacing = 0.6.sp
            ),
            color = OnSurface,
            textAlign = TextAlign.Center
        )
        Text(
            stringResource(step.bodyRes),
            style = BodyMd.copy(
                fontSize = 17.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = GlassLabel.copy(alpha = 0.9f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = Dimens.Xs)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            features.forEach { feature ->
                IntroFeatureRow(feature)
            }
        }
    }
}

@Composable
private fun IntroFeatureRow(feature: IntroFeature) {
    GlassSurface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 18.dp)
        ) {
            Text(
                stringResource(feature.titleRes),
                style = BodyMd.copy(
                    fontSize = 18.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = PrimaryAccent
            )
            Text(
                stringResource(feature.bodyRes),
                style = BodyMd.copy(
                    fontSize = 15.sp,
                    lineHeight = 21.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = GlassLabel.copy(alpha = 0.86f),
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

@Composable
private fun SetupScreenTitle(titleRes: Int, bodyRes: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            stringResource(titleRes),
            style = DisplayStat.copy(
                fontFamily = VigorlyDisplayFamily,
                fontSize = 34.sp,
                lineHeight = 36.sp,
                letterSpacing = 0.5.sp
            ),
            color = OnSurface
        )
        Text(
            stringResource(bodyRes),
            style = BodyMd.copy(
                fontSize = 16.sp,
                lineHeight = 23.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = GlassLabel.copy(alpha = 0.88f)
        )
    }
}

@Composable
private fun MultiSelectionStep(
    titleRes: Int,
    bodyRes: Int,
    options: List<SelectOption>,
    selected: Set<String>,
    onToggle: (String) -> Unit,
    fillRemaining: Boolean = true
) {
    Column(
        modifier = if (fillRemaining) Modifier.fillMaxSize() else Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        SetupScreenTitle(titleRes, bodyRes)
        Text(
            stringResource(R.string.setup_multi_select_hint),
            style = BodyMd.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold),
            color = PrimaryAccent
        )
        SelectionGrid(
            options = options,
            selected = selected,
            onToggle = onToggle,
            fillRemaining = fillRemaining,
            modifier = if (fillRemaining) {
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
            } else {
                Modifier.fillMaxWidth()
            }
        )
    }
}

@Composable
private fun SelectionGrid(
    options: List<SelectOption>,
    selected: Set<String>,
    onToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
    fillRemaining: Boolean = true
) {
    val rows = remember(options) {
        val chunks = mutableListOf<List<SelectOption>>()
        var index = 0
        while (index < options.size) {
            val remaining = options.size - index
            val take = if (remaining == 1) 1 else 2
            chunks += options.subList(index, index + take)
            index += take
        }
        chunks
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier
                    .then(
                        if (fillRemaining) Modifier.weight(1f) else Modifier.height(118.dp)
                    )
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                row.forEach { option ->
                    SetupOptionCard(
                        title = stringResource(option.titleRes),
                        subtitle = stringResource(option.subtitleRes),
                        icon = option.icon(option.key),
                        selected = option.key in selected,
                        onClick = { onToggle(option.key) },
                        stacked = fillRemaining && row.size > 1,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                }
            }
        }
    }
}

@Composable
private fun WeeklyStep(sessions: Int?, options: List<Int>, onChange: (Int) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        SetupScreenTitle(R.string.setup_step_weekly_title, R.string.setup_step_weekly_body)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            options.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    row.forEach { count ->
                        WeeklyChoiceCard(
                            count = count,
                            description = weeklySessionDescription(count),
                            selected = sessions == count,
                            onClick = { onChange(count) },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WeeklyChoiceCard(
    count: Int,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(22.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .border(
                width = if (selected) 1.5.dp else 0.dp,
                color = if (selected) PrimaryAccent else Color.Transparent,
                shape = shape
            )
    ) {
        GlassSurface(
            modifier = Modifier.fillMaxSize(),
            shape = shape,
            onClick = onClick
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    count.toString(),
                    style = DisplayStat.copy(fontSize = 40.sp, lineHeight = 40.sp),
                    color = if (selected) PrimaryAccent else Primary
                )
                Column {
                    Text(
                        stringResource(R.string.setup_weekly_option_title, count),
                        style = BodyMd.copy(
                            fontSize = 15.sp,
                            lineHeight = 18.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (selected) PrimaryAccent else Primary,
                        maxLines = 2
                    )
                    Text(
                        description,
                        style = BodyMd.copy(
                            fontSize = 13.sp,
                            lineHeight = 17.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = GlassLabel.copy(alpha = 0.78f),
                        modifier = Modifier.padding(top = 4.dp),
                        maxLines = 2
                    )
                }
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
            SetupScreenTitle(R.string.setup_step_rings_title, R.string.setup_step_rings_body)
        }
        SetupActivityRingsGuide(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )
        Text(
            stringResource(R.string.setup_rings_how_it_works),
            style = BodyMd.copy(
                fontSize = 15.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = GlassLabel.copy(alpha = 0.75f),
            modifier = Modifier.padding(bottom = Dimens.Sm)
        )
    }
}

@Composable
private fun CompleteStepContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        VigorlyBrandMark(compact = true)
        Text(
            stringResource(R.string.setup_step_ready_title),
            style = DisplayStat.copy(
                fontFamily = VigorlyDisplayFamily,
                fontSize = 42.sp,
                lineHeight = 44.sp,
                letterSpacing = 0.6.sp
            ),
            color = OnSurface,
            textAlign = TextAlign.Center
        )
        Text(
            stringResource(R.string.setup_step_ready_body),
            style = BodyMd.copy(
                fontSize = 17.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = GlassLabel.copy(alpha = 0.9f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = Dimens.Sm)
        )
        IntroFeatureRow(
            IntroFeature(
                R.string.onboarding_feature_home_title,
                R.string.onboarding_complete_home_body
            )
        )
        IntroFeatureRow(
            IntroFeature(
                R.string.onboarding_feature_workouts_title,
                R.string.onboarding_complete_workouts_body
            )
        )
    }
}
