package com.example.vigorly.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.vigorly.R
import com.example.vigorly.ui.components.GlassCard
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.Primary

@Composable
fun ProfileQuickActions(
    onOpenWorkouts: () -> Unit,
    onOpenInsights: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.Sm)
    ) {
        QuickActionChip(stringResource(R.string.workouts_title), Icons.Default.FitnessCenter, onOpenWorkouts, Modifier.weight(1f))
        QuickActionChip(stringResource(R.string.profile_insights), Icons.Default.Insights, onOpenInsights, Modifier.weight(1f))
    }
}

@Composable
private fun QuickActionChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.clickable(onClick = onClick)) {
        Row(
            Modifier.padding(Dimens.Md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = Primary)
            Text(label, style = BodyMd, color = OnSurface, modifier = Modifier.padding(start = Dimens.Sm))
        }
    }
}
