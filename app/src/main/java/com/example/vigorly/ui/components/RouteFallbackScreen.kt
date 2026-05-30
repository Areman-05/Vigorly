package com.example.vigorly.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.vigorly.R
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.ButtonText
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.PrimaryContainer

@Composable
fun RouteFallbackScreen(
    title: String,
    message: String,
    onGoBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.ContainerMargin),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Text(title, style = HeadlineMd, color = OnSurface)
        Text(message, style = BodyMd, color = OnSurface, modifier = Modifier.padding(top = Dimens.Sm))
        Button(
            onClick = onGoBack,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.Lg),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = PrimaryContainer)
        ) {
            Text(stringResource(R.string.go_back), style = ButtonText)
        }
    }
}
