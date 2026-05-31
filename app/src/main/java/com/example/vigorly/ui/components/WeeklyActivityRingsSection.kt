package com.example.vigorly.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.data.activity.WeeklyActivityRingDay
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer
import java.time.LocalDate

@Composable
fun WeeklyActivityRingsSection(
    days: List<WeeklyActivityRingDay>,
    weekRangeLabel: String,
    modifier: Modifier = Modifier,
    selectedDate: LocalDate? = null,
    onDayClick: (LocalDate) -> Unit = {},
    animate: Boolean = true
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.activity_weekly_title),
            style = LabelCaps,
            color = OnSurfaceVariant
        )
        if (weekRangeLabel.isNotBlank()) {
            Spacer(Modifier.height(Dimens.Xs))
            Text(
                text = weekRangeLabel,
                style = BodyMd.copy(fontSize = 13.sp),
                color = OnSurfaceVariant.copy(alpha = 0.75f)
            )
        }
        Spacer(Modifier.height(Dimens.Md))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            days.forEach { day ->
                WeeklyActivityRingDayCell(
                    day = day,
                    isSelected = selectedDate == day.date,
                    animate = animate,
                    onClick = { if (!day.isFuture) onDayClick(day.date) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun TodayNebulaAura(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "todayNebula")
    val pulse by transition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "nebulaPulse"
    )

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val baseRadius = size.minDimension * 0.52f * pulse

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    PrimaryAccent.copy(alpha = 0.22f),
                    PrimaryContainer.copy(alpha = 0.10f),
                    Color.Transparent
                ),
                center = center,
                radius = baseRadius
            ),
            radius = baseRadius,
            center = center
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Primary.copy(alpha = 0.16f),
                    PrimaryAccent.copy(alpha = 0.06f),
                    Color.Transparent
                ),
                center = center,
                radius = baseRadius * 0.72f
            ),
            radius = baseRadius * 0.72f,
            center = center
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    PrimaryContainer.copy(alpha = 0.14f),
                    Color.Transparent
                ),
                center = center,
                radius = baseRadius * 0.48f
            ),
            radius = baseRadius * 0.48f,
            center = center
        )
    }
}

@Composable
private fun WeeklyActivityRingDayCell(
    day: WeeklyActivityRingDay,
    isSelected: Boolean,
    animate: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val targetMove = if (day.isFuture) 0f else day.moveProgress
    val targetExercise = if (day.isFuture) 0f else day.exerciseProgress
    val targetStand = if (day.isFuture) 0f else day.standProgress

    val move by animateFloatAsState(
        targetValue = if (animate) targetMove else targetMove,
        animationSpec = tween(650, easing = FastOutSlowInEasing),
        label = "weekMove_${day.date}"
    )
    val exercise by animateFloatAsState(
        targetValue = if (animate) targetExercise else targetExercise,
        animationSpec = tween(650, easing = FastOutSlowInEasing),
        label = "weekExercise_${day.date}"
    )
    val stand by animateFloatAsState(
        targetValue = if (animate) targetStand else targetStand,
        animationSpec = tween(650, easing = FastOutSlowInEasing),
        label = "weekStand_${day.date}"
    )

    Column(
        modifier = modifier
            .padding(horizontal = 2.dp)
            .clickable(enabled = !day.isFuture, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(56.dp)
        ) {
            if (day.isToday || isSelected) {
                TodayNebulaAura(Modifier.fillMaxSize())
            }
            MiniActivityRings(
                moveProgress = move,
                exerciseProgress = exercise,
                standProgress = stand,
                size = 42.dp
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = day.dayLabel,
            style = BodyMd.copy(
                fontSize = 11.sp,
                fontWeight = if (day.isToday || isSelected) FontWeight.Bold else FontWeight.Medium
            ),
            color = when {
                day.isToday || isSelected -> PrimaryAccent
                day.isFuture -> OnSurfaceVariant.copy(alpha = 0.35f)
                day.hasActivity -> OnSurface
                else -> OnSurfaceVariant.copy(alpha = 0.55f)
            },
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        Text(
            text = day.date.dayOfMonth.toString(),
            style = BodyMd.copy(fontSize = 10.sp),
            color = when {
                day.isToday || isSelected -> Primary
                day.isFuture -> OnSurfaceVariant.copy(alpha = 0.28f)
                else -> OnSurfaceVariant.copy(alpha = 0.45f)
            }
        )
    }
}
