package com.example.vigorly.ui.profile

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.example.vigorly.data.model.AthleticStat
import com.example.vigorly.ui.components.AthleticRadarChart
import com.example.vigorly.ui.components.FlatProgressBar
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer
import com.example.vigorly.util.AthleticStatLabels

private val StatTeal = Color(0xFF20C997)
private val StatViolet = Color(0xFF9775FA)

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
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Primary.copy(alpha = 0.14f),
                        PrimaryAccent.copy(alpha = 0.06f),
                        PrimaryContainer.copy(alpha = 0.04f)
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
                style = BodyMd.copy(fontSize = 17.sp, fontWeight = FontWeight.SemiBold),
                color = PrimaryAccent,
                modifier = Modifier
                    .padding(top = 6.dp, bottom = Dimens.Sm)
                    .clip(RoundedCornerShape(10.dp))
                    .background(PrimaryAccent.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }

        AthleticRadarChart(displayStats, Modifier.padding(bottom = Dimens.Md))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
        "endurance", "stamina" -> PrimaryContainer
        "speed" -> StatViolet
        "mobility" -> StatTeal
        else -> Primary
    }

    Column {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stat.label,
                style = BodyMd.copy(fontSize = 17.sp, fontWeight = FontWeight.Medium),
                color = OnSurface
            )
            Text(
                "$animatedValue",
                style = LabelCaps.copy(fontSize = 15.sp),
                color = accent,
                fontWeight = FontWeight.SemiBold
            )
        }
        FlatProgressBar(
            progress = animatedValue / 100f,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
                .height(6.dp),
            color = accent,
            trackColor = accent.copy(alpha = 0.15f)
        )
    }
}
