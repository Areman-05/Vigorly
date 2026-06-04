package com.example.vigorly.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer
import com.example.vigorly.ui.theme.RingTrack

private data class RingSpec(
    val radiusFrac: Float,
    val progress: Float,
    val color: Color
)

@Composable
fun ActivityRingsLogo(
    modifier: Modifier = Modifier,
    size: Dp = 280.dp,
    strokeWidth: Dp = 12.dp,
    @Suppress("UNUSED_PARAMETER") animate: Boolean = true
) {
    val rings = listOf(
        RingSpec(0.92f, 0.82f, PrimaryAccent.copy(alpha = 0.9f)),
        RingSpec(0.68f, 0.58f, PrimaryContainer.copy(alpha = 0.85f)),
        RingSpec(0.44f, 0.74f, Primary)
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.matchParentSize()) {
            val cx = this.size.width / 2f
            val cy = this.size.height / 2f
            val base = this.size.minDimension / 2f
            val stroke = strokeWidth.toPx()
            rings.forEach { ring ->
                val r = base * ring.radiusFrac
                val start = -90f
                val arcSize = Size(r * 2, r * 2)
                val topLeft = Offset(cx - r, cy - r)
                val style = Stroke(stroke, cap = StrokeCap.Round)
                drawArc(RingTrack.copy(alpha = 0.45f), start, 360f, false, topLeft, arcSize, style = style)
                drawArc(ring.color, start, 360f * ring.progress, false, topLeft, arcSize, style = style)
            }
        }
    }
}
