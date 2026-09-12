package com.example.vigorly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.example.vigorly.ui.theme.GlassLabel
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.OnSurface
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
    val shape = RoundedCornerShape(22.dp)

    GlassSurface(
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        onClick = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(bounded = true, color = accent.copy(alpha = 0.28f)),
                    onClick = onClick
                )
                .padding(horizontal = 16.dp, vertical = 15.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MetaPill(
                        text = WorkoutLabels.typeLabel(workout.type),
                        accent = accent
                    )
                    MetaPill(
                        text = stringResource(R.string.workout_duration_chip, workout.durationMinutes),
                        accent = accent,
                        emphasized = true
                    )
                }
                IconButton(
                    onClick = onFavoriteToggle,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isFavorite) PrimaryAccent else GlassLabel.copy(alpha = 0.85f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = workout.name,
                style = HeadlineMd.copy(
                    fontSize = 22.sp,
                    lineHeight = 27.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.3).sp
                ),
                color = OnSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = workout.targetDescription.ifBlank { workout.targetMuscles },
                style = BodyMd.copy(
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    letterSpacing = 0.1.sp
                ),
                color = GlassLabel.copy(alpha = 0.82f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(Modifier.height(14.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IntensityBadge(intensity = workout.intensity)
                Text(
                    text = stringResource(R.string.workout_calories_chip, workout.estimatedCalories),
                    style = BodyMd.copy(fontSize = 14.sp, fontWeight = FontWeight.Medium),
                    color = GlassLabel.copy(alpha = 0.78f)
                )
            }
        }
    }
}

@Composable
private fun MetaPill(
    text: String,
    accent: Color,
    emphasized: Boolean = false
) {
    Text(
        text = text.uppercase(),
        style = BodyMd.copy(
            fontSize = 12.sp,
            lineHeight = 14.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.55.sp
        ),
        color = OnSurface.copy(alpha = 0.92f),
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(
                if (emphasized) accent.copy(alpha = 0.28f) else Color.White.copy(alpha = 0.08f)
            )
            .padding(horizontal = 11.dp, vertical = 6.dp)
    )
}

private fun typeAccent(type: WorkoutType) = when (type) {
    WorkoutType.STRENGTH -> PrimaryAccent
    WorkoutType.HIIT -> PrimaryContainer
    WorkoutType.CARDIO -> Primary
    WorkoutType.RECOVERY -> GlassLabel
    WorkoutType.PILATES -> Primary.copy(alpha = 0.92f)
    WorkoutType.MOBILITY -> GlassLabel.copy(alpha = 0.9f)
    WorkoutType.SWIM -> Primary.copy(alpha = 0.85f)
}
