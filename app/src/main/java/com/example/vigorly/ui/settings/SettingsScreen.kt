package com.example.vigorly.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.vigorly.R
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.ui.components.GlassCard
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.ButtonText
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryContainer

@Composable
fun SettingsScreen(
    repository: VigorlyRepository,
    onOpenInsights: () -> Unit = {},
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val profile by repository.profile.collectAsState()
    val notifications by repository.notificationsEnabled.collectAsState()
    val unitsMetric by repository.unitsMetric.collectAsState()
    val weeklyGoal by repository.weeklyGoal.collectAsState()
    var nameInput by remember(profile.displayName) { mutableStateOf(profile.displayName) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Dimens.ContainerMargin)
    ) {
        Text(stringResource(R.string.settings_title), style = HeadlineMd, color = OnSurface)
        if (profile.isProMember) {
            GlassCard(Modifier.fillMaxWidth().padding(top = Dimens.Md)) {
                Row(
                    Modifier.padding(Dimens.Md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.WorkspacePremium, null, tint = Primary)
                    Column(Modifier.padding(start = Dimens.Sm)) {
                        Text(stringResource(R.string.settings_pro_title), style = HeadlineMd, color = OnSurface)
                        Text(stringResource(R.string.settings_pro_subtitle), style = BodyMd, color = OnSurfaceVariant)
                    }
                }
            }
        }
        GlassCard(Modifier.fillMaxWidth().padding(top = Dimens.Md)) {
            Column(Modifier.padding(Dimens.Md)) {
                Text(stringResource(R.string.settings_profile), style = HeadlineMd, color = OnSurface)
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text(stringResource(R.string.settings_display_name)) },
                    modifier = Modifier.fillMaxWidth().padding(top = Dimens.Sm),
                    singleLine = true
                )
                Button(
                    onClick = { if (nameInput.isNotBlank()) repository.updateDisplayName(nameInput) },
                    modifier = Modifier.padding(top = Dimens.Sm)
                ) {
                    Text(stringResource(R.string.settings_save_profile), style = ButtonText)
                }
            }
        }
        GlassCard(Modifier.fillMaxWidth().padding(top = Dimens.Md)) {
            Column(Modifier.padding(Dimens.Md)) {
                SettingToggle(stringResource(R.string.settings_reminders), notifications, repository::setNotificationsEnabled)
                SettingToggle(stringResource(R.string.settings_metric_units), unitsMetric, repository::setUnitsMetric)
            }
        }
        GlassCard(Modifier.fillMaxWidth().padding(top = Dimens.Md)) {
            Column(Modifier.padding(Dimens.Md)) {
                Text(stringResource(R.string.settings_weekly_target), style = BodyMd, color = OnSurface)
                Row(
                    Modifier.fillMaxWidth().padding(top = Dimens.Sm),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = { repository.setWeeklyTargetSessions((weeklyGoal.targetSessions - 1).coerceAtLeast(1)) }) {
                        Text("-")
                    }
                    Text("${weeklyGoal.targetSessions}", style = HeadlineMd, color = Primary)
                    Button(onClick = { repository.setWeeklyTargetSessions(weeklyGoal.targetSessions + 1) }) {
                        Text("+")
                    }
                }
            }
        }
        Button(
            onClick = onOpenInsights,
            modifier = Modifier.fillMaxWidth().padding(top = Dimens.Md)
        ) {
            Icon(Icons.Default.Insights, null, modifier = Modifier.padding(end = Dimens.Sm))
            Text(stringResource(R.string.settings_open_insights), style = ButtonText)
        }
        Button(
            onClick = repository::resetOnboarding,
            modifier = Modifier.fillMaxWidth().padding(top = Dimens.Md),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainer)
        ) {
            Text(stringResource(R.string.reset_onboarding), style = ButtonText)
        }
        Button(
            onClick = repository::resetDailyGoals,
            modifier = Modifier.fillMaxWidth().padding(top = Dimens.Sm)
        ) {
            Text(stringResource(R.string.reset_daily_goals), style = ButtonText)
        }
        Button(
            onClick = repository::resetWeeklyProgress,
            modifier = Modifier.fillMaxWidth().padding(top = Dimens.Sm)
        ) {
            Text(stringResource(R.string.reset_weekly_progress), style = ButtonText)
        }
        Button(
            onClick = repository::clearWorkoutHistory,
            modifier = Modifier.fillMaxWidth().padding(top = Dimens.Md),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainer)
        ) {
            Text(stringResource(R.string.clear_history), style = ButtonText)
        }
        Text(
            stringResource(R.string.settings_version, "1.0"),
            style = BodyMd,
            color = OnSurfaceVariant,
            modifier = Modifier.padding(top = Dimens.Lg)
        )
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth().padding(top = Dimens.Md),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainer)
        ) {
            Text(stringResource(R.string.settings_logout), style = ButtonText)
        }
    }
}

@Composable
private fun SettingToggle(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = Dimens.Sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = BodyMd, color = OnSurface)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
