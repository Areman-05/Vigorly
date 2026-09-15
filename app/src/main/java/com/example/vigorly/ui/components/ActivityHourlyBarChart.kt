package com.example.vigorly.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.GlassLabel

private const val HOUR_COUNT = 24

/** Marcas repartidas simétricamente (inicio, cada 4 h y cierre). */
private val CHART_HOUR_MARKERS = listOf(0, 4, 8, 12, 16, 20, 24)

private data class HourlyBarLayout(
    val barCount: Int,
    val gap: Float,
    val barWidth: Float
) {
    fun barStartX(index: Int): Float = index * (barWidth + gap)
}

private fun computeHourlyBarLayout(widthPx: Float, barCount: Int): HourlyBarLayout {
    val gap = widthPx * 0.018f
    val barWidth = ((widthPx - gap * (barCount - 1)) / barCount).coerceAtLeast(2f)
    return HourlyBarLayout(barCount, gap, barWidth)
}

@Composable
fun ActivityHourlyBarChart(
    values: List<Float>,
    barColor: Color,
    modifier: Modifier = Modifier,
    height: Dp = 112.dp,
    maxValue: Float? = null,
    highlightColor: Color = barColor.copy(alpha = 0.35f)
) {
    val dataKey = remember(values) { values.hashCode() }
    val progress = remember { Animatable(0f) }

    LaunchedEffect(dataKey) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 820, easing = FastOutSlowInEasing)
        )
    }

    val normalizedValues = remember(values) {
        values.take(HOUR_COUNT).let { list ->
            if (list.size >= HOUR_COUNT) list else list + List(HOUR_COUNT - list.size) { 0f }
        }
    }
    val peak = maxValue ?: normalizedValues.maxOrNull()?.coerceAtLeast(0f) ?: 0f
    val normalizedMax = if (peak > 0f) peak else 1f
    val barProgress = progress.value
    val peakIndex = remember(normalizedValues) {
        normalizedValues.withIndex().maxByOrNull { it.value }?.index ?: -1
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
        ) {
            val layout = computeHourlyBarLayout(size.width, HOUR_COUNT)
            val topRadius = layout.barWidth / 2f.coerceAtMost(7f)

            // Guía horizontal suave
            drawLine(
                color = Color.White.copy(alpha = 0.06f),
                start = Offset(0f, size.height),
                end = Offset(size.width, size.height),
                strokeWidth = 1.5f
            )

            normalizedValues.forEachIndexed { index, value ->
                if (value <= 0f) return@forEachIndexed
                val fraction = (value / normalizedMax).coerceIn(0f, 1f)
                val barHeight = size.height * fraction * barProgress
                if (barHeight < 1f) return@forEachIndexed
                val x = layout.barStartX(index)
                val y = size.height - barHeight
                val isPeak = index == peakIndex
                val brush = Brush.verticalGradient(
                    colors = listOf(
                        if (isPeak) barColor else barColor.copy(alpha = 0.88f),
                        highlightColor
                    ),
                    startY = y,
                    endY = size.height
                )
                drawRoundRect(
                    brush = brush,
                    topLeft = Offset(x, y),
                    size = Size(layout.barWidth, barHeight),
                    cornerRadius = CornerRadius(topRadius, topRadius)
                )
            }
        }

        ActivityChartHourLabels(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        )
    }
}

@Composable
private fun ActivityChartHourLabels(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CHART_HOUR_MARKERS.forEachIndexed { index, hour ->
            if (index > 0) {
                Spacer(Modifier.weight(1f))
            }
            val isEdge = index == 0 || index == CHART_HOUR_MARKERS.lastIndex
            Text(
                text = hour.toString(),
                style = BodyMd.copy(
                    fontSize = 11.sp,
                    fontWeight = if (isEdge) FontWeight.SemiBold else FontWeight.Medium
                ),
                color = GlassLabel.copy(alpha = if (isEdge) 0.72f else 0.48f),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}
