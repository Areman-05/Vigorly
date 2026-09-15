package com.example.vigorly.ui.session

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.data.model.SessionSummary
import com.example.vigorly.ui.components.GlassSurface
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.DisplayStat
import com.example.vigorly.ui.theme.GlassLabel
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.SurfaceContainer
import com.example.vigorly.ui.theme.SurfaceContainerLowest
import com.example.vigorly.ui.workout.WorkoutDetailStartCta
import com.example.vigorly.util.TimeFormatter
import com.example.vigorly.util.WorkoutLabels

@Composable
fun SessionSummaryScreen(
    summary: SessionSummary,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    coverUrl: String? = null
) {
    @Suppress("UNUSED_PARAMETER")
    val ignoredCover = coverUrl
    val typeLabel = WorkoutLabels.typeLabel(summary.workoutType)
    val detailShape = RoundedCornerShape(22.dp)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceContainerLowest)
    ) {
        // Soft celebration glow + confetti shapes (like reference)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        PrimaryAccent.copy(alpha = 0.28f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.15f, h * 0.08f),
                    radius = w * 0.55f
                ),
                center = Offset(w * 0.15f, h * 0.08f),
                radius = w * 0.55f
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF6B1A2A).copy(alpha = 0.35f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.9f, h * 0.05f),
                    radius = w * 0.5f
                ),
                center = Offset(w * 0.9f, h * 0.05f),
                radius = w * 0.5f
            )
            val triColors = listOf(
                Color(0xFFFF6B4A).copy(alpha = 0.45f),
                Color(0xFFB48CFF).copy(alpha = 0.4f),
                Color(0xFFB8E63A).copy(alpha = 0.35f),
                Color(0xFFFF2D55).copy(alpha = 0.4f)
            )
            val positions = listOf(
                Offset(w * 0.12f, h * 0.18f) to 18f,
                Offset(w * 0.78f, h * 0.14f) to 14f,
                Offset(w * 0.88f, h * 0.28f) to 16f,
                Offset(w * 0.22f, h * 0.32f) to 12f,
                Offset(w * 0.65f, h * 0.22f) to 11f
            )
            positions.forEachIndexed { i, (origin, side) ->
                val path = Path().apply {
                    moveTo(origin.x, origin.y - side)
                    lineTo(origin.x + side * 0.9f, origin.y + side * 0.55f)
                    lineTo(origin.x - side * 0.9f, origin.y + side * 0.55f)
                    close()
                }
                drawPath(path, color = triColors[i % triColors.size])
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = Dimens.ContainerMargin)
                .padding(top = 48.dp, bottom = Dimens.Xl)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Hero glass card — larger + lower than detail list
                GlassSurface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 22.dp, vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🎉",
                            fontSize = 30.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        Text(
                            text = summary.workoutName,
                            style = HeadlineMd.copy(
                                fontSize = 28.sp,
                                lineHeight = 32.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.4).sp
                            ),
                            color = OnSurface,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = stringResource(R.string.session_finished_status),
                            style = BodyMd.copy(fontSize = 15.sp),
                            color = OnSurface.copy(alpha = 0.72f),
                            modifier = Modifier.padding(top = 6.dp, bottom = 24.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            SummaryHeroStat(
                                label = stringResource(R.string.summary_stat_type),
                                value = typeLabel
                            )
                            SummaryHeroStat(
                                label = stringResource(R.string.summary_stat_duration),
                                value = TimeFormatter.formatElapsed(summary.elapsedSeconds)
                            )
                            SummaryHeroStat(
                                label = stringResource(R.string.summary_stat_calories),
                                value = summary.caloriesBurned.toString()
                            )
                        }
                    }
                }

                // Solid opaque detail list (not glass) — contrast vs hero
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(detailShape)
                        .background(SurfaceContainer)
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.08f),
                            shape = detailShape
                        )
                        .padding(horizontal = 18.dp, vertical = 6.dp)
                ) {
                    SummaryDetailRow(
                        label = stringResource(R.string.summary_stat_type),
                        value = typeLabel
                    )
                    SummaryDetailDivider()
                    SummaryDetailRow(
                        label = stringResource(R.string.summary_stat_duration),
                        value = TimeFormatter.formatElapsed(summary.elapsedSeconds)
                    )
                    SummaryDetailDivider()
                    SummaryDetailRow(
                        label = stringResource(R.string.summary_stat_calories),
                        value = summary.caloriesBurned.toString()
                    )
                    SummaryDetailDivider()
                    SummaryDetailRow(
                        label = stringResource(R.string.summary_stat_focus),
                        value = summary.focusZone.ifBlank { "—" }
                    )
                    SummaryDetailDivider()
                    SummaryDetailRow(
                        label = stringResource(R.string.summary_stat_exercises),
                        value = stringResource(
                            R.string.summary_meta_exercises,
                            summary.exercisesCompleted,
                            summary.totalExercises
                        )
                    )
                    if (summary.levelAfter > summary.levelBefore) {
                        SummaryDetailDivider()
                        SummaryDetailRow(
                            label = stringResource(R.string.summary_stat_level_up),
                            value = stringResource(
                                R.string.summary_level_up_value,
                                summary.levelBefore,
                                summary.levelAfter
                            )
                        )
                    } else {
                        SummaryDetailDivider()
                        SummaryDetailRow(
                            label = stringResource(R.string.summary_stat_level),
                            value = stringResource(
                                R.string.summary_level_progress,
                                summary.levelAfter,
                                summary.workoutsUntilNextLevel
                            )
                        )
                    }
                    SummaryDetailDivider()
                    SummaryDetailRow(
                        label = stringResource(R.string.summary_stat_streak),
                        value = stringResource(R.string.dashboard_snap_streak_value, summary.streakDays)
                    )
                    SummaryDetailDivider()
                    SummaryDetailRow(
                        label = stringResource(R.string.summary_stat_weekly),
                        value = stringResource(
                            R.string.dashboard_snap_week_value,
                            summary.weeklyCompleted,
                            summary.weeklyTarget
                        )
                    )
                    if (summary.newlyUnlockedTitles.isNotEmpty()) {
                        SummaryDetailDivider()
                        SummaryDetailRow(
                            label = stringResource(R.string.summary_stat_achievements),
                            value = summary.newlyUnlockedTitles.joinToString(", ")
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            WorkoutDetailStartCta(
                onClick = onDone,
                labelRes = R.string.session_done,
                showPlayIcon = false,
                cornerRadius = 18.dp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SummaryHeroStat(
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = BodyMd.copy(fontSize = 12.sp),
            color = GlassLabel.copy(alpha = 0.78f)
        )
        Text(
            text = value,
            style = DisplayStat.copy(
                fontSize = 18.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Bold
            ),
            color = OnSurface,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}

@Composable
private fun SummaryDetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = BodyMd.copy(fontSize = 15.sp),
            color = OnSurface.copy(alpha = 0.78f)
        )
        Text(
            text = value,
            style = BodyMd.copy(fontSize = 15.sp, fontWeight = FontWeight.Medium),
            color = OnSurface
        )
    }
}

@Composable
private fun SummaryDetailDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color.White.copy(alpha = 0.08f))
    )
}
