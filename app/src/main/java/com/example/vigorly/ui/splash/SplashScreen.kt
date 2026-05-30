package com.example.vigorly.ui.splash

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.vigorly.R
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.navigation.AppDestination
import com.example.vigorly.ui.theme.Background
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.DisplayHero
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer
import com.example.vigorly.ui.theme.RingTrack
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    repository: VigorlyRepository,
    onFinished: (AppDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val pulse = rememberInfiniteTransition(label = "splash_pulse")
    val ringScale by pulse.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Reverse),
        label = "ring_scale"
    )
    val glowAlpha by pulse.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Reverse),
        label = "glow_alpha"
    )

    LaunchedEffect(Unit) {
        repository.preloadAppData()
        delay(3000)
        onFinished(repository.resolveStartDestination())
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                Modifier
                    .size(220.dp)
                    .scale(ringScale),
                contentAlignment = Alignment.Center
            ) {
                Canvas(Modifier.matchParentSize()) {
                    fun drawRing(radiusFraction: Float, progress: Float, color: androidx.compose.ui.graphics.Color) {
                        val radius = size.minDimension / 2f * radiusFraction
                        val topLeft = Offset(size.width / 2 - radius, size.height / 2 - radius)
                        val arcSize = Size(radius * 2, radius * 2)
                        drawArc(
                            color = RingTrack,
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = 14f, cap = StrokeCap.Round)
                        )
                        drawArc(
                            color = color,
                            startAngle = -90f,
                            sweepAngle = 360f * progress,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = 14f, cap = StrokeCap.Round)
                        )
                    }
                    drawRing(0.88f, 0.78f, PrimaryAccent.copy(alpha = glowAlpha))
                    drawRing(0.62f, 0.55f, PrimaryContainer.copy(alpha = glowAlpha))
                    drawRing(0.36f, 0.82f, Primary.copy(alpha = glowAlpha))
                }
                Text(stringResource(R.string.brand_name), style = DisplayHero, color = Primary, modifier = Modifier.alpha(0.95f))
            }
            Text(
                stringResource(R.string.splash_tagline),
                style = LabelCaps,
                color = OnSurfaceVariant,
                modifier = Modifier.padding(top = 24.dp)
            )
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .padding(top = 32.dp)
                    .height(4.dp),
                color = Primary,
                trackColor = RingTrack
            )
            Text(
                stringResource(R.string.splash_loading),
                style = BodyMd,
                color = OnSurfaceVariant,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
}
