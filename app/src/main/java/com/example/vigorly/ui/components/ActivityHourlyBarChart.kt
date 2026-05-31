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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.OnSurfaceVariant

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
    height: Dp = 96.dp,
    maxValue: Float? = null
) {
    val dataKey = remember(values) { values.hashCode() }
    val progress = remember { Animatable(0f) }

    LaunchedEffect(dataKey) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 720, easing = FastOutSlowInEasing)
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

    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
        ) {
            val layout = computeHourlyBarLayout(size.width, HOUR_COUNT)
            val topRadius = layout.barWidth / 2f.coerceAtMost(6f)

            normalizedValues.forEachIndexed { index, value ->
                if (value <= 0f) return@forEachIndexed
                val fraction = (value / normalizedMax).coerceIn(0f, 1f)
                val barHeight = size.height * fraction * barProgress
                if (barHeight < 1f) return@forEachIndexed
                val x = layout.barStartX(index)
                val y = size.height - barHeight
                drawRoundRect(
                    color = barColor.copy(alpha = 0.92f),
                    topLeft = Offset(x, y),
                    size = Size(layout.barWidth, barHeight),
                    cornerRadius = CornerRadius(topRadius, topRadius)
                )
            }
        }

        ActivityChartHourLabels(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
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
                    fontSize = 10.sp,
                    fontWeight = if (isEdge) FontWeight.Medium else FontWeight.Normal
                ),
                color = OnSurfaceVariant.copy(alpha = if (isEdge) 0.72f else 0.58f),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}
