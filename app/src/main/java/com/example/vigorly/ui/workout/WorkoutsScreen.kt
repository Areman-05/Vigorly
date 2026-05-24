package com.example.vigorly.ui.workout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.vigorly.data.model.WorkoutType
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.ui.components.GlassCard
import com.example.vigorly.ui.components.WorkoutChip
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary

@Composable
fun WorkoutsScreen(
    repository: VigorlyRepository,
    onWorkoutClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf<WorkoutType?>(null) }
    val workouts = repository.listWorkouts().filter {
        selectedFilter == null || it.type == selectedFilter
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Dimens.ContainerMargin)
    ) {
        Text("Workouts", style = HeadlineMd, color = OnSurface)
        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = Dimens.Md),
            horizontalArrangement = Arrangement.spacedBy(Dimens.Sm)
        ) {
            FilterChip(
                selected = selectedFilter == null,
                onClick = { selectedFilter = null },
                label = { Text("All") },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Primary.copy(0.2f))
            )
            WorkoutType.entries.forEach { type ->
                FilterChip(
                    selected = selectedFilter == type,
                    onClick = { selectedFilter = type },
                    label = { Text(type.name) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Primary.copy(0.2f))
                )
            }
        }
        workouts.forEach { workout ->
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Dimens.Md)
                    .clickable { onWorkoutClick(workout.id) }
            ) {
                Column(Modifier.padding(Dimens.Md)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(Dimens.Sm)) {
                        WorkoutChip(workout.type.name)
                        WorkoutChip("${workout.durationMinutes} MIN", primary = true)
                    }
                    Text(workout.name, style = HeadlineMd, color = OnSurface, modifier = Modifier.padding(top = Dimens.Sm))
                    Text(
                        "${workout.intensity} • ${workout.estimatedCalories} kcal",
                        style = BodyMd,
                        color = OnSurfaceVariant
                    )
                }
            }
        }
    }
}