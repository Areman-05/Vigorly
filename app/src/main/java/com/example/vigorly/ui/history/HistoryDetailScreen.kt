package com.example.vigorly.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.data.model.WorkoutHistoryItem
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.ui.components.GlassSurface
import com.example.vigorly.ui.iconForName
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.DisplayStat
import com.example.vigorly.ui.theme.GlassLabel
import com.example.vigorly.ui.theme.HeadlineLgMobile
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.workout.WorkoutDetailExerciseRow
import com.example.vigorly.ui.workout.WorkoutDetailSectionEnter
import com.example.vigorly.ui.workout.rememberWorkoutDetailVisible
import com.example.vigorly.util.HistoryLabels
import com.example.vigorly.util.WorkoutLabels

@Composable
fun HistoryDetailScreen(
    item: WorkoutHistoryItem,
    repository: VigorlyRepository,
    modifier: Modifier = Modifier
) {
    val workout = item.workoutId?.let { repository.getWorkout(it) }
    val type = HistoryLabels.parseWorkoutType(item.workoutType) ?: workout?.type
    val exercises = workout?.blocks?.flatMap { it.exercises }.orEmpty()
    val contentVisible = rememberWorkoutDetailVisible()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.ContainerMargin, vertical = Dimens.Md)
    ) {
        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 0) {
            GlassSurface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = iconForName(item.iconName),
                            contentDescription = null,
                            tint = OnSurface,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column(Modifier.padding(start = Dimens.Md)) {
                        val sessionDateFull = HistoryLabels.formatSessionDateFull(item.completedAtMillis)
                        if (sessionDateFull.isNotBlank()) {
                            Text(
                                sessionDateFull.uppercase(),
                                style = LabelCaps.copy(fontSize = 10.sp),
                                color = GlassLabel.copy(alpha = 0.55f)
                            )
                        }
                        Text(
                            stringResource(R.string.session_detail_label),
                            style = LabelCaps,
                            color = GlassLabel.copy(alpha = 0.5f),
                            modifier = Modifier.padding(top = if (sessionDateFull.isNotBlank()) 6.dp else 0.dp)
                        )
                        Text(
                            item.title,
                            style = HeadlineLgMobile.copy(fontSize = 22.sp, lineHeight = 26.sp),
                            color = OnSurface,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Text(
                            HistoryLabels.displayTimestamp(item),
                            style = BodyMd.copy(fontSize = 14.sp),
                            color = GlassLabel.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(Dimens.Md))

        WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 140) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DetailMetricTile(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.summary_stat_duration),
                    value = "${item.durationMinutes}",
                    suffix = "min"
                )
                DetailMetricTile(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.summary_stat_calories),
                    value = "${item.calories}",
                    suffix = "kcal"
                )
            }
        }

        type?.let { workoutType ->
            WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 260) {
                GlassSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimens.Md),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            stringResource(R.string.history_detail_workout).uppercase(),
                            style = LabelCaps.copy(fontSize = 10.sp),
                            color = GlassLabel.copy(alpha = 0.55f)
                        )
                        Text(
                            WorkoutLabels.typeLabel(workoutType),
                            style = BodyMd.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                            color = OnSurface,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                        workout?.targetMuscles?.let { muscles ->
                            Text(
                                muscles,
                                style = BodyMd.copy(fontSize = 13.sp),
                                color = GlassLabel.copy(alpha = 0.65f),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        if (exercises.isNotEmpty()) {
            WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = 380) {
                Text(
                    stringResource(R.string.history_detail_exercises).uppercase(),
                    style = LabelCaps,
                    color = GlassLabel.copy(alpha = 0.55f),
                    modifier = Modifier.padding(top = Dimens.Lg, bottom = Dimens.Sm)
                )
            }
            var delay = 440
            exercises.forEachIndexed { index, exercise ->
                WorkoutDetailSectionEnter(visible = contentVisible, enterDelayMillis = delay) {
                    WorkoutDetailExerciseRow(
                        exercise = exercise,
                        index = index + 1,
                        accent = OnSurface.copy(alpha = 0.85f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                delay += 45
            }
        }

        Spacer(Modifier.height(Dimens.Xl))
    }
}

@Composable
private fun DetailMetricTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    suffix: String? = null
) {
    GlassSurface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp)
        ) {
            Text(
                label.uppercase(),
                style = LabelCaps.copy(fontSize = 10.sp),
                color = GlassLabel.copy(alpha = 0.55f)
            )
            Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.padding(top = 8.dp)) {
                Text(
                    value,
                    style = DisplayStat.copy(fontSize = 26.sp, lineHeight = 28.sp),
                    color = OnSurface
                )
                suffix?.let {
                    Text(
                        it,
                        style = BodyMd.copy(fontSize = 13.sp),
                        color = GlassLabel.copy(alpha = 0.55f),
                        modifier = Modifier.padding(start = 3.dp, bottom = 3.dp)
                    )
                }
            }
        }
    }
}
