package com.example.vigorly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.data.model.WorkoutDetail
import com.example.vigorly.data.model.WorkoutType
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer
import com.example.vigorly.util.WorkoutLabels

@Composable
fun WorkoutListCard(
    workout: WorkoutDetail,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = typeAccent(workout.type)
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        accent.copy(alpha = 0.14f),
                        accent.copy(alpha = 0.04f)
                    )
                )
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = accent.copy(alpha = 0.35f)),
                onClick = onClick
            )
            .padding(Dimens.Md)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.Sm)) {
                WorkoutChip(
                    text = WorkoutLabels.typeLabel(workout.type),
                    accent = accent
                )
                WorkoutChip(
                    text = stringResource(R.string.workout_duration_chip, workout.durationMinutes),
                    accent = accent,
                    filled = true
                )
            }
            FavoriteToggle(
                isFavorite = isFavorite,
                onToggle = onFavoriteToggle
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = workout.name,
            style = HeadlineMd.copy(fontSize = 20.sp, lineHeight = 24.sp),
            color = OnSurface,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = workout.targetDescription,
            style = BodyMd.copy(fontSize = 13.sp),
            color = OnSurfaceVariant.copy(alpha = 0.72f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.dp)
        )
        Spacer(Modifier.height(12.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens.Sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IntensityBadge(intensity = workout.intensity)
            Text(
                text = stringResource(R.string.workout_calories_chip, workout.estimatedCalories),
                style = LabelCaps.copy(fontSize = 10.sp),
                color = OnSurfaceVariant.copy(alpha = 0.65f)
            )
        }
    }
}

private fun typeAccent(type: WorkoutType) = when (type) {
    WorkoutType.STRENGTH -> PrimaryAccent
    WorkoutType.HIIT -> PrimaryContainer
    WorkoutType.CARDIO -> Primary
    WorkoutType.RECOVERY -> OnSurfaceVariant
    WorkoutType.SWIM -> Primary.copy(alpha = 0.85f)
}
