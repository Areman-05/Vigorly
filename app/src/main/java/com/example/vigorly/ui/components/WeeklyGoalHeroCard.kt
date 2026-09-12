package com.example.vigorly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.data.model.WeeklyGoal
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.DisplayStat
import com.example.vigorly.ui.theme.GlassLabel
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.util.WeeklyProgressCalculator

@Composable
fun WeeklyGoalHeroCard(
    goal: WeeklyGoal,
    modifier: Modifier = Modifier
) {
    val displayCompleted = WeeklyProgressCalculator.displayCompletedSessions(goal)
    val progress = (displayCompleted / goal.targetSessions.toFloat().coerceAtLeast(1f)).coerceIn(0f, 1f)
    val isComplete = WeeklyProgressCalculator.isComplete(goal)
    val remaining = WeeklyProgressCalculator.remainingSessions(goal)
    val percent = WeeklyProgressCalculator.percent(goal)
    val status = when {
        isComplete -> stringResource(R.string.weekly_goal_complete)
        remaining == 0 -> stringResource(R.string.weekly_goal_on_track)
        else -> stringResource(R.string.weekly_goal_remaining, remaining, percent)
    }

    GlassSurface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.weekly_goal_title),
                    style = BodyMd.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.1).sp
                    ),
                    color = OnSurface,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "$displayCompleted/${goal.targetSessions}",
                    style = DisplayStat.copy(fontSize = 22.sp, lineHeight = 24.sp),
                    color = OnSurface,
                    fontWeight = FontWeight.Bold
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color.White.copy(alpha = 0.14f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.coerceIn(0.04f, 1f))
                        .height(7.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Primary, PrimaryAccent)
                            )
                        )
                )
            }
            Text(
                text = status,
                style = BodyMd.copy(fontSize = 13.sp, lineHeight = 17.sp),
                color = GlassLabel.copy(alpha = 0.88f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
