package com.example.vigorly.ui.dashboard

import com.example.vigorly.ui.components.PulsingButton
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.vigorly.data.model.RecentActivity
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.util.MetricFormatter
import com.example.vigorly.ui.components.ActivityRings
import com.example.vigorly.ui.components.GlassCard
import com.example.vigorly.ui.components.StreakCard
import com.example.vigorly.ui.components.WeeklyGoalCard
import androidx.compose.material3.TextButton
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.ButtonText
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.HeadlineLgMobile
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer
import com.example.vigorly.ui.theme.SurfaceContainer
import com.example.vigorly.ui.theme.SurfaceContainerLow
import com.example.vigorly.ui.theme.Tertiary
import com.example.vigorly.ui.iconForName

@Composable
fun DashboardScreen(
    repository: VigorlyRepository,
    onStartWorkout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val goals by repository.dailyGoals.collectAsState()
    val weeklyGoal by repository.weeklyGoal.collectAsState()
    val recent by repository.recentActivity.collectAsState()
    val profile by repository.profile.collectAsState()
    val firstName = profile.displayName.substringBefore(" ").ifBlank { profile.displayName }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.ContainerMargin, vertical = Dimens.Lg)
    ) {
        Text(
            text = "Hey, $firstName",
            style = HeadlineLgMobile,
            color = OnSurface,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = Dimens.Sm)
        )
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Ready to train?", style = BodyMd, color = OnSurfaceVariant)
            TextButton(onClick = repository::refreshDailyGoalsFromActivity) {
                Text("Sync activity", style = ButtonText, color = Primary)
            }
        }
        StreakCard(streakDays = profile.activeStreakDays, modifier = Modifier.padding(bottom = Dimens.Md))
        WeeklyGoalCard(goal = weeklyGoal, modifier = Modifier.padding(bottom = Dimens.Md))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ActivityRings(
                moveProgress = goals.moveProgress,
                exerciseProgress = goals.exerciseProgress,
                standProgress = goals.standProgress,
                centerPercent = goals.dailyGoalPercent
            )
            Spacer(Modifier.height(Dimens.Md))
            PulseStartButton(onClick = onStartWorkout)
        }
        Spacer(Modifier.height(Dimens.Lg))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Dimens.Md)) {
            MetricGlassCard(
                modifier = Modifier.weight(1f),
                label = "MOVE",
                value = "${goals.moveCalories}",
                suffix = "/ ${goals.moveCaloriesGoal} kcal",
                icon = Icons.Default.LocalFireDepartment,
                glowColor = PrimaryAccent
            )
            MetricGlassCard(
                modifier = Modifier.weight(1f),
                label = "STEPS",
                value = "%,d".format(goals.steps),
                suffix = "/ ${if (goals.stepsGoal >= 1000) "${goals.stepsGoal / 1000}k" else goals.stepsGoal}",
                icon = Icons.Default.DirectionsWalk,
                glowColor = PrimaryContainer
            )
        }
        Spacer(Modifier.height(Dimens.Md))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Dimens.Md)) {
            SmallMetricCard(Modifier.weight(1f), Icons.Default.Favorite, "${goals.heartRateBpm}", "BPM")
            SmallMetricCard(
                Modifier.weight(1f),
                Icons.Default.Bedtime,
                MetricFormatter.formatSleepHours(goals.sleepHours),
                "SLEEP"
            )
        }
        Spacer(Modifier.height(Dimens.Md))
        RecentSection(recent)
    }
}

@Composable
private fun PulseStartButton(onClick: () -> Unit) {
    PulsingButton(onClick = onClick) {
        Icon(Icons.Default.PlayArrow, contentDescription = null)
        Text("START WORKOUT", style = ButtonText, modifier = Modifier.padding(start = 4.dp))
    }
}

@Composable
private fun MetricGlassCard(
    modifier: Modifier,
    label: String,
    value: String,
    suffix: String,
    icon: ImageVector,
    glowColor: androidx.compose.ui.graphics.Color
) {
    GlassCard(modifier = modifier.height(128.dp)) {
        Box(Modifier.fillMaxSize().padding(Dimens.Md)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(label, style = LabelCaps, color = OnSurfaceVariant)
                Icon(icon, contentDescription = null, tint = Primary, modifier = Modifier.size(20.dp))
            }
            Column(Modifier.align(Alignment.BottomStart)) {
                Text(value, style = HeadlineLgMobile, color = OnSurface, fontWeight = FontWeight.Bold)
                Text(suffix, style = BodyMd, color = OnSurfaceVariant)
            }
            Box(
                Modifier
                    .align(Alignment.BottomEnd)
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(glowColor.copy(alpha = 0.2f))
            )
        }
    }
}

@Composable
private fun SmallMetricCard(modifier: Modifier, icon: ImageVector, value: String, label: String) {
    GlassCard(modifier = modifier.height(100.dp)) {
        Column(
            Modifier.fillMaxSize().padding(Dimens.Md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = if (label == "SLEEP") Tertiary else Primary)
            Text(value, style = HeadlineMd, color = OnSurface)
            Text(label, style = LabelCaps, color = OnSurfaceVariant)
        }
    }
}

@Composable
private fun RecentSection(items: List<RecentActivity>) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(Dimens.Md)) {
            Text("Recent", style = ButtonText, color = OnSurface)
            Spacer(Modifier.height(Dimens.Sm))
            items.forEach { item ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceContainerLow)
                        .padding(Dimens.Sm),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Primary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(iconForName(item.iconName), null, tint = Primary, modifier = Modifier.size(20.dp))
                        }
                        Column(Modifier.padding(start = Dimens.Sm)) {
                            Text(item.title, style = BodyMd, color = OnSurface)
                            Text(item.timeLabel, style = LabelCaps, color = OnSurfaceVariant)
                        }
                    }
                    Text("${item.durationMinutes}m", style = ButtonText, color = OnSurface)
                }
            }
        }
    }
}
