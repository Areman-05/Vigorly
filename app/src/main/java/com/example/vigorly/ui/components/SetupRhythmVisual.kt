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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.vigorly.core.testing.UiTestEnvironment
import com.example.vigorly.ui.performance.UiPerformance
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer
import com.example.vigorly.ui.theme.RingTrack
import kotlinx.coroutines.isActive
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SetupRhythmVisual(
    modifier: Modifier = Modifier,
    size: Dp = 220.dp
) {
    var phase by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        if (UiTestEnvironment.disableContinuousUiMotion || !UiPerformance.decorativeMotionEnabled) {
            return@LaunchedEffect
        }
        var lastFrame = withFrameNanos { it }
        while (isActive) {
            withFrameNanos { frameTime ->
                phase += (frameTime - lastFrame) / 1_000_000_000f
                lastFrame = frameTime
            }
        }
    }

    Canvas(modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val cx = w / 2f
        val cy = h * 0.52f

        drawCircle(color = PrimaryContainer.copy(0.12f), radius = w * 0.42f, center = Offset(cx, cy))
        drawCircle(
            color = PrimaryAccent.copy(0.08f + 0.04f * sin(phase * 1.4f)),
            radius = w * 0.28f,
            center = Offset(cx, cy)
        )

        val barCount = 7
        val barWidth = w * 0.07f
        val gap = w * 0.025f
        val totalBarsWidth = barCount * barWidth + (barCount - 1) * gap
        var x = cx - totalBarsWidth / 2f
        val maxBarHeight = h * 0.38f
        val baseY = cy + maxBarHeight * 0.55f
        val wavePoints = mutableListOf<Offset>()

        repeat(barCount) { index ->
            val wave = sin(phase * 2.2f + index * 0.85f)
            val barFraction = 0.35f + 0.65f * ((wave + 1f) * 0.5f)
            val barHeight = maxBarHeight * barFraction
            val top = baseY - barHeight
            val color = when (index % 3) {
                0 -> PrimaryAccent.copy(0.75f + 0.25f * sin(phase + index))
                1 -> PrimaryContainer.copy(0.8f)
                else -> Primary.copy(0.85f)
            }
            drawRoundRect(
                color = color,
                topLeft = Offset(x, top),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
            )
            wavePoints.add(Offset(x + barWidth / 2f, top - 6f))
            x += barWidth + gap
        }

        if (wavePoints.size >= 2) {
            val path = Path().apply {
                moveTo(wavePoints.first().x, wavePoints.first().y)
                wavePoints.drop(1).forEach { lineTo(it.x, it.y) }
            }
            drawPath(
                path = path,
                color = PrimaryAccent.copy(0.45f),
                style = Stroke(width = 3f, cap = StrokeCap.Round)
            )
        }

        listOf(
            Triple(0.38f, 0.22f, phase * 55f),
            Triple(0.38f, 0.22f, -phase * 42f + 120f),
            Triple(0.38f, 0.22f, phase * 35f + 240f)
        ).forEachIndexed { i, (radiusFrac, dotR, angleDeg) ->
            val rad = Math.toRadians(angleDeg.toDouble())
            val orbitR = w * radiusFrac
            val ox = cx + orbitR * cos(rad).toFloat()
            val oy = cy + orbitR * sin(rad).toFloat()
            val dotColor = when (i) {
                0 -> PrimaryAccent
                1 -> PrimaryContainer
                else -> Primary
            }
            drawCircle(dotColor.copy(0.85f), w * dotR * 0.5f, Offset(ox, oy))
            drawCircle(RingTrack.copy(0.35f), w * dotR * 0.5f, Offset(ox, oy), style = Stroke(2f))
        }
    }
}
