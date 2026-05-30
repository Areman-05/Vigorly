package com.example.vigorly.ui.milestones

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.vigorly.R
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.ui.components.GlassCard
import com.example.vigorly.ui.iconForName
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.SurfaceContainer

private fun milestoneHint(id: String): String? = when (id) {
    "streak_100" -> "Reach a 100-day streak"
    "lift_10k" -> "Log 200+ strength sessions"
    "run_5k" -> "Complete 150+ workouts"
    "elite" -> "350+ workouts as Pro member"
    else -> null
}

@Composable
fun MilestonesScreen(
    repository: VigorlyRepository,
    modifier: Modifier = Modifier
) {
    val milestones by repository.milestones.collectAsState()
    val unlocked = milestones.count { it.unlocked }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.ContainerMargin)
    ) {
        Text(stringResource(R.string.profile_milestones), style = HeadlineMd, color = OnSurface)
        Text(
            "$unlocked of ${milestones.size} unlocked",
            style = LabelCaps,
            color = OnSurfaceVariant,
            modifier = Modifier.padding(top = Dimens.Xs, bottom = Dimens.Md)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(Dimens.Md),
            verticalArrangement = Arrangement.spacedBy(Dimens.Md)
        ) {
            items(milestones) { milestone ->
                GlassCard(Modifier.fillMaxWidth()) {
                    Column(
                        Modifier.padding(Dimens.Md).alpha(if (milestone.unlocked) 1f else 0.5f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(SurfaceContainer)
                                .border(
                                    1.dp,
                                    if (milestone.unlocked) Primary.copy(0.3f) else Color.White.copy(0.1f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                iconForName(milestone.iconName),
                                contentDescription = null,
                                tint = if (milestone.unlocked) Primary else OnSurfaceVariant,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Text(
                            "${milestone.title}\n${milestone.subtitle}",
                            style = LabelCaps,
                            color = if (milestone.unlocked) OnSurface else OnSurfaceVariant,
                            modifier = Modifier.padding(top = Dimens.Sm)
                        )
                        if (!milestone.unlocked) {
                            milestoneHint(milestone.id)?.let { hint ->
                                Text(hint, style = BodyMd, color = OnSurfaceVariant, modifier = Modifier.padding(top = Dimens.Xs))
                            }
                        }
                    }
                }
            }
        }
    }
}
