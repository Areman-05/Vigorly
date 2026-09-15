package com.example.vigorly.ui.profile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ProfileAvatarView(
    avatarUrl: String?,
    modifier: Modifier = Modifier,
    size: Dp = 96.dp
) {
    val showRemote = ProfileAvatarCatalog.isRemoteUrl(avatarUrl)
    val preset = ProfileAvatarCatalog.resolve(avatarUrl)

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (showRemote) {
            AsyncImage(
                model = avatarUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            PresetAvatarContent(preset = preset)
        }
    }
}

@Composable
private fun PresetAvatarContent(preset: ProfileAvatarCatalog.Preset) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val radius = size.minDimension / 2f
        val center = Offset(size.width / 2f, size.height / 2f)

        drawCircle(
            brush = Brush.linearGradient(
                colors = listOf(preset.gradientStart, preset.gradientMid, preset.gradientEnd),
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height)
            ),
            radius = radius,
            center = center
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(preset.accent.copy(alpha = 0.38f), Color.Transparent),
                center = Offset(center.x * 0.7f, center.y * 0.55f),
                radius = radius * 0.9f
            ),
            radius = radius,
            center = center
        )
        drawMotif(preset.motif, preset.accent, center, radius)
    }
}

private fun DrawScope.drawMotif(
    motif: ProfileAvatarCatalog.Motif,
    accent: Color,
    center: Offset,
    radius: Float
) {
    val ink = Color.White.copy(alpha = 0.92f)
    val soft = accent.copy(alpha = 0.85f)
    when (motif) {
        ProfileAvatarCatalog.Motif.Rings -> {
            drawCircle(ink, radius * 0.38f, center, style = Stroke(3.2f))
            drawCircle(soft, radius * 0.22f, center, style = Stroke(2.4f))
            drawCircle(ink, radius * 0.08f, center)
        }
        ProfileAvatarCatalog.Motif.Hex -> {
            drawPath(regularPolygon(center, radius * 0.36f, 6), ink, style = Stroke(3f, cap = StrokeCap.Round))
            drawCircle(soft, radius * 0.1f, center)
        }
        ProfileAvatarCatalog.Motif.Wave -> {
            val path = Path().apply {
                val y = center.y
                moveTo(center.x - radius * 0.42f, y)
                cubicTo(
                    center.x - radius * 0.22f, y - radius * 0.22f,
                    center.x - radius * 0.08f, y + radius * 0.22f,
                    center.x, y
                )
                cubicTo(
                    center.x + radius * 0.08f, y - radius * 0.22f,
                    center.x + radius * 0.22f, y + radius * 0.22f,
                    center.x + radius * 0.42f, y
                )
            }
            drawPath(path, ink, style = Stroke(3.4f, cap = StrokeCap.Round))
        }
        ProfileAvatarCatalog.Motif.Leaf -> {
            val path = Path().apply {
                moveTo(center.x, center.y - radius * 0.38f)
                cubicTo(
                    center.x + radius * 0.34f, center.y - radius * 0.1f,
                    center.x + radius * 0.2f, center.y + radius * 0.28f,
                    center.x, center.y + radius * 0.36f
                )
                cubicTo(
                    center.x - radius * 0.2f, center.y + radius * 0.28f,
                    center.x - radius * 0.34f, center.y - radius * 0.1f,
                    center.x, center.y - radius * 0.38f
                )
                close()
            }
            drawPath(path, ink.copy(alpha = 0.9f), style = Stroke(2.8f))
            drawLine(
                soft,
                Offset(center.x, center.y - radius * 0.28f),
                Offset(center.x, center.y + radius * 0.28f),
                strokeWidth = 2f
            )
        }
        ProfileAvatarCatalog.Motif.Spark -> {
            repeat(4) { i ->
                rotate(i * 45f, center) {
                    drawLine(
                        if (i % 2 == 0) ink else soft,
                        Offset(center.x, center.y - radius * 0.4f),
                        Offset(center.x, center.y - radius * 0.14f),
                        strokeWidth = 3f,
                        cap = StrokeCap.Round
                    )
                }
            }
            drawCircle(ink, radius * 0.1f, center)
        }
        ProfileAvatarCatalog.Motif.Diamond -> {
            val path = Path().apply {
                moveTo(center.x, center.y - radius * 0.38f)
                lineTo(center.x + radius * 0.28f, center.y)
                lineTo(center.x, center.y + radius * 0.38f)
                lineTo(center.x - radius * 0.28f, center.y)
                close()
            }
            drawPath(path, ink, style = Stroke(2.8f))
        }
        ProfileAvatarCatalog.Motif.Arc -> {
            drawArc(
                color = ink,
                startAngle = 200f,
                sweepAngle = 140f,
                useCenter = false,
                topLeft = Offset(center.x - radius * 0.34f, center.y - radius * 0.34f),
                size = Size(radius * 0.68f, radius * 0.68f),
                style = Stroke(3.2f, cap = StrokeCap.Round)
            )
        }
        ProfileAvatarCatalog.Motif.Orbit -> {
            drawCircle(ink, radius * 0.28f, center, style = Stroke(2.6f))
            drawCircle(soft, radius * 0.08f, Offset(center.x + radius * 0.28f, center.y))
        }
        ProfileAvatarCatalog.Motif.Bloom -> {
            repeat(6) { i ->
                rotate(i * 60f, center) {
                    drawCircle(
                        ink.copy(alpha = 0.75f),
                        radius * 0.11f,
                        Offset(center.x, center.y - radius * 0.22f)
                    )
                }
            }
            drawCircle(soft, radius * 0.1f, center)
        }
        ProfileAvatarCatalog.Motif.Crest -> {
            val path = Path().apply {
                moveTo(center.x, center.y - radius * 0.36f)
                lineTo(center.x + radius * 0.26f, center.y - radius * 0.08f)
                lineTo(center.x + radius * 0.18f, center.y + radius * 0.3f)
                lineTo(center.x, center.y + radius * 0.18f)
                lineTo(center.x - radius * 0.18f, center.y + radius * 0.3f)
                lineTo(center.x - radius * 0.26f, center.y - radius * 0.08f)
                close()
            }
            drawPath(path, ink, style = Stroke(2.6f))
        }
        ProfileAvatarCatalog.Motif.Grid -> {
            val step = radius * 0.18f
            for (i in -1..1) {
                drawLine(
                    ink.copy(alpha = 0.8f),
                    Offset(center.x + i * step, center.y - radius * 0.28f),
                    Offset(center.x + i * step, center.y + radius * 0.28f),
                    strokeWidth = 2.2f
                )
                drawLine(
                    soft.copy(alpha = 0.75f),
                    Offset(center.x - radius * 0.28f, center.y + i * step),
                    Offset(center.x + radius * 0.28f, center.y + i * step),
                    strokeWidth = 2.2f
                )
            }
        }
        ProfileAvatarCatalog.Motif.Shard -> {
            val path = Path().apply {
                moveTo(center.x - radius * 0.08f, center.y - radius * 0.38f)
                lineTo(center.x + radius * 0.3f, center.y - radius * 0.05f)
                lineTo(center.x + radius * 0.05f, center.y + radius * 0.36f)
                lineTo(center.x - radius * 0.28f, center.y + radius * 0.08f)
                close()
            }
            drawPath(path, ink, style = Stroke(2.8f))
        }
    }
}

private fun regularPolygon(center: Offset, radius: Float, sides: Int): Path {
    val path = Path()
    for (i in 0 until sides) {
        val angle = Math.toRadians((-90.0 + i * (360.0 / sides)))
        val x = center.x + radius * cos(angle).toFloat()
        val y = center.y + radius * sin(angle).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}
