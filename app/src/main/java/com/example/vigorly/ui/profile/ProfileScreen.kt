package com.example.vigorly.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.ui.components.AthleticRadarChart
import com.example.vigorly.ui.components.GlassCard
import com.example.vigorly.ui.components.LevelProgressBar
import com.example.vigorly.util.LevelCalculator
import com.example.vigorly.ui.iconForName
import com.example.vigorly.ui.theme.BodyLg
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.ButtonText
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.DisplayStat
import com.example.vigorly.ui.theme.HeadlineLgMobile
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryContainer
import com.example.vigorly.ui.theme.SurfaceContainer
import com.example.vigorly.ui.theme.SurfaceContainerLow
import com.example.vigorly.ui.theme.SurfaceVariant
import androidx.compose.material3.Icon

@Composable
fun ProfileScreen(
    repository: VigorlyRepository,
    onViewAllMilestones: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val profile by repository.profile.collectAsState()
    val stats by repository.athleticStats.collectAsState()
    val milestones by repository.milestones.collectAsState()
    val history by repository.history.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.ContainerMargin, vertical = Dimens.Lg)
    ) {
        GlassCard(Modifier.fillMaxWidth()) {
            Box {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(
                            Brush.linearGradient(
                                listOf(PrimaryContainer.copy(alpha = 0.2f), androidx.compose.ui.graphics.Color.Transparent)
                            )
                        )
                )
                Row(
                    Modifier.padding(Dimens.Lg),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = profile.avatarUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .border(2.dp, Primary, CircleShape)
                    )
                    Column(Modifier.padding(start = Dimens.Md)) {
                        Text(profile.displayName, style = HeadlineLgMobile, color = OnSurface)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.WorkspacePremium, null, tint = OnSurfaceVariant, modifier = Modifier.size(16.dp))
                            Text(
                                if (profile.isProMember) "Pro Member" else "Member",
                                style = BodyMd,
                                color = OnSurfaceVariant,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(Dimens.Md))
        GlassCard(Modifier.fillMaxWidth()) {
            LevelProgressBar(
                level = profile.level,
                progress = LevelCalculator.progressToNextLevel(profile.totalWorkouts),
                workoutsUntilNext = LevelCalculator.workoutsUntilNextLevel(profile.totalWorkouts),
                modifier = Modifier.padding(Dimens.Md)
            )
        }
        Spacer(Modifier.height(Dimens.Md))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Dimens.Md)) {
            StatCard(Modifier.weight(1f), "Workouts", profile.totalWorkouts.toString(), "total", primaryValue = true)
            StatCard(Modifier.weight(1f), "Active Streak", profile.activeStreakDays.toString(), "days", primaryValue = false)
        }
        Spacer(Modifier.height(Dimens.Md))
        GlassCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(Dimens.Lg)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Athletic Profile", style = HeadlineMd, color = OnSurface)
                    Text(
                        "LVL ${profile.level}",
                        style = LabelCaps,
                        color = OnSurfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(SurfaceVariant)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
                AthleticRadarChart(stats, Modifier.padding(top = Dimens.Md))
            }
        }
        Spacer(Modifier.height(Dimens.Md))
        GlassCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(Dimens.Lg)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Milestones", style = HeadlineMd, color = OnSurface)
                    TextButton(onClick = onViewAllMilestones) { Text("View All", style = ButtonText, color = Primary) }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    milestones.forEach { m ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.alpha(if (m.unlocked) 1f else 0.5f)
                        ) {
                            Box(
                                Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(SurfaceContainer)
                                    .border(1.dp, if (m.unlocked) Primary.copy(0.3f) else androidx.compose.ui.graphics.Color.White.copy(0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(iconForName(m.iconName), null, tint = if (m.unlocked) Primary else OnSurfaceVariant, modifier = Modifier.size(28.dp))
                            }
                            Text("${m.title}\n${m.subtitle}", style = LabelCaps, color = if (m.unlocked) OnSurface else OnSurfaceVariant)
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(Dimens.Md))
        GlassCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(Dimens.Lg)) {
                Text("Recent History", style = HeadlineMd, color = OnSurface)
                Spacer(Modifier.height(Dimens.Md))
                history.forEach { item ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = Dimens.Sm)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceContainerLow)
                            .padding(Dimens.Md),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(SurfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(iconForName(item.iconName), null, tint = OnSurface)
                            }
                            Column(Modifier.padding(start = Dimens.Md)) {
                                Text(item.title, style = BodyLg, color = OnSurface)
                                Text(item.timestampLabel, style = BodyMd, color = OnSurfaceVariant)
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("${item.durationMinutes} MIN", style = ButtonText, color = Primary)
                            Text("${item.calories} KCAL", style = LabelCaps, color = OnSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(modifier: Modifier, label: String, value: String, suffix: String, primaryValue: Boolean) {
    GlassCard(modifier) {
        Column(Modifier.padding(Dimens.Md)) {
            Text(label.uppercase(), style = LabelCaps, color = OnSurfaceVariant)
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, style = DisplayStat, color = if (primaryValue) Primary else OnSurface)
                Text(suffix, style = BodyMd, color = OnSurfaceVariant, modifier = Modifier.padding(start = 4.dp, bottom = 8.dp))
            }
        }
    }
}
