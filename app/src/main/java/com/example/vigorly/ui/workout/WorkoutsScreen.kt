package com.example.vigorly.ui.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.util.WorkoutSort

@OptIn(ExperimentalMaterial3Api::class)
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
    val assistantFilters by workoutsViewModel.assistantFilters.collectAsState()
    val workouts by workoutsViewModel.filteredWorkouts.collectAsState()
    val favorites by repository.favorites.collectAsState()

    var assistantVisible by rememberSaveable { mutableStateOf(false) }

    val sortLabel = when (sort) {
        WorkoutSort.DURATION_ASC -> stringResource(R.string.sort_duration_asc)
        WorkoutSort.DURATION_DESC -> stringResource(R.string.sort_duration_desc)
        WorkoutSort.NAME_ASC -> stringResource(R.string.sort_name_asc)
    }

    val assistantActive = assistantFilters.maxDurationMinutes != null ||
        assistantFilters.minDurationMinutes != null ||
        assistantFilters.highIntensityOnly ||
        assistantFilters.lowIntensityOnly

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = Dimens.ContainerMargin,
                end = Dimens.ContainerMargin,
                top = Dimens.Lg,
                bottom = 100.dp
            )
        ) {
            item(key = "header") {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.workouts_title),
                        style = HeadlineLgMobile,
                        color = OnSurface,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = workoutsViewModel::cycleSort) {
                        Text(sortLabel, style = BodyMd, color = Primary)
                    }
                }
            }

            if (assistantActive) {
                item(key = "assistant_banner") {
                    AssistantActiveBanner(
                        onClear = workoutsViewModel::clearAssistantConstraints
                    )
                }
            }

            item(key = "search") {
                Spacer(Modifier.height(Dimens.Sm))
                WorkoutSearchBar(
                    query = searchQuery,
                    onQueryChange = workoutsViewModel::setSearchQuery
                )
            }

            item(key = "filters") {
                WorkoutFilterChips(
                    favoritesOnly = favoritesOnly,
                    selectedType = selectedFilter,
                    onFavoritesClick = workoutsViewModel::onFavoritesChipClick,
                    onSelectAll = workoutsViewModel::selectAll,
                    onTypeSelected = workoutsViewModel::selectType
                )
            }

            if (workouts.isEmpty()) {
                item(key = "empty") {
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
                }
            } else {
                items(
                    items = workouts,
                    key = { it.id }
                ) { workout ->
                    WorkoutListCard(
                        workout = workout,
                        isFavorite = favorites.contains(workout.id),
                        onFavoriteToggle = { repository.toggleFavorite(workout.id) },
                        onClick = { onWorkoutClick(workout.id) },
                        modifier = Modifier.padding(bottom = Dimens.Md)
                    )
                }
            }
        }

        WorkoutAssistantFab(
            onClick = { assistantVisible = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 22.dp, bottom = 72.dp)
        )

        WorkoutAssistantSheet(
            visible = assistantVisible,
            onDismiss = { assistantVisible = false },
            onApply = workoutsViewModel::applyAssistant
        )
    }
}

@Composable
private fun AssistantActiveBanner(onClear: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Dimens.Sm, bottom = Dimens.Xs)
            .clip(RoundedCornerShape(12.dp))
            .background(PrimaryAccent.copy(alpha = 0.12f))
            .padding(horizontal = Dimens.Md, vertical = Dimens.Sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.workout_assistant_active_banner),
            style = BodyMd.copy(fontSize = 13.sp),
            color = PrimaryAccent,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = stringResource(R.string.workout_assistant_clear),
            style = BodyMd.copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
            color = OnSurface,
            modifier = Modifier.clickable(onClick = onClear)
        )
    }
}
