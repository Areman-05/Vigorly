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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer
import com.example.vigorly.ui.theme.RingTrack
import kotlinx.coroutines.isActive
import kotlin.math.abs
import kotlin.math.sin

/** Login: barras en pirámide con pico central — familia visual del setup, composición propia. */
@Composable
fun AuthLoginVisual(
    modifier: Modifier = Modifier,
    size: Dp = 180.dp
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

    Canvas(modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val cx = w / 2f
        val cy = h * 0.54f

        drawCircle(
            color = PrimaryContainer.copy(0.11f),
            radius = w * 0.4f,
            center = Offset(cx, cy)
        )
        drawCircle(
            color = PrimaryAccent.copy(0.07f + 0.04f * sin(phase * 1.3f)),
            radius = w * 0.26f,
            center = Offset(cx, cy)
        )

        val barCount = 5
        val barWidth = w * 0.09f
        val gap = w * 0.04f
        val totalWidth = barCount * barWidth + (barCount - 1) * gap
        var x = cx - totalWidth / 2f
        val maxHeight = h * 0.42f
        val baseY = cy + maxHeight * 0.45f
        val centerIndex = barCount / 2

        repeat(barCount) { index ->
            val distFromCenter = abs(index - centerIndex).toFloat()
            val peakBias = 1f - distFromCenter * 0.22f
            val wave = sin(phase * 2f + index * 0.9f)
            val fraction = (0.4f + 0.6f * peakBias) * (0.55f + 0.45f * ((wave + 1f) * 0.5f))
            val barHeight = maxHeight * fraction
            val color = when (index % 3) {
                0 -> PrimaryAccent.copy(0.8f + 0.2f * sin(phase + index))
                1 -> PrimaryContainer.copy(0.82f)
                else -> Primary.copy(0.88f)
            }
            drawRoundRect(
                color = color,
                topLeft = Offset(x, baseY - barHeight),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
            )
            x += barWidth + gap
        }

        drawRoundRect(
            color = RingTrack.copy(0.35f),
            topLeft = Offset(cx - totalWidth / 2f, baseY),
            size = Size(totalWidth, w * 0.012f),
            cornerRadius = CornerRadius(w * 0.006f, w * 0.006f)
        )
    }
}
