package com.example.vigorly.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.vigorly.R
import com.example.vigorly.data.model.AuthError
import com.example.vigorly.data.model.AuthResult
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.ui.components.AuthGradientBackground
import com.example.vigorly.ui.components.AuthHeroVisual
import com.example.vigorly.ui.theme.BodyLg
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
    var authError by remember { mutableStateOf<AuthError?>(null) }
    val scope = rememberCoroutineScope()

    AuthGradientBackground(modifier) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(Dimens.ContainerMargin),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AuthHeroVisual(size = 200.dp)
            Text(
                stringResource(R.string.brand_name),
                style = DisplayStat.copy(fontWeight = FontWeight.Black),
                color = PrimaryAccent,
                modifier = Modifier.padding(top = Dimens.Md)
            )
            Text(
                stringResource(R.string.auth_login_title),
                style = HeadlineMd,
                color = OnSurface,
                modifier = Modifier.padding(top = Dimens.Xs)
            )
            Text(
                stringResource(R.string.auth_login_subtitle),
                style = BodyLg,
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
                    value = email,
                    onValueChange = { email = it; authError = null },
                    label = { Text(stringResource(R.string.auth_email)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
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
                    colors = authFieldColors,
                    shape = RoundedCornerShape(16.dp)
                )
                authError?.let {
                    Text(
                        authErrorMessage(it),
                        style = BodyMd,
                        color = PrimaryAccent,
                        modifier = Modifier.padding(start = Dimens.Xs)
                    )
                }
                Button(
                    onClick = {
                        scope.launch {
                            when (val result = repository.login(email, password)) {
                                is AuthResult.Success -> onLoginSuccess(!repository.onboardingCompleted.value)
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
                    Text(stringResource(R.string.auth_login_button), style = ButtonText)
                }
            }
            Spacer(Modifier.height(Dimens.Lg))
            TextButton(onClick = onNavigateRegister) {
                Text(stringResource(R.string.auth_no_account), style = BodyMd, color = OnSurfaceVariant)
                Text(" ", style = BodyMd)
                Text(stringResource(R.string.auth_register_link), style = ButtonText, color = Primary)
            }
        }
    }
}

@Composable
fun authErrorMessage(error: AuthError): String = when (error) {
    AuthError.INVALID_CREDENTIALS -> stringResource(R.string.auth_error_invalid)
    AuthError.EMAIL_ALREADY_EXISTS -> stringResource(R.string.auth_error_exists)
    AuthError.FIELDS_REQUIRED -> stringResource(R.string.auth_error_fields)
    AuthError.PASSWORD_TOO_SHORT -> stringResource(R.string.auth_error_password)
    AuthError.PASSWORD_WEAK -> stringResource(R.string.auth_error_password_weak)
    AuthError.INVALID_EMAIL -> stringResource(R.string.auth_error_invalid_email)
    AuthError.INVALID_USERNAME -> stringResource(R.string.auth_error_invalid_username)
    AuthError.INVALID_BIRTH_DATE -> stringResource(R.string.auth_error_invalid_birth_date)
}
