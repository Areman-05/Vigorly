package com.example.vigorly.ui.profile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Senyera catalana (4 franjas rojas sobre fondo amarillo). */
@Composable
fun CatalanFlag(modifier: Modifier = Modifier, width: Dp = 28.dp, height: Dp = 18.dp) {
    val yellow = Color(0xFFFCDD09)
    val red = Color(0xFFDA291C)
    Canvas(modifier = modifier.size(width, height)) {
        drawRect(yellow, size = size)
        val stripeH = size.height / 9f
        // 4 red stripes on yellow: pattern Y R Y R Y R Y R Y
        for (i in listOf(1, 3, 5, 7)) {
            drawRect(
                color = red,
                topLeft = Offset(0f, stripeH * i),
                size = Size(size.width, stripeH)
            )
        }
    }
}
