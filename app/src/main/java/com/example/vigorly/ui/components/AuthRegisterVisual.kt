package com.example.vigorly.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.vigorly.core.testing.UiTestEnvironment
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer
import com.example.vigorly.ui.theme.RingTrack
import kotlinx.coroutines.isActive
import kotlin.math.cos
import kotlin.math.sin

/** Registro: badge con anillo giratorio y satélites — familia visual del setup, composición propia. */
@Composable
fun AuthRegisterVisual(
    modifier: Modifier = Modifier,
    size: Dp = 104.dp
) {
    var phase by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        if (UiTestEnvironment.disableContinuousUiMotion) return@LaunchedEffect
        var lastFrame = withFrameNanos { it }
        while (isActive) {
            withFrameNanos { frameTime ->
                phase += (frameTime - lastFrame) / 1_000_000_000f
                lastFrame = frameTime
            }
        }
    }

    val breathe = 1f + 0.035f * sin(phase * 1.2f)
    val badgeSize = size * 0.78f

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.matchParentSize()) {
            val cx = this.size.width / 2f
            val cy = this.size.height / 2f
            val outerR = this.size.minDimension / 2f * 0.92f
            val stroke = outerR * 0.045f

            drawCircle(
                color = PrimaryContainer.copy(0.1f),
                radius = outerR * 0.88f,
                center = Offset(cx, cy)
            )

            drawArc(
                color = RingTrack.copy(0.4f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(cx - outerR, cy - outerR),
                size = Size(outerR * 2f, outerR * 2f),
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
            drawArc(
                color = PrimaryAccent.copy(0.75f + 0.2f * sin(phase * 1.5f)),
                startAngle = -90f + phase * 38f,
                sweepAngle = 110f + 25f * sin(phase * 0.9f),
                useCenter = false,
                topLeft = Offset(cx - outerR, cy - outerR),
                size = Size(outerR * 2f, outerR * 2f),
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )

            listOf(45f, 225f).forEachIndexed { i, angleDeg ->
                val rad = Math.toRadians((angleDeg + phase * 18f).toDouble())
                val orbitR = outerR * 0.78f
                val ox = cx + orbitR * cos(rad).toFloat()
                val oy = cy + orbitR * sin(rad).toFloat()
                val dotColor = if (i == 0) PrimaryAccent else Primary
                drawCircle(dotColor.copy(0.8f), outerR * 0.07f, Offset(ox, oy))
            }
        }

        Box(
            modifier = Modifier
                .scale(breathe)
                .size(badgeSize)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            PrimaryContainer.copy(0.6f),
                            Primary.copy(0.22f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.PersonAdd,
                contentDescription = null,
                tint = PrimaryAccent,
                modifier = Modifier.size(badgeSize * 0.44f)
            )
        }
    }
}
