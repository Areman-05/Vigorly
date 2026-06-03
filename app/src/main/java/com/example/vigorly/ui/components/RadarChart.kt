package com.example.vigorly.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.data.model.AthleticStat
import com.example.vigorly.util.AthleticStatLabels
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AthleticRadarChart(
    stats: List<AthleticStat>,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth().height(300.dp)) {
        val chartSize = minOf(maxWidth, maxHeight)
        Canvas(modifier = Modifier.fillMaxWidth().height(280.dp)) {
            if (stats.isEmpty()) return@Canvas
            val center = Offset(size.width / 2, size.height / 2)
            val radius = minOf(size.width, size.height) / 2f * 0.72f
            val count = stats.size
            val gridColor = Color.White.copy(alpha = 0.1f)

            for (level in 1..5) {
                val r = radius * level / 5f
                val path = Path()
                stats.indices.forEach { i ->
                    val angle = -Math.PI / 2 + (2 * Math.PI * i / count)
                    val x = center.x + r * cos(angle).toFloat()
                    val y = center.y + r * sin(angle).toFloat()
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                path.close()
                drawPath(path, gridColor, style = Stroke(1f))
            }

            stats.indices.forEach { i ->
                val angle = -Math.PI / 2 + (2 * Math.PI * i / count)
                val x = center.x + radius * cos(angle).toFloat()
                val y = center.y + radius * sin(angle).toFloat()
                drawLine(gridColor, center, Offset(x, y), 1f)
            }

            val dataPath = Path()
            stats.forEachIndexed { i, stat ->
                val angle = -Math.PI / 2 + (2 * Math.PI * i / count)
                val r = radius * (stat.value / 100f)
                val x = center.x + r * cos(angle).toFloat()
                val y = center.y + r * sin(angle).toFloat()
                if (i == 0) dataPath.moveTo(x, y) else dataPath.lineTo(x, y)
            }
            dataPath.close()
            drawPath(dataPath, Primary.copy(alpha = 0.2f))
            drawPath(dataPath, Primary, style = Stroke(2f))
        }

        val count = stats.size
        stats.forEachIndexed { i, stat ->
            val angle = -Math.PI / 2 + (2 * Math.PI * i / count)
            val labelRadius = chartSize.value * 0.42f
            val offsetX = (chartSize.value * 0.5f + labelRadius * cos(angle).toFloat()).dp - 24.dp
            val offsetY = (140f + labelRadius * sin(angle).toFloat()).dp - 8.dp
            Text(
                text = AthleticStatLabels.displayLabel(stat.label),
                style = LabelCaps.copy(fontSize = 13.sp, lineHeight = 16.sp),
                color = OnSurfaceVariant,
                modifier = Modifier.offset(x = offsetX, y = offsetY)
            )
        }
    }
}
