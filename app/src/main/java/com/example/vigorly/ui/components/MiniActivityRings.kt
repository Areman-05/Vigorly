package com.example.vigorly.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer
import com.example.vigorly.ui.theme.RingTrack

@Composable
fun MiniActivityRings(
    moveProgress: Float,
    exerciseProgress: Float,
    standProgress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 28.dp,
    muted: Boolean = false
) {
    val alpha = if (muted) 0.28f else 1f
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val stroke = this.size.minDimension * 0.14f
            fun drawRing(
                radiusFraction: Float,
                progress: Float,
                color: Color
            ) {
                val radius = this.size.minDimension / 2f * radiusFraction
                val topLeft = Offset(this.size.width / 2 - radius, this.size.height / 2 - radius)
                val arcSize = Size(radius * 2, radius * 2)
                drawArc(
                    color = RingTrack.copy(alpha = if (muted) 0.35f else 0.92f),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )
                val sweep = 360f * progress.coerceIn(0f, 1f)
                if (sweep <= 0f) return
                drawArc(
                    color = color.copy(alpha = alpha),
                    startAngle = -90f,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )
            }
            drawRing(0.92f, moveProgress, PrimaryAccent)
            drawRing(0.68f, exerciseProgress, PrimaryContainer)
            drawRing(0.44f, standProgress, Primary)
        }
    }
}
