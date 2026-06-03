package com.example.vigorly.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QuickMetric(
            value = "$sessions",
            label = stringResource(R.string.profile_workouts_stat),
            accent = PrimaryAccent,
            tintTop = PrimaryAccent.copy(alpha = 0.28f),
            tintBottom = PrimaryAccent.copy(alpha = 0.08f),
            modifier = Modifier.weight(1f)
        )
        QuickMetric(
            value = "$totalMinutes",
            suffix = "min",
            label = stringResource(R.string.profile_stat_minutes),
            accent = PrimaryContainer,
            tintTop = Primary.copy(alpha = 0.35f),
            tintBottom = Primary.copy(alpha = 0.1f),
            modifier = Modifier.weight(1f)
        )
        QuickMetric(
            value = "%,d".format(totalCalories),
            suffix = "kcal",
            label = stringResource(R.string.profile_stat_calories),
            accent = PrimaryContainer,
            tintTop = PrimaryContainer.copy(alpha = 0.32f),
            tintBottom = PrimaryContainer.copy(alpha = 0.1f),
            modifier = Modifier.weight(1f)
        )
        QuickMetric(
            value = "$streakDays",
            suffix = stringResource(R.string.profile_stat_days),
            label = stringResource(R.string.profile_streak_stat),
            accent = StreakOrange,
            tintTop = StreakOrange.copy(alpha = 0.3f),
            tintBottom = StreakOrange.copy(alpha = 0.08f),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickMetric(
    value: String,
    label: String,
    accent: Color,
    tintTop: Color,
    tintBottom: Color,
    modifier: Modifier = Modifier,
    suffix: String? = null
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.verticalGradient(listOf(tintTop, tintBottom)))
            .padding(vertical = 12.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                value,
                style = DisplayStat.copy(fontSize = 22.sp, lineHeight = 24.sp),
                color = accent,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            suffix?.let {
                Text(
                    it,
                    style = BodyMd.copy(fontSize = 11.sp),
                    color = accent.copy(alpha = 0.75f),
                    modifier = Modifier.padding(start = 2.dp, bottom = 2.dp)
                )
            }
        }
        Text(
            label,
            style = LabelCaps.copy(fontSize = 9.sp),
            color = OnSurfaceVariant.copy(alpha = 0.85f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp),
            maxLines = 2
        )
    }
}
