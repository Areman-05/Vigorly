package com.example.vigorly.ui.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.unit.dp
import com.example.vigorly.R
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.ui.components.ActivityRings
import com.example.vigorly.ui.components.GlassCard
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
import com.example.vigorly.ui.theme.PrimaryContainer

private data class SetupStep(val titleRes: Int, val bodyRes: Int)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SetupWizardScreen(
    repository: VigorlyRepository,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableIntStateOf(0) }
    var fitnessGoal by remember { mutableStateOf("wellness") }
    var activityLevel by remember { mutableStateOf("moderate") }
    var weeklySessions by remember { mutableIntStateOf(4) }
    var notifications by remember { mutableStateOf(true) }

    val introSteps = listOf(
        SetupStep(R.string.onboarding_welcome_title, R.string.onboarding_welcome_body),
        SetupStep(R.string.onboarding_goals_title, R.string.onboarding_goals_body),
        SetupStep(R.string.onboarding_ready_title, R.string.onboarding_ready_body)
    )
    val totalSteps = introSteps.size + 5
    val progress = (step + 1) / totalSteps.toFloat()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Dimens.ContainerMargin)
    ) {
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(4.dp),
            color = Primary,
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
            step < introSteps.size -> {
                val current = introSteps[step]
                GlassCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(Dimens.Lg)) {
                        Text(stringResource(current.titleRes), style = HeadlineMd, color = OnSurface)
                        Text(
                            stringResource(current.bodyRes),
                            style = BodyMd,
                            color = OnSurfaceVariant,
                            modifier = Modifier.padding(top = Dimens.Md)
                        )
                    }
                }
            }
            step == introSteps.size -> GoalStep(fitnessGoal) { fitnessGoal = it }
            step == introSteps.size + 1 -> ActivityStep(activityLevel) { activityLevel = it }
            step == introSteps.size + 2 -> WeeklyStep(weeklySessions) { weeklySessions = it }
            step == introSteps.size + 3 -> {
                GlassCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(Dimens.Lg), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(stringResource(R.string.setup_step_rings_title), style = HeadlineMd, color = OnSurface)
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
                        Text(stringResource(R.string.setup_step_ready_title), style = DisplayStat, color = Primary)
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
                    repository.saveSetupPreferences(fitnessGoal, activityLevel, weeklySessions, notifications)
                    onComplete()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GoalStep(selected: String, onSelect: (String) -> Unit) {
    GlassCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(Dimens.Lg)) {
            Text(stringResource(R.string.setup_step_goal_title), style = HeadlineMd, color = OnSurface)
            Text(stringResource(R.string.setup_step_goal_body), style = BodyMd, color = OnSurfaceVariant, modifier = Modifier.padding(top = Dimens.Sm))
            FlowRow(Modifier.padding(top = Dimens.Md), horizontalArrangement = Arrangement.spacedBy(Dimens.Sm)) {
                goalOption("strength", R.string.setup_goal_strength, selected, onSelect)
                goalOption("cardio", R.string.setup_goal_cardio, selected, onSelect)
                goalOption("weight", R.string.setup_goal_weight, selected, onSelect)
                goalOption("wellness", R.string.setup_goal_wellness, selected, onSelect)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ActivityStep(selected: String, onSelect: (String) -> Unit) {
    GlassCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(Dimens.Lg)) {
            Text(stringResource(R.string.setup_step_activity_title), style = HeadlineMd, color = OnSurface)
            Text(stringResource(R.string.setup_step_activity_body), style = BodyMd, color = OnSurfaceVariant, modifier = Modifier.padding(top = Dimens.Sm))
            FlowRow(Modifier.padding(top = Dimens.Md), horizontalArrangement = Arrangement.spacedBy(Dimens.Sm)) {
                goalOption("sedentary", R.string.setup_activity_sedentary, selected, onSelect)
                goalOption("moderate", R.string.setup_activity_moderate, selected, onSelect)
                goalOption("active", R.string.setup_activity_active, selected, onSelect)
                goalOption("athlete", R.string.setup_activity_athlete, selected, onSelect)
            }
        }
    }
}

@Composable
private fun WeeklyStep(sessions: Int, onChange: (Int) -> Unit) {
    GlassCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(Dimens.Lg)) {
            Text(stringResource(R.string.setup_step_weekly_title), style = HeadlineMd, color = OnSurface)
            Text(stringResource(R.string.setup_step_weekly_body), style = BodyMd, color = OnSurfaceVariant, modifier = Modifier.padding(top = Dimens.Sm))
            Row(
                Modifier.fillMaxWidth().padding(top = Dimens.Lg),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { onChange((sessions - 1).coerceAtLeast(1)) }) { Text("-") }
                Text("$sessions", style = DisplayStat, color = Primary)
                Button(onClick = { onChange((sessions + 1).coerceAtMost(14)) }) { Text("+") }
            }
        }
    }
}

@Composable
private fun goalOption(
    key: String,
    labelRes: Int,
    selected: String,
    onSelect: (String) -> Unit
) {
    FilterChip(
        selected = selected == key,
        onClick = { onSelect(key) },
        label = { Text(stringResource(labelRes)) },
        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Primary.copy(alpha = 0.25f))
    )
}
