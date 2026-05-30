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
import androidx.compose.ui.unit.dp
import com.example.vigorly.R
import com.example.vigorly.data.model.AuthError
import com.example.vigorly.data.model.AuthResult
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.ui.components.ActivityRingsLogo
import com.example.vigorly.ui.components.AuthGradientBackground
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
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer
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
    var birthDate by remember { mutableStateOf("") }
    var authError by remember { mutableStateOf<AuthError?>(null) }
    val scope = rememberCoroutineScope()

    AuthGradientBackground(modifier) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(Dimens.ContainerMargin),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(Dimens.Md))
            ActivityRingsLogo(size = 140.dp, strokeWidth = 7.dp)
            Text(
                stringResource(R.string.auth_register_title),
                style = DisplayStat.copy(fontWeight = FontWeight.Bold),
                color = PrimaryAccent
            )
            Text(
                stringResource(R.string.auth_register_subtitle),
                style = HeadlineMd,
                color = OnSurface,
                modifier = Modifier.padding(top = Dimens.Xs, bottom = Dimens.Lg)
            )
            GlassCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(Dimens.Lg), verticalArrangement = Arrangement.spacedBy(Dimens.Sm)) {
                    Text(stringResource(R.string.auth_register_form_hint), style = LabelCaps, color = OnSurfaceVariant)
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it; authError = null },
                        label = { Text(stringResource(R.string.auth_username)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = authFieldColors,
                        shape = RoundedCornerShape(14.dp)
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; authError = null },
                        label = { Text(stringResource(R.string.auth_email)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = authFieldColors,
                        shape = RoundedCornerShape(14.dp)
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; authError = null },
                        label = { Text(stringResource(R.string.auth_password)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        colors = authFieldColors,
                        shape = RoundedCornerShape(14.dp)
                    )
                    OutlinedTextField(
                        value = birthDate,
                        onValueChange = { birthDate = it; authError = null },
                        label = { Text(stringResource(R.string.auth_birth_date)) },
                        placeholder = { Text(stringResource(R.string.auth_birth_date_hint)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = authFieldColors,
                        shape = RoundedCornerShape(14.dp)
                    )
                    authError?.let { Text(authErrorMessage(it), style = BodyMd, color = PrimaryAccent) }
                    Button(
                        onClick = {
                            scope.launch {
                                when (val result = repository.register(email, password, username, birthDate)) {
                                    is AuthResult.Success -> onRegisterSuccess()
                                    is AuthResult.Error -> authError = result.messageKey
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryContainer,
                            contentColor = OnPrimaryContainer
                        )
                    ) {
                        Text(stringResource(R.string.auth_register_button), style = ButtonText)
                    }
                }
            }
            Spacer(Modifier.height(Dimens.Md))
            TextButton(onClick = onNavigateLogin) {
                Text(stringResource(R.string.auth_has_account), style = BodyMd, color = OnSurfaceVariant)
                Text(" ", style = BodyMd)
                Text(stringResource(R.string.auth_login_link), style = ButtonText, color = Primary)
            }
        }
    }
}
