package com.example.vigorly.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.example.vigorly.util.BirthDateVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.vigorly.R
import com.example.vigorly.core.testing.VigorlyTestTags
import com.example.vigorly.data.model.AuthError
import com.example.vigorly.data.model.AuthResult
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.ui.components.AuthGradientBackground
import com.example.vigorly.ui.components.AuthRegisterVisual
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.ButtonText
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.DisplayStat
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.OnPrimaryContainer
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer
import com.example.vigorly.util.AuthValidator
import com.example.vigorly.util.BirthDateFormatter
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
    var username by remember { mutableStateOf("") }
    var birthDateDigits by remember { mutableStateOf("") }
    var authError by remember { mutableStateOf<AuthError?>(null) }
    val scope = rememberCoroutineScope()
    val birthDateFormatted = BirthDateFormatter.toFormatted(birthDateDigits)
    val birthDateComplete = birthDateDigits.length == 8
    val passwordChecks = AuthValidator.passwordRequirementsMet(password)

    AuthGradientBackground(modifier) {
        Column(
            Modifier
                .fillMaxSize()
                .testTag(VigorlyTestTags.REGISTER)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(Dimens.ContainerMargin),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(Dimens.Md))
            AuthRegisterVisual(
                modifier = Modifier.padding(bottom = Dimens.Sm),
                size = 104.dp
            )
            Text(
                stringResource(R.string.brand_name),
                style = DisplayStat.copy(fontWeight = FontWeight.Black),
                color = PrimaryAccent,
                modifier = Modifier.padding(top = Dimens.Md)
            )
            Text(
                stringResource(R.string.auth_register_title),
                style = HeadlineMd,
                color = OnSurface,
                modifier = Modifier.padding(top = Dimens.Xs)
            )
            Text(
                stringResource(R.string.auth_register_subtitle),
                style = BodyMd,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.Sm, bottom = Dimens.Xl)
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Dimens.Md)
            ) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it; authError = null },
                    label = { Text(stringResource(R.string.auth_username)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = authError == AuthError.INVALID_USERNAME,
                    supportingText = if (authError == AuthError.INVALID_USERNAME) {
                        { Text(authErrorMessage(AuthError.INVALID_USERNAME)) }
                    } else null,
                    colors = authFieldColors,
                    shape = RoundedCornerShape(16.dp)
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; authError = null },
                    label = { Text(stringResource(R.string.auth_email)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = authError == AuthError.INVALID_EMAIL,
                    supportingText = if (authError == AuthError.INVALID_EMAIL) {
                        { Text(authErrorMessage(AuthError.INVALID_EMAIL)) }
                    } else null,
                    colors = authFieldColors,
                    shape = RoundedCornerShape(16.dp)
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; authError = null },
                    label = { Text(stringResource(R.string.auth_password)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    isError = authError == AuthError.PASSWORD_WEAK,
                    supportingText = {
                        CompactPasswordHint(
                            checks = passwordChecks,
                            showError = authError == AuthError.PASSWORD_WEAK
                        )
                    },
                    colors = authFieldColors,
                    shape = RoundedCornerShape(16.dp)
                )
                OutlinedTextField(
                    value = birthDateDigits,
                    onValueChange = {
                        birthDateDigits = BirthDateFormatter.digitsOnly(it)
                        authError = null
                    },
                    label = { Text(stringResource(R.string.auth_birth_date)) },
                    placeholder = { Text(stringResource(R.string.auth_birth_date_hint)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = BirthDateVisualTransformation(),
                    isError = authError == AuthError.INVALID_BIRTH_DATE,
                    supportingText = when {
                        authError == AuthError.INVALID_BIRTH_DATE -> {
                            { Text(authErrorMessage(AuthError.INVALID_BIRTH_DATE)) }
                        }
                        birthDateDigits.isNotEmpty() && !birthDateComplete -> {
                            { Text(stringResource(R.string.auth_birth_date_incomplete)) }
                        }
                        else -> null
                    },
                    colors = authFieldColors,
                    shape = RoundedCornerShape(16.dp)
                )
                authError?.let { error ->
                    if (error !in setOf(
                            AuthError.INVALID_EMAIL,
                            AuthError.INVALID_USERNAME,
                            AuthError.PASSWORD_WEAK,
                            AuthError.INVALID_BIRTH_DATE
                        )
                    ) {
                        Text(
                            authErrorMessage(error),
                            style = BodyMd,
                            color = PrimaryAccent,
                            modifier = Modifier.padding(start = Dimens.Xs)
                        )
                    }
                }
                Button(
                    onClick = {
                        val error = AuthValidator.validateRegistration(
                            email, password, username, birthDateFormatted
                        )
                        if (error != null) {
                            authError = error
                            return@Button
                        }
                        scope.launch {
                            when (val result = repository.register(
                                email, password, username, birthDateFormatted
                            )) {
                                is AuthResult.Success -> onRegisterSuccess()
                                is AuthResult.Error -> authError = result.messageKey
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .padding(top = Dimens.Xs),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryContainer,
                        contentColor = OnPrimaryContainer
                    )
                ) {
                    Text(stringResource(R.string.auth_register_button), style = ButtonText)
                }
            }
            Spacer(Modifier.height(Dimens.Lg))
            TextButton(onClick = onNavigateLogin) {
                Text(stringResource(R.string.auth_has_account), style = BodyMd, color = OnSurfaceVariant)
                Text(" ", style = BodyMd)
                Text(stringResource(R.string.auth_login_link), style = ButtonText, color = Primary)
            }
            Spacer(Modifier.height(Dimens.Md))
        }
    }
}
