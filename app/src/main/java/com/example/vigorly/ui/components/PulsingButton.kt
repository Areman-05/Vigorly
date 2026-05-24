package com.example.vigorly.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import com.example.vigorly.ui.theme.PrimaryAccent

@Composable
fun PulsingButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = PrimaryAccent,
    contentColor: Color = Color.White,
    shape: Shape = RoundedCornerShape(999.dp),
    pulseEnabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val scale = if (pulseEnabled) {
        val transition = rememberInfiniteTransition(label = "pulse")
        transition.animateFloat(
            initialValue = 1f,
            targetValue = 1.04f,
            animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
            label = "scale"
        ).value
    } else 1f

    Button(
        onClick = onClick,
        modifier = modifier.scale(scale),
        shape = shape,
        colors = ButtonDefaults.buttonColors(containerColor = containerColor, contentColor = contentColor),
        content = content
    )
}
