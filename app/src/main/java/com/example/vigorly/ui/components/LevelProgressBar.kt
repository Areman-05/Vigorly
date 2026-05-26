package com.example.vigorly.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary

@Composable
fun LevelProgressBar(
    level: Int,
    progress: Float,
    workoutsUntilNext: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text("LEVEL $level", style = LabelCaps, color = OnSurfaceVariant)
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth().padding(vertical = Dimens.Sm),
            color = Primary,
            trackColor = OnSurfaceVariant.copy(alpha = 0.2f)
        )
        Text(
            "$workoutsUntilNext workouts to level ${level + 1}",
            style = BodyMd,
            color = OnSurfaceVariant
        )
    }
}
