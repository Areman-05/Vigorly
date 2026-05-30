package com.example.vigorly.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.vigorly.ui.theme.DisplayStat
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer
import com.example.vigorly.ui.theme.RingTrack

@Composable
fun ActivityRings(
    moveProgress: Float,
    exerciseProgress: Float,
    standProgress: Float,
    centerPercent: Int,
    modifier: Modifier = Modifier,
    ringSize: Dp = 256.dp,
    showCenterLabel: Boolean = true
) {
    val animatedMove by animateFloatAsState(moveProgress, tween(800), label = "move")
    val animatedExercise by animateFloatAsState(exerciseProgress, tween(800), label = "exercise")
    val animatedStand by animateFloatAsState(standProgress, tween(800), label = "stand")

    Box(modifier = modifier.size(ringSize), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val stroke = this.size.minDimension * 0.0625f
            fun drawRing(radiusFraction: Float, progress: Float, color: androidx.compose.ui.graphics.Color) {
                val radius = this.size.minDimension / 2f * radiusFraction
                val topLeft = Offset(this.size.width / 2 - radius, this.size.height / 2 - radius)
                val arcSize = Size(radius * 2, radius * 2)
                drawArc(
                    color = RingTrack,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = 360f * progress.coerceIn(0f, 1f),
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )
            }
            drawRing(0.84f, animatedMove, PrimaryAccent)
            drawRing(0.60f, animatedExercise, PrimaryContainer)
            drawRing(0.36f, animatedStand, Primary)
        }
        if (showCenterLabel) {
            androidx.compose.foundation.layout.Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$centerPercent%",
                    style = DisplayStat,
                    color = Primary
                )
                Text(
                    text = "DAILY GOAL",
                    style = LabelCaps,
                    color = OnSurfaceVariant
                )
            }
        }
    }
}
