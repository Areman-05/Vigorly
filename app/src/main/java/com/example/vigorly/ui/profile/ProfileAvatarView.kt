package com.example.vigorly.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.vigorly.ui.iconForName

@Composable
fun ProfileAvatarView(
    avatarUrl: String?,
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    borderColor: Color = Color.White.copy(alpha = 0.35f),
    iconScale: Float = 0.42f
) {
    val preset = ProfileAvatarCatalog.resolve(avatarUrl)

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .then(
                if (borderColor.alpha > 0f) {
                    Modifier.border(2.dp, borderColor, CircleShape)
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(preset.gradientStart, preset.gradientEnd)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                iconForName(preset.iconName),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(size * iconScale)
            )
        }
    }
}
