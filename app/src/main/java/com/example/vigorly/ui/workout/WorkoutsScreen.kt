package com.example.vigorly.ui.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vigorly.R
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.di.AppViewModelFactory
import com.example.vigorly.ui.components.WorkoutFilterChips
import com.example.vigorly.ui.components.WorkoutListCard
import com.example.vigorly.ui.components.WorkoutSearchBar
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.HeadlineLgMobile
import com.example.vigorly.ui.theme.LabelCaps
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
            .padding(horizontal = Dimens.ContainerMargin, vertical = Dimens.Lg)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.workouts_title),
                    style = HeadlineLgMobile,
                    color = OnSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.workouts_count, workouts.size),
                    style = LabelCaps.copy(fontSize = 10.sp),
                    color = OnSurfaceVariant.copy(alpha = 0.65f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            TextButton(onClick = workoutsViewModel::cycleSort) {
                Text(sortLabel, style = BodyMd, color = Primary)
            }
        }

        Spacer(Modifier.height(Dimens.Sm))

        WorkoutSearchBar(
            query = searchQuery,
            onQueryChange = workoutsViewModel::setSearchQuery
        )

        WorkoutFilterChips(
            favoritesOnly = favoritesOnly,
            selectedType = selectedFilter,
            onFavoritesToggle = workoutsViewModel::toggleFavoritesOnly,
            onTypeSelected = workoutsViewModel::setSelectedType
        )

        if (workouts.isEmpty()) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimens.Xl),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.no_workouts_found),
                    style = BodyMd,
                    color = OnSurface,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = stringResource(R.string.workouts_empty_hint),
                    style = BodyMd.copy(fontSize = 13.sp),
                    color = OnSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = Dimens.Xs)
                )
            }
        } else {
            workouts.forEach { workout ->
                WorkoutListCard(
                    workout = workout,
                    isFavorite = favorites.contains(workout.id),
                    onFavoriteToggle = { repository.toggleFavorite(workout.id) },
                    onClick = { onWorkoutClick(workout.id) },
                    modifier = Modifier.padding(bottom = Dimens.Md)
                )
            }
        }

        Spacer(Modifier.height(Dimens.Md))
    }
}
