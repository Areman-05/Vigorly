package com.example.vigorly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FlatProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color,
    trackColor: Color
) {
    val fraction = progress.coerceIn(0f, 1f)
    Box(modifier = modifier.clip(RoundedCornerShape(3.dp))) {
        Box(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(trackColor)
        )
        Box(
            Modifier
                .fillMaxWidth(fraction)
                .fillMaxHeight()
                .background(color)
        )
    }
}
