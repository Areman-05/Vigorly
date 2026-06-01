package com.example.vigorly.ui.workout

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.example.vigorly.data.model.WorkoutDetail
import com.example.vigorly.ui.components.FavoriteToggle
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.RingTrack

@Composable
fun WorkoutDetailHero(
    workout: WorkoutDetail,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = WorkoutTypeTheme.accent(workout.type)
    val transition = rememberInfiniteTransition(label = "workoutHero")
    val pulse by transition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2200), RepeatMode.Reverse),
        label = "pulse"
    )
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(24000), RepeatMode.Restart),
        label = "rotation"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Box(
            Modifier
                .matchParentSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            accent.copy(alpha = 0.2f * pulse),
                            accent.copy(alpha = 0.04f),
                            androidx.compose.ui.graphics.Color.Transparent
                        ),
                        radius = 420f
                    )
                )
        )
        Canvas(Modifier.matchParentSize()) {
            val stroke = size.minDimension * 0.028f
            rotate(rotation) {
                listOf(0.78f, 0.62f, 0.48f).forEachIndexed { index, radiusFraction ->
                    val radius = size.minDimension / 2f * radiusFraction
                    val topLeft = Offset(size.width / 2 - radius, size.height / 2 - radius)
                    val arcSize = Size(radius * 2, radius * 2)
                    val sweep = 110f + index * 40f
                    drawArc(
                        color = RingTrack.copy(alpha = 0.35f),
                        startAngle = -90f + index * 28f,
                        sweepAngle = sweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = accent.copy(alpha = (0.35f + index * 0.12f) * pulse),
                        startAngle = -90f + index * 28f,
                        sweepAngle = sweep * 0.72f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )
                }
            }
        }
        Box(
            Modifier.align(Alignment.Center),
            contentAlignment = Alignment.Center
        ) {
            Box(
                Modifier
                    .size((96 * pulse).dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.14f * pulse)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = WorkoutTypeTheme.icon(workout.type),
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(44.dp)
                )
            }
        }
        Box(Modifier.align(Alignment.TopEnd).padding(Dimens.ContainerMargin)) {
            FavoriteToggle(isFavorite = isFavorite, onToggle = onFavoriteToggle)
        }
    }
}
