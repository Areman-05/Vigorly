package com.example.vigorly.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.vigorly.ui.theme.GlassCrystalBase
import com.example.vigorly.ui.theme.GlassCrystalEdge
import com.example.vigorly.ui.theme.GlassCrystalLift
import com.example.vigorly.ui.theme.GlassCrystalSheen

/**
 * Bola de cristal: semitransparente sobre el aurora + borde luminoso.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    cornerRadius: Dp = 24.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    GlassSurface(
        modifier = modifier,
        shape = shape,
        onClick = onClick
    ) {
        Column(content = content)
    }
}

@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = ripple(bounded = true, color = Color.White.copy(alpha = 0.14f)),
            onClick = onClick
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        GlassCrystalLift,
                        GlassCrystalBase,
                        GlassCrystalBase.copy(alpha = 0.72f)
                    )
                )
            )
            .border(
                BorderStroke(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            GlassCrystalEdge,
                            GlassCrystalSheen,
                            Color.White.copy(alpha = 0.1f)
                        )
                    )
                ),
                shape = shape
            )
            .then(clickModifier),
        content = content
    )
}
