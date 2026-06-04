package com.example.vigorly.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.data.model.AthleticStat
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer
import com.example.vigorly.util.AthleticStatLabels
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

private val StatTeal = Color(0xFF20C997)
private val StatViolet = Color(0xFF9775FA)

@Composable
fun AthleticRadarChart(
    stats: List<AthleticStat>,
    modifier: Modifier = Modifier
) {
    if (stats.isEmpty()) return

    val animatedFractions = stats.map { stat ->
        animateFloatAsState(
            targetValue = stat.value / 100f,
            animationSpec = tween(800),
            label = "radar_${stat.label}"
        )
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        val count = stats.size

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val r = minOf(size.width, size.height) / 2f * 0.62f
            val gridColor = Color.White.copy(alpha = 0.12f)

            for (level in 1..4) {
                val ring = r * level / 4f
                val path = Path()
                stats.indices.forEach { i ->
                    val angle = vertexAngle(i, count)
                    val x = center.x + ring * cos(angle)
                    val y = center.y + ring * sin(angle)
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                path.close()
                drawPath(path, gridColor, style = Stroke(1.2f))
            }

            stats.indices.forEach { i ->
                val angle = vertexAngle(i, count)
                drawLine(
                    gridColor,
                    center,
                    Offset(
                        center.x + r * cos(angle),
                        center.y + r * sin(angle)
                    ),
                    strokeWidth = 1f
                )
            }

            val dataPath = Path()
            stats.forEachIndexed { i, _ ->
                val angle = vertexAngle(i, count)
                val fraction = animatedFractions[i].value.coerceIn(0.08f, 1f)
                val x = center.x + r * fraction * cos(angle)
                val y = center.y + r * fraction * sin(angle)
                if (i == 0) dataPath.moveTo(x, y) else dataPath.lineTo(x, y)
            }
            dataPath.close()
            drawPath(
                dataPath,
                Brush.radialGradient(
                    colors = listOf(
                        PrimaryAccent.copy(alpha = 0.35f),
                        Primary.copy(alpha = 0.12f)
                    ),
                    center = center,
                    radius = r
                )
            )
            drawPath(dataPath, PrimaryAccent.copy(alpha = 0.9f), style = Stroke(2.5f))

            stats.forEachIndexed { i, stat ->
                val angle = vertexAngle(i, count)
                val fraction = animatedFractions[i].value.coerceIn(0.08f, 1f)
                val point = Offset(
                    center.x + r * fraction * cos(angle),
                    center.y + r * fraction * sin(angle)
                )
                drawCircle(
                    color = accentForKey(AthleticStatLabels.normalizeKey(stat.label)),
                    radius = 5.dp.toPx(),
                    center = point
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.85f),
                    radius = 2.dp.toPx(),
                    center = point
                )
            }
        }

        stats.forEachIndexed { i, stat ->
            val angle = vertexAngle(i, count)
            val offsetX = (maxWidth * 0.5f + maxWidth * 0.38f * cos(angle)) - 40.dp
            val offsetY = (140.dp + 110.dp * sin(angle)) - 18.dp
            val displayScore = (animatedFractions[i].value * 100f).roundToInt()
                .coerceIn(0, 100)
            Column(
                modifier = Modifier
                    .width(80.dp)
                    .offset(x = offsetX, y = offsetY),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    AthleticStatLabels.displayLabel(stat.label),
                    style = BodyMd.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium),
                    color = OnSurfaceVariant.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
                Text(
                    "$displayScore",
                    style = BodyMd.copy(fontSize = 15.sp, fontWeight = FontWeight.Bold),
                    color = accentForKey(AthleticStatLabels.normalizeKey(stat.label)),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private fun vertexAngle(index: Int, count: Int): Float =
    (-Math.PI / 2 + (2 * Math.PI * index / count)).toFloat()

private fun accentForKey(key: String): Color = when (key) {
    "strength", "power" -> PrimaryAccent
    "endurance", "stamina" -> PrimaryContainer
    "speed" -> StatViolet
    "mobility" -> StatTeal
    else -> Primary
}
