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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.vigorly.R
import com.example.vigorly.data.model.Milestone
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.ui.iconForName
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.SurfaceContainer

@Composable
fun MilestonesScreen(
    repository: VigorlyRepository,
    modifier: Modifier = Modifier
) {
    val milestones by repository.milestones.collectAsState()
    val unlocked = milestones.filter { it.unlocked }
    val locked = milestones.filter { !it.unlocked }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Dimens.ContainerMargin)
    ) {
        Text(stringResource(R.string.profile_milestones), style = HeadlineMd, color = OnSurface)
        Text(
            stringResource(R.string.profile_milestones_unlocked, unlocked.size, milestones.size),
            style = LabelCaps,
            color = OnSurfaceVariant,
            modifier = Modifier.padding(top = Dimens.Xs, bottom = Dimens.Md)
        )

        if (unlocked.isNotEmpty()) {
            Text(
                stringResource(R.string.milestones_section_unlocked),
                style = LabelCaps,
                color = PrimaryAccent.copy(alpha = 0.9f),
                modifier = Modifier.padding(bottom = Dimens.Sm)
            )
            unlocked.chunked(2).forEach { row ->
                MilestoneRow(row, locked = false)
            }
        }

        if (locked.isNotEmpty()) {
            Text(
                stringResource(R.string.milestones_section_locked),
                style = LabelCaps,
                color = OnSurfaceVariant.copy(alpha = 0.75f),
                modifier = Modifier.padding(top = Dimens.Md, bottom = Dimens.Sm)
            )
            locked.chunked(2).forEach { row ->
                MilestoneRow(row, locked = true)
            }
        }
    }
}

@Composable
private fun MilestoneRow(row: List<Milestone>, locked: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = Dimens.Md),
        horizontalArrangement = Arrangement.spacedBy(Dimens.Md)
    ) {
        row.forEach { milestone ->
            MilestoneGridCard(
                milestone = milestone,
                locked = locked,
                modifier = Modifier.weight(1f)
            )
        }
        if (row.size == 1) {
            Box(Modifier.weight(1f))
        }
    }
}

@Composable
private fun MilestoneGridCard(
    milestone: Milestone,
    locked: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
            .background(SurfaceContainer.copy(alpha = if (locked) 0.6f else 1f))
            .padding(Dimens.Md)
            .alpha(if (locked) 0.55f else 1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(SurfaceContainer)
                .border(
                    1.dp,
                    if (!locked) PrimaryAccent.copy(0.35f) else Color.White.copy(0.1f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                iconForName(milestone.iconName),
                contentDescription = null,
                tint = if (!locked) PrimaryAccent else OnSurfaceVariant,
                modifier = Modifier.size(32.dp)
            )
        }
        Text(
            "${milestone.title}\n${milestone.subtitle}",
            style = LabelCaps,
            color = if (!locked) OnSurface else OnSurfaceVariant,
            modifier = Modifier.padding(top = Dimens.Sm)
        )
        if (locked) {
            milestoneHint(milestone.id)?.let { hint ->
                Text(
                    hint,
                    style = BodyMd,
                    color = OnSurfaceVariant,
                    modifier = Modifier.padding(top = Dimens.Xs)
                )
            }
        }
    }
}

private fun milestoneHint(id: String): String? = when (id) {
    "streak_100" -> "Consigue una racha de 100 días"
    "lift_10k" -> "Registra 200+ sesiones de fuerza"
    "run_5k" -> "Completa 150+ entrenamientos"
    "elite" -> "350+ entrenamientos como Pro"
    else -> null
}
