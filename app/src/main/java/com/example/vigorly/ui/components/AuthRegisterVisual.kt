package com.example.vigorly.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer
import com.example.vigorly.ui.theme.RingTrack

@Composable
fun AuthRegisterVisual(
    modifier: Modifier = Modifier,
    size: Dp = 160.dp
) {
    Canvas(modifier.size(size)) {
        val cx = this.size.width / 2f
        val cy = this.size.height / 2f
        val base = this.size.minDimension / 2f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    PrimaryAccent.copy(0.18f),
                    PrimaryAccent.copy(0.04f),
                    PrimaryAccent.copy(0f)
                ),
                center = Offset(cx, cy),
                radius = base * 0.88f
            ),
            radius = base * 0.88f,
            center = Offset(cx, cy)
        )
        drawCircle(
            color = RingTrack.copy(0.35f),
            radius = base * 0.72f,
            center = Offset(cx, cy),
            style = Stroke(width = base * 0.025f)
        )

        val plateW = base * 0.26f
        val plateH = base * 0.44f
        val barW = base * 0.78f
        val barH = base * 0.08f

        drawRoundRect(
            color = PrimaryAccent.copy(0.92f),
            topLeft = Offset(cx - barW / 2f - plateW * 0.88f, cy - plateH / 2f),
            size = Size(plateW, plateH),
            cornerRadius = CornerRadius(plateW * 0.2f, plateW * 0.2f)
        )
        drawRoundRect(
            color = PrimaryAccent.copy(0.92f),
            topLeft = Offset(cx + barW / 2f - plateW * 0.12f, cy - plateH / 2f),
            size = Size(plateW, plateH),
            cornerRadius = CornerRadius(plateW * 0.2f, plateW * 0.2f)
        )
        drawRoundRect(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    PrimaryContainer.copy(0.85f),
                    PrimaryContainer,
                    PrimaryContainer.copy(0.85f)
                ),
                startX = cx - barW / 2f,
                endX = cx + barW / 2f
            ),
            topLeft = Offset(cx - barW / 2f, cy - barH / 2f),
            size = Size(barW, barH),
            cornerRadius = CornerRadius(barH / 2f, barH / 2f)
        )
    }
}
