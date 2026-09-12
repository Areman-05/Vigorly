package com.example.vigorly.ui.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.core.testing.VigorlyTestTags
import com.example.vigorly.data.model.WorkoutDetail
import com.example.vigorly.ui.components.FrostedGlassCircleButton
import com.example.vigorly.ui.components.WorkoutBrowseRow
import com.example.vigorly.ui.components.WorkoutSearchBar
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.GlassLabel
import com.example.vigorly.ui.theme.HeadlineLgMobile
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.PrimaryAccent

@Composable
fun PlaylistDetailScreen(
    title: String,
    workouts: List<WorkoutDetail>,
    favorites: Set<String>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onBack: () -> Unit,
    onWorkoutClick: (String) -> Unit,
    onFavoriteToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
    canEdit: Boolean = false,
    onEdit: (() -> Unit)? = null
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
                text = title,
                style = HeadlineLgMobile.copy(
                    fontSize = 22.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.4).sp
                ),
                color = OnSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 56.dp)
            )
            if (canEdit && onEdit != null) {
                FrostedGlassCircleButton(
                    onClick = onEdit,
                    size = 40.dp,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = stringResource(R.string.workout_lists_edit),
                        tint = OnSurface,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = Dimens.ContainerMargin,
                end = Dimens.ContainerMargin,
                bottom = Dimens.Xl
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(key = "search") {
                WorkoutSearchBar(
                    query = searchQuery,
                    onQueryChange = onSearchChange,
                    modifier = Modifier.fillMaxWidth(),
                    compact = true
                )
            }

            if (workouts.isEmpty()) {
                item(key = "empty") {
                    Text(
                        text = stringResource(R.string.workout_lists_detail_empty),
                        style = BodyMd.copy(fontSize = 14.sp, letterSpacing = 0.1.sp),
                        color = GlassLabel.copy(alpha = 0.85f),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                    if (canEdit && onEdit != null) {
                        TextButton(onClick = onEdit) {
                            Text(
                                text = stringResource(R.string.workout_lists_edit),
                                color = PrimaryAccent
                            )
                        }
                    }
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
