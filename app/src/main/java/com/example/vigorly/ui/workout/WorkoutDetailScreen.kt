package com.example.vigorly.ui.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.vigorly.R
import com.example.vigorly.core.testing.VigorlyTestTags
import com.example.vigorly.data.model.Exercise
import com.example.vigorly.data.model.WorkoutDetail
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.ui.components.FrostedGlassCircleButton
import com.example.vigorly.ui.components.GlassSurface
import com.example.vigorly.ui.components.RemoteCoverImage
import com.example.vigorly.ui.theme.Background
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.GlassLabel
import com.example.vigorly.ui.theme.HeadlineLgMobile
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.SurfaceContainer
import com.example.vigorly.util.WorkoutDetailBrief
import com.example.vigorly.util.WorkoutLabels
import java.util.Locale

@Composable
fun WorkoutDetailScreen(
    workout: WorkoutDetail,
    repository: VigorlyRepository,
    onStartWorkout: () -> Unit,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val favorites by repository.favorites.collectAsState()
    val isFavorite = workout.id in favorites
    val intensity = WorkoutLabels.intensityLabel(workout.intensity)
    val typeLabel = WorkoutLabels.typeLabel(workout.type)
    val exercises = remember(workout.id, workout.blocks) {
        workout.blocks.flatMap { it.exercises }
    }
    var showPlan by remember { mutableStateOf(false) }
    val brief = remember(workout.id, workout.type, workout.durationMinutes, workout.intensity, workout.targetMuscles, workout.targetDescription, workout.blocks) {
        WorkoutDetailBrief.forWorkout(workout, Locale.getDefault())
    }

    if (showPlan) {
        SessionPlanDialog(
            exercises = exercises,
            onDismiss = { showPlan = false }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            RemoteCoverImage(
                url = workout.heroImageUrl,
                contentDescription = workout.name
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.28f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.55f)
                            )
                        )
                    )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FrostedGlassCircleButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag(VigorlyTestTags.TOPBAR_BACK)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.go_back),
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                FrostedGlassCircleButton(
                    onClick = { repository.toggleFavorite(workout.id) }
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = stringResource(R.string.workout_favorite_cd),
                        tint = if (isFavorite) PrimaryAccent else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.ContainerMargin)
                .padding(top = Dimens.Lg, bottom = Dimens.Xl)
        ) {
            Text(
                text = workout.name,
                style = HeadlineLgMobile.copy(
                    fontSize = 32.sp,
                    lineHeight = 36.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                ),
                color = OnSurface,
                modifier = Modifier.padding(bottom = Dimens.Md)
            )

            DetailMetaRow(
                icon = Icons.Outlined.Timer,
                text = stringResource(
                    R.string.workout_detail_meta_duration,
                    workout.durationMinutes,
                    intensity
                )
            )
            DetailMetaRow(
                icon = Icons.Outlined.LocalFireDepartment,
                text = stringResource(
                    R.string.workout_detail_meta_calories,
                    workout.estimatedCalories
                )
            )
            DetailMetaRow(
                icon = Icons.Outlined.Person,
                text = workout.targetMuscles.ifBlank { workout.targetDescription }
            )
            DetailMetaRow(
                icon = Icons.Outlined.Tv,
                text = stringResource(R.string.workout_detail_meta_format, typeLabel)
            )
            DetailMetaRow(
                icon = Icons.Outlined.FitnessCenter,
                text = stringResource(R.string.workout_detail_meta_equipment)
            )

            Text(
                text = brief,
                style = BodyMd.copy(
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    letterSpacing = 0.05.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = OnSurface.copy(alpha = 0.9f),
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = Dimens.Lg)
            )

            if (exercises.isNotEmpty()) {
                GlassSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimens.Xl),
                    shape = RoundedCornerShape(20.dp),
                    onClick = { showPlan = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White.copy(alpha = 0.08f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.FitnessCenter,
                                contentDescription = null,
                                tint = OnSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 14.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.workout_detail_exercises),
                                style = HeadlineMd.copy(
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = (-0.2).sp
                                ),
                                color = OnSurface
                            )
                            Text(
                                text = stringResource(
                                    R.string.workout_detail_exercises_hint
                                ),
                                style = BodyMd.copy(fontSize = 13.sp),
                                color = GlassLabel.copy(alpha = 0.75f),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        Text(
                            text = exercises.size.toString(),
                            style = BodyMd.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = GlassLabel.copy(alpha = 0.7f)
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = GlassLabel.copy(alpha = 0.55f),
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .size(22.dp)
                        )
                    }
                }
            }

            WorkoutDetailStartCta(
                onClick = onStartWorkout,
                labelRes = R.string.start_exercise,
                showPlayIcon = false,
                cornerRadius = 18.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.Xl)
            )
        }
    }
}

@Composable
private fun SessionPlanDialog(
    exercises: List<Exercise>,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(SurfaceContainer)
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.workout_detail_exercises),
                    style = HeadlineMd.copy(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.3).sp
                    ),
                    color = OnSurface,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = exercises.size.toString(),
                    style = BodyMd.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = GlassLabel.copy(alpha = 0.7f),
                    modifier = Modifier.padding(end = 4.dp)
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = OnSurface)
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(top = 8.dp)
            ) {
                exercises.forEachIndexed { index, exercise ->
                    SessionExerciseRow(
                        index = index + 1,
                        name = exercise.name,
                        detail = exercise.setsRepsLabel,
                        imageUrl = exercise.imageUrl,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailMetaRow(
    icon: ImageVector,
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = OnSurface.copy(alpha = 0.88f),
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = text,
            style = BodyMd.copy(
                fontSize = 16.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.15.sp
            ),
            color = OnSurface.copy(alpha = 0.94f)
        )
    }
}

@Composable
private fun SessionExerciseRow(
    index: Int,
    name: String,
    detail: String,
    imageUrl: String?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(Color.White.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = index.toString(),
                style = BodyMd.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = OnSurface.copy(alpha = 0.85f)
            )
        }
        Box(
            modifier = Modifier
                .padding(start = 10.dp)
                .size(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.06f))
        ) {
            if (!imageUrl.isNullOrBlank()) {
                RemoteCoverImage(url = imageUrl, contentDescription = name)
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        ) {
            Text(
                text = name,
                style = BodyMd.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.1).sp
                ),
                color = OnSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (detail.isNotBlank()) {
                Text(
                    text = detail,
                    style = BodyMd.copy(fontSize = 13.sp, fontWeight = FontWeight.Medium),
                    color = GlassLabel.copy(alpha = 0.75f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
