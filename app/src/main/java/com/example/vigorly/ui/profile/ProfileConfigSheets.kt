package com.example.vigorly.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.vigorly.R
import com.example.vigorly.ui.components.GlassSurface
import com.example.vigorly.ui.settings.SettingsWeeklyStepper
import com.example.vigorly.ui.setup.SetupStepIcons
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.DisplayStat
import com.example.vigorly.ui.theme.GlassLabel
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer
import com.example.vigorly.ui.theme.Surface
import com.example.vigorly.ui.theme.SurfaceContainer

enum class ProfileConfigSheet {
    AccountOverview,
    Account,
    Language,
    Reminders,
    Units,
    TrainingPrefs,
    FitnessGoal,
    ActivityLevel,
    Location,
    WeightGoal,
    WeeklyGoal,
    ManageData
}

@Composable
fun ProfileConfigHost(
    sheet: ProfileConfigSheet?,
    displayName: String,
    avatarUrl: String?,
    accountEmail: String,
    canChangePassword: Boolean,
    revealablePassword: String? = null,
    locale: String,
    notifications: Boolean,
    unitsMetric: Boolean,
    fitnessGoal: String,
    activityLevel: String,
    workoutLocation: String,
    weightGoalKg: Float?,
    weeklyTarget: Int,
    weeklyCompleted: Int,
    onDismiss: () -> Unit,
    onOpenSheet: (ProfileConfigSheet) -> Unit,
    onSaveName: (String) -> Unit,
    onSaveEmail: (String, (com.example.vigorly.data.model.AuthError?) -> Unit) -> Unit,
    onChangePassword: (current: String, newPassword: String, (com.example.vigorly.data.model.AuthError?) -> Unit) -> Unit,
    onLocale: (String) -> Unit,
    onNotifications: (Boolean) -> Unit,
    onUnitsMetric: (Boolean) -> Unit,
    onFitnessGoal: (String) -> Unit,
    onActivityLevel: (String) -> Unit,
    onLocation: (String) -> Unit,
    onWeightGoalKg: (Float?) -> Unit,
    onWeeklyTarget: (Int) -> Unit,
    onResetOnboarding: () -> Unit,
    onResetDaily: () -> Unit,
    onResetWeekly: () -> Unit,
    onClearHistory: () -> Unit
) {
    if (sheet == null) return

    when (sheet) {
        ProfileConfigSheet.AccountOverview -> AccountOverviewSheet(
            displayName = displayName,
            avatarUrl = avatarUrl,
            email = accountEmail,
            canChangePassword = canChangePassword,
            revealablePassword = revealablePassword,
            onDismiss = onDismiss,
            onEditAccount = { onOpenSheet(ProfileConfigSheet.Account) }
        )
        ProfileConfigSheet.Account -> AccountSheet(
            displayName = displayName,
            avatarUrl = avatarUrl,
            email = accountEmail,
            canChangePassword = canChangePassword,
            revealablePassword = revealablePassword,
            onDismiss = onDismiss,
            onSaveName = onSaveName,
            onSaveEmail = onSaveEmail,
            onChangePassword = onChangePassword
        )
        ProfileConfigSheet.Language -> LanguageSheet(
            selected = locale,
            onDismiss = onDismiss,
            onSelect = {
                onLocale(it)
                onDismiss()
            }
        )
        ProfileConfigSheet.Reminders -> RemindersSheet(
            enabled = notifications,
            onDismiss = onDismiss,
            onEnabledChange = onNotifications
        )
        ProfileConfigSheet.Units -> UnitsSheet(
            metric = unitsMetric,
            onDismiss = onDismiss,
            onSelect = {
                onUnitsMetric(it)
                onDismiss()
            }
        )
        ProfileConfigSheet.TrainingPrefs -> TrainingPrefsSheet(
            categoryLabel = formatChoiceLabels(ProfileConfigOptions.workoutCategories, fitnessGoal),
            intensityLabel = formatChoiceLabels(ProfileConfigOptions.intensities, activityLevel),
            durationLabel = formatChoiceLabels(ProfileConfigOptions.durations, workoutLocation),
            onDismiss = onDismiss,
            onOpenCategories = { onOpenSheet(ProfileConfigSheet.FitnessGoal) },
            onOpenIntensity = { onOpenSheet(ProfileConfigSheet.ActivityLevel) },
            onOpenDuration = { onOpenSheet(ProfileConfigSheet.Location) }
        )
        ProfileConfigSheet.FitnessGoal -> MultiChoiceSheet(
            title = stringResource(R.string.profile_config_fitness_title),
            subtitle = stringResource(R.string.profile_config_fitness_subtitle),
            choices = ProfileConfigOptions.workoutCategories,
            selectedKeys = ProfileConfigOptions.parseCsv(fitnessGoal)
                .map { it.lowercase() }
                .filter { key -> ProfileConfigOptions.workoutCategories.any { it.key == key } }
                .toSet(),
            iconFor = SetupStepIcons::category,
            multi = true,
            onDismiss = { onOpenSheet(ProfileConfigSheet.TrainingPrefs) },
            onConfirm = {
                onFitnessGoal(ProfileConfigOptions.toCsv(it))
                onOpenSheet(ProfileConfigSheet.TrainingPrefs)
            }
        )
        ProfileConfigSheet.ActivityLevel -> MultiChoiceSheet(
            title = stringResource(R.string.profile_config_activity_title),
            subtitle = stringResource(R.string.profile_config_activity_subtitle),
            choices = ProfileConfigOptions.intensities,
            selectedKeys = ProfileConfigOptions.parseCsv(activityLevel)
                .map { it.lowercase() }
                .filter { key -> ProfileConfigOptions.intensities.any { it.key == key } }
                .toSet(),
            iconFor = SetupStepIcons::intensity,
            multi = true,
            onDismiss = { onOpenSheet(ProfileConfigSheet.TrainingPrefs) },
            onConfirm = {
                onActivityLevel(ProfileConfigOptions.toCsv(it))
                onOpenSheet(ProfileConfigSheet.TrainingPrefs)
            }
        )
        ProfileConfigSheet.Location -> MultiChoiceSheet(
            title = stringResource(R.string.profile_config_location_title),
            subtitle = stringResource(R.string.profile_config_location_subtitle),
            choices = ProfileConfigOptions.durations,
            selectedKeys = ProfileConfigOptions.parseCsv(workoutLocation)
                .map { it.lowercase() }
                .filter { key -> ProfileConfigOptions.durations.any { it.key == key } }
                .toSet(),
            iconFor = SetupStepIcons::durationPref,
            multi = true,
            onDismiss = { onOpenSheet(ProfileConfigSheet.TrainingPrefs) },
            onConfirm = {
                onLocation(ProfileConfigOptions.toCsv(it))
                onOpenSheet(ProfileConfigSheet.TrainingPrefs)
            }
        )
        ProfileConfigSheet.WeightGoal -> WeightGoalSheet(
            weightGoalKg = weightGoalKg,
            unitsMetric = unitsMetric,
            onDismiss = onDismiss,
            onSave = {
                onWeightGoalKg(it)
                onDismiss()
            }
        )
        ProfileConfigSheet.WeeklyGoal -> WeeklyGoalSheet(
            target = weeklyTarget,
            completed = weeklyCompleted,
            onDismiss = onDismiss,
            onChange = onWeeklyTarget
        )
        ProfileConfigSheet.ManageData -> ManageDataSheet(
            onDismiss = onDismiss,
            onResetOnboarding = onResetOnboarding,
            onResetDaily = onResetDaily,
            onResetWeekly = onResetWeekly,
            onClearHistory = onClearHistory
        )
    }
}

private const val EmptyPrefLabel = "\u2014"

@Composable
private fun formatChoiceLabels(
    choices: List<ProfileConfigOptions.Choice>,
    raw: String
): String {
    val keys = ProfileConfigOptions.parseCsv(raw)
    if (keys.isEmpty()) return EmptyPrefLabel
    return keys.mapNotNull { key ->
        choices.find { it.key.equals(key, ignoreCase = true) }?.let { stringResource(it.titleRes) }
    }.joinToString(", ").ifBlank { EmptyPrefLabel }
}

@Composable
private fun ConfigSheetScaffold(
    title: String,
    subtitle: String,
    onDismiss: () -> Unit,
    headerAction: (@Composable () -> Unit)? = null,
    expandToBottom: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val sheetShape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    val maxSheetHeight = LocalConfiguration.current.screenHeightDp.dp * 0.88f
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.78f))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onDismiss
                )
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .then(
                        if (expandToBottom) {
                            Modifier.fillMaxHeight()
                        } else {
                            Modifier
                                .wrapContentHeight()
                                .heightIn(max = maxSheetHeight)
                        }
                    )
                    .clip(sheetShape)
                    .background(Surface)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {}
                    )
                    .then(if (expandToBottom) Modifier.statusBarsPadding() else Modifier)
                    .navigationBarsPadding()
                    .padding(horizontal = 22.dp)
                    .padding(top = 28.dp, bottom = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            style = HeadlineMd.copy(
                                fontSize = 22.sp,
                                lineHeight = 28.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = (-0.3).sp
                            ),
                            color = OnSurface
                        )
                        Text(
                            text = subtitle,
                            style = BodyMd.copy(
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                letterSpacing = 0.05.sp
                            ),
                            color = GlassLabel.copy(alpha = 0.72f),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    if (headerAction != null) {
                        headerAction()
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = OnSurface)
                    }
                }
                Spacer(modifier = Modifier.height(22.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (expandToBottom) {
                                Modifier
                                    .weight(1f)
                                    .verticalScroll(rememberScrollState())
                            } else {
                                Modifier
                            }
                        )
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
private fun AccountOverviewSheet(
    displayName: String,
    avatarUrl: String?,
    email: String,
    canChangePassword: Boolean,
    revealablePassword: String?,
    onDismiss: () -> Unit,
    onEditAccount: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val passwordMasked = stringResource(R.string.profile_account_password_masked)
    val passwordGoogle = stringResource(R.string.profile_account_password_google_short)
    val passwordShown = when {
        !canChangePassword -> passwordGoogle
        passwordVisible && !revealablePassword.isNullOrEmpty() -> revealablePassword
        else -> passwordMasked
    }

    ConfigSheetScaffold(
        title = stringResource(R.string.profile_overview_title),
        subtitle = stringResource(R.string.profile_overview_subtitle),
        onDismiss = onDismiss,
        headerAction = {
            IconButton(onClick = onEditAccount) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = stringResource(R.string.profile_overview_edit_cta),
                    tint = OnSurface
                )
            }
        }
    ) {
        GlassSurface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProfileAvatarView(avatarUrl = avatarUrl, size = 56.dp)
                    Column(modifier = Modifier.padding(start = 14.dp).weight(1f)) {
                        Text(
                            text = displayName.ifBlank { stringResource(R.string.profile_default_name) },
                            style = HeadlineMd.copy(
                                fontSize = 20.sp,
                                lineHeight = 24.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.3).sp
                            ),
                            color = OnSurface,
                            maxLines = 1
                        )
                        Text(
                            text = email.ifBlank { EmptyPrefLabel },
                            style = BodyMd.copy(fontSize = 13.sp),
                            color = GlassLabel.copy(alpha = 0.8f),
                            modifier = Modifier.padding(top = 4.dp),
                            maxLines = 1
                        )
                    }
                }
                OverviewHairline()
                OverviewPasswordRow(
                    label = stringResource(R.string.profile_account_password_section),
                    value = passwordShown,
                    visible = passwordVisible,
                    enabled = canChangePassword,
                    onToggle = {
                        if (!revealablePassword.isNullOrEmpty()) {
                            passwordVisible = !passwordVisible
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun OverviewHairline() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .height(1.dp)
            .background(Color.White.copy(alpha = 0.07f))
    )
}

@Composable
private fun OverviewPasswordRow(
    label: String,
    value: String,
    visible: Boolean,
    enabled: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
        ) {
            Text(
                text = label,
                style = BodyMd.copy(fontSize = 14.sp),
                color = GlassLabel.copy(alpha = 0.78f)
            )
            Text(
                text = value,
                style = BodyMd.copy(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
                color = OnSurface,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        IconButton(
            onClick = onToggle,
            enabled = enabled
        ) {
            Icon(
                imageVector = if (visible) {
                    Icons.Outlined.VisibilityOff
                } else {
                    Icons.Outlined.Visibility
                },
                contentDescription = stringResource(
                    if (visible) R.string.profile_password_hide else R.string.profile_password_show
                ),
                tint = if (enabled) OnSurface else GlassLabel.copy(alpha = 0.35f)
            )
        }
    }
}

@Composable
private fun AccountSheet(
    displayName: String,
    avatarUrl: String?,
    email: String,
    canChangePassword: Boolean,
    revealablePassword: String? = null,
    onDismiss: () -> Unit,
    onSaveName: (String) -> Unit,
    onSaveEmail: (String, (com.example.vigorly.data.model.AuthError?) -> Unit) -> Unit,
    onChangePassword: (current: String, newPassword: String, (com.example.vigorly.data.model.AuthError?) -> Unit) -> Unit
) {
    var nameDraft by remember(displayName) { mutableStateOf(displayName) }
    var emailDraft by remember(email) { mutableStateOf(email) }
    var currentPassword by remember(revealablePassword) { mutableStateOf(revealablePassword.orEmpty()) }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }
    var saving by remember { mutableStateOf(false) }

    val nameValid = nameDraft.trim().length in 2..24
    val nameChanged = nameDraft.trim() != displayName.trim()
    val emailChanged = emailDraft.trim().lowercase() != email.trim().lowercase()
    val wantsPasswordChange = canChangePassword && newPassword.isNotBlank()
    val passwordMismatch = newPassword.isNotBlank() && newPassword != confirmPassword
    val canSave = !saving && nameValid && (nameChanged || emailChanged || wantsPasswordChange) &&
        (!wantsPasswordChange || !passwordMismatch)

    val errInvalid = stringResource(R.string.auth_error_invalid)
    val errExists = stringResource(R.string.auth_error_exists)
    val errUsernameTaken = stringResource(R.string.auth_error_username_exists)
    val errFields = stringResource(R.string.auth_error_fields)
    val errPassword = stringResource(R.string.auth_error_password)
    val errWeak = stringResource(R.string.auth_error_password_weak)
    val errEmail = stringResource(R.string.auth_error_invalid_email)
    val errUsername = stringResource(R.string.auth_error_invalid_username)
    val errMismatch = stringResource(R.string.profile_account_password_mismatch)

    fun mapError(error: com.example.vigorly.data.model.AuthError): String = when (error) {
        com.example.vigorly.data.model.AuthError.INVALID_CREDENTIALS -> errInvalid
        com.example.vigorly.data.model.AuthError.EMAIL_ALREADY_EXISTS -> errExists
        com.example.vigorly.data.model.AuthError.USERNAME_ALREADY_EXISTS -> errUsernameTaken
        com.example.vigorly.data.model.AuthError.FIELDS_REQUIRED -> errFields
        com.example.vigorly.data.model.AuthError.PASSWORD_TOO_SHORT -> errPassword
        com.example.vigorly.data.model.AuthError.PASSWORD_WEAK -> errWeak
        com.example.vigorly.data.model.AuthError.INVALID_EMAIL -> errEmail
        com.example.vigorly.data.model.AuthError.INVALID_USERNAME -> errUsername
        else -> errFields
    }

    fun submit() {
        if (!canSave) return
        if (passwordMismatch) {
            errorText = errMismatch
            return
        }
        errorText = null
        saving = true

        fun finishOk() {
            saving = false
            onDismiss()
        }

        fun fail(error: com.example.vigorly.data.model.AuthError?) {
            saving = false
            errorText = error?.let(::mapError)
        }

        if (nameChanged) onSaveName(nameDraft.trim())

        fun maybePasswordThenFinish() {
            if (!wantsPasswordChange) {
                finishOk()
                return
            }
            onChangePassword("", newPassword) { pwdError ->
                if (pwdError != null) fail(pwdError) else finishOk()
            }
        }

        if (emailChanged) {
            onSaveEmail(emailDraft.trim()) { emailError ->
                if (emailError != null) fail(emailError) else maybePasswordThenFinish()
            }
        } else {
            maybePasswordThenFinish()
        }
    }

    ConfigSheetScaffold(
        title = stringResource(R.string.profile_account_edit_title),
        subtitle = stringResource(R.string.profile_account_edit_subtitle),
        onDismiss = onDismiss,
        expandToBottom = true
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            GlassSurface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ProfileAvatarView(avatarUrl = avatarUrl, size = 72.dp)
                    Text(
                        text = nameDraft.ifBlank { stringResource(R.string.profile_default_name) },
                        style = HeadlineMd.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                        color = OnSurface,
                        modifier = Modifier.padding(top = 10.dp, bottom = 16.dp),
                        maxLines = 1
                    )
                    AccountField(
                        label = stringResource(R.string.settings_display_name),
                        value = nameDraft,
                        onValueChange = { if (it.length <= 24) nameDraft = it },
                        placeholder = stringResource(R.string.profile_config_name_placeholder)
                    )
                    Spacer(Modifier.height(12.dp))
                    AccountField(
                        label = stringResource(R.string.profile_account_email_label),
                        value = emailDraft,
                        onValueChange = { emailDraft = it },
                        placeholder = stringResource(R.string.auth_email),
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Email
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            GlassSurface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = stringResource(R.string.profile_account_password_section),
                        style = BodyMd.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                        color = OnSurface
                    )
                    if (!canChangePassword) {
                        Text(
                            text = stringResource(R.string.profile_account_google_password),
                            style = BodyMd.copy(fontSize = 13.sp),
                            color = GlassLabel.copy(alpha = 0.85f),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    } else {
                        Spacer(Modifier.height(12.dp))
                        AccountField(
                            label = stringResource(R.string.profile_account_password_current),
                            value = currentPassword,
                            onValueChange = { },
                            placeholder = stringResource(R.string.profile_account_password_masked),
                            isPassword = true,
                            readOnly = true
                        )
                        Spacer(Modifier.height(10.dp))
                        AccountField(
                            label = stringResource(R.string.profile_account_password_new),
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            placeholder = "",
                            isPassword = true
                        )
                        Spacer(Modifier.height(10.dp))
                        AccountField(
                            label = stringResource(R.string.profile_account_password_confirm),
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            placeholder = "",
                            isPassword = true
                        )
                        Text(
                            text = stringResource(R.string.auth_password_req_summary),
                            style = BodyMd.copy(fontSize = 11.sp),
                            color = GlassLabel.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            if (errorText != null) {
                Text(
                    text = errorText.orEmpty(),
                    style = BodyMd.copy(fontSize = 13.sp, fontWeight = FontWeight.Medium),
                    color = PrimaryAccent,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        PrimarySheetButton(
            label = stringResource(R.string.settings_save_profile),
            enabled = canSave,
            onClick = ::submit
        )
    }
}

@Composable
private fun AccountField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false,
    readOnly: Boolean = false,
    keyboardType: androidx.compose.ui.text.input.KeyboardType = androidx.compose.ui.text.input.KeyboardType.Text
) {
    var passwordVisible by remember { mutableStateOf(false) }
    Text(
        text = label,
        style = BodyMd.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
        color = GlassLabel.copy(alpha = 0.85f)
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
            .padding(start = 16.dp, end = if (isPassword) 4.dp else 16.dp, top = 4.dp, bottom = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = value,
                onValueChange = { if (!readOnly) onValueChange(it) },
                readOnly = readOnly,
                singleLine = true,
                textStyle = BodyMd.copy(fontSize = 16.sp, color = OnSurface),
                cursorBrush = SolidColor(OnSurface),
                visualTransformation = if (isPassword && !passwordVisible) {
                    androidx.compose.ui.text.input.PasswordVisualTransformation()
                } else {
                    androidx.compose.ui.text.input.VisualTransformation.None
                },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = if (isPassword) {
                        androidx.compose.ui.text.input.KeyboardType.Password
                    } else {
                        keyboardType
                    }
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 10.dp),
                decorationBox = { inner ->
                    if (value.isEmpty() && placeholder.isNotEmpty()) {
                        Text(
                            placeholder,
                            color = GlassLabel.copy(alpha = 0.45f),
                            style = BodyMd.copy(fontSize = 16.sp)
                        )
                    }
                    inner()
                }
            )
            if (isPassword) {
                IconButton(
                    onClick = { passwordVisible = !passwordVisible },
                    enabled = value.isNotEmpty()
                ) {
                    Icon(
                        imageVector = if (passwordVisible) {
                            Icons.Outlined.VisibilityOff
                        } else {
                            Icons.Outlined.Visibility
                        },
                        contentDescription = stringResource(
                            if (passwordVisible) {
                                R.string.profile_password_hide
                            } else {
                                R.string.profile_password_show
                            }
                        ),
                        tint = if (value.isNotEmpty()) {
                            OnSurface.copy(alpha = 0.75f)
                        } else {
                            GlassLabel.copy(alpha = 0.35f)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LanguageSheet(
    selected: String,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    ConfigSheetScaffold(
        title = stringResource(R.string.settings_language),
        subtitle = stringResource(R.string.profile_config_language_subtitle),
        onDismiss = onDismiss
    ) {
        GlassSurface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                ProfileConfigOptions.languages.forEachIndexed { index, lang ->
                    val isSelected = lang.code == selected
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(lang.code) }
                            .padding(horizontal = 14.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (lang.code == "ca") {
                            CatalanFlag(
                                modifier = Modifier.clip(RoundedCornerShape(3.dp)),
                                width = 28.dp,
                                height = 18.dp
                            )
                        } else {
                            Text(lang.flagEmoji.orEmpty(), fontSize = 22.sp)
                        }
                        Text(
                            text = stringResource(lang.labelRes),
                            style = BodyMd.copy(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
                            color = OnSurface,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 12.dp)
                        )
                        if (isSelected) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = OnSurface)
                        }
                    }
                    if (index < ProfileConfigOptions.languages.lastIndex) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp)
                                .height(1.dp)
                                .background(Color.White.copy(alpha = 0.07f))
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RemindersSheet(
    enabled: Boolean,
    onDismiss: () -> Unit,
    onEnabledChange: (Boolean) -> Unit
) {
    ConfigSheetScaffold(
        title = stringResource(R.string.settings_reminders),
        subtitle = stringResource(R.string.profile_config_reminders_subtitle),
        onDismiss = onDismiss
    ) {
        GlassSurface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Icon(
                    Icons.Outlined.NotificationsActive,
                    contentDescription = null,
                    tint = OnSurface,
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = stringResource(R.string.profile_config_reminders_hero),
                    style = HeadlineMd.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                    color = OnSurface,
                    modifier = Modifier.padding(top = 12.dp)
                )
                Text(
                    text = stringResource(R.string.profile_config_reminders_body),
                    style = BodyMd.copy(fontSize = 14.sp),
                    color = GlassLabel.copy(alpha = 0.9f),
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        GlassSurface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.profile_config_reminders_toggle),
                        style = BodyMd.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                        color = OnSurface
                    )
                    Text(
                        text = if (enabled) {
                            stringResource(R.string.profile_config_reminders_on)
                        } else {
                            stringResource(R.string.profile_config_reminders_off)
                        },
                        style = BodyMd.copy(fontSize = 12.sp),
                        color = GlassLabel.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                Switch(
                    checked = enabled,
                    onCheckedChange = onEnabledChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = OnSurface,
                        checkedTrackColor = Color.White.copy(alpha = 0.28f),
                        uncheckedThumbColor = OnSurfaceVariant,
                        uncheckedTrackColor = OnSurfaceVariant.copy(alpha = 0.25f)
                    )
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        PrimarySheetButton(
            label = stringResource(R.string.profile_config_done),
            enabled = true,
            onClick = onDismiss
        )
    }
}

@Composable
private fun TrainingPrefsSheet(
    categoryLabel: String,
    intensityLabel: String,
    durationLabel: String,
    onDismiss: () -> Unit,
    onOpenCategories: () -> Unit,
    onOpenIntensity: () -> Unit,
    onOpenDuration: () -> Unit
) {
    ConfigSheetScaffold(
        title = stringResource(R.string.profile_training_prefs_title),
        subtitle = stringResource(R.string.profile_training_prefs_subtitle),
        onDismiss = onDismiss
    ) {
        GlassSurface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                TrainingPrefRow(
                    title = stringResource(R.string.profile_config_fitness_title),
                    subtitle = if (categoryLabel == EmptyPrefLabel) {
                        stringResource(R.string.profile_config_fitness_row_hint)
                    } else {
                        categoryLabel
                    },
                    icon = SetupStepIcons.category("strength"),
                    onClick = onOpenCategories
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                        .height(1.dp)
                        .background(Color.White.copy(alpha = 0.07f))
                )
                TrainingPrefRow(
                    title = stringResource(R.string.profile_config_activity_title),
                    subtitle = if (intensityLabel == EmptyPrefLabel) {
                        stringResource(R.string.profile_config_activity_row_hint)
                    } else {
                        intensityLabel
                    },
                    icon = SetupStepIcons.intensity("moderate"),
                    onClick = onOpenIntensity
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                        .height(1.dp)
                        .background(Color.White.copy(alpha = 0.07f))
                )
                TrainingPrefRow(
                    title = stringResource(R.string.profile_config_location_title),
                    subtitle = if (durationLabel == EmptyPrefLabel) {
                        stringResource(R.string.profile_config_location_row_hint)
                    } else {
                        durationLabel
                    },
                    icon = SetupStepIcons.durationPref("medium"),
                    onClick = onOpenDuration
                )
            }
        }
    }
}

@Composable
private fun TrainingPrefRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = OnSurface, modifier = Modifier.size(20.dp))
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                title,
                style = BodyMd.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                color = OnSurface
            )
            Text(
                subtitle,
                style = BodyMd.copy(fontSize = 12.sp),
                color = GlassLabel.copy(alpha = 0.75f),
                modifier = Modifier.padding(top = 2.dp)
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

@Composable
private fun UnitsSheet(
    metric: Boolean,
    onDismiss: () -> Unit,
    onSelect: (Boolean) -> Unit
) {
    ConfigSheetScaffold(
        title = stringResource(R.string.profile_config_units_title),
        subtitle = stringResource(R.string.profile_config_units_subtitle),
        onDismiss = onDismiss
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            GlassSurface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                onClick = { onSelect(true) }
            ) {
                UnitOptionRow(
                    title = stringResource(R.string.profile_config_units_metric),
                    examples = stringResource(R.string.profile_config_units_metric_examples),
                    selected = metric
                )
            }
            GlassSurface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                onClick = { onSelect(false) }
            ) {
                UnitOptionRow(
                    title = stringResource(R.string.profile_config_units_imperial),
                    examples = stringResource(R.string.profile_config_units_imperial_examples),
                    selected = !metric
                )
            }
        }
    }
}

@Composable
private fun UnitOptionRow(
    title: String,
    examples: String,
    selected: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = BodyMd.copy(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
                color = OnSurface
            )
            Text(
                text = examples,
                style = BodyMd.copy(fontSize = 13.sp),
                color = GlassLabel.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        if (selected) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = OnSurface,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun MultiChoiceSheet(
    title: String,
    subtitle: String,
    choices: List<ProfileConfigOptions.Choice>,
    selectedKeys: Set<String>,
    iconFor: (String) -> androidx.compose.ui.graphics.vector.ImageVector,
    multi: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (Set<String>) -> Unit
) {
    var draft by remember(selectedKeys) { mutableStateOf(selectedKeys) }

    ConfigSheetScaffold(title = title, subtitle = subtitle, onDismiss = onDismiss) {
        GlassSurface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 4.dp)
            ) {
                choices.forEachIndexed { index, choice ->
                    val selected = choice.key in draft
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                draft = if (multi) {
                                    if (selected) draft - choice.key else draft + choice.key
                                } else {
                                    setOf(choice.key)
                                }
                            }
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.08f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = iconFor(choice.key),
                                contentDescription = null,
                                tint = OnSurface,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 12.dp)
                        ) {
                            Text(
                                text = stringResource(choice.titleRes),
                                style = BodyMd.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                                color = OnSurface
                            )
                            Text(
                                text = stringResource(choice.subtitleRes),
                                style = BodyMd.copy(fontSize = 12.sp),
                                color = GlassLabel.copy(alpha = 0.8f),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                        if (selected) {
                            Icon(
                                Icons.Default.Check,
                                null,
                                tint = OnSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    if (index < choices.lastIndex) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp)
                                .height(1.dp)
                                .background(Color.White.copy(alpha = 0.07f))
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        PrimarySheetButton(
            label = stringResource(R.string.profile_config_apply),
            enabled = draft.isNotEmpty(),
            onClick = { onConfirm(draft) }
        )
    }
}

@Composable
private fun WeightGoalSheet(
    weightGoalKg: Float?,
    unitsMetric: Boolean,
    onDismiss: () -> Unit,
    onSave: (Float?) -> Unit
) {
    var draft by remember(weightGoalKg, unitsMetric) {
        mutableStateOf(
            weightGoalKg?.let {
                if (unitsMetric) "%.1f".format(it) else "%.1f".format(it * 2.20462f)
            }.orEmpty()
        )
    }
    val unit = if (unitsMetric) "kg" else "lb"
    val parsedRaw = draft.replace(',', '.').toFloatOrNull()
    val parsedKg = parsedRaw?.let { if (unitsMetric) it else it / 2.20462f }
    val valid = draft.isBlank() || (parsedKg != null && parsedKg in 30f..300f)

    ConfigSheetScaffold(
        title = stringResource(R.string.profile_config_weight_title),
        subtitle = stringResource(R.string.profile_config_weight_subtitle),
        onDismiss = onDismiss
    ) {
        GlassSurface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = stringResource(R.string.analysis_weight_goal_field, unit),
                    style = BodyMd.copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                    color = GlassLabel.copy(alpha = 0.85f)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    BasicTextField(
                        value = draft,
                        onValueChange = {
                            draft = it.filter { ch -> ch.isDigit() || ch == '.' || ch == ',' }
                        },
                        singleLine = true,
                        textStyle = BodyMd.copy(fontSize = 22.sp, color = OnSurface, fontWeight = FontWeight.Bold),
                        cursorBrush = SolidColor(OnSurface),
                        modifier = Modifier.fillMaxWidth(),
                        decorationBox = { inner ->
                            if (draft.isEmpty()) {
                                Text(
                                    stringResource(R.string.profile_config_weight_placeholder),
                                    color = GlassLabel.copy(alpha = 0.45f),
                                    style = BodyMd.copy(fontSize = 22.sp)
                                )
                            }
                            inner()
                        }
                    )
                }
                Text(
                    text = stringResource(R.string.profile_config_weight_hint),
                    style = BodyMd.copy(fontSize = 12.sp),
                    color = GlassLabel.copy(alpha = 0.75f),
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        if (weightGoalKg != null) {
            GlassSurface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                onClick = { onSave(null) }
            ) {
                Text(
                    text = stringResource(R.string.profile_config_weight_clear),
                    style = BodyMd.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                    color = OnSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp)
                )
            }
            Spacer(Modifier.height(12.dp))
        }
        PrimarySheetButton(
            label = stringResource(R.string.profile_config_apply),
            enabled = valid && draft.isNotBlank(),
            onClick = { onSave(parsedKg) }
        )
    }
}

@Composable
private fun WeeklyGoalSheet(
    target: Int,
    completed: Int,
    onDismiss: () -> Unit,
    onChange: (Int) -> Unit
) {
    ConfigSheetScaffold(
        title = stringResource(R.string.weekly_goal_title),
        subtitle = stringResource(R.string.profile_config_weekly_subtitle),
        onDismiss = onDismiss
    ) {
        GlassSurface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.profile_config_weekly_progress, completed, target),
                    style = BodyMd.copy(fontSize = 14.sp, fontWeight = FontWeight.Medium),
                    color = GlassLabel,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    repeat(target.coerceAtMost(14)) { index ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(10.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(
                                    if (index < completed) OnSurface.copy(alpha = 0.85f)
                                    else Color.White.copy(alpha = 0.12f)
                                )
                        )
                    }
                }
                Spacer(Modifier.height(22.dp))
                SettingsWeeklyStepper(
                    targetSessions = target,
                    onDecrease = { onChange((target - 1).coerceAtLeast(1)) },
                    onIncrease = { onChange((target + 1).coerceAtMost(14)) }
                )
                Text(
                    text = stringResource(R.string.profile_weekly_sessions_value, target),
                    style = DisplayStat.copy(fontSize = 15.sp),
                    color = OnSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp)
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        PrimarySheetButton(
            label = stringResource(R.string.profile_config_done),
            enabled = true,
            onClick = onDismiss
        )
    }
}

@Composable
private fun ManageDataSheet(
    onDismiss: () -> Unit,
    onResetOnboarding: () -> Unit,
    onResetDaily: () -> Unit,
    onResetWeekly: () -> Unit,
    onClearHistory: () -> Unit
) {
    var pending by remember { mutableStateOf<(() -> Unit)?>(null) }
    var pendingTitle by remember { mutableStateOf("") }
    var pendingBody by remember { mutableStateOf("") }

    if (pending != null) {
        Dialog(
            onDismissRequest = { pending = null },
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
                Icon(Icons.Outlined.WarningAmber, null, tint = OnSurface, modifier = Modifier.size(28.dp))
                Text(
                    pendingTitle,
                    style = HeadlineMd.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                    color = OnSurface,
                    modifier = Modifier.padding(top = 10.dp)
                )
                Text(
                    pendingBody,
                    style = BodyMd.copy(fontSize = 14.sp),
                    color = GlassLabel.copy(alpha = 0.85f),
                    modifier = Modifier.padding(top = 8.dp, bottom = 14.dp)
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { pending = null }) {
                        Text(stringResource(R.string.analysis_dialog_close), color = GlassLabel)
                    }
                    TextButton(
                        onClick = {
                            pending?.invoke()
                            pending = null
                            onDismiss()
                        }
                    ) {
                        Text(
                            stringResource(R.string.profile_config_confirm),
                            color = PrimaryAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    val titleOnboarding = stringResource(R.string.reset_onboarding)
    val bodyOnboarding = stringResource(R.string.settings_reset_onboarding_hint)
    val titleDaily = stringResource(R.string.reset_daily_goals)
    val bodyDaily = stringResource(R.string.settings_reset_daily_hint)
    val titleWeekly = stringResource(R.string.reset_weekly_progress)
    val bodyWeekly = stringResource(R.string.settings_reset_weekly_hint)
    val titleClear = stringResource(R.string.clear_history)
    val bodyClear = stringResource(R.string.settings_clear_history_hint)

    ConfigSheetScaffold(
        title = stringResource(R.string.profile_config_data_title),
        subtitle = stringResource(R.string.profile_config_data_subtitle),
        onDismiss = onDismiss
    ) {
        DataActionCard(
            icon = Icons.Outlined.Replay,
            title = titleOnboarding,
            body = bodyOnboarding,
            onClick = {
                pendingTitle = titleOnboarding
                pendingBody = bodyOnboarding
                pending = onResetOnboarding
            }
        )
        Spacer(Modifier.height(8.dp))
        DataActionCard(
            icon = Icons.Outlined.RestartAlt,
            title = titleDaily,
            body = bodyDaily,
            onClick = {
                pendingTitle = titleDaily
                pendingBody = bodyDaily
                pending = onResetDaily
            }
        )
        Spacer(Modifier.height(8.dp))
        DataActionCard(
            icon = Icons.Outlined.RestartAlt,
            title = titleWeekly,
            body = bodyWeekly,
            onClick = {
                pendingTitle = titleWeekly
                pendingBody = bodyWeekly
                pending = onResetWeekly
            }
        )
        Spacer(Modifier.height(8.dp))
        DataActionCard(
            icon = Icons.Outlined.DeleteOutline,
            title = titleClear,
            body = bodyClear,
            destructive = true,
            onClick = {
                pendingTitle = titleClear
                pendingBody = bodyClear
                pending = onClearHistory
            }
        )
    }
}

@Composable
private fun DataActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    body: String,
    onClick: () -> Unit,
    destructive: Boolean = false
) {
    GlassSurface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = OnSurface, modifier = Modifier.size(20.dp))
            }
            Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                Text(
                    title,
                    style = BodyMd.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                    color = OnSurface
                )
                Text(
                    body,
                    style = BodyMd.copy(fontSize = 12.sp),
                    color = GlassLabel.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun PrimarySheetButton(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (enabled) {
                    Brush.horizontalGradient(listOf(PrimaryAccent, PrimaryContainer))
                } else {
                    Brush.horizontalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.12f),
                            Color.White.copy(alpha = 0.08f)
                        )
                    )
                }
            )
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = BodyMd.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold),
            color = if (enabled) Color.White else GlassLabel.copy(alpha = 0.45f)
        )
    }
}

@Composable
private fun GlassSheetButton(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.10f))
            .border(1.dp, Color.White.copy(alpha = 0.28f), RoundedCornerShape(16.dp))
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = BodyMd.copy(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
            color = if (enabled) OnSurface else GlassLabel.copy(alpha = 0.45f)
        )
    }
}
