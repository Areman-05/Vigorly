package com.example.vigorly.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.core.testing.VigorlyTestTags
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.di.AppViewModelFactory
import com.example.vigorly.presentation.feature.settings.SettingsViewModel
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.HeadlineLgMobile
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer

@Composable
fun SettingsScreen(
    repository: VigorlyRepository,
    onOpenInsights: () -> Unit = {},
    onRestartOnboarding: () -> Unit = {},
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelFactory(repository))
) {
    val profile by viewModel.profile.collectAsState()
    val notifications by viewModel.notificationsEnabled.collectAsState()
    val unitsMetric by viewModel.unitsMetric.collectAsState()
    val weeklyGoal by viewModel.weeklyGoal.collectAsState()
    var nameInput by remember(profile.displayName) { mutableStateOf(profile.displayName) }
    val nameDirty = nameInput != profile.displayName && nameInput.isNotBlank()

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag(VigorlyTestTags.SETTINGS)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.ContainerMargin, vertical = Dimens.Lg)
    ) {
        Text(
            stringResource(R.string.settings_title),
            style = HeadlineLgMobile.copy(fontSize = 30.sp, lineHeight = 36.sp),
            color = PrimaryAccent,
            fontWeight = FontWeight.Bold
        )
        Text(
            stringResource(R.string.settings_subtitle),
            style = BodyMd.copy(fontSize = 16.sp),
            color = OnSurfaceVariant.copy(alpha = 0.85f),
            modifier = Modifier.padding(top = 6.dp, bottom = Dimens.Md)
        )

        if (profile.isProMember) {
            SettingsSectionCard {
                RowWithIcon(
                    icon = Icons.Default.WorkspacePremium,
                    title = stringResource(R.string.settings_pro_title),
                    subtitle = stringResource(R.string.settings_pro_subtitle),
                    iconTint = PrimaryAccent
                )
            }
        }

        SettingsSectionTitle(stringResource(R.string.settings_section_account))
        SettingsSectionCard {
            Text(
                stringResource(R.string.settings_display_name),
                style = BodyMd.copy(fontSize = 15.sp),
                color = OnSurfaceVariant.copy(alpha = 0.85f)
            )
            OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.Sm),
                singleLine = true,
                textStyle = BodyMd.copy(fontSize = 17.sp, color = OnSurface),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryAccent,
                    unfocusedBorderColor = OnSurfaceVariant.copy(alpha = 0.35f),
                    focusedLabelColor = PrimaryAccent,
                    cursorColor = PrimaryAccent
                )
            )
            if (nameDirty) {
                SettingsPrimaryButton(
                    text = stringResource(R.string.settings_save_profile),
                    onClick = { viewModel.updateDisplayName(nameInput.trim()) },
                    modifier = Modifier.padding(top = Dimens.Md)
                )
            }
        }

        SettingsSectionTitle(stringResource(R.string.settings_section_preferences))
        SettingsSectionCard {
            SettingsToggleRow(
                label = stringResource(R.string.settings_reminders),
                subtitle = stringResource(R.string.settings_reminders_hint),
                checked = notifications,
                onCheckedChange = viewModel::setNotificationsEnabled
            )
            SettingsToggleRow(
                label = stringResource(R.string.settings_metric_units),
                subtitle = stringResource(R.string.settings_metric_units_hint),
                checked = unitsMetric,
                onCheckedChange = viewModel::setUnitsMetric
            )
        }

        SettingsSectionTitle(stringResource(R.string.settings_section_weekly))
        SettingsSectionCard {
            Text(
                stringResource(R.string.settings_weekly_target),
                style = BodyMd.copy(fontSize = 16.sp, fontWeight = FontWeight.Medium),
                color = OnSurface
            )
            Text(
                stringResource(R.string.settings_weekly_target_hint),
                style = BodyMd.copy(fontSize = 14.sp),
                color = OnSurfaceVariant.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 4.dp, bottom = Dimens.Md)
            )
            SettingsWeeklyStepper(
                targetSessions = weeklyGoal.targetSessions,
                onDecrease = {
                    viewModel.setWeeklyTargetSessions((weeklyGoal.targetSessions - 1).coerceAtLeast(1))
                },
                onIncrease = {
                    viewModel.setWeeklyTargetSessions((weeklyGoal.targetSessions + 1).coerceAtMost(14))
                }
            )
        }

        SettingsSectionTitle(stringResource(R.string.settings_section_explore))
        SettingsNavRow(
            title = stringResource(R.string.settings_open_insights),
            icon = Icons.Default.Insights,
            onClick = onOpenInsights
        )

        SettingsSectionTitle(stringResource(R.string.settings_section_data))
        SettingsSectionCard {
            SettingsActionRow(
                title = stringResource(R.string.reset_onboarding),
                subtitle = stringResource(R.string.settings_reset_onboarding_hint),
                onClick = {
                    viewModel.resetOnboarding()
                    onRestartOnboarding()
                }
            )
            Spacer(Modifier.height(8.dp))
            SettingsActionRow(
                title = stringResource(R.string.reset_daily_goals),
                subtitle = stringResource(R.string.settings_reset_daily_hint),
                onClick = viewModel::resetDailyGoals
            )
            Spacer(Modifier.height(8.dp))
            SettingsActionRow(
                title = stringResource(R.string.reset_weekly_progress),
                subtitle = stringResource(R.string.settings_reset_weekly_hint),
                onClick = viewModel::resetWeeklyProgress
            )
            Spacer(Modifier.height(8.dp))
            SettingsActionRow(
                title = stringResource(R.string.clear_history),
                subtitle = stringResource(R.string.settings_clear_history_hint),
                onClick = viewModel::clearWorkoutHistory,
                destructive = true
            )
        }

        SettingsPrimaryButton(
            text = stringResource(R.string.settings_logout),
            onClick = onLogout,
            destructive = true,
            modifier = Modifier.padding(top = Dimens.Xl)
        )

        Spacer(Modifier.height(Dimens.Lg))
    }
}

@Composable
private fun RowWithIcon(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconTint: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(28.dp))
        Column(Modifier.padding(start = Dimens.Md)) {
            Text(title, style = HeadlineMd.copy(fontSize = 18.sp), color = OnSurface, fontWeight = FontWeight.SemiBold)
            Text(
                subtitle,
                style = BodyMd.copy(fontSize = 15.sp),
                color = OnSurfaceVariant.copy(alpha = 0.85f),
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
