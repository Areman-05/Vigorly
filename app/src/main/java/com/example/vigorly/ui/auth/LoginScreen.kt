package com.example.vigorly.ui.auth

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.auth.GoogleSignInCancelledException
import com.example.vigorly.auth.GoogleSignInConfigException
import com.example.vigorly.auth.GoogleSignInHelper
import com.example.vigorly.auth.GoogleSignInNoAccountException
import com.example.vigorly.auth.GoogleSignInNotConfiguredException
import com.example.vigorly.core.testing.VigorlyTestTags
import com.example.vigorly.data.model.AuthError
import com.example.vigorly.data.model.AuthResult
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.ui.components.AuthGradientBackground
import com.example.vigorly.ui.components.GlassSurface
import com.example.vigorly.ui.components.VigorlyBrandMark
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.ButtonText
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.GlassLabel
import com.example.vigorly.ui.theme.HeadlineLgMobile
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.RingTrack
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    repository: VigorlyRepository,
    onLoginSuccess: (needsSetup: Boolean) -> Unit,
    onNavigateRegister: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var authError by remember { mutableStateOf<AuthError?>(null) }
    var isGoogleLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val activity = LocalContext.current.findActivity()
    val webClientId = stringResource(R.string.google_web_client_id)

    AuthGradientBackground(modifier) {
        Column(
            Modifier
                .fillMaxSize()
                .testTag(VigorlyTestTags.LOGIN)
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 28.dp, bottom = Dimens.Lg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            VigorlyBrandMark(compact = true)
            Text(
                text = stringResource(R.string.auth_login_title),
                style = HeadlineLgMobile.copy(letterSpacing = (-0.8).sp),
                color = OnSurface,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp)
            )
            Text(
                text = stringResource(R.string.auth_login_subtitle),
                style = BodyMd.copy(fontSize = 15.sp, lineHeight = 22.sp),
                color = GlassLabel.copy(alpha = 0.72f),
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 28.dp)
            )

            AuthGlassInput(
                value = email,
                onValueChange = { email = it; authError = null },
                label = stringResource(R.string.auth_email),
                placeholder = stringResource(R.string.auth_email),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                testTag = VigorlyTestTags.LOGIN_EMAIL
            )
            Spacer(Modifier.height(16.dp))
            AuthGlassInput(
                value = password,
                onValueChange = { password = it; authError = null },
                label = stringResource(R.string.auth_password),
                placeholder = stringResource(R.string.auth_password),
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                testTag = VigorlyTestTags.LOGIN_PASSWORD,
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
            authError?.let {
                Text(
                    authErrorMessage(it),
                    style = BodyMd.copy(fontSize = 13.sp),
                    color = PrimaryAccent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                )
            }

            Button(
                onClick = {
                    scope.launch {
                        when (val result = repository.login(email, password)) {
                            is AuthResult.Success -> onLoginSuccess(result.needsSetup)
                            is AuthResult.Error -> authError = result.messageKey
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp)
                    .height(56.dp)
                    .testTag(VigorlyTestTags.LOGIN_SUBMIT),
                shape = AuthFieldShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryAccent,
                    contentColor = OnSurface
                )
            ) {
                Text(stringResource(R.string.auth_login_button), style = ButtonText)
            }

            AuthDivider(Modifier.padding(vertical = 20.dp))

            GlassSurface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = AuthFieldShape,
                onClick = {
                    if (isGoogleLoading) return@GlassSurface
                    val host = activity ?: return@GlassSurface
                    scope.launch {
                        isGoogleLoading = true
                        authError = null
                        GoogleSignInHelper(host).signIn(webClientId)
                            .onSuccess { info ->
                                when (val result = repository.loginWithGoogle(info)) {
                                    is AuthResult.Success ->
                                        onLoginSuccess(result.needsSetup)
                                    is AuthResult.Error -> authError = result.messageKey
                                }
                            }
                            .onFailure { error ->
                                authError = when (error) {
                                    is GoogleSignInCancelledException -> AuthError.GOOGLE_SIGN_IN_CANCELLED
                                    is GoogleSignInNotConfiguredException -> AuthError.GOOGLE_NOT_CONFIGURED
                                    is GoogleSignInNoAccountException -> AuthError.GOOGLE_NO_ACCOUNT
                                    is GoogleSignInConfigException -> AuthError.GOOGLE_CONFIG_ERROR
                                    else -> AuthError.GOOGLE_SIGN_IN_FAILED
                                }
                            }
                        isGoogleLoading = false
                    }
                }
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (isGoogleLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp,
                            color = PrimaryAccent
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_google),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = androidx.compose.ui.graphics.Color.Unspecified
                            )
                            Text(
                                stringResource(R.string.auth_google_button),
                                style = ButtonText,
                                color = OnSurface,
                                modifier = Modifier.padding(start = Dimens.Sm)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(18.dp))
            TextButton(onClick = onNavigateRegister) {
                Text(
                    stringResource(R.string.auth_no_account),
                    style = BodyMd,
                    color = GlassLabel.copy(alpha = 0.7f)
                )
                Text("  ", style = BodyMd)
                Text(
                    stringResource(R.string.auth_register_link),
                    style = ButtonText.copy(fontWeight = FontWeight.SemiBold),
                    color = PrimaryAccent
                )
            }
        }
    }
}

@Composable
internal fun AuthDivider(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = RingTrack.copy(alpha = 0.45f)
        )
        Text(
            stringResource(R.string.auth_or_divider),
            style = BodyMd.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium),
            color = GlassLabel.copy(alpha = 0.5f),
            modifier = Modifier.padding(horizontal = Dimens.Md)
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = RingTrack.copy(alpha = 0.45f)
        )
    }
}

@Composable
fun authErrorMessage(error: AuthError): String = when (error) {
    AuthError.INVALID_CREDENTIALS -> stringResource(R.string.auth_error_invalid)
    AuthError.EMAIL_ALREADY_EXISTS -> stringResource(R.string.auth_error_exists)
    AuthError.USERNAME_ALREADY_EXISTS -> stringResource(R.string.auth_error_username_exists)
    AuthError.FIELDS_REQUIRED -> stringResource(R.string.auth_error_fields)
    AuthError.PASSWORD_TOO_SHORT -> stringResource(R.string.auth_error_password)
    AuthError.PASSWORD_WEAK -> stringResource(R.string.auth_error_password_weak)
    AuthError.INVALID_EMAIL -> stringResource(R.string.auth_error_invalid_email)
    AuthError.INVALID_USERNAME -> stringResource(R.string.auth_error_invalid_username)
    AuthError.INVALID_BIRTH_DATE -> stringResource(R.string.auth_error_invalid_birth_date)
    AuthError.GOOGLE_SIGN_IN_FAILED -> stringResource(R.string.auth_error_google_failed)
    AuthError.GOOGLE_SIGN_IN_CANCELLED -> stringResource(R.string.auth_error_google_cancelled)
    AuthError.GOOGLE_NOT_CONFIGURED -> stringResource(R.string.auth_error_google_not_configured)
    AuthError.GOOGLE_NO_ACCOUNT -> stringResource(R.string.auth_error_google_no_account)
    AuthError.GOOGLE_CONFIG_ERROR -> stringResource(R.string.auth_error_google_config)
}

private fun Context.findActivity(): Activity? {
    var ctx: Context? = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return ctx as? Activity
}
