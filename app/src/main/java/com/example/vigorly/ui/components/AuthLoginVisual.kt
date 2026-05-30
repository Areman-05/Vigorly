package com.example.vigorly.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer
import com.example.vigorly.ui.theme.RingTrack

@Composable
fun AuthLoginVisual(
    modifier: Modifier = Modifier,
    size: Dp = 160.dp
) {
    Canvas(modifier.size(size)) {
        val cx = this.size.width / 2f
        val cy = this.size.height / 2f + this.size.height * 0.04f
        val base = this.size.minDimension / 2f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    PrimaryAccent.copy(0.14f),
                    PrimaryAccent.copy(0.03f),
                    PrimaryAccent.copy(0f)
                ),
                center = Offset(cx, cy),
                radius = base * 0.9f
            ),
            radius = base * 0.9f,
            center = Offset(cx, cy)
        )

        val handleStroke = base * 0.09f
        val handlePath = Path().apply {
            moveTo(cx - base * 0.22f, cy - base * 0.08f)
            cubicTo(
                cx - base * 0.34f, cy - base * 0.52f,
                cx + base * 0.34f, cy - base * 0.52f,
                cx + base * 0.22f, cy - base * 0.08f
            )
        }
        drawPath(
            path = handlePath,
            color = PrimaryAccent.copy(0.9f),
            style = Stroke(width = handleStroke, cap = StrokeCap.Round)
        )

        val bellW = base * 0.62f
        val bellH = base * 0.58f
        val bellTop = cy + base * 0.02f
        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    PrimaryContainer.copy(0.95f),
                    PrimaryAccent.copy(0.88f),
                    PrimaryAccent.copy(0.75f)
                ),
                startY = bellTop,
                endY = bellTop + bellH
            ),
            topLeft = Offset(cx - bellW / 2f, bellTop),
            size = Size(bellW, bellH),
            cornerRadius = CornerRadius(bellW * 0.42f, bellW * 0.42f)
        )

        drawRoundRect(
            color = RingTrack.copy(0.25f),
            topLeft = Offset(cx - bellW / 2f, bellTop + bellH - base * 0.06f),
            size = Size(bellW, base * 0.06f),
            cornerRadius = CornerRadius(bellW * 0.08f, bellW * 0.08f),
            style = Fill
        )
    }
}
