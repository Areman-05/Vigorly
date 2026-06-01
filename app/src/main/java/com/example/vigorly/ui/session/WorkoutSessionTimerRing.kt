package com.example.vigorly.ui.session

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.ui.theme.DisplayStat
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer
import com.example.vigorly.ui.theme.RingTrack

enum class SessionTimerMode {
    ACTIVE,
    REST,
    PAUSED
}

@Composable
fun WorkoutSessionTimerRing(
    primaryText: String,
    secondaryText: String,
    progress: Float,
    accent: Color = PrimaryAccent,
    modifier: Modifier = Modifier,
    ringSize: Dp = 240.dp,
    mode: SessionTimerMode = SessionTimerMode.ACTIVE
) {
    val targetProgress = progress.coerceIn(0f, 1f)
    val tickDuration = if (mode == SessionTimerMode.REST) 950 else 650
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(tickDuration, easing = FastOutSlowInEasing),
        label = "sessionRingProgress"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "pausePulse")
    val pausePulse by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pausePulseAlpha"
    )

    val ringColor = when (mode) {
        SessionTimerMode.REST -> PrimaryContainer
        SessionTimerMode.PAUSED -> accent.copy(alpha = 0.55f * pausePulse)
        SessionTimerMode.ACTIVE -> accent
    }
    val glowAlpha = when (mode) {
        SessionTimerMode.REST -> 0.18f
        SessionTimerMode.PAUSED -> 0.1f * pausePulse
        SessionTimerMode.ACTIVE -> 0.2f
    }

    Box(
        modifier = modifier.size(ringSize),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .size(ringSize * 1.12f)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            ringColor.copy(alpha = glowAlpha),
                            Color.Transparent
                        )
                    )
                )
        )
        Canvas(Modifier.fillMaxSize()) {
            val stroke = size.minDimension * 0.055f
            val radius = size.minDimension / 2f * 0.84f
            val topLeft = Offset(size.width / 2 - radius, size.height / 2 - radius)
            val arcSize = Size(radius * 2, radius * 2)
            drawArc(
                color = RingTrack.copy(alpha = 0.38f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
            val sweep = 360f * animatedProgress
            if (sweep > 0.5f) {
                drawArc(
                    color = ringColor,
                    startAngle = -90f,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )
            }
        }
        Column(
            modifier = Modifier
                .widthIn(max = ringSize * 0.72f)
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                primaryText,
                style = DisplayStat.copy(fontSize = 40.sp, lineHeight = 42.sp),
                color = OnSurface,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                secondaryText,
                style = LabelCaps.copy(fontSize = 10.sp, lineHeight = 13.sp),
                color = when (mode) {
                    SessionTimerMode.REST -> PrimaryContainer
                    SessionTimerMode.PAUSED -> OnSurfaceVariant.copy(alpha = 0.8f)
                    SessionTimerMode.ACTIVE -> OnSurfaceVariant.copy(alpha = 0.75f)
                },
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 2,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
