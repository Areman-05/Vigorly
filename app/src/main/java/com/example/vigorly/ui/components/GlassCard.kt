package com.example.vigorly.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.vigorly.ui.theme.GlassBorder
import com.example.vigorly.ui.theme.GlassOverlay

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    if (onClick != null) {
        Surface(
            onClick = onClick,
            modifier = modifier,
            shape = shape,
            color = GlassOverlay,
            border = BorderStroke(1.dp, GlassBorder),
            content = { Column(content = content) }
        )
    } else {
        Surface(
            modifier = modifier,
            shape = shape,
            color = GlassOverlay,
            border = BorderStroke(1.dp, GlassBorder),
            content = { Column(content = content) }
        )
    }
}
