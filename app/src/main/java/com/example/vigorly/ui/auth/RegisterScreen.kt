package com.example.vigorly.ui.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.core.testing.VigorlyTestTags
import com.example.vigorly.data.model.AuthError
import com.example.vigorly.data.model.AuthResult
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.ui.components.AuthGradientBackground
import com.example.vigorly.ui.components.VigorlyBrandMark
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.ButtonText
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.GlassLabel
import com.example.vigorly.ui.theme.HeadlineLgMobile
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.util.AuthValidator
import com.example.vigorly.util.BirthDateFormatter
import com.example.vigorly.util.BirthDateVisualTransformation
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    repository: VigorlyRepository,
    onRegisterSuccess: () -> Unit,
    onNavigateLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var passwordFocused by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }
    var birthDateDigits by remember { mutableStateOf("") }
    var authError by remember { mutableStateOf<AuthError?>(null) }
    var submitting by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val emailFocus = remember { FocusRequester() }
    val passwordFocus = remember { FocusRequester() }
    val birthFocus = remember { FocusRequester() }
    val birthDateFormatted = BirthDateFormatter.toFormatted(birthDateDigits)
    val birthDateComplete = birthDateDigits.length == 8
    val passwordChecks = AuthValidator.passwordRequirementsMet(password)
    val usernameError = authError == AuthError.INVALID_USERNAME ||
        authError == AuthError.USERNAME_ALREADY_EXISTS
    val emailError = authError == AuthError.INVALID_EMAIL ||
        authError == AuthError.EMAIL_ALREADY_EXISTS
    val passwordWeak = authError == AuthError.PASSWORD_WEAK
    val showPasswordRules = passwordFocused || password.isNotEmpty() || passwordWeak

    fun submit() {
        if (submitting) return
        focusManager.clearFocus()
        val error = AuthValidator.validateRegistration(
            email, password, username, birthDateFormatted
        )
        if (error != null) {
            authError = error
            return
        }
        scope.launch {
            submitting = true
            when (val result = repository.register(
                email, password, username, birthDateFormatted
            )) {
                is AuthResult.Success -> onRegisterSuccess()
                is AuthResult.Error -> authError = result.messageKey
            }
            submitting = false
        }
    }

    AuthGradientBackground(modifier) {
        Column(
            Modifier
                .fillMaxSize()
                .testTag(VigorlyTestTags.REGISTER)
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp, bottom = Dimens.Lg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            VigorlyBrandMark(compact = true)
            Text(
                text = stringResource(R.string.auth_register_title),
                style = HeadlineLgMobile.copy(letterSpacing = (-0.8).sp),
                color = OnSurface,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            )
            Text(
                text = stringResource(R.string.auth_register_subtitle),
                style = BodyMd.copy(fontSize = 15.sp, lineHeight = 22.sp),
                color = GlassLabel.copy(alpha = 0.72f),
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 24.dp)
            )

            AuthGlassInput(
                value = username,
                onValueChange = { username = it; authError = null },
                label = stringResource(R.string.auth_username),
                placeholder = stringResource(R.string.auth_username),
                isError = usernameError,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrectEnabled = false,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { emailFocus.requestFocus() })
            )
            if (usernameError) {
                FieldError(authErrorMessage(authError!!))
            }

            Spacer(Modifier.height(16.dp))
            AuthGlassInput(
                value = email,
                onValueChange = { email = it; authError = null },
                label = stringResource(R.string.auth_email),
                placeholder = stringResource(R.string.auth_email),
                isError = emailError,
                modifier = Modifier.focusRequester(emailFocus),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrectEnabled = false,
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { passwordFocus.requestFocus() })
            )
            if (emailError) {
                FieldError(authErrorMessage(authError!!))
            }

            Spacer(Modifier.height(20.dp))
            AuthGlassInput(
                value = password,
                onValueChange = { password = it; authError = null },
                label = stringResource(R.string.auth_password),
                placeholder = stringResource(R.string.auth_password),
                isError = passwordWeak,
                modifier = Modifier.focusRequester(passwordFocus),
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { birthFocus.requestFocus() }),
                onFocusChange = { passwordFocused = it },
                trailing = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) {
                                Icons.Outlined.VisibilityOff
                            } else {
                                Icons.Outlined.Visibility
                            },
                            contentDescription = stringResource(
                                if (passwordVisible) R.string.auth_password_hide
                                else R.string.auth_password_show
                            ),
                            tint = GlassLabel.copy(alpha = 0.8f)
                        )
                    }
                }
            )
            Spacer(Modifier.height(10.dp))
            CompactPasswordHint(
                checks = passwordChecks,
                showError = passwordWeak,
                visible = showPasswordRules
            )

            Spacer(Modifier.height(20.dp))
            AuthGlassInput(
                value = birthDateDigits,
                onValueChange = {
                    birthDateDigits = BirthDateFormatter.digitsOnly(it)
                    authError = null
                },
                label = stringResource(R.string.auth_birth_date),
                placeholder = stringResource(R.string.auth_birth_date_hint),
                isError = authError == AuthError.INVALID_BIRTH_DATE,
                modifier = Modifier.focusRequester(birthFocus),
                visualTransformation = BirthDateVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { submit() })
            )
            if (authError == AuthError.INVALID_BIRTH_DATE) {
                FieldError(authErrorMessage(AuthError.INVALID_BIRTH_DATE))
            } else if (birthDateDigits.isNotEmpty() && !birthDateComplete) {
                Text(
                    stringResource(R.string.auth_birth_date_incomplete),
                    style = BodyMd.copy(fontSize = 13.sp),
                    color = GlassLabel.copy(alpha = 0.7f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, start = 4.dp)
                )
            }
            authError?.let { error ->
                if (error !in setOf(
                        AuthError.INVALID_EMAIL,
                        AuthError.EMAIL_ALREADY_EXISTS,
                        AuthError.INVALID_USERNAME,
                        AuthError.USERNAME_ALREADY_EXISTS,
                        AuthError.PASSWORD_WEAK,
                        AuthError.INVALID_BIRTH_DATE
                    )
                ) {
                    FieldError(authErrorMessage(error))
                }
            }

            Button(
                onClick = { submit() },
                enabled = !submitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp)
                    .height(56.dp),
                shape = AuthFieldShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryAccent,
                    contentColor = OnSurface
                )
            ) {
                Text(stringResource(R.string.auth_register_button), style = ButtonText)
            }

            Spacer(Modifier.height(10.dp))
            TextButton(onClick = onNavigateLogin) {
                Text(
                    stringResource(R.string.auth_has_account),
                    style = BodyMd,
                    color = GlassLabel.copy(alpha = 0.7f)
                )
                Text("  ", style = BodyMd)
                Text(
                    stringResource(R.string.auth_login_link),
                    style = ButtonText.copy(fontWeight = FontWeight.SemiBold),
                    color = PrimaryAccent
                )
            }
        }
    }
}

@Composable
private fun FieldError(message: String) {
    Text(
        text = message,
        style = BodyMd.copy(fontSize = 13.sp),
        color = PrimaryAccent,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 4.dp)
    )
}
