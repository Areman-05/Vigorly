package com.example.vigorly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.PrimaryAccent

@Composable
fun WorkoutChip(
    text: String,
    modifier: Modifier = Modifier,
    accent: Color = PrimaryAccent,
    filled: Boolean = false
) {
    Text(
        text = text.uppercase(),
        style = LabelCaps.copy(fontSize = 9.sp, lineHeight = 11.sp),
        color = if (filled) OnSurface else accent.copy(alpha = 0.9f),
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(
                if (filled) accent.copy(alpha = 0.22f) else accent.copy(alpha = 0.1f)
            )
            .padding(horizontal = 10.dp, vertical = 5.dp)
    )
}
