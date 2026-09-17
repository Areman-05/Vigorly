package com.example.vigorly.ui.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vigorly.R
import com.example.vigorly.core.testing.VigorlyTestTags
import com.example.vigorly.data.catalog.WorkoutCoverUrls
import com.example.vigorly.data.model.WorkoutDetail
import com.example.vigorly.data.model.WorkoutType
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.di.AppViewModelFactory
import com.example.vigorly.ui.components.FrostedGlassCircleButton
import com.example.vigorly.ui.components.RecommendedWorkoutCard
import com.example.vigorly.ui.components.RemoteCoverImage
import com.example.vigorly.ui.components.WorkoutBrowseRow
import com.example.vigorly.ui.components.WorkoutListCard
import com.example.vigorly.ui.components.WorkoutSearchBar
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.GlassLabel
import com.example.vigorly.ui.theme.HeadlineLgMobile
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer
import com.example.vigorly.util.WorkoutBrowseFilters
import com.example.vigorly.util.WorkoutLabels

@Composable
fun WorkoutsScreen(
    repository: VigorlyRepository,
    onWorkoutClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    onFilterOverlayChange: (Boolean) -> Unit = {},
    workoutsViewModel: WorkoutsViewModel = viewModel(factory = AppViewModelFactory(repository))
) {
    val searchQuery by workoutsViewModel.searchQuery.collectAsState()
    val selectedType by workoutsViewModel.selectedType.collectAsState()
    val favoritesOnly by workoutsViewModel.favoritesOnly.collectAsState()
    val selectedPlaylistId by workoutsViewModel.selectedPlaylistId.collectAsState()
    val browseFilters by workoutsViewModel.browseFilters.collectAsState()
    val workouts by workoutsViewModel.filteredWorkouts.collectAsState()
    val favoriteWorkouts by workoutsViewModel.favoriteWorkouts.collectAsState()
    val favorites by repository.favorites.collectAsState()
    val playlists by repository.playlists.collectAsState()

    var filterVisible by rememberSaveable { mutableStateOf(false) }
    var chipLow by rememberSaveable { mutableStateOf(false) }
    var chipModerate by rememberSaveable { mutableStateOf(false) }
    var chipHigh by rememberSaveable { mutableStateOf(false) }

    val categoryMode = selectedType != null && searchQuery.isBlank() && !favoritesOnly
    val favoritesMode = favoritesOnly && selectedPlaylistId == null
    val playlistMode = favoritesOnly && selectedPlaylistId != null

    LaunchedEffect(filterVisible, categoryMode, favoritesOnly) {
        onFilterOverlayChange(filterVisible || categoryMode || favoritesOnly)
    }
    DisposableEffect(Unit) {
        onDispose { onFilterOverlayChange(false) }
    }

    LaunchedEffect(selectedType) {
        chipLow = false
        chipModerate = false
        chipHigh = false
    }

    val browseHome = searchQuery.isBlank() &&
        selectedType == null &&
        !favoritesOnly &&
        browseFilters.isEmpty

    if (playlistMode) {
        val playlist = playlists.firstOrNull { it.id == selectedPlaylistId }
        var showEditPlaylist by remember(selectedPlaylistId) { mutableStateOf(false) }
        val resolved = remember(selectedPlaylistId, playlists, searchQuery) {
            val pl = playlists.firstOrNull { it.id == selectedPlaylistId }
            val all = repository.listWorkouts()
            val list = pl?.workoutIds?.mapNotNull { id -> all.find { it.id == id } }.orEmpty()
            val q = searchQuery.trim().lowercase()
            if (q.isBlank()) list
            else list.filter {
                it.name.lowercase().contains(q) ||
                    it.targetDescription.lowercase().contains(q) ||
                    it.type.name.lowercase().contains(q)
            }
        }
        Box(modifier = modifier.fillMaxSize()) {
            PlaylistDetailScreen(
                title = playlist?.name ?: stringResource(R.string.workout_lists_title),
                workouts = resolved,
                favorites = favorites,
                searchQuery = searchQuery,
                onSearchChange = workoutsViewModel::setSearchQuery,
                onBack = workoutsViewModel::clearSelectedPlaylist,
                onWorkoutClick = onWorkoutClick,
                onFavoriteToggle = repository::toggleFavorite,
                canEdit = playlist != null,
                onEdit = { showEditPlaylist = true },
                onDeleteList = {
                    playlist?.id?.let { repository.deletePlaylist(it) }
                    workoutsViewModel.clearSelectedPlaylist()
                },
                onRemoveWorkout = { workoutId ->
                    playlist?.let {
                        repository.updatePlaylistWorkouts(
                            it.id,
                            it.workoutIds.filterNot { id -> id == workoutId }
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        if (showEditPlaylist && playlist != null) {
            PlaylistEditorDialog(
                title = stringResource(R.string.workout_lists_edit),
                initialName = playlist.name,
                initialSelectedIds = playlist.workoutIds.toSet(),
                candidates = favoriteWorkouts,
                showNameField = true,
                confirmLabel = stringResource(R.string.workout_lists_update),
                takenNames = playlists.map { it.name }.toSet(),
                currentName = playlist.name,
                onDismiss = { showEditPlaylist = false },
                onConfirm = { name, ids ->
                    repository.renamePlaylist(playlist.id, name)
                    repository.updatePlaylistWorkouts(playlist.id, ids)
                    showEditPlaylist = false
                }
            )
        }
        return
    }

    if (favoritesMode) {
        Box(modifier = modifier.fillMaxSize()) {
            FavoritesBrowseScreen(
                favorites = workouts,
                playlists = playlists,
                searchQuery = searchQuery,
                onSearchChange = workoutsViewModel::setSearchQuery,
                onBack = workoutsViewModel::clearFavoritesMode,
                onWorkoutClick = onWorkoutClick,
                onFavoriteToggle = repository::toggleFavorite,
                onCreatePlaylist = { name, ids ->
                    repository.createPlaylist(name, ids)
                },
                onOpenPlaylist = { playlist -> workoutsViewModel.openPlaylist(playlist.id) },
                onDeletePlaylist = { playlist -> repository.deletePlaylist(playlist.id) },
                onFilterClick = { filterVisible = true },
                filterActive = !browseFilters.isEmpty,
                pickerCandidates = favoriteWorkouts,
                modifier = Modifier.fillMaxSize()
            )
            WorkoutFilterPanel(
                visible = filterVisible,
                initial = browseFilters,
                onDismiss = { filterVisible = false },
                onApply = workoutsViewModel::applyBrowseFilters
            )
        }
        return
    }

    if (categoryMode && selectedType != null) {
        CategoryBrowseScreen(
            type = selectedType!!,
            workouts = workouts.filter { workout ->
                matchesIntensityChips(
                    workout = workout,
                    low = chipLow,
                    moderate = chipModerate,
                    high = chipHigh
                )
            },
            favorites = favorites,
            chipLow = chipLow,
            chipModerate = chipModerate,
            chipHigh = chipHigh,
            onChipLow = { chipLow = !chipLow },
            onChipModerate = { chipModerate = !chipModerate },
            onChipHigh = { chipHigh = !chipHigh },
            onBack = workoutsViewModel::clearSelectedType,
            onWorkoutClick = onWorkoutClick,
            onFavoriteToggle = repository::toggleFavorite,
            modifier = modifier
        )
        return
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .testTag(VigorlyTestTags.WORKOUTS)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = Dimens.ContainerMargin,
                end = Dimens.ContainerMargin,
                top = Dimens.Sm,
                bottom = Dimens.FloatingNavClearance
            )
        ) {
            item(key = "title") {
                Text(
                    text = stringResource(R.string.workouts_title),
                    style = HeadlineLgMobile,
                    color = OnSurface
                )
            }

            item(key = "search_row") {
                Spacer(Modifier.height(Dimens.Md))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    WorkoutSearchBar(
                        query = searchQuery,
                        onQueryChange = workoutsViewModel::setSearchQuery,
                        modifier = Modifier.weight(1f),
                        compact = true
                    )
                    GlassFilterButton(
                        active = !browseFilters.isEmpty,
                        onClick = { filterVisible = true }
                    )
                }
            }

            if (!browseHome) {
                item(key = "active_filters") {
                    Spacer(Modifier.height(Dimens.Sm))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when {
                                favoritesOnly -> stringResource(R.string.favorites_only)
                                selectedType != null -> WorkoutLabels.typeLabel(selectedType!!)
                                else -> stringResource(R.string.workout_filter_results)
                            },
                            style = HeadlineMd.copy(
                                fontSize = 22.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = (-0.2).sp
                            ),
                            color = OnSurface
                        )
                        Text(
                            text = stringResource(R.string.workout_filter_clear),
                            style = BodyMd.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.1.sp
                            ),
                            color = PrimaryAccent,
                            modifier = Modifier.clickable(onClick = workoutsViewModel::clearAllFilters)
                        )
                    }
                    Spacer(Modifier.height(Dimens.Sm))
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
                        }
                    }
                } else {
                    items(workouts, key = { it.id }) { workout ->
                        WorkoutListCard(
                            workout = workout,
                            isFavorite = favorites.contains(workout.id),
                            onFavoriteToggle = { repository.toggleFavorite(workout.id) },
                            onClick = { onWorkoutClick(workout.id) },
                            modifier = Modifier.padding(bottom = Dimens.Md)
                        )
                    }
                }
            } else {
                item(key = "fav_header") {
                    Spacer(Modifier.height(Dimens.Md))
                    SectionHeaderRow(
                        title = stringResource(R.string.workouts_favorites_section),
                        action = stringResource(R.string.dashboard_view_all),
                        onAction = workoutsViewModel::showAllFavorites
                    )
                    Spacer(Modifier.height(Dimens.Sm))
                }
                if (favoriteWorkouts.isNotEmpty()) {
                    item(key = "fav_card") {
                        val fav = favoriteWorkouts.first()
                        RecommendedWorkoutCard(
                            workout = fav,
                            isFavorite = true,
                            onFavoriteToggle = { repository.toggleFavorite(fav.id) },
                            onClick = { onWorkoutClick(fav.id) },
                            modifier = Modifier.padding(bottom = Dimens.Md)
                        )
                    }
                } else {
                    item(key = "fav_empty") {
                        Text(
                            text = stringResource(R.string.workouts_favorites_empty),
                            style = BodyMd.copy(
                                fontSize = 15.sp,
                                letterSpacing = 0.1.sp
                            ),
                            color = GlassLabel.copy(alpha = 0.8f),
                            modifier = Modifier.padding(bottom = Dimens.Md)
                        )
                    }
                }

                item(key = "cat_header") {
                    Spacer(Modifier.height(Dimens.Sm))
                    Text(
                        text = stringResource(R.string.workouts_browse_category),
                        style = HeadlineMd.copy(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = (-0.2).sp
                        ),
                        color = OnSurface
                    )
                    Spacer(Modifier.height(Dimens.Sm))
                }

                items(WorkoutType.entries.toList(), key = { it.name }) { type ->
                    CategoryHeroCard(
                        type = type,
                        coverUrl = WorkoutCoverUrls.category(type),
                        onClick = { workoutsViewModel.selectType(type) },
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }
        }

        WorkoutFilterPanel(
            visible = filterVisible,
            initial = browseFilters,
            onDismiss = { filterVisible = false },
            onApply = workoutsViewModel::applyBrowseFilters
        )
    }
}

@Composable
private fun CategoryBrowseScreen(
    type: WorkoutType,
    workouts: List<WorkoutDetail>,
    favorites: Set<String>,
    chipLow: Boolean,
    chipModerate: Boolean,
    chipHigh: Boolean,
    onChipLow: () -> Unit,
    onChipModerate: () -> Unit,
    onChipHigh: () -> Unit,
    onBack: () -> Unit,
    onWorkoutClick: (String) -> Unit,
    onFavoriteToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .testTag(VigorlyTestTags.WORKOUTS)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.ContainerMargin, vertical = 8.dp)
        ) {
            FrostedGlassCircleButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .testTag(VigorlyTestTags.TOPBAR_BACK)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.go_back),
                    tint = OnSurface,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = WorkoutLabels.typeLabel(type),
                style = HeadlineLgMobile.copy(
                    fontSize = 26.sp,
                    lineHeight = 32.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.45).sp
                ),
                color = OnSurface,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.ContainerMargin)
                .padding(bottom = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CategoryFilterChip(
                label = stringResource(R.string.intensity_low),
                selected = chipLow,
                onClick = onChipLow,
                modifier = Modifier.weight(1f)
            )
            CategoryFilterChip(
                label = stringResource(R.string.intensity_moderate),
                selected = chipModerate,
                onClick = onChipModerate,
                modifier = Modifier.weight(1f)
            )
            CategoryFilterChip(
                label = stringResource(R.string.intensity_high),
                selected = chipHigh,
                onClick = onChipHigh,
                modifier = Modifier.weight(1f)
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = Dimens.ContainerMargin,
                end = Dimens.ContainerMargin,
                top = 4.dp,
                bottom = Dimens.Xl
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            if (workouts.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.no_workouts_found),
                        style = BodyMd.copy(fontSize = 15.sp, letterSpacing = 0.1.sp),
                        color = GlassLabel,
                        modifier = Modifier.padding(vertical = Dimens.Xl)
                    )
                }
            } else {
                items(workouts, key = { it.id }) { workout ->
                    WorkoutBrowseRow(
                        workout = workout,
                        isFavorite = workout.id in favorites,
                        onFavoriteToggle = { onFavoriteToggle(workout.id) },
                        onClick = { onWorkoutClick(workout.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(999.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                if (selected) {
                    Brush.verticalGradient(
                        colors = listOf(
                            PrimaryAccent.copy(alpha = 0.95f),
                            PrimaryContainer.copy(alpha = 0.85f)
                        )
                    )
                } else {
                    Brush.verticalGradient(
                        colors = listOf(
                            com.example.vigorly.ui.theme.GlassCrystalLift,
                            com.example.vigorly.ui.theme.GlassCrystalBase
                        )
                    )
                }
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = if (selected) {
                        listOf(Color.White.copy(alpha = 0.35f), Color.White.copy(alpha = 0.12f))
                    } else {
                        listOf(
                            com.example.vigorly.ui.theme.GlassCrystalEdge,
                            com.example.vigorly.ui.theme.GlassCrystalSheen
                        )
                    }
                ),
                shape = shape
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = BodyMd.copy(
                fontSize = 15.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                letterSpacing = 0.15.sp
            ),
            color = if (selected) Color.White else OnSurface.copy(alpha = 0.92f),
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

private fun matchesIntensityChips(
    workout: WorkoutDetail,
    low: Boolean,
    moderate: Boolean,
    high: Boolean
): Boolean {
    val intensityFilters = buildList {
        if (low) add("low")
        if (moderate) add("moderate")
        if (high) add("high")
    }
    if (intensityFilters.isEmpty()) return true
    val key = WorkoutBrowseFilters.intensityKey(workout.intensity)
    return key in intensityFilters
}

@Composable
private fun SectionHeaderRow(
    title: String,
    action: String,
    onAction: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = HeadlineMd.copy(
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.2).sp
            ),
            color = OnSurface
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(onClick = onAction)
        ) {
            Text(
                text = action,
                style = BodyMd.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.1.sp
                ),
                color = GlassLabel.copy(alpha = 0.85f)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = GlassLabel.copy(alpha = 0.85f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun CategoryHeroCard(
    type: WorkoutType,
    coverUrl: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(22.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(148.dp)
            .clip(shape)
            .clickable(onClick = onClick)
    ) {
        RemoteCoverImage(url = coverUrl, contentDescription = WorkoutLabels.typeLabel(type))
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.35f),
                            Color.Black.copy(alpha = 0.78f)
                        )
                    )
                )
        )
        Text(
            text = WorkoutLabels.typeLabel(type),
            style = HeadlineMd.copy(
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.4).sp
            ),
            color = OnSurface,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(Dimens.Md)
        )
    }
}
