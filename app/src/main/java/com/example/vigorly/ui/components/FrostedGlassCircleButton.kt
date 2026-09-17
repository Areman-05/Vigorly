package com.example.vigorly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.vigorly.ui.theme.GlassCrystalBase
import com.example.vigorly.ui.theme.GlassCrystalEdge
import com.example.vigorly.ui.theme.GlassCrystalLift
import com.example.vigorly.ui.theme.GlassCrystalSheen

/**
 * Mismo cristal que [GlassSurface]/ lift → base → borde luminoso + sheen superior.
 */
@Composable
fun FrostedGlassCircleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .size(size)
            .alpha(if (enabled) 1f else 0.38f)
            .clip(CircleShape)
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
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        GlassCrystalEdge,
                        GlassCrystalSheen,
                        Color.White.copy(alpha = 0.1f)
                    )
                ),
                shape = CircleShape
            )
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = Color.White.copy(alpha = 0.2f)),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0f to Color.White.copy(alpha = 0.16f),
                            0.42f to Color.Transparent,
                            1f to Color.Transparent
                        )
                    )
                )
        )
        content()
    }
}
