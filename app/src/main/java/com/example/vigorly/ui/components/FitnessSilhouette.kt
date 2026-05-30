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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer

enum class FitnessPose {
    HERO,
    RUNNER,
    SQUAT,
    STRETCH,
    LIFT,
    CYCLING,
    WELCOME,
    RINGS,
    HOME_GYM,
    OUTDOOR
}

@Composable
fun FitnessSilhouette(
    pose: FitnessPose,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    accent: Color = PrimaryAccent,
    body: Color = Primary,
    glow: Color = PrimaryContainer,
    animate: Boolean = false
) {
    animateFloatAsState(
        targetValue = if (animate) 1f else 1f,
        animationSpec = tween(800),
        label = "silhouette_pulse"
    )
    Canvas(modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val cx = w / 2f
        drawCircle(glow.copy(alpha = 0.25f), w * 0.38f, Offset(cx, h * 0.55f))
        when (pose) {
            FitnessPose.HERO -> drawHero(cx, h, accent, body)
            FitnessPose.RUNNER -> drawRunner(cx, h, accent, body)
            FitnessPose.SQUAT -> drawSquat(cx, h, accent, body)
            FitnessPose.STRETCH -> drawStretch(cx, h, accent, body)
            FitnessPose.LIFT -> drawLift(cx, h, accent, body)
            FitnessPose.CYCLING -> drawCycling(cx, h, accent, body)
            FitnessPose.WELCOME -> drawWelcome(cx, h, accent, body)
            FitnessPose.RINGS -> drawRingsFigure(cx, h, accent, body)
            FitnessPose.HOME_GYM -> drawHomeGym(cx, h, accent, body)
            FitnessPose.OUTDOOR -> drawOutdoor(cx, h, accent, body)
        }
    }
}

private fun DrawScope.drawLimb(path: Path, color: Color, width: Float = 7f) {
    drawPath(path, color, style = Stroke(width, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

private fun DrawScope.drawHead(cx: Float, top: Float, color: Color, scale: Float = 1f) {
    drawCircle(color, 10f * scale, Offset(cx, top + 12f * scale))
}

private fun DrawScope.drawHero(cx: Float, h: Float, accent: Color, body: Color) {
    drawHead(cx, h * 0.08f, body, 1.1f)
    drawLimb(Path().apply { moveTo(cx, h * 0.22f); lineTo(cx, h * 0.52f) }, body, 8f)
    drawLimb(Path().apply {
        moveTo(cx, h * 0.28f); lineTo(cx - 28f, h * 0.18f)
        moveTo(cx, h * 0.28f); lineTo(cx + 28f, h * 0.18f)
    }, accent, 7f)
    drawLimb(Path().apply {
        moveTo(cx, h * 0.52f); lineTo(cx - 18f, h * 0.78f)
        moveTo(cx, h * 0.52f); lineTo(cx + 18f, h * 0.78f)
    }, body, 8f)
}

private fun DrawScope.drawRunner(cx: Float, h: Float, accent: Color, body: Color) {
    rotate(8f, Offset(cx, h * 0.5f)) {
        drawHead(cx, h * 0.1f, body)
        drawLimb(Path().apply { moveTo(cx, h * 0.24f); lineTo(cx + 6f, h * 0.5f) }, body, 7f)
        drawLimb(Path().apply {
            moveTo(cx + 6f, h * 0.5f); lineTo(cx - 10f, h * 0.82f)
            moveTo(cx + 6f, h * 0.5f); lineTo(cx + 30f, h * 0.72f)
        }, accent, 7f)
        drawLimb(Path().apply {
            moveTo(cx, h * 0.3f); lineTo(cx - 26f, h * 0.38f)
            moveTo(cx, h * 0.32f); lineTo(cx + 22f, h * 0.22f)
        }, accent, 6f)
    }
}

private fun DrawScope.drawSquat(cx: Float, h: Float, accent: Color, body: Color) {
    drawHead(cx, h * 0.14f, body)
    drawLimb(Path().apply { moveTo(cx, h * 0.28f); lineTo(cx, h * 0.42f) }, body, 8f)
    drawLimb(Path().apply { moveTo(cx - 34f, h * 0.48f); lineTo(cx + 34f, h * 0.48f) }, accent, 9f)
    drawLimb(Path().apply {
        moveTo(cx - 34f, h * 0.48f); lineTo(cx - 22f, h * 0.72f)
        moveTo(cx + 34f, h * 0.48f); lineTo(cx + 22f, h * 0.72f)
    }, body, 8f)
}

private fun DrawScope.drawStretch(cx: Float, h: Float, accent: Color, body: Color) {
    drawHead(cx, h * 0.12f, body)
    drawLimb(Path().apply { moveTo(cx, h * 0.26f); lineTo(cx, h * 0.58f) }, body, 7f)
    drawLimb(Path().apply {
        moveTo(cx, h * 0.3f); lineTo(cx - 32f, h * 0.08f)
        moveTo(cx, h * 0.3f); lineTo(cx + 32f, h * 0.08f)
    }, accent, 6f)
}

private fun DrawScope.drawLift(cx: Float, h: Float, accent: Color, body: Color) {
    drawHead(cx, h * 0.1f, body)
    drawLimb(Path().apply { moveTo(cx, h * 0.24f); lineTo(cx, h * 0.5f) }, body, 8f)
    drawLimb(Path().apply { moveTo(cx - 38f, h * 0.34f); lineTo(cx + 38f, h * 0.34f) }, accent, 10f)
}

private fun DrawScope.drawCycling(cx: Float, h: Float, accent: Color, body: Color) {
    drawCircle(accent.copy(0.4f), 26f, Offset(cx - 18f, h * 0.78f), style = Stroke(4f))
    drawCircle(accent.copy(0.4f), 26f, Offset(cx + 18f, h * 0.78f), style = Stroke(4f))
    drawHead(cx + 4f, h * 0.12f, body)
    drawLimb(Path().apply { moveTo(cx + 4f, h * 0.26f); lineTo(cx, h * 0.48f) }, body, 7f)
}

private fun DrawScope.drawWelcome(cx: Float, h: Float, accent: Color, body: Color) {
    drawHead(cx, h * 0.14f, body, 1.15f)
    drawLimb(Path().apply { moveTo(cx, h * 0.28f); lineTo(cx, h * 0.56f) }, body, 8f)
    drawLimb(Path().apply {
        moveTo(cx, h * 0.34f); lineTo(cx - 36f, h * 0.22f)
        moveTo(cx, h * 0.34f); lineTo(cx + 36f, h * 0.22f)
    }, accent, 7f)
}

private fun DrawScope.drawRingsFigure(cx: Float, h: Float, accent: Color, body: Color) {
    drawCircle(accent.copy(0.35f), 34f, Offset(cx, h * 0.42f), style = Stroke(5f))
    drawCircle(Primary.copy(0.35f), 24f, Offset(cx, h * 0.42f), style = Stroke(4f))
    drawWelcome(cx, h, accent, body)
}

private fun DrawScope.drawHomeGym(cx: Float, h: Float, accent: Color, body: Color) {
    drawRoundRect(accent.copy(0.35f), topLeft = Offset(cx - 30f, h * 0.62f), size = Size(60f, 8f))
    drawSquat(cx, h, accent, body)
}

private fun DrawScope.drawOutdoor(cx: Float, h: Float, accent: Color, body: Color) {
    drawLimb(Path().apply { moveTo(cx - 40f, h * 0.82f); lineTo(cx + 40f, h * 0.82f) }, accent.copy(0.5f), 3f)
    drawRunner(cx, h, accent, body)
}
