package com.example.vigorly.ui.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.data.model.WorkoutDetail
import com.example.vigorly.ui.components.WorkoutChip
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.HeadlineLgMobile
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.util.WorkoutLabels

@Composable
fun WorkoutDetailTitleSection(
    workout: WorkoutDetail,
    modifier: Modifier = Modifier
) {
    val accent = WorkoutTypeTheme.accent(workout.type)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            WorkoutLabels.typeLabel(workout.type).uppercase(),
            style = LabelCaps.copy(fontSize = 11.sp),
            color = accent.copy(alpha = 0.95f)
        )
        Text(
            workout.name,
            style = HeadlineLgMobile.copy(fontSize = 28.sp, lineHeight = 32.sp),
            color = OnSurface,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 8.dp, start = Dimens.Sm, end = Dimens.Sm)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 12.dp)
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
            style = BodyMd.copy(fontSize = 15.sp, lineHeight = 22.sp),
            color = OnSurfaceVariant.copy(alpha = 0.82f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 14.dp, start = Dimens.Sm, end = Dimens.Sm)
        )
    }
}
