package com.example.vigorly.ui.profile

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.MonitorWeight
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.Straighten
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.vigorly.R
import com.example.vigorly.core.testing.VigorlyTestTags
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.di.AppViewModelFactory
import com.example.vigorly.presentation.feature.profile.ProfileViewModel
import com.example.vigorly.ui.components.GlassSurface
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.GlassLabel
import com.example.vigorly.ui.theme.HeadlineLgMobile
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.SurfaceContainer

@Composable
fun ProfileScreen(
    repository: VigorlyRepository,
    onRestartOnboarding: () -> Unit = {},
    onLogout: () -> Unit = {},
    onOpenHistory: () -> Unit = {},
    onOpenHistoryItem: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel(factory = AppViewModelFactory(repository))
) {
    val profile by viewModel.profile.collectAsState()
    val account by viewModel.currentAccount.collectAsState()
    val revealablePassword by viewModel.revealablePassword.collectAsState()
    val weeklyGoal by viewModel.weeklyGoal.collectAsState()
    val notifications by viewModel.notificationsEnabled.collectAsState()
    val unitsMetric by viewModel.unitsMetric.collectAsState()
    val locale by viewModel.appLocale.collectAsState()
    val fitnessGoal by viewModel.fitnessGoal.collectAsState()
    val activityLevel by viewModel.activityLevel.collectAsState()
    val workoutLocation by viewModel.workoutLocation.collectAsState()
    val weightGoalKg by viewModel.weightGoalKg.collectAsState()
    val localeJava = remember(locale) {
        java.util.Locale.forLanguageTag(locale.replace('_', '-'))
    }

    val selectedAvatarId = remember(profile.avatarUrl) {
        ProfileAvatarCatalog.presetId(profile.avatarUrl)
    }
    val hasCustomPhoto = remember(profile.avatarUrl) {
        ProfileAvatarCatalog.isRemoteUrl(profile.avatarUrl)
    }
    val accountEmail = account?.email.orEmpty()
    val canChangePassword = account != null &&
        !(account!!.authProvider == "google" &&
            account!!.password.isBlank() &&
            account!!.passwordHash.isBlank())

    var avatarPickerVisible by remember { mutableStateOf(false) }
    var sheet by remember { mutableStateOf<ProfileConfigSheet?>(null) }
    var showLogoutConfirm by remember { mutableStateOf(false) }

    if (showLogoutConfirm) {
        LogoutConfirmDialog(
            onDismiss = { showLogoutConfirm = false },
            onConfirm = {
                showLogoutConfirm = false
                onLogout()
            }
        )
    }

    ProfileAvatarPickerSheet(
        visible = avatarPickerVisible,
        selectedId = selectedAvatarId,
        hasCustomPhoto = hasCustomPhoto,
        onDismiss = { avatarPickerVisible = false },
        onSelectPreset = { id ->
            viewModel.setAvatarPreset(id)
            avatarPickerVisible = false
        },
        onPickPhoto = { uri ->
            viewModel.setAvatarFromUri(uri)
            avatarPickerVisible = false
        }
    )

    val context = LocalContext.current
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.setNotificationsEnabled(granted)
    }

    ProfileConfigHost(
        sheet = sheet,
        displayName = profile.displayName,
        avatarUrl = profile.avatarUrl,
        accountEmail = accountEmail,
        canChangePassword = canChangePassword,
        revealablePassword = revealablePassword,
        locale = locale,
        notifications = notifications,
        unitsMetric = unitsMetric,
        fitnessGoal = fitnessGoal,
        activityLevel = activityLevel,
        workoutLocation = workoutLocation,
        weightGoalKg = weightGoalKg,
        weeklyTarget = weeklyGoal.targetSessions,
        weeklyCompleted = weeklyGoal.completedSessions,
        onDismiss = { sheet = null },
        onOpenSheet = { sheet = it },
        onSaveName = viewModel::updateDisplayName,
        onSaveEmail = viewModel::updateAccountEmail,
        onChangePassword = viewModel::updateAccountPassword,
        onLocale = viewModel::setAppLocale,
        onNotifications = { enabled ->
            if (!enabled) {
                viewModel.setNotificationsEnabled(false)
            } else if (Build.VERSION.SDK_INT >= 33) {
                val granted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
                if (granted) {
                    viewModel.setNotificationsEnabled(true)
                } else {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            } else {
                viewModel.setNotificationsEnabled(true)
            }
        },
        onUnitsMetric = viewModel::setUnitsMetric,
        onFitnessGoal = viewModel::setFitnessGoal,
        onActivityLevel = viewModel::setActivityLevel,
        onLocation = viewModel::setWorkoutLocation,
        onWeightGoalKg = viewModel::setWeightGoalKg,
        onWeeklyTarget = viewModel::setWeeklyTargetSessions,
        onResetOnboarding = {
            viewModel.resetOnboarding()
            onRestartOnboarding()
        },
        onResetDaily = viewModel::resetDailyGoals,
        onResetWeekly = viewModel::resetWeeklyProgress,
        onClearHistory = viewModel::clearWorkoutHistory
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .testTag(VigorlyTestTags.PROFILE)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.ContainerMargin)
            .padding(top = Dimens.Sm, bottom = Dimens.FloatingNavClearance),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.profile_title),
            style = HeadlineLgMobile,
            color = OnSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Dimens.Md)
        )

        Box(
            modifier = Modifier
                .size(128.dp)
                .clip(CircleShape)
                .border(2.dp, Color.White, CircleShape)
                .clickable { avatarPickerVisible = true },
            contentAlignment = Alignment.Center
        ) {
            ProfileAvatarView(
                avatarUrl = profile.avatarUrl,
                size = 120.dp
            )
        }

        Text(
            text = profile.displayName.ifBlank {
                stringResource(R.string.profile_default_name)
            },
            style = HeadlineMd.copy(
                fontSize = 22.sp,
                lineHeight = 26.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.3).sp
            ),
            color = OnSurface,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )
        Text(
            text = stringResource(R.string.profile_level_value, profile.level),
            style = BodyMd.copy(
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.2.sp
            ),
            color = GlassLabel.copy(alpha = 0.82f),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        )

        val langLabel = ProfileConfigOptions.languages
            .find { it.code == locale }
            ?.let { stringResource(it.labelRes) }
            ?: locale
        val weightLabel = weightGoalKg?.let { kg ->
            if (unitsMetric) {
                "%.1f kg".format(localeJava, kg)
            } else {
                "%.1f lb".format(localeJava, kg * 2.20462f)
            }
        } ?: stringResource(R.string.profile_config_weight_none)

        Spacer(Modifier.height(22.dp))
        ProfileSectionCard {
            ProfileConfigRow(
                label = stringResource(R.string.profile_title),
                subtitle = stringResource(R.string.profile_identity_cta),
                icon = Icons.Outlined.Person,
                onClick = { sheet = ProfileConfigSheet.AccountOverview }
            )
            ProfileConfigDivider()
            ProfileConfigRow(
                label = stringResource(R.string.profile_menu_history),
                subtitle = stringResource(R.string.history_calendar_hint),
                icon = Icons.Outlined.CalendarMonth,
                onClick = onOpenHistory,
                testTag = VigorlyTestTags.PROFILE_OPEN_HISTORY
            )
            ProfileConfigDivider()
            ProfileConfigRow(
                label = stringResource(R.string.settings_reminders),
                subtitle = if (notifications) {
                    stringResource(R.string.profile_config_reminders_on)
                } else {
                    stringResource(R.string.profile_config_reminders_off)
                },
                icon = Icons.Outlined.Notifications,
                onClick = { sheet = ProfileConfigSheet.Reminders }
            )
            ProfileConfigDivider()
            ProfileConfigRow(
                label = stringResource(R.string.settings_language),
                subtitle = langLabel,
                icon = Icons.Outlined.Language,
                onClick = { sheet = ProfileConfigSheet.Language }
            )
            ProfileConfigDivider()
            ProfileConfigRow(
                label = stringResource(R.string.profile_config_units_title),
                subtitle = if (unitsMetric) {
                    stringResource(R.string.profile_config_units_metric)
                } else {
                    stringResource(R.string.profile_config_units_imperial)
                },
                icon = Icons.Outlined.Straighten,
                onClick = { sheet = ProfileConfigSheet.Units }
            )
            ProfileConfigDivider()
            ProfileConfigRow(
                label = stringResource(R.string.profile_training_prefs_title),
                subtitle = stringResource(R.string.profile_training_prefs_row_hint),
                icon = Icons.Outlined.FitnessCenter,
                onClick = { sheet = ProfileConfigSheet.TrainingPrefs }
            )
            ProfileConfigDivider()
            ProfileConfigRow(
                label = stringResource(R.string.weekly_goal_title),
                subtitle = stringResource(
                    R.string.profile_weekly_sessions_value,
                    weeklyGoal.targetSessions
                ),
                icon = Icons.Outlined.Flag,
                onClick = { sheet = ProfileConfigSheet.WeeklyGoal }
            )
            ProfileConfigDivider()
            ProfileConfigRow(
                label = stringResource(R.string.profile_config_weight_title),
                subtitle = weightLabel,
                icon = Icons.Outlined.MonitorWeight,
                onClick = { sheet = ProfileConfigSheet.WeightGoal }
            )
            ProfileConfigDivider()
            ProfileConfigRow(
                label = stringResource(R.string.profile_config_data_title),
                subtitle = stringResource(R.string.profile_config_data_row_hint),
                icon = Icons.Outlined.Storage,
                onClick = { sheet = ProfileConfigSheet.ManageData }
            )
            ProfileConfigDivider()
            ProfileConfigRow(
                label = stringResource(R.string.settings_logout),
                subtitle = stringResource(R.string.profile_logout_row_hint),
                icon = Icons.AutoMirrored.Outlined.Logout,
                onClick = { showLogoutConfirm = true },
                testTag = VigorlyTestTags.SETTINGS
            )
        }

        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.settings_version, appVersionName()),
            style = BodyMd.copy(fontSize = 12.sp, letterSpacing = 0.1.sp),
            color = GlassLabel.copy(alpha = 0.4f)
        )
    }
}

@Composable
private fun LogoutConfirmDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(SurfaceContainer)
                .padding(20.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Logout,
                contentDescription = null,
                tint = OnSurface,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = stringResource(R.string.settings_logout_confirm_title),
                style = HeadlineMd.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                color = OnSurface,
                modifier = Modifier.padding(top = 10.dp)
            )
            Text(
                text = stringResource(R.string.settings_logout_confirm_body),
                style = BodyMd.copy(fontSize = 14.sp),
                color = GlassLabel.copy(alpha = 0.85f),
                modifier = Modifier.padding(top = 8.dp, bottom = 14.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = stringResource(R.string.settings_logout_cancel),
                        color = GlassLabel
                    )
                }
                TextButton(onClick = onConfirm) {
                    Text(
                        text = stringResource(R.string.settings_logout_confirm_action),
                        color = PrimaryAccent,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileSectionCard(content: @Composable () -> Unit) {
    GlassSurface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun appVersionName(): String {
    val context = LocalContext.current
    return remember(context) {
        runCatching {
            val info = if (android.os.Build.VERSION.SDK_INT >= 33) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(context.packageName, 0)
            }
            info.versionName ?: "1.0"
        }.getOrDefault("1.0")
    }
}

@Composable
private fun ProfileConfigDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(1.dp)
            .background(Color.White.copy(alpha = 0.07f))
    )
}

@Composable
private fun ProfileConfigRow(
    label: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    testTag: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = OnSurface,
                modifier = Modifier.size(18.dp)
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = label,
                style = BodyMd.copy(
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = (-0.1).sp
                ),
                color = OnSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = BodyMd.copy(
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    letterSpacing = 0.05.sp
                ),
                color = GlassLabel.copy(alpha = 0.68f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 3.dp)
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = GlassLabel.copy(alpha = 0.55f),
            modifier = Modifier.size(20.dp)
        )
    }
}
