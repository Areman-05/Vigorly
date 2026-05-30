package com.example.vigorly.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
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
import kotlinx.coroutines.isActive
import kotlin.math.sin

private data class RingSpec(
    val radiusFrac: Float,
    val progress: Float,
    val speedFactor: Float,
    val colorOf: (Float) -> Color
)

@Composable
fun ActivityRingsLogo(
    modifier: Modifier = Modifier,
    size: Dp = 280.dp,
    strokeWidth: Dp = 12.dp,
    animate: Boolean = true
) {
    var phase by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(animate) {
        if (!animate) return@LaunchedEffect
        var lastFrame = withFrameNanos { it }
        while (isActive) {
            withFrameNanos { frameTime ->
                val deltaSeconds = (frameTime - lastFrame) / 1_000_000_000f
                lastFrame = frameTime
                phase += deltaSeconds
            }
        }
    }

    val ringRotation = phase * 32f
    val breathe = 1f + 0.04f * sin(phase * 1.1f)
    val shimmer = 0.68f + 0.32f * ((sin(phase * 1.7f + 0.5f) + 1f) * 0.5f)

    val rings = listOf(
        RingSpec(0.92f, 0.82f, 1f) { PrimaryAccent.copy(shimmer * 0.9f) },
        RingSpec(0.68f, 0.58f, -0.6f) { PrimaryContainer.copy(shimmer * 0.85f) },
        RingSpec(0.44f, 0.74f, 1f) { Primary.copy(shimmer) }
    )

    Box(
        modifier = modifier
            .size(size)
            .then(if (animate) Modifier.scale(breathe.toFloat()) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.matchParentSize()) {
            val cx = this.size.width / 2f
            val cy = this.size.height / 2f
            val base = this.size.minDimension / 2f
            val stroke = strokeWidth.toPx()
            rings.forEach { ring ->
                val r = base * ring.radiusFrac
                val start = -90f + ringRotation * ring.speedFactor
                val arcSize = Size(r * 2, r * 2)
                val topLeft = Offset(cx - r, cy - r)
                val style = Stroke(stroke, cap = StrokeCap.Round)
                drawArc(RingTrack.copy(alpha = 0.45f), start, 360f, false, topLeft, arcSize, style = style)
                drawArc(ring.colorOf(shimmer), start, 360f * ring.progress, false, topLeft, arcSize, style = style)
            }
        }
    }
}
