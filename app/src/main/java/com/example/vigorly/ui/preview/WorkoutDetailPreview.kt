package com.example.vigorly.ui.preview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.vigorly.data.catalog.WorkoutCatalog
import com.example.vigorly.ui.components.GlassCard
import com.example.vigorly.ui.components.IntensityBadge
import com.example.vigorly.ui.components.WorkoutChip
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.VigorlyTheme

@Preview(showBackground = true, backgroundColor = 0xFF121317)
@Composable
fun WorkoutDetailHeaderPreview() {
    val workout = WorkoutCatalog.allWorkouts().first()
    VigorlyTheme {
        GlassCard {
            Column(Modifier.padding(Dimens.Md)) {
                WorkoutChip(workout.type.name)
                Text(workout.name, style = HeadlineMd, color = OnSurface, modifier = Modifier.padding(top = Dimens.Sm))
                Text(workout.description, style = BodyMd, color = OnSurfaceVariant, modifier = Modifier.padding(top = Dimens.Xs))
                IntensityBadge(workout.intensity)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121317)
@Composable
fun WorkoutCatalogPreview() {
    VigorlyTheme {
        Column(Modifier.padding(Dimens.Md)) {
            WorkoutCatalog.allWorkouts().take(3).forEach { workout ->
                Text(workout.name, style = HeadlineMd, color = OnSurface)
            }
        }
    }
}
