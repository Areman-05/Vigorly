package com.example.vigorly.ui.workout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.res.stringResource
import com.example.vigorly.R
import com.example.vigorly.ui.components.FavoriteToggle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vigorly.di.AppViewModelFactory
import com.example.vigorly.data.model.WorkoutType
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.ui.components.GlassCard
import com.example.vigorly.ui.components.IntensityBadge
import com.example.vigorly.ui.components.WorkoutChip
import com.example.vigorly.ui.components.WorkoutSearchBar
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.util.WorkoutFilter
import com.example.vigorly.util.WorkoutSort

@Composable
fun WorkoutsScreen(
    repository: VigorlyRepository,
    onWorkoutClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    workoutsViewModel: WorkoutsViewModel = viewModel(factory = AppViewModelFactory(repository))
) {
    val searchQuery by workoutsViewModel.searchQuery.collectAsState()
    val selectedFilter by workoutsViewModel.selectedType.collectAsState()
    val sort by workoutsViewModel.sort.collectAsState()
    val favoritesOnly by workoutsViewModel.favoritesOnly.collectAsState()
    val favorites by repository.favorites.collectAsState()

    var workouts = WorkoutFilter.filter(
        repository.listWorkouts(),
        searchQuery,
        selectedFilter,
        sort
    )
    if (favoritesOnly) {
        workouts = WorkoutFilter.filterFavorites(workouts, favorites)
    }

    val sortLabel = when (sort) {
        WorkoutSort.DURATION_ASC -> stringResource(R.string.sort_duration_asc)
        WorkoutSort.DURATION_DESC -> stringResource(R.string.sort_duration_desc)
        WorkoutSort.NAME_ASC -> stringResource(R.string.sort_name_asc)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Dimens.ContainerMargin)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.workouts_title), style = HeadlineMd, color = OnSurface)
            TextButton(onClick = workoutsViewModel::cycleSort) {
                Text(sortLabel, style = BodyMd, color = Primary)
            }
        }
        WorkoutSearchBar(
            query = searchQuery,
            onQueryChange = workoutsViewModel::setSearchQuery
        )
        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = Dimens.Md),
            horizontalArrangement = Arrangement.spacedBy(Dimens.Sm)
        ) {
            FilterChip(
                selected = favoritesOnly,
                onClick = workoutsViewModel::toggleFavoritesOnly,
                label = { Text(stringResource(R.string.favorites_only)) },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Primary.copy(0.2f))
            )
            FilterChip(
                selected = selectedFilter == null,
                onClick = { workoutsViewModel.setSelectedType(null) },
                label = { Text(stringResource(R.string.filter_all)) },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Primary.copy(0.2f))
            )
            WorkoutType.entries.forEach { type ->
                FilterChip(
                    selected = selectedFilter == type,
                    onClick = { workoutsViewModel.setSelectedType(type) },
                    label = { Text(type.name) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Primary.copy(0.2f))
                )
            }
        }
        if (workouts.isEmpty()) {
            Text(stringResource(R.string.no_workouts_found), style = BodyMd, color = OnSurfaceVariant)
        }
        workouts.forEach { workout ->
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Dimens.Md)
                    .clickable { onWorkoutClick(workout.id) }
            ) {
                Column(Modifier.padding(Dimens.Md)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.Sm)) {
                            WorkoutChip(workout.type.name)
                            WorkoutChip("${workout.durationMinutes} MIN", primary = true)
                        }
                        FavoriteToggle(
                            isFavorite = favorites.contains(workout.id),
                            onToggle = { repository.toggleFavorite(workout.id) }
                        )
                    }
                    Text(workout.name, style = HeadlineMd, color = OnSurface, modifier = Modifier.padding(top = Dimens.Sm))
                    Row(horizontalArrangement = Arrangement.spacedBy(Dimens.Sm)) {
                        IntensityBadge(workout.intensity)
                        Text(
                            "${workout.estimatedCalories} kcal",
                            style = BodyMd,
                            color = OnSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
