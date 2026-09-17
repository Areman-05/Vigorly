package com.example.vigorly.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.data.activity.WeeklyActivityRingDay
import com.example.vigorly.ui.performance.UiPerformance
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.GlassLabel
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.PrimaryAccent
import java.time.LocalDate

@Composable
fun WeeklyActivityDayRail(
    days: List<WeeklyActivityRingDay>,
    selectedDate: LocalDate?,
    onDayClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    animate: Boolean = UiPerformance.decorativeMotionEnabled
) {
    if (days.isEmpty()) return

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        days.forEach { day ->
            WeekDayCell(
                day = day,
                isSelected = selectedDate == day.date,
                animate = animate,
                onClick = { if (!day.isFuture) onDayClick(day.date) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun WeekDayCell(
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
        targetValue = targetMove,
        animationSpec = tween(if (animate) 480 else 0, easing = FastOutSlowInEasing),
        label = "weekMove_${day.date}"
    )
    val exercise by animateFloatAsState(
        targetValue = targetExercise,
        animationSpec = tween(if (animate) 520 else 0, easing = FastOutSlowInEasing),
        label = "weekExercise_${day.date}"
    )
    val stand by animateFloatAsState(
        targetValue = targetStand,
        animationSpec = tween(if (animate) 560 else 0, easing = FastOutSlowInEasing),
        label = "weekStand_${day.date}"
    )

    val interaction = remember { MutableInteractionSource() }
    val muted = day.isFuture
    val labelColor = when {
        muted -> GlassLabel.copy(alpha = 0.28f)
        isSelected || day.isToday -> OnSurface
        else -> GlassLabel.copy(alpha = 0.72f)
    }
    val numberColor = when {
        muted -> GlassLabel.copy(alpha = 0.22f)
        isSelected || day.isToday -> OnSurface
        else -> GlassLabel.copy(alpha = 0.9f)
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(
                enabled = !day.isFuture,
                interactionSource = interaction,
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 10.dp, horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = day.dayLabel.take(2),
            style = BodyMd.copy(
                fontSize = 11.sp,
                fontWeight = if (isSelected || day.isToday) FontWeight.SemiBold else FontWeight.Medium,
                letterSpacing = 0.6.sp
            ),
            color = labelColor,
            maxLines = 1
        )

        Spacer(Modifier.height(8.dp))

        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(44.dp)) {
            MiniActivityRings(
                moveProgress = move,
                exerciseProgress = exercise,
                standProgress = stand,
                size = 42.dp,
                muted = muted
            )
            if (day.isToday && !isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(PrimaryAccent)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = day.date.dayOfMonth.toString(),
            style = BodyMd.copy(
                fontSize = 14.sp,
                fontWeight = if (isSelected || day.isToday) FontWeight.Bold else FontWeight.Medium
            ),
            color = numberColor
        )
    }
}
