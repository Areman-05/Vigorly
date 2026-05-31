package com.example.vigorly.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.vigorly.R
import com.example.vigorly.data.model.WorkoutDetail
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary

@Composable
fun RecommendedWorkoutCard(
    workout: WorkoutDetail,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    VigorlyOutlineCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(Dimens.Md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(stringResource(R.string.for_you_label), style = LabelCaps, color = Primary)
                Text(workout.name, style = HeadlineMd, color = OnSurface, modifier = Modifier.padding(top = Dimens.Xs))
                Text(
                    "${workout.durationMinutes} min · ${workout.intensity}",
                    style = BodyMd,
                    color = OnSurfaceVariant,
                    modifier = Modifier.padding(top = Dimens.Xs)
                )
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = OnSurfaceVariant)
        }
    }
}
