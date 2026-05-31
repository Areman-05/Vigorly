package com.example.vigorly.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.vigorly.ui.theme.Primary

@Composable
fun VigorlyOutlineCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(18.dp)
    val border = BorderStroke(1.dp, Primary.copy(alpha = 0.22f))
    if (onClick != null) {
        Surface(
            onClick = onClick,
            modifier = modifier,
            shape = shape,
            color = Color.Transparent,
            border = border,
            content = { Column(content = content) }
        )
    } else {
        Surface(
            modifier = modifier,
            shape = shape,
            color = Color.Transparent,
            border = border,
            content = { Column(content = content) }
        )
    }
}
