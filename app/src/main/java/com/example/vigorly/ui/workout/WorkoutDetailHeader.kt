package com.example.vigorly.ui.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.vigorly.ui.components.FavoriteToggle
import com.example.vigorly.ui.components.WorkoutChip
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.HeadlineLgMobile
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.util.WorkoutLabels

@Composable
fun WorkoutDetailHeader(
    workout: WorkoutDetail,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = WorkoutTypeTheme.accent(workout.type)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        accent.copy(alpha = 0.18f),
                        Primary.copy(alpha = 0.08f),
                        PrimaryAccent.copy(alpha = 0.04f)
                    )
                )
            )
            .padding(Dimens.Md),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            accent.copy(alpha = 0.28f),
                            accent.copy(alpha = 0.08f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = WorkoutTypeTheme.icon(workout.type),
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(28.dp)
            )
        }

        Column(
            Modifier
                .weight(1f)
                .padding(horizontal = Dimens.Md)
        ) {
            Text(
                WorkoutLabels.typeLabel(workout.type).uppercase(),
                style = LabelCaps.copy(fontSize = 10.sp),
                color = accent.copy(alpha = 0.95f)
            )
            Text(
                workout.name,
                style = HeadlineLgMobile.copy(fontSize = 22.sp, lineHeight = 26.sp),
                color = OnSurface,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                WorkoutChip(
                    text = stringResource(R.string.workout_duration_chip, workout.durationMinutes),
                    accent = accent,
                    filled = true
                )
                WorkoutChip(
                    text = WorkoutLabels.intensityLabel(workout.intensity),
                    accent = accent
                )
            }
            Text(
                workout.description,
                style = BodyMd.copy(fontSize = 14.sp, lineHeight = 20.sp),
                color = OnSurfaceVariant.copy(alpha = 0.8f),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 10.dp)
            )
        }

        FavoriteToggle(isFavorite = isFavorite, onToggle = onFavoriteToggle)
    }
}
