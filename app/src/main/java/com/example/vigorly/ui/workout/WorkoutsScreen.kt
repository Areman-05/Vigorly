package com.example.vigorly.ui.workout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.ui.components.GlassCard
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant

@Composable
fun WorkoutsScreen(
    repository: VigorlyRepository,
    onWorkoutClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Dimens.ContainerMargin)
    ) {
        Text("Workouts", style = HeadlineMd, color = OnSurface, modifier = Modifier.padding(bottom = Dimens.Md))
        repository.listWorkoutIds().forEach { id ->
            val workout = repository.getWorkout(id) ?: return@forEach
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Dimens.Md)
                    .clickable { onWorkoutClick(id) }
            ) {
                Column(Modifier.padding(Dimens.Md)) {
                    Text(workout.name, style = HeadlineMd, color = OnSurface)
                    Text(
                        "${workout.type.name} • ${workout.durationMinutes} min",
                        style = BodyMd,
                        color = OnSurfaceVariant
                    )
                }
            }
        }
    }
}
