package com.example.vigorly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.data.model.WeeklyGoal
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.DisplayStat
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.util.WeeklyProgressCalculator

@Composable
fun WeeklyGoalCard(
    goal: WeeklyGoal,
    modifier: Modifier = Modifier
) {
    val displayCompleted = WeeklyProgressCalculator.displayCompletedSessions(goal)
    val progress = (displayCompleted / goal.targetSessions.toFloat().coerceAtLeast(1f)).coerceIn(0f, 1f)
    val isComplete = WeeklyProgressCalculator.isComplete(goal)
    val remaining = WeeklyProgressCalculator.remainingSessions(goal)
    val daysLeft = WeeklyProgressCalculator.daysRemainingInWeek()
    val percent = WeeklyProgressCalculator.percent(goal)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Primary.copy(alpha = 0.11f),
                        PrimaryAccent.copy(alpha = 0.05f)
                    )
                )
            )
            .padding(Dimens.Md)
    ) {
        Row(Modifier.fillMaxWidth()) {
            Column(Modifier.weight(1f)) {
                Text(
                    stringResource(R.string.weekly_goal_title),
                    style = HeadlineMd.copy(fontSize = 18.sp),
                    color = OnSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isComplete) {
                        stringResource(R.string.weekly_goal_complete)
                    } else {
                        stringResource(R.string.weekly_goal_days_left, daysLeft)
                    },
                    style = LabelCaps.copy(fontSize = 10.sp),
                    color = OnSurfaceVariant.copy(alpha = 0.65f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Text(
                text = "$displayCompleted/${goal.targetSessions}",
                style = DisplayStat.copy(fontSize = 26.sp, lineHeight = 28.sp),
                color = if (isComplete) PrimaryAccent else OnSurface,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(Dimens.Md))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(OnSurfaceVariant.copy(alpha = 0.14f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Primary, PrimaryAccent)
                        )
                    )
            )
        }

        Spacer(Modifier.height(Dimens.Sm))

        Text(
            text = when {
                isComplete -> stringResource(R.string.weekly_goal_on_track)
                remaining == 0 -> stringResource(R.string.weekly_goal_on_track)
                else -> stringResource(R.string.weekly_goal_remaining, remaining, percent)
            },
            style = BodyMd.copy(fontSize = 13.sp),
            color = if (isComplete) PrimaryAccent.copy(0.9f) else OnSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}
