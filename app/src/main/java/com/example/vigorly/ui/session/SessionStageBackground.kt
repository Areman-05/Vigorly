package com.example.vigorly.ui.session

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.SurfaceContainerLowest

@Composable
fun SessionStageBackground(
    modifier: Modifier = Modifier,
    showConfetti: Boolean = true
) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceContainerLowest)
    ) {
        val w = size.width
        val h = size.height
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    PrimaryAccent.copy(alpha = 0.28f),
                    Color.Transparent
                ),
                center = Offset(w * 0.15f, h * 0.08f),
                radius = w * 0.55f
            ),
            center = Offset(w * 0.15f, h * 0.08f),
            radius = w * 0.55f
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF6B1A2A).copy(alpha = 0.35f),
                    Color.Transparent
                ),
                center = Offset(w * 0.9f, h * 0.05f),
                radius = w * 0.5f
            ),
            center = Offset(w * 0.9f, h * 0.05f),
            radius = w * 0.5f
        )
        if (!showConfetti) return@Canvas
        val triColors = listOf(
            Color(0xFFFF6B4A).copy(alpha = 0.45f),
            Color(0xFFB48CFF).copy(alpha = 0.4f),
            Color(0xFFB8E63A).copy(alpha = 0.35f),
            Color(0xFFFF2D55).copy(alpha = 0.4f)
        )
        val positions = listOf(
            Offset(w * 0.12f, h * 0.18f) to 18f,
            Offset(w * 0.78f, h * 0.14f) to 14f,
            Offset(w * 0.88f, h * 0.28f) to 16f,
            Offset(w * 0.22f, h * 0.32f) to 12f,
            Offset(w * 0.65f, h * 0.22f) to 11f
        )
        positions.forEachIndexed { i, (origin, side) ->
            val path = Path().apply {
                moveTo(origin.x, origin.y - side)
                lineTo(origin.x + side * 0.9f, origin.y + side * 0.55f)
                lineTo(origin.x - side * 0.9f, origin.y + side * 0.55f)
                close()
            }
            drawPath(path, color = triColors[i % triColors.size])
        }
    }
}
