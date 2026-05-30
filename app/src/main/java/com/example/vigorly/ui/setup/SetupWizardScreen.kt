package com.example.vigorly.ui.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.vigorly.R
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.ui.components.ActivityRings
import com.example.vigorly.ui.components.AuthGradientBackground
import com.example.vigorly.ui.components.FitnessPose
import com.example.vigorly.ui.components.FitnessSilhouette
import com.example.vigorly.ui.components.GlassCard
import com.example.vigorly.ui.components.SetupOptionCard
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.ButtonText
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.DisplayStat
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnPrimaryContainer
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer

private data class IntroStep(val titleRes: Int, val bodyRes: Int, val pose: FitnessPose)
private data class SelectOption(val key: String, val titleRes: Int, val subtitleRes: Int, val pose: FitnessPose)

@Composable
fun SetupWizardScreen(
    repository: VigorlyRepository,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableIntStateOf(0) }
    var fitnessGoal by remember { mutableStateOf("wellness") }
    var activityLevel by remember { mutableStateOf("moderate") }
    var workoutLocation by remember { mutableStateOf("home") }
    var preferredTime by remember { mutableStateOf("flexible") }
    var weeklySessions by remember { mutableIntStateOf(4) }
    var notifications by remember { mutableStateOf(true) }

    val introSteps = listOf(
        IntroStep(R.string.onboarding_welcome_title, R.string.onboarding_welcome_body, FitnessPose.WELCOME),
        IntroStep(R.string.onboarding_goals_title, R.string.onboarding_goals_body, FitnessPose.RINGS),
        IntroStep(R.string.onboarding_ready_title, R.string.onboarding_ready_body, FitnessPose.RUNNER)
    )
    val goalOptions = listOf(
        SelectOption("strength", R.string.setup_goal_strength, R.string.setup_goal_strength_desc, FitnessPose.LIFT),
        SelectOption("cardio", R.string.setup_goal_cardio, R.string.setup_goal_cardio_desc, FitnessPose.RUNNER),
        SelectOption("weight", R.string.setup_goal_weight, R.string.setup_goal_weight_desc, FitnessPose.SQUAT),
        SelectOption("muscle", R.string.setup_goal_muscle, R.string.setup_goal_muscle_desc, FitnessPose.LIFT),
        SelectOption("endurance", R.string.setup_goal_endurance, R.string.setup_goal_endurance_desc, FitnessPose.CYCLING),
        SelectOption("flexibility", R.string.setup_goal_flexibility, R.string.setup_goal_flexibility_desc, FitnessPose.STRETCH),
        SelectOption("wellness", R.string.setup_goal_wellness, R.string.setup_goal_wellness_desc, FitnessPose.HERO)
    )
    val activityOptions = listOf(
        SelectOption("sedentary", R.string.setup_activity_sedentary, R.string.setup_activity_sedentary_desc, FitnessPose.STRETCH),
        SelectOption("light", R.string.setup_activity_light, R.string.setup_activity_light_desc, FitnessPose.WELCOME),
        SelectOption("moderate", R.string.setup_activity_moderate, R.string.setup_activity_moderate_desc, FitnessPose.RUNNER),
        SelectOption("active", R.string.setup_activity_active, R.string.setup_activity_active_desc, FitnessPose.SQUAT),
        SelectOption("athlete", R.string.setup_activity_athlete, R.string.setup_activity_athlete_desc, FitnessPose.HERO)
    )
    val locationOptions = listOf(
        SelectOption("gym", R.string.setup_location_gym, R.string.setup_location_gym_desc, FitnessPose.LIFT),
        SelectOption("home", R.string.setup_location_home, R.string.setup_location_home_desc, FitnessPose.HOME_GYM),
        SelectOption("outdoor", R.string.setup_location_outdoor, R.string.setup_location_outdoor_desc, FitnessPose.OUTDOOR),
        SelectOption("mixed", R.string.setup_location_mixed, R.string.setup_location_mixed_desc, FitnessPose.CYCLING)
    )
    val timeOptions = listOf(
        SelectOption("morning", R.string.setup_time_morning, R.string.setup_time_morning_desc, FitnessPose.STRETCH),
        SelectOption("midday", R.string.setup_time_midday, R.string.setup_time_midday_desc, FitnessPose.RUNNER),
        SelectOption("afternoon", R.string.setup_time_afternoon, R.string.setup_time_afternoon_desc, FitnessPose.SQUAT),
        SelectOption("evening", R.string.setup_time_evening, R.string.setup_time_evening_desc, FitnessPose.LIFT),
        SelectOption("flexible", R.string.setup_time_flexible, R.string.setup_time_flexible_desc, FitnessPose.HERO)
    )
    val sessionOptions = listOf(2, 3, 4, 5, 6, 7)

    val totalSteps = introSteps.size + 7
    val progress = (step + 1) / totalSteps.toFloat()
    val introCount = introSteps.size

    AuthGradientBackground(modifier) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(Dimens.ContainerMargin)
        ) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(5.dp),
                color = PrimaryAccent,
                trackColor = OnSurfaceVariant.copy(alpha = 0.2f)
            )
            Text(
                stringResource(R.string.setup_progress, step + 1, totalSteps),
                style = LabelCaps,
                color = OnSurfaceVariant,
                modifier = Modifier.padding(top = Dimens.Md)
            )
            Spacer(Modifier.height(Dimens.Lg))

            when {
                step < introCount -> {
                    val current = introSteps[step]
                    GlassCard(Modifier.fillMaxWidth()) {
                        Column(
                            Modifier.padding(Dimens.Lg),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            FitnessSilhouette(pose = current.pose, size = 130.dp)
                            Text(
                                stringResource(current.titleRes),
                                style = HeadlineMd.copy(fontWeight = FontWeight.Bold),
                                color = OnSurface,
                                modifier = Modifier.padding(top = Dimens.Md)
                            )
                            Text(
                                stringResource(current.bodyRes),
                                style = BodyMd,
                                color = OnSurfaceVariant,
                                modifier = Modifier.padding(top = Dimens.Sm)
                            )
                        }
                    }
                }
                step == introCount -> SelectionStep(
                    titleRes = R.string.setup_step_goal_title,
                    bodyRes = R.string.setup_step_goal_body,
                    options = goalOptions,
                    selected = fitnessGoal,
                    onSelect = { fitnessGoal = it }
                )
                step == introCount + 1 -> SelectionStep(
                    titleRes = R.string.setup_step_activity_title,
                    bodyRes = R.string.setup_step_activity_body,
                    options = activityOptions,
                    selected = activityLevel,
                    onSelect = { activityLevel = it }
                )
                step == introCount + 2 -> SelectionStep(
                    titleRes = R.string.setup_step_location_title,
                    bodyRes = R.string.setup_step_location_body,
                    options = locationOptions,
                    selected = workoutLocation,
                    onSelect = { workoutLocation = it }
                )
                step == introCount + 3 -> SelectionStep(
                    titleRes = R.string.setup_step_time_title,
                    bodyRes = R.string.setup_step_time_body,
                    options = timeOptions,
                    selected = preferredTime,
                    onSelect = { preferredTime = it }
                )
                step == introCount + 4 -> WeeklyStep(weeklySessions, sessionOptions) { weeklySessions = it }
                step == introCount + 5 -> {
                    GlassCard(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(Dimens.Lg), horizontalAlignment = Alignment.CenterHorizontally) {
                            FitnessSilhouette(pose = FitnessPose.RINGS, size = 100.dp)
                            Text(
                                stringResource(R.string.setup_step_rings_title),
                                style = HeadlineMd,
                                color = OnSurface,
                                modifier = Modifier.padding(top = Dimens.Sm)
                            )
                            Text(
                                stringResource(R.string.setup_step_rings_body),
                                style = BodyMd,
                                color = OnSurfaceVariant,
                                modifier = Modifier.padding(top = Dimens.Sm, bottom = Dimens.Md)
                            )
                            ActivityRings(0.7f, 0.5f, 0.8f, 67)
                            Row(
                                Modifier.fillMaxWidth().padding(top = Dimens.Lg),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(stringResource(R.string.settings_reminders), style = BodyMd, color = OnSurface)
                                Switch(checked = notifications, onCheckedChange = { notifications = it })
                            }
                        }
                    }
                }
                else -> {
                    GlassCard(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(Dimens.Lg), horizontalAlignment = Alignment.CenterHorizontally) {
                            FitnessSilhouette(pose = FitnessPose.HERO, size = 120.dp)
                            Text(
                                stringResource(R.string.setup_step_ready_title),
                                style = DisplayStat.copy(fontWeight = FontWeight.Bold),
                                color = PrimaryAccent
                            )
                            Text(
                                stringResource(R.string.setup_step_ready_body),
                                style = BodyMd,
                                color = OnSurfaceVariant,
                                modifier = Modifier.padding(top = Dimens.Md)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(Dimens.Xl))
            Button(
                onClick = {
                    if (step < totalSteps - 1) {
                        step++
                    } else {
                        repository.saveSetupPreferences(
                            fitnessGoal,
                            activityLevel,
                            weeklySessions,
                            notifications,
                            workoutLocation,
                            preferredTime
                        )
                        onComplete()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
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

@Composable
private fun SelectionStep(
    titleRes: Int,
    bodyRes: Int,
    options: List<SelectOption>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.Sm)) {
        Text(stringResource(titleRes), style = HeadlineMd, color = OnSurface)
        Text(stringResource(bodyRes), style = BodyMd, color = OnSurfaceVariant)
        options.forEach { option ->
            SetupOptionCard(
                title = stringResource(option.titleRes),
                subtitle = stringResource(option.subtitleRes),
                pose = option.pose,
                selected = selected == option.key,
                onClick = { onSelect(option.key) }
            )
        }
    }
}

@Composable
private fun WeeklyStep(sessions: Int, options: List<Int>, onChange: (Int) -> Unit) {
    Column {
        Text(stringResource(R.string.setup_step_weekly_title), style = HeadlineMd, color = OnSurface)
        Text(
            stringResource(R.string.setup_step_weekly_body),
            style = BodyMd,
            color = OnSurfaceVariant,
            modifier = Modifier.padding(top = Dimens.Sm, bottom = Dimens.Md)
        )
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.Sm)) {
            options.chunked(3).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Dimens.Sm)) {
                    row.forEach { count ->
                        val selected = sessions == count
                        GlassCard(
                            modifier = Modifier.weight(1f).height(72.dp),
                            onClick = { onChange(count) }
                        ) {
                            Column(
                                Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    "$count",
                                    style = DisplayStat,
                                    color = if (selected) Primary else OnSurface
                                )
                                Text(
                                    stringResource(R.string.setup_sessions_per_week),
                                    style = LabelCaps,
                                    color = OnSurfaceVariant
                                )
                            }
                        }
                    }
                    repeat(3 - row.size) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
