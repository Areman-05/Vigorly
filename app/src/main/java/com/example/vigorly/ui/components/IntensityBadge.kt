package com.example.vigorly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.SurfaceContainerHigh
import com.example.vigorly.util.WorkoutLabels

@Composable
fun IntensityBadge(
    intensity: String,
    modifier: Modifier = Modifier
) {
    val isHigh = WorkoutLabels.intensityIsHigh(intensity)
    Text(
        text = WorkoutLabels.intensityLabel(intensity).uppercase(),
        style = LabelCaps.copy(fontSize = 9.sp, lineHeight = 11.sp),
        color = if (isHigh) PrimaryAccent else OnSurface.copy(alpha = 0.85f),
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(
                if (isHigh) PrimaryAccent.copy(alpha = 0.16f) else SurfaceContainerHigh.copy(alpha = 0.65f)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}
