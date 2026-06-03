package com.example.vigorly.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.DisplayStat
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer

private val StreakOrange = Color(0xFFFF922B)

@Composable
fun ProfileQuickMetrics(
    sessions: Int,
    totalMinutes: Int,
    totalCalories: Int,
    streakDays: Int,
    modifier: Modifier = Modifier
) {
    val streakLine = if (streakDays == 1) {
        stringResource(R.string.profile_metric_streak_one)
    } else {
        stringResource(R.string.profile_metric_streak_many, streakDays)
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickMetric(
                valueLine = sessions.toString(),
                label = stringResource(R.string.profile_metric_sessions),
                accent = PrimaryAccent,
                tintTop = PrimaryAccent.copy(alpha = 0.28f),
                tintBottom = PrimaryAccent.copy(alpha = 0.08f),
                modifier = Modifier.weight(1f)
            )
            QuickMetric(
                valueLine = stringResource(R.string.profile_metric_minutes_value, totalMinutes),
                label = stringResource(R.string.profile_stat_minutes),
                accent = PrimaryContainer,
                tintTop = Primary.copy(alpha = 0.35f),
                tintBottom = Primary.copy(alpha = 0.1f),
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickMetric(
                valueLine = stringResource(R.string.profile_metric_calories_value, totalCalories),
                label = stringResource(R.string.profile_stat_calories),
                accent = PrimaryContainer,
                tintTop = PrimaryContainer.copy(alpha = 0.32f),
                tintBottom = PrimaryContainer.copy(alpha = 0.1f),
                modifier = Modifier.weight(1f)
            )
            QuickMetric(
                valueLine = streakLine,
                label = stringResource(R.string.profile_metric_streak),
                accent = StreakOrange,
                tintTop = StreakOrange.copy(alpha = 0.3f),
                tintBottom = StreakOrange.copy(alpha = 0.08f),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickMetric(
    valueLine: String,
    label: String,
    accent: Color,
    tintTop: Color,
    tintBottom: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .heightIn(min = 76.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.verticalGradient(listOf(tintTop, tintBottom)))
            .padding(vertical = 12.dp, horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = valueLine,
            style = DisplayStat.copy(fontSize = 24.sp, lineHeight = 26.sp),
            color = accent,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = label,
            style = LabelCaps.copy(
                fontSize = 12.sp,
                lineHeight = 14.sp,
                letterSpacing = 0.04.sp
            ),
            color = OnSurfaceVariant.copy(alpha = 0.85f),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
        )
    }
}
