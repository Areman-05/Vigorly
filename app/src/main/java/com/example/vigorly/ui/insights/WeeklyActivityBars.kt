package com.example.vigorly.ui.insights

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer
import com.example.vigorly.util.WeeklyDayMinutes

@Composable
fun WeeklyActivityBars(
    days: List<WeeklyDayMinutes>,
    modifier: Modifier = Modifier
) {
    val maxMinutes = days.maxOfOrNull { it.minutes }?.coerceAtLeast(1) ?: 1
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(R.string.insights_weekly_activity),
                style = LabelCaps.copy(fontSize = 13.sp),
                color = PrimaryAccent.copy(alpha = 0.9f),
                fontWeight = FontWeight.SemiBold
            )
            val weekTotal = days.sumOf { it.minutes }
            Text(
                stringResource(R.string.insights_weekly_total_minutes, weekTotal),
                style = BodyMd.copy(fontSize = 16.sp),
                color = OnSurfaceVariant.copy(alpha = 0.8f)
            )
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = Dimens.Md)
                .height(128.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            days.forEach { day ->
                val fraction = day.minutes / maxMinutes.toFloat()
                val barColor = when {
                    day.minutes <= 0 -> Primary.copy(alpha = 0.2f)
                    day.isToday -> PrimaryAccent
                    else -> PrimaryContainer
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        Modifier
                            .width(30.dp)
                            .height((88 * fraction).coerceAtLeast(if (day.minutes > 0) 6f else 4f).dp)
                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                            .background(
                                if (day.minutes > 0 && day.isToday) {
                                    Brush.verticalGradient(
                                        listOf(PrimaryAccent, PrimaryContainer)
                                    )
                                } else {
                                    Brush.verticalGradient(listOf(barColor, barColor))
                                }
                            )
                    )
                    Text(
                        day.dayLabel,
                        style = BodyMd.copy(
                            fontSize = 14.sp,
                            fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (day.isToday) PrimaryAccent else OnSurface,
                        modifier = Modifier.padding(top = Dimens.Xs)
                    )
                }
            }
        }
    }
}
