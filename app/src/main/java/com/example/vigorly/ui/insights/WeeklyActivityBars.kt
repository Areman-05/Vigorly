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
import androidx.compose.ui.res.stringResource
import com.example.vigorly.R
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryContainer
import com.example.vigorly.util.WeeklyDayMinutes

@Composable
fun WeeklyActivityBars(
    days: List<WeeklyDayMinutes>,
    modifier: Modifier = Modifier
) {
    val maxMinutes = days.maxOfOrNull { it.minutes }?.coerceAtLeast(1) ?: 1
    Column(modifier = modifier.fillMaxWidth()) {
        Text(stringResource(R.string.weekly_minutes_label), style = LabelCaps, color = OnSurfaceVariant)
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = Dimens.Md)
                .height(120.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            days.forEach { day ->
                val fraction = day.minutes / maxMinutes.toFloat()
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        Modifier
                            .width(28.dp)
                            .height((80 * fraction).coerceAtLeast(4f).dp)
                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                            .background(if (day.minutes > 0) Primary else PrimaryContainer.copy(alpha = 0.4f))
                    )
                    Text(day.dayLabel, style = BodyMd, color = OnSurface, modifier = Modifier.padding(top = Dimens.Xs))
                }
            }
        }
    }
}
