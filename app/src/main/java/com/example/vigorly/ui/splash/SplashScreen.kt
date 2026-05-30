package com.example.vigorly.ui.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.vigorly.R
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.navigation.AppDestination
import com.example.vigorly.ui.components.SplashGradientBackground
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.DisplayHero
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
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
    var loadProgress by remember { mutableFloatStateOf(0f) }
    val progressAnim by animateFloatAsState(loadProgress, tween(2800, easing = FastOutSlowInEasing), label = "load")
    val pulse = rememberInfiniteTransition(label = "splash")
    val ringRotation by pulse.animateFloat(
        0f, 360f,
        infiniteRepeatable(tween(8000, easing = LinearEasing)),
        label = "ring_rot"
    )
    val breathe by pulse.animateFloat(
        0.96f, 1.04f,
        infiniteRepeatable(tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "breathe"
    )
    val shimmer by pulse.animateFloat(
        0.4f, 1f,
        infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "shimmer"
    )

    LaunchedEffect(Unit) {
        repository.preloadAppData()
        loadProgress = 0.35f
        delay(900)
        loadProgress = 0.72f
        delay(900)
        loadProgress = 1f
        delay(700)
        onFinished(repository.resolveStartDestination())
    }

    Box(modifier = modifier.fillMaxSize()) {
        SplashGradientBackground()
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            Box(
                Modifier
                    .size(280.dp)
                    .scale(breathe),
                contentAlignment = Alignment.Center
            ) {
                Canvas(Modifier.matchParentSize()) {
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    val base = size.minDimension / 2f
                    listOf(
                        Triple(0.92f, 0.82f, PrimaryAccent.copy(shimmer * 0.9f)),
                        Triple(0.68f, 0.58f, PrimaryContainer.copy(shimmer * 0.85f)),
                        Triple(0.44f, 0.74f, Primary.copy(shimmer))
                    ).forEachIndexed { i, (frac, prog, color) ->
                        val r = base * frac
                        val start = -90f + ringRotation * (if (i % 2 == 0) 1f else -0.6f)
                        drawArc(
                            RingTrack.copy(alpha = 0.45f),
                            start, 360f, false,
                            topLeft = Offset(cx - r, cy - r),
                            size = androidx.compose.ui.geometry.Size(r * 2, r * 2),
                            style = Stroke(12f, cap = StrokeCap.Round)
                        )
                        drawArc(
                            color, start, 360f * prog, false,
                            topLeft = Offset(cx - r, cy - r),
                            size = androidx.compose.ui.geometry.Size(r * 2, r * 2),
                            style = Stroke(12f, cap = StrokeCap.Round)
                        )
                    }
                }
            }
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
                    drawRoundRect(RingTrack, cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f))
                    drawRoundRect(
                        brush = Brush.horizontalGradient(listOf(PrimaryContainer, PrimaryAccent)),
                        size = androidx.compose.ui.geometry.Size(size.width * progressAnim, size.height),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f)
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
