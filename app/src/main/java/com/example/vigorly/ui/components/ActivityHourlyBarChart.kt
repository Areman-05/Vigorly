package com.example.vigorly.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.vigorly.ui.theme.RingTrack

@Composable
fun ActivityHourlyBarChart(
    values: List<Float>,
    barColor: Color,
    modifier: Modifier = Modifier,
    height: Dp = 72.dp,
    maxValue: Float? = null
) {
    val normalizedMax = maxValue ?: values.maxOrNull()?.coerceAtLeast(1f) ?: 1f

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val barCount = values.size.coerceAtLeast(1)
        val gap = size.width * 0.012f
        val barWidth = ((size.width - gap * (barCount - 1)) / barCount).coerceAtLeast(2f)
        val corner = barWidth / 2f

        values.forEachIndexed { index, value ->
            val fraction = (value / normalizedMax).coerceIn(0f, 1f)
            val barHeight = (size.height * fraction).coerceAtLeast(if (value > 0f) 4f else 2f)
            val x = index * (barWidth + gap)
            val y = size.height - barHeight
            drawRoundRect(
                color = RingTrack,
                topLeft = Offset(x, 0f),
                size = Size(barWidth, size.height),
                cornerRadius = CornerRadius(corner, corner)
            )
            if (value > 0f) {
                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(corner, corner)
                )
            }
        }
    }
}
