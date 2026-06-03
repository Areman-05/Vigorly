package com.example.vigorly.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Insights
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.vigorly.R
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer

@Composable
fun ProfileQuickActions(
    onOpenWorkouts: () -> Unit,
    onOpenInsights: () -> Unit,
    onOpenHistory: () -> Unit,
    onViewAllMilestones: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.Sm)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.Sm)
        ) {
            ProfileShortcutTile(
                label = stringResource(R.string.workouts_title),
                icon = Icons.Default.FitnessCenter,
                onClick = onOpenWorkouts,
                accent = PrimaryAccent,
                modifier = Modifier.weight(1f)
            )
            ProfileShortcutTile(
                label = stringResource(R.string.history_title),
                icon = Icons.Default.History,
                onClick = onOpenHistory,
                accent = Primary,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.Sm)
        ) {
            ProfileShortcutTile(
                label = stringResource(R.string.profile_milestones),
                icon = Icons.Default.EmojiEvents,
                onClick = onViewAllMilestones,
                accent = PrimaryContainer,
                modifier = Modifier.weight(1f)
            )
            ProfileShortcutTile(
                label = stringResource(R.string.profile_insights),
                icon = Icons.Default.Insights,
                onClick = onOpenInsights,
                accent = PrimaryAccent.copy(alpha = 0.85f),
                modifier = Modifier.weight(1f)
            )
        }
    }
}
