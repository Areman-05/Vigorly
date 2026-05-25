package com.example.vigorly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.SurfaceContainerHigh

@Composable
fun IntensityBadge(
    intensity: String,
    modifier: Modifier = Modifier
) {
    val isHigh = intensity.equals("High", ignoreCase = true)
    Text(
        text = intensity.uppercase(),
        style = LabelCaps,
        color = if (isHigh) Primary else OnSurface,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isHigh) Primary.copy(alpha = 0.15f) else SurfaceContainerHigh)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}
