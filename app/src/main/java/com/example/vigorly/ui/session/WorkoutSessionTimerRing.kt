package com.example.vigorly.ui.session

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.ui.theme.DisplayStat
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.RingTrack

@Composable
fun WorkoutSessionTimerRing(
    primaryText: String,
    secondaryText: String,
    progress: Float,
    accent: Color = PrimaryAccent,
    modifier: Modifier = Modifier,
    ringSize: Dp = 220.dp,
    pulseWhenActive: Boolean = true
) {
    val transition = rememberInfiniteTransition(label = "sessionRing")
    val pulse by transition.animateFloat(
        initialValue = if (pulseWhenActive) 0.92f else 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1800), RepeatMode.Reverse),
        label = "pulse"
    )
    val clampedProgress = progress.coerceIn(0f, 1f)

    Box(modifier.size(ringSize), contentAlignment = Alignment.Center) {
        Canvas(Modifier.matchParentSize()) {
            val stroke = this.size.minDimension * 0.055f
            val radius = this.size.minDimension / 2f * 0.82f * pulse
            val topLeft = Offset(this.size.width / 2 - radius, this.size.height / 2 - radius)
            val arcSize = Size(radius * 2, radius * 2)
            drawArc(
                color = RingTrack.copy(alpha = 0.4f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
            drawArc(
                color = accent.copy(alpha = 0.95f),
                startAngle = -90f,
                sweepAngle = 360f * clampedProgress,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                primaryText,
                style = DisplayStat.copy(fontSize = 36.sp, lineHeight = 38.sp),
                color = OnSurface,
                fontWeight = FontWeight.Bold
            )
            Text(
                secondaryText,
                style = DisplayStat.copy(fontSize = 11.sp, lineHeight = 14.sp),
                color = OnSurfaceVariant.copy(alpha = 0.75f),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
