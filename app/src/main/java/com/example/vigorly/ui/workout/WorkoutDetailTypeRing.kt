package com.example.vigorly.ui.workout

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.vigorly.data.model.WorkoutDetail
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer
import com.example.vigorly.ui.theme.RingTrack
import com.example.vigorly.util.WorkoutLabels

@Composable
fun WorkoutDetailTypeRing(
    workout: WorkoutDetail,
    modifier: Modifier = Modifier,
    ringSize: androidx.compose.ui.unit.Dp = 200.dp
) {
    val accent = WorkoutTypeTheme.accent(workout.type)
    val durationProgress = (workout.durationMinutes / 60f).coerceIn(0.25f, 1f)
    val intensityProgress = WorkoutLabels.intensityProgress(workout.intensity)
    val caloriesProgress = (workout.estimatedCalories / 520f).coerceIn(0.2f, 1f)

    val animatedDuration by animateFloatAsState(durationProgress, tween(900), label = "duration")
    val animatedIntensity by animateFloatAsState(intensityProgress, tween(900, delayMillis = 120), label = "intensity")
    val animatedCalories by animateFloatAsState(caloriesProgress, tween(900, delayMillis = 240), label = "calories")

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(ringSize + 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .size(ringSize + 48.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            accent.copy(alpha = 0.22f),
                            accent.copy(alpha = 0.06f),
                            Color.Transparent
                        )
                    )
                )
        )
        Box(Modifier.size(ringSize), contentAlignment = Alignment.Center) {
            Canvas(Modifier.matchParentSize()) {
                val stroke = size.minDimension * 0.058f
                fun drawRing(radiusFraction: Float, progress: Float, color: Color) {
                    val radius = size.minDimension / 2f * radiusFraction
                    val topLeft = Offset(size.width / 2 - radius, size.height / 2 - radius)
                    val arcSize = Size(radius * 2, radius * 2)
                    drawArc(
                        color = RingTrack.copy(alpha = 0.45f),
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
                drawRing(0.82f, animatedDuration, accent)
                drawRing(0.58f, animatedIntensity, PrimaryContainer)
                drawRing(0.34f, animatedCalories, PrimaryAccent)
            }
            Box(
                Modifier
                    .size(ringSize * 0.38f)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                accent.copy(alpha = 0.28f),
                                Primary.copy(alpha = 0.1f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = WorkoutTypeTheme.icon(workout.type),
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(ringSize * 0.17f)
                )
            }
        }
    }
}
