package com.example.vigorly.ui.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.PlaylistAdd
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.core.testing.VigorlyTestTags
import com.example.vigorly.data.model.WorkoutDetail
import com.example.vigorly.data.model.WorkoutPlaylist
import com.example.vigorly.ui.components.FrostedGlassCircleButton
import com.example.vigorly.ui.components.GlassSurface
import com.example.vigorly.ui.components.RecommendedWorkoutCard
import com.example.vigorly.ui.components.RemoteCoverImage
import com.example.vigorly.ui.components.WorkoutSearchBar
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.GlassCrystalBase
import com.example.vigorly.ui.theme.GlassCrystalEdge
import com.example.vigorly.ui.theme.GlassCrystalLift
import com.example.vigorly.ui.theme.GlassCrystalSheen
import com.example.vigorly.ui.theme.GlassLabel
import com.example.vigorly.ui.theme.HeadlineLgMobile
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.OnSurface

@Composable
fun FavoritesBrowseScreen(
    favorites: List<WorkoutDetail>,
    playlists: List<WorkoutPlaylist>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onBack: () -> Unit,
    onWorkoutClick: (String) -> Unit,
    onFavoriteToggle: (String) -> Unit,
    onCreatePlaylist: (name: String, workoutIds: List<String>) -> Unit,
    onOpenPlaylist: (WorkoutPlaylist) -> Unit,
    onDeletePlaylist: (WorkoutPlaylist) -> Unit,
    onFilterClick: () -> Unit,
    filterActive: Boolean,
    pickerCandidates: List<WorkoutDetail> = favorites,
    modifier: Modifier = Modifier
) {
    var showCreateDialog by remember { mutableStateOf(false) }

    val query = searchQuery.trim().lowercase()
    val filteredFavorites = remember(favorites, query) {
        if (query.isBlank()) favorites
        else favorites.filter {
            it.name.lowercase().contains(query) ||
                it.targetDescription.lowercase().contains(query) ||
                it.targetMuscles.lowercase().contains(query) ||
                it.type.name.lowercase().contains(query)
        }
    }
    val customLists = remember(playlists, query) {
        playlists.filterNot { it.isAuto }.filter {
            query.isBlank() || it.name.lowercase().contains(query)
        }
    }
    val workoutById = remember(pickerCandidates) {
        pickerCandidates.associateBy { it.id }
    }

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
                text = stringResource(R.string.workouts_favorites_section),
                style = HeadlineLgMobile.copy(
                    fontSize = 24.sp,
                    lineHeight = 30.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.4).sp
                ),
                color = OnSurface,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = Dimens.ContainerMargin,
                end = Dimens.ContainerMargin,
                bottom = Dimens.Xl
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item(key = "search_row") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    WorkoutSearchBar(
                        query = searchQuery,
                        onQueryChange = onSearchChange,
                        modifier = Modifier.weight(1f),
                        compact = true
                    )
                    GlassFilterButton(active = filterActive, onClick = onFilterClick)
                }
            }

            item(key = "lists_header") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.workout_lists_title),
                        style = HeadlineMd.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = (-0.2).sp
                        ),
                        color = OnSurface
                    )
                    FrostedGlassCircleButton(
                        onClick = { showCreateDialog = true },
                        size = 36.dp
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.workout_lists_create),
                            tint = OnSurface,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            if (customLists.isEmpty()) {
                item(key = "lists_empty") {
                    Text(
                        text = stringResource(R.string.workout_lists_empty_hint),
                        style = BodyMd.copy(fontSize = 14.sp, letterSpacing = 0.1.sp),
                        color = GlassLabel.copy(alpha = 0.75f)
                    )
                }
            } else {
                items(customLists, key = { it.id }) { playlist ->
                    val cover = playlist.workoutIds.firstOrNull()?.let { workoutById[it]?.heroImageUrl }
                    PlaylistRow(
                        name = playlist.name,
                        count = playlist.workoutIds.size,
                        coverUrl = cover,
                        onClick = { onOpenPlaylist(playlist) },
                        onDelete = { onDeletePlaylist(playlist) }
                    )
                }
            }

            item(key = "fav_header") {
                Text(
                    text = stringResource(R.string.workouts_favorites_section),
                    style = HeadlineMd.copy(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.2).sp
                    ),
                    color = OnSurface,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (filteredFavorites.isEmpty()) {
                item(key = "fav_empty") {
                    Text(
                        text = stringResource(R.string.workouts_favorites_empty),
                        style = BodyMd.copy(fontSize = 15.sp, letterSpacing = 0.1.sp),
                        color = GlassLabel.copy(alpha = 0.8f)
                    )
                }
            } else {
                items(filteredFavorites, key = { it.id }) { workout ->
                    RecommendedWorkoutCard(
                        workout = workout,
                        isFavorite = true,
                        onFavoriteToggle = { onFavoriteToggle(workout.id) },
                        onClick = { onWorkoutClick(workout.id) }
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        PlaylistEditorDialog(
            title = stringResource(R.string.workout_lists_create),
            initialName = "",
            initialSelectedIds = emptySet(),
            candidates = pickerCandidates,
            showNameField = true,
            confirmLabel = stringResource(R.string.workout_lists_save),
            takenNames = playlists.map { it.name }.toSet(),
            onDismiss = { showCreateDialog = false },
            onConfirm = { name, ids ->
                onCreatePlaylist(name, ids)
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun GlassFilterButton(
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(
                Brush.verticalGradient(
                    colors = if (active) {
                        listOf(
                            Color.White.copy(alpha = 0.22f),
                            Color.White.copy(alpha = 0.10f)
                        )
                    } else {
                        listOf(GlassCrystalLift, GlassCrystalBase)
                    }
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(GlassCrystalEdge, GlassCrystalSheen)
                ),
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Tune,
            contentDescription = stringResource(R.string.workout_filter_title),
            tint = OnSurface,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun PlaylistRow(
    name: String,
    count: Int,
    coverUrl: String?,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    GlassSurface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.06f))
            ) {
                if (coverUrl != null) {
                    RemoteCoverImage(url = coverUrl, contentDescription = name)
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.PlaylistAdd,
                        contentDescription = null,
                        tint = GlassLabel.copy(alpha = 0.7f),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(26.dp)
                    )
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp)
            ) {
                Text(
                    text = name,
                    style = HeadlineMd.copy(
                        fontSize = 18.sp,
                        lineHeight = 23.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.2).sp
                    ),
                    color = OnSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(R.string.workout_lists_count, count),
                    style = BodyMd.copy(
                        fontSize = 14.sp,
                        lineHeight = 19.sp,
                        letterSpacing = 0.1.sp
                    ),
                    color = GlassLabel.copy(alpha = 0.88f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Icon(
                imageVector = Icons.Outlined.DeleteOutline,
                contentDescription = stringResource(R.string.workout_lists_delete),
                tint = GlassLabel.copy(alpha = 0.85f),
                modifier = Modifier
                    .size(22.dp)
                    .clickable(onClick = onDelete)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = GlassLabel.copy(alpha = 0.85f),
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(20.dp)
            )
        }
    }
}
