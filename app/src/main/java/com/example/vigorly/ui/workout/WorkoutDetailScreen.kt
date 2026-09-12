package com.example.vigorly.ui.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.example.vigorly.R
import com.example.vigorly.core.testing.VigorlyTestTags
import com.example.vigorly.data.model.WorkoutDetail
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.ui.components.FrostedGlassCircleButton
import com.example.vigorly.ui.components.RemoteCoverImage
import com.example.vigorly.ui.theme.Background
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.GlassLabel
import com.example.vigorly.ui.theme.HeadlineLgMobile
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.util.WarmupStep
import com.example.vigorly.util.WorkoutLabels
import com.example.vigorly.util.WorkoutWarmupBuilder

@Composable
fun WorkoutDetailScreen(
    workout: WorkoutDetail,
    repository: VigorlyRepository,
    onStartWorkout: () -> Unit,
    onBackClick: () -> Unit = {},
    onRelatedWorkoutClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val favorites by repository.favorites.collectAsState()
    val isFavorite = workout.id in favorites
    val intensity = WorkoutLabels.intensityLabel(workout.intensity)
    val typeLabel = WorkoutLabels.typeLabel(workout.type)
    val warmups = remember(workout.id) { WorkoutWarmupBuilder.forWorkout(workout) }

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
                text = workout.description,
                style = BodyMd.copy(
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    letterSpacing = 0.1.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = OnSurface.copy(alpha = 0.9f),
                modifier = Modifier.padding(top = Dimens.Lg)
            )

            if (warmups.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimens.Xl, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.workout_detail_warmup),
                        style = HeadlineMd.copy(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = (-0.2).sp
                        ),
                        color = OnSurface
                    )
                    Text(
                        text = stringResource(R.string.workout_detail_related_count, warmups.size),
                        style = BodyMd.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.1.sp
                        ),
                        color = PrimaryAccent,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                Text(
                    text = stringResource(R.string.workout_detail_warmup_hint),
                    style = BodyMd.copy(fontSize = 14.sp, letterSpacing = 0.1.sp),
                    color = GlassLabel.copy(alpha = 0.75f),
                    modifier = Modifier.padding(bottom = Dimens.Sm)
                )

                warmups.forEach { step ->
                    WarmupStepRow(
                        step = step,
                        modifier = Modifier.padding(bottom = 14.dp)
                    )
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
private fun WarmupStepRow(
    step: WarmupStep,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White.copy(alpha = 0.06f))
        ) {
            RemoteCoverImage(url = step.imageUrl, contentDescription = step.name)
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 14.dp)
        ) {
            Text(
                text = step.name,
                style = HeadlineMd.copy(
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.15).sp
                ),
                color = OnSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null,
                    tint = Primary.copy(alpha = 0.9f),
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = step.durationLabel,
                    style = BodyMd.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.15.sp
                    ),
                    color = OnSurface.copy(alpha = 0.85f),
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
        }
    }
}
