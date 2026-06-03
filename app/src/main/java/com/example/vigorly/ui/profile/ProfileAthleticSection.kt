package com.example.vigorly.ui.profile

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.data.model.AthleticStat
import com.example.vigorly.ui.components.AthleticRadarChart
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.util.AthleticStatLabels

@Composable
fun ProfileAthleticSection(
    stats: List<AthleticStat>,
    modifier: Modifier = Modifier
) {
    val displayStats = AthleticStatLabels.forDisplay(stats)
    val dominant = AthleticStatLabels.dominantStat(stats)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Primary.copy(alpha = 0.1f),
                        PrimaryAccent.copy(alpha = 0.04f)
                    )
                )
            )
            .padding(Dimens.Md)
    ) {
        ProfileSectionHeader(title = stringResource(R.string.profile_athletic))

        dominant?.let { top ->
            Text(
                stringResource(
                    R.string.profile_athletic_highlight,
                    AthleticStatLabels.displayLabel(top.label),
                    top.value
                ),
                style = BodyMd.copy(fontSize = 13.sp),
                color = OnSurfaceVariant.copy(alpha = 0.85f),
                modifier = Modifier.padding(top = 4.dp, bottom = Dimens.Sm)
            )
        }

        AthleticRadarChart(displayStats, Modifier.padding(bottom = Dimens.Md))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            displayStats.forEach { stat ->
                AthleticStatBar(stat = stat)
            }
        }
    }
}

@Composable
private fun AthleticStatBar(stat: AthleticStat) {
    val animatedValue by animateIntAsState(
        targetValue = stat.value,
        animationSpec = tween(700),
        label = "statBar"
    )
    val accent = when (AthleticStatLabels.normalizeKey(stat.label)) {
        "strength", "power" -> PrimaryAccent
        "endurance", "stamina" -> Primary
        else -> Primary.copy(alpha = 0.85f)
    }

    Column {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stat.label,
                style = BodyMd.copy(fontSize = 13.sp, fontWeight = FontWeight.Medium),
                color = OnSurface
            )
            Text(
                "$animatedValue",
                style = LabelCaps.copy(fontSize = 10.sp),
                color = accent
            )
        }
        LinearProgressIndicator(
            progress = { animatedValue / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
                .height(5.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = accent,
            trackColor = OnSurfaceVariant.copy(alpha = 0.12f)
        )
    }
}
