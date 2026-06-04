package com.example.vigorly.ui.insights

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer
import com.example.vigorly.util.WeeklyDayMinutes

@Composable
fun WeeklyActivityBars(
    days: List<WeeklyDayMinutes>,
    modifier: Modifier = Modifier,
    showHeader: Boolean = true
) {
    val maxMinutes = days.maxOfOrNull { it.minutes }?.coerceAtLeast(1) ?: 1
    val weekTotal = days.sumOf { it.minutes }

    Column(modifier = modifier.fillMaxWidth()) {
        if (showHeader) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.insights_weekly_activity),
                    style = BodyMd.copy(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
                    color = OnSurface
                )
                Text(
                    stringResource(R.string.insights_weekly_total_minutes, weekTotal),
                    style = BodyMd.copy(fontSize = 15.sp),
                    color = OnSurfaceVariant.copy(alpha = 0.85f)
                )
            }
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = if (showHeader) Dimens.Md else Dimens.Sm)
                .height(128.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            days.forEachIndexed { index, day ->
                AnimatedDayBar(
                    day = day,
                    maxMinutes = maxMinutes,
                    animationDelayMillis = index * 55
                )
            }
        }
    }
}

@Composable
private fun AnimatedDayBar(
    day: WeeklyDayMinutes,
    maxMinutes: Int,
    animationDelayMillis: Int
) {
    val targetFraction = day.minutes / maxMinutes.toFloat()
    val animatedFraction by animateFloatAsState(
        targetValue = targetFraction,
        animationSpec = tween(durationMillis = 650, delayMillis = animationDelayMillis),
        label = "bar_${day.dayLabel}"
    )
    val barHeight = (88f * animatedFraction).coerceAtLeast(if (day.minutes > 0) 6f else 4f)

    val barBrush = when {
        day.minutes <= 0 -> Brush.verticalGradient(
            listOf(Primary.copy(alpha = 0.22f), Primary.copy(alpha = 0.1f))
        )
        day.isToday -> Brush.verticalGradient(listOf(PrimaryAccent, PrimaryContainer))
        else -> Brush.verticalGradient(listOf(PrimaryContainer, Primary.copy(alpha = 0.7f))
        )
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .width(30.dp)
                .height(barHeight.dp)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(barBrush)
        )
        Text(
            day.dayLabel,
            style = BodyMd.copy(
                fontSize = 13.sp,
                fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal
            ),
            color = if (day.isToday) PrimaryAccent else OnSurface,
            modifier = Modifier.padding(top = Dimens.Xs)
        )
    }
}
