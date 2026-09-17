package com.example.vigorly.ui.session

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

    Box(modifier = modifier.fillMaxSize()) {
        SessionStageBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = Dimens.ContainerMargin)
                .padding(top = 48.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 120.dp),
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
                        .padding(bottom = 8.dp)
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

        // CTA flotante: la card puede quedar detrás; fade transparente, sin barra negra.
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            SurfaceContainerLowest.copy(alpha = 0.55f),
                            SurfaceContainerLowest.copy(alpha = 0.92f)
                        )
                    )
                )
                .navigationBarsPadding()
                .padding(horizontal = Dimens.ContainerMargin)
                .padding(top = 28.dp, bottom = 16.dp)
        ) {
            WorkoutDetailStartCta(
                onClick = onDone,
                labelRes = R.string.session_done,
                showPlayIcon = false,
                cornerRadius = 999.dp,
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
