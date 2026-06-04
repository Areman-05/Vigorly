package com.example.vigorly.ui.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.vigorly.R
import com.example.vigorly.core.testing.UiTestEnvironment
import com.example.vigorly.core.testing.VigorlyTestTags
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.navigation.AppDestination
import com.example.vigorly.ui.components.ActivityRingsLogo
import com.example.vigorly.ui.components.SplashGradientBackground
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.DisplayHero
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer
import com.example.vigorly.ui.theme.RingTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun SplashScreen(
    repository: VigorlyRepository,
    onFinished: (AppDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    var loadProgress by remember { mutableFloatStateOf(0f) }
    val progressAnim by animateFloatAsState(loadProgress, tween(2800, easing = FastOutSlowInEasing), label = "load")

    LaunchedEffect(Unit) {
        repository.preloadAppData()
        val destination = repository.resolveStartDestination()
        if (UiTestEnvironment.isInstrumentedTest) {
            withContext(Dispatchers.Main.immediate) { onFinished(destination) }
            return@LaunchedEffect
        }
        val stepDelay = 900L
        val finishDelay = 700L
        loadProgress = 0.35f
        delay(stepDelay)
        loadProgress = 0.72f
        delay(stepDelay)
        loadProgress = 1f
        delay(finishDelay)
        withContext(Dispatchers.Main.immediate) { onFinished(destination) }
    }

    Box(modifier = modifier.fillMaxSize().testTag(VigorlyTestTags.SPLASH)) {
        SplashGradientBackground()
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            ActivityRingsLogo(
                size = 280.dp,
                animate = !UiTestEnvironment.disableContinuousUiMotion
            )
            Text(
                stringResource(R.string.brand_name),
                style = DisplayHero.copy(fontWeight = FontWeight.Black),
                color = OnSurface,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                stringResource(R.string.splash_tagline),
                style = LabelCaps,
                color = PrimaryAccent.copy(alpha = 0.85f),
                modifier = Modifier.padding(top = 8.dp)
            )
            Box(
                Modifier
                    .padding(top = 40.dp)
                    .fillMaxWidth(0.62f)
                    .height(6.dp)
            ) {
                Canvas(Modifier.matchParentSize()) {
                    drawRoundRect(RingTrack, cornerRadius = CornerRadius(8f))
                    drawRoundRect(
                        brush = Brush.horizontalGradient(listOf(PrimaryContainer, PrimaryAccent)),
                        size = Size(size.width * progressAnim, size.height),
                        cornerRadius = CornerRadius(8f)
                    )
                }
            }
            Text(
                stringResource(R.string.splash_loading),
                style = BodyMd,
                color = OnSurfaceVariant,
                modifier = Modifier.padding(top = 14.dp).alpha(0.85f + progressAnim * 0.15f)
            )
        }
    }
}
