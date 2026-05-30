package com.example.vigorly.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer
import com.example.vigorly.ui.theme.RingTrack
import kotlinx.coroutines.isActive
import kotlin.math.sin

private data class OffsetRing(
    val centerFrac: Offset,
    val radiusFrac: Float,
    val progress: Float,
    val speed: Float,
    val color: androidx.compose.ui.graphics.Color
)

@Composable
fun AuthLoginVisual(
    modifier: Modifier = Modifier,
    size: Dp = 160.dp
) {
    var phase by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        var lastFrame = withFrameNanos { it }
        while (isActive) {
            withFrameNanos { frameTime ->
                phase += (frameTime - lastFrame) / 1_000_000_000f
                lastFrame = frameTime
            }
        }
    }

    val pulse = 0.5f + 0.5f * sin(phase * 1.4f)

    Canvas(modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val base = this.size.minDimension / 2f
        val stroke = base * 0.09f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    PrimaryAccent.copy(0.14f + 0.06f * pulse),
                    PrimaryAccent.copy(0.03f),
                    PrimaryAccent.copy(0f)
                ),
                center = Offset(w * 0.52f, h * 0.48f),
                radius = base * 0.95f
            ),
            radius = base * 0.95f,
            center = Offset(w * 0.52f, h * 0.48f)
        )

        val rings = listOf(
            OffsetRing(Offset(0.54f, 0.50f), 0.42f, 0.78f, 1f, PrimaryAccent),
            OffsetRing(Offset(0.46f, 0.54f), 0.34f, 0.62f, -0.7f, PrimaryContainer),
            OffsetRing(Offset(0.58f, 0.56f), 0.26f, 0.88f, 1.2f, Primary)
        )

        rings.forEach { ring ->
            val cx = w * ring.centerFrac.x
            val cy = h * ring.centerFrac.y
            val r = base * ring.radiusFrac
            val start = -90f + phase * 28f * ring.speed
            val arcSize = Size(r * 2f, r * 2f)
            val topLeft = Offset(cx - r, cy - r)
            val arcStyle = Stroke(stroke, cap = StrokeCap.Round)
            drawArc(
                color = RingTrack.copy(0.4f),
                startAngle = start,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = arcStyle
            )
            drawArc(
                color = ring.color.copy(0.55f + 0.35f * pulse),
                startAngle = start,
                sweepAngle = 360f * ring.progress,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = arcStyle
            )
        }
    }
}
