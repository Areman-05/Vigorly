package com.example.vigorly.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.vigorly.ui.performance.UiPerformance
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer
import com.example.vigorly.ui.theme.RingTrackGlass

@Composable
fun CircularStatRing(
    progress: Float,
    accent: Color,
    modifier: Modifier = Modifier,
    size: Dp = 72.dp,
    strokeWidth: Dp = 8.dp,
    trackColor: Color = RingTrackGlass
) {
    val target = progress.coerceIn(0f, 1f)
    val animated by animateFloatAsState(
        targetValue = if (UiPerformance.decorativeMotionEnabled) target else target,
        animationSpec = tween(durationMillis = if (UiPerformance.decorativeMotionEnabled) 900 else 0),
        label = "statRing"
    )
    val strokePx = with(LocalDensity.current) { strokeWidth.toPx() }

    Canvas(modifier = modifier.size(size)) {
        val diameter = this.size.minDimension - strokePx
        val topLeft = Offset(strokePx / 2f, strokePx / 2f)
        val arcSize = Size(diameter, diameter)

        drawArc(
            color = trackColor,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokePx, cap = StrokeCap.Round)
        )
        if (animated > 0.001f) {
            drawArc(
                color = accent,
                startAngle = -90f,
                sweepAngle = 360f * animated,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        }
    }
}

/**
 * Tres anillos concéntricos (movimiento / tiempo / ejercicio) como en Resumen.
 */
@Composable
fun TripleActivityRing(
    moveProgress: Float,
    exerciseProgress: Float,
    standProgress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 148.dp,
    strokeWidth: Dp = 11.dp,
    gap: Dp = 6.dp
) {
    val move by animateFloatAsState(
        targetValue = moveProgress.coerceIn(0f, 1f),
        animationSpec = tween(if (UiPerformance.decorativeMotionEnabled) 900 else 0),
        label = "moveRing"
    )
    val exercise by animateFloatAsState(
        targetValue = exerciseProgress.coerceIn(0f, 1f),
        animationSpec = tween(if (UiPerformance.decorativeMotionEnabled) 950 else 0),
        label = "exerciseRing"
    )
    val stand by animateFloatAsState(
        targetValue = standProgress.coerceIn(0f, 1f),
        animationSpec = tween(if (UiPerformance.decorativeMotionEnabled) 1000 else 0),
        label = "standRing"
    )
    val strokePx = with(LocalDensity.current) { strokeWidth.toPx() }
    val gapPx = with(LocalDensity.current) { gap.toPx() }

    Canvas(modifier = modifier.size(size)) {
        val rings = listOf(
            Triple(move, PrimaryAccent, 0),
            Triple(exercise, PrimaryContainer, 1),
            Triple(stand, Primary, 2)
        )
        rings.forEach { (progress, accent, index) ->
            val inset = strokePx / 2f + index * (strokePx + gapPx)
            val diameter = this.size.minDimension - inset * 2f
            if (diameter <= 0f) return@forEach
            val topLeft = Offset(inset, inset)
            val arcSize = Size(diameter, diameter)
            drawArc(
                color = RingTrackGlass,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
            if (progress > 0.001f) {
                drawArc(
                    color = accent,
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            }
        }
    }
}
