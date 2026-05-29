package com.example.vigorly.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.vigorly.R
import com.example.vigorly.ui.components.GlassCard
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.ButtonText
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.DisplayHero
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.OnPrimaryContainer
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.PrimaryContainer

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var page by remember { mutableIntStateOf(0) }
    val pages = listOf(
        stringResource(R.string.onboarding_welcome_title) to stringResource(R.string.onboarding_welcome_body),
        stringResource(R.string.onboarding_goals_title) to stringResource(R.string.onboarding_goals_body),
        stringResource(R.string.onboarding_ready_title) to stringResource(R.string.onboarding_ready_body)
    )
    val (title, body) = pages[page]

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.ContainerMargin),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.brand_name), style = DisplayHero, color = OnSurface)
        Spacer(Modifier.height(Dimens.Xl))
        GlassCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(Dimens.Lg)) {
                Text(title, style = HeadlineMd, color = OnSurface)
                Text(body, style = BodyMd, color = OnSurfaceVariant, modifier = Modifier.padding(top = Dimens.Md))
            }
        }
        Spacer(Modifier.height(Dimens.Xl))
        Button(
            onClick = {
                if (page < pages.lastIndex) page++ else onComplete()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryContainer,
                contentColor = OnPrimaryContainer
            )
        ) {
            Text(
                if (page < pages.lastIndex) stringResource(R.string.onboarding_next) else stringResource(R.string.onboarding_start),
                style = ButtonText
            )
        }
    }
}
