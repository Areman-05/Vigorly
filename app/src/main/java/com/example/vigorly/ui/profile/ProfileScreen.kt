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
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.ManageAccounts
import androidx.compose.material.icons.outlined.MonitorWeight
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.Straighten
import androidx.compose.material.icons.outlined.WorkspacePremium
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import com.example.vigorly.ui.theme.DisplayStat
import com.example.vigorly.ui.theme.GlassLabel
import com.example.vigorly.ui.theme.HeadlineLgMobile
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer
import com.example.vigorly.ui.theme.SurfaceContainer

@Composable
fun ProfileScreen(
    repository: VigorlyRepository,
    onRestartOnboarding: () -> Unit = {},
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel(factory = AppViewModelFactory(repository))
) {
    val profile by viewModel.profile.collectAsState()
    val account by viewModel.currentAccount.collectAsState()
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
        !(account!!.authProvider == "google" && account!!.passwordHash.isBlank())

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
            .padding(top = Dimens.Sm, bottom = Dimens.FloatingNavClearance)
    ) {
        Text(
            text = stringResource(R.string.profile_title),
            style = HeadlineLgMobile.copy(
                fontSize = 34.sp,
                lineHeight = 40.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp
            ),
            color = OnSurface,
            modifier = Modifier.padding(bottom = Dimens.Md)
        )

        ProfileIdentityCard(
            displayName = profile.displayName.ifBlank {
                stringResource(R.string.profile_default_name)
            },
            email = accountEmail,
            avatarUrl = profile.avatarUrl,
            onAvatarClick = { avatarPickerVisible = true },
            onAccountClick = { sheet = ProfileConfigSheet.Account }
        )

        Spacer(Modifier.height(12.dp))

        ProfileStatsRow(
            level = profile.level,
            streakDays = profile.activeStreakDays,
            totalWorkouts = profile.totalWorkouts
        )

        Spacer(Modifier.height(Dimens.Lg))

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

        ProfileSectionHeader(stringResource(R.string.profile_section_account))
        ProfileSectionCard {
            ProfileConfigRow(
                label = stringResource(R.string.profile_account_title),
                subtitle = stringResource(R.string.profile_account_row_hint),
                icon = Icons.Outlined.ManageAccounts,
                onClick = { sheet = ProfileConfigSheet.Account }
            )
        }

        Spacer(Modifier.height(18.dp))
        ProfileSectionHeader(stringResource(R.string.profile_section_app))
        ProfileSectionCard {
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
        }

        Spacer(Modifier.height(18.dp))
        ProfileSectionHeader(stringResource(R.string.profile_section_goals))
        ProfileSectionCard {
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
        }

        Spacer(Modifier.height(18.dp))
        ProfileSectionHeader(stringResource(R.string.profile_section_more))
        ProfileSectionCard {
            ProfileConfigRow(
                label = stringResource(R.string.profile_training_prefs_title),
                subtitle = stringResource(R.string.profile_training_prefs_row_hint),
                icon = Icons.Outlined.FitnessCenter,
                onClick = { sheet = ProfileConfigSheet.TrainingPrefs }
            )
            ProfileConfigDivider()
            ProfileConfigRow(
                label = stringResource(R.string.profile_config_data_title),
                subtitle = stringResource(R.string.profile_config_data_row_hint),
                icon = Icons.Outlined.Storage,
                onClick = { sheet = ProfileConfigSheet.ManageData }
            )
        }

        Spacer(Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.settings_version, appVersionName()),
            style = BodyMd.copy(fontSize = 12.sp, letterSpacing = 0.1.sp),
            color = GlassLabel.copy(alpha = 0.45f),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.settings_logout),
            style = BodyMd.copy(
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.1.sp
            ),
            color = GlassLabel.copy(alpha = 0.78f),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(12.dp))
                .clickable { showLogoutConfirm = true }
                .testTag(VigorlyTestTags.SETTINGS)
                .padding(horizontal = 16.dp, vertical = 12.dp)
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
private fun ProfileIdentityCard(
    displayName: String,
    email: String,
    avatarUrl: String?,
    onAvatarClick: () -> Unit,
    onAccountClick: () -> Unit
) {
    GlassSurface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        onClick = onAccountClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clickable(onClick = onAvatarClick)
            ) {
                ProfileAvatarView(
                    avatarUrl = avatarUrl,
                    size = 72.dp,
                    modifier = Modifier.align(Alignment.Center)
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(PrimaryAccent, PrimaryContainer))
                        )
                        .border(1.5.dp, Color.White.copy(alpha = 0.35f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = stringResource(R.string.profile_change_avatar),
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp)
            ) {
                Text(
                    text = displayName,
                    style = HeadlineMd.copy(
                        fontSize = 20.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.3).sp
                    ),
                    color = OnSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = email.ifBlank { stringResource(R.string.profile_account_row_hint) },
                    style = BodyMd.copy(
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        letterSpacing = 0.05.sp
                    ),
                    color = GlassLabel.copy(alpha = 0.75f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = stringResource(R.string.profile_identity_cta),
                    style = BodyMd.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.15.sp
                    ),
                    color = OnSurface.copy(alpha = 0.88f),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = GlassLabel.copy(alpha = 0.55f),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun ProfileStatsRow(
    level: Int,
    streakDays: Int,
    totalWorkouts: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ProfileStatChip(
            value = level.toString(),
            label = stringResource(R.string.dashboard_snap_level),
            icon = Icons.Outlined.WorkspacePremium,
            modifier = Modifier.weight(1f)
        )
        ProfileStatChip(
            value = stringResource(R.string.dashboard_snap_streak_value, streakDays),
            label = stringResource(R.string.dashboard_snap_streak),
            icon = Icons.Outlined.LocalFireDepartment,
            modifier = Modifier.weight(1f)
        )
        ProfileStatChip(
            value = totalWorkouts.toString(),
            label = stringResource(R.string.profile_stat_workouts_short),
            icon = Icons.Outlined.FitnessCenter,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ProfileStatChip(
    value: String,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    GlassSurface(
        modifier = modifier.height(88.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = OnSurface,
                modifier = Modifier.size(16.dp)
            )
            Column {
                Text(
                    text = value,
                    style = DisplayStat.copy(
                        fontSize = 18.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.2).sp
                    ),
                    color = OnSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = label,
                    style = BodyMd.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.1.sp
                    ),
                    color = GlassLabel.copy(alpha = 0.78f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun ProfileSectionHeader(title: String) {
    Text(
        text = title,
        style = HeadlineMd.copy(
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (-0.2).sp
        ),
        color = OnSurface,
        modifier = Modifier.padding(bottom = 10.dp)
    )
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
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
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
