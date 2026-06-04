package com.example.vigorly.ui.milestones

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
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.core.testing.VigorlyTestTags
import com.example.vigorly.data.MilestoneHints
import androidx.compose.ui.platform.testTag
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
            .testTag(VigorlyTestTags.MILESTONES)
            .verticalScroll(rememberScrollState())
            .padding(Dimens.ContainerMargin)
    ) {
        Text(
            stringResource(R.string.profile_milestones),
            style = HeadlineMd.copy(fontSize = 28.sp),
            color = OnSurface
        )
        Text(
            stringResource(R.string.profile_milestones_unlocked, unlocked.size, milestones.size),
            style = LabelCaps.copy(fontSize = 14.sp),
            color = OnSurfaceVariant,
            modifier = Modifier.padding(top = Dimens.Xs, bottom = Dimens.Lg)
        )

        if (unlocked.isNotEmpty()) {
            Text(
                stringResource(R.string.milestones_section_unlocked),
                style = LabelCaps.copy(fontSize = 14.sp),
                color = PrimaryAccent.copy(alpha = 0.9f),
                modifier = Modifier.padding(bottom = Dimens.Sm)
            )
            unlocked.forEachIndexed { index, milestone ->
                MilestoneListItem(milestone = milestone, locked = false)
                if (index < unlocked.lastIndex) {
                    MilestoneDivider()
                }
            }
        }

        if (locked.isNotEmpty()) {
            Text(
                stringResource(R.string.milestones_section_locked),
                style = LabelCaps.copy(fontSize = 14.sp),
                color = OnSurfaceVariant.copy(alpha = 0.75f),
                modifier = Modifier.padding(top = Dimens.Lg, bottom = Dimens.Sm)
            )
            locked.forEachIndexed { index, milestone ->
                MilestoneListItem(milestone = milestone, locked = true)
                if (index < locked.lastIndex) {
                    MilestoneDivider()
                }
            }
        }
    }
}

@Composable
private fun MilestoneDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 2.dp),
        color = OnSurfaceVariant.copy(alpha = 0.08f)
    )
}

@Composable
private fun MilestoneListItem(
    milestone: Milestone,
    locked: Boolean
) {
    val accent = if (locked) OnSurfaceVariant else PrimaryAccent
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .alpha(if (locked) 0.5f else 1f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.Md)
    ) {
        Box(
            Modifier
                .size(48.dp)
                .clip(CircleShape)
                .border(
                    1.dp,
                    if (locked) Color.White.copy(alpha = 0.08f) else PrimaryAccent.copy(alpha = 0.35f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                iconForName(milestone.iconName),
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(24.dp)
            )
        }
        Column(Modifier.weight(1f)) {
            Text(
                "${milestone.title} · ${milestone.subtitle}",
                style = LabelCaps.copy(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
                color = if (locked) OnSurfaceVariant else OnSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (locked) {
                MilestoneHints.hint(milestone.id)?.let { hint ->
                    Text(
                        hint,
                        style = BodyMd.copy(fontSize = 15.sp),
                        color = OnSurfaceVariant.copy(alpha = 0.75f),
                        modifier = Modifier.padding(top = 2.dp),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
