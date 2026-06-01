package com.example.vigorly.ui.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.data.model.Exercise
import com.example.vigorly.ui.iconForName
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant

@Composable
fun WorkoutExerciseTimelineRow(
    exercise: Exercise,
    accent: Color,
    isLast: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = if (isLast) 0.dp else Dimens.Sm)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(end = Dimens.Md)
        ) {
            Box(
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    iconForName(exercise.iconName ?: "fitness_center"),
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(22.dp)
                )
            }
            if (!isLast) {
                Box(
                    Modifier
                        .width(2.dp)
                        .height(28.dp)
                        .padding(top = 4.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(accent.copy(alpha = 0.22f))
                )
            }
        }
        Column(
            Modifier
                .weight(1f)
                .padding(top = 6.dp, bottom = if (isLast) 0.dp else Dimens.Md)
        ) {
            Text(
                exercise.name,
                style = HeadlineMd.copy(fontSize = 17.sp, lineHeight = 22.sp),
                color = OnSurface,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                exercise.setsRepsLabel,
                style = BodyMd.copy(fontSize = 13.sp),
                color = OnSurfaceVariant.copy(alpha = 0.72f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
