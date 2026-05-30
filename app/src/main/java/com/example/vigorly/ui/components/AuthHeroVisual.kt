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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer
import com.example.vigorly.ui.theme.RingTrack
import kotlinx.coroutines.isActive
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AuthHeroVisual(
    modifier: Modifier = Modifier,
    size: Dp = 200.dp
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
        val cx = this.size.width / 2f
        val cy = this.size.height / 2f
        val base = this.size.minDimension / 2f
        val pulse = 0.5f + 0.5f * sin(phase * 1.8f)

        drawCircle(
            color = PrimaryAccent.copy(alpha = 0.06f + 0.05f * pulse),
            radius = base * (0.88f + 0.04f * pulse),
            center = Offset(cx, cy)
        )
        drawCircle(
            color = PrimaryContainer.copy(alpha = 0.1f + 0.06f * sin(phase * 2.4f)),
            radius = base * 0.55f,
            center = Offset(cx, cy)
        )

        repeat(3) { index ->
            val wingAngle = phase * 38f + index * 120f
            rotate(wingAngle, Offset(cx, cy)) {
                drawEnergyWing(
                    center = Offset(cx, cy),
                    length = base * 0.72f,
                    color = when (index) {
                        0 -> PrimaryAccent.copy(0.55f + 0.25f * pulse)
                        1 -> PrimaryContainer.copy(0.65f)
                        else -> Primary.copy(0.7f)
                    },
                    stroke = base * 0.07f
                )
            }
        }

        listOf(
            Triple(0.34f, 1.6f, 0f),
            Triple(0.42f, -1.2f, 72f),
            Triple(0.48f, 1.9f, 144f),
            Triple(0.38f, -1.5f, 216f),
            Triple(0.44f, 1.3f, 288f)
        ).forEachIndexed { index, (radiusFrac, speed, offsetDeg) ->
            val rad = Math.toRadians((phase * 55f * speed + offsetDeg).toDouble())
            val orbitR = base * radiusFrac
            val ox = cx + orbitR * cos(rad).toFloat()
            val oy = cy + orbitR * sin(rad).toFloat() * 0.82f
            val dotColor = when (index % 3) {
                0 -> PrimaryAccent
                1 -> PrimaryContainer
                else -> Primary
            }
            drawCircle(dotColor.copy(0.9f), base * 0.055f, Offset(ox, oy))
            drawCircle(RingTrack.copy(0.35f), base * 0.055f, Offset(ox, oy), style = Stroke(base * 0.012f))
        }

        rotate(phase * -48f, Offset(cx, cy)) {
            val diamond = base * 0.14f
            val path = Path().apply {
                moveTo(cx, cy - diamond)
                lineTo(cx + diamond * 0.85f, cy)
                lineTo(cx, cy + diamond)
                lineTo(cx - diamond * 0.85f, cy)
                close()
            }
            drawPath(path, PrimaryAccent.copy(0.35f + 0.2f * pulse))
            drawPath(path, PrimaryAccent.copy(0.85f), style = Stroke(base * 0.035f, cap = StrokeCap.Round))
        }
    }
}

private fun DrawScope.drawEnergyWing(
    center: Offset,
    length: Float,
    color: androidx.compose.ui.graphics.Color,
    stroke: Float
) {
    val path = Path().apply {
        moveTo(center.x, center.y - length * 0.12f)
        cubicTo(
            center.x + length * 0.55f, center.y - length * 0.35f,
            center.x + length * 0.75f, center.y + length * 0.05f,
            center.x + length * 0.42f, center.y + length * 0.38f
        )
    }
    drawPath(path, color, style = Stroke(stroke, cap = StrokeCap.Round))
}
