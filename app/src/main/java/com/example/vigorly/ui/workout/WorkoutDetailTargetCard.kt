package com.example.vigorly.ui.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.DisplayStat
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryContainer

@Composable
fun WorkoutDetailTargetCard(
    targetMuscles: String,
    targetDescription: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        PrimaryContainer.copy(alpha = 0.16f),
                        Primary.copy(alpha = 0.06f),
                        accent.copy(alpha = 0.04f)
                    )
                )
            )
            .padding(Dimens.Md)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Rounded.FitnessCenter,
                contentDescription = null,
                tint = accent,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.16f))
                    .padding(8.dp)
            )
            Text(
                stringResource(R.string.workout_target).uppercase(),
                style = LabelCaps,
                color = accent.copy(alpha = 0.9f),
                modifier = Modifier.padding(start = Dimens.Sm)
            )
        }
        Spacer(Modifier.height(Dimens.Md))
        Text(
            targetMuscles,
            style = DisplayStat.copy(fontSize = 30.sp, lineHeight = 32.sp),
            color = OnSurface,
            fontWeight = FontWeight.Bold
        )
        Text(
            targetDescription,
            style = BodyMd.copy(fontSize = 14.sp, lineHeight = 20.sp),
            color = OnSurfaceVariant.copy(alpha = 0.78f),
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}
