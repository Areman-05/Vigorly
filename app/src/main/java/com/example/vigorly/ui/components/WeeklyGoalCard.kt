package com.example.vigorly.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.vigorly.R
import com.example.vigorly.data.model.WeeklyGoal
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.util.WeeklyProgressCalculator

@Composable
fun WeeklyGoalCard(
    goal: WeeklyGoal,
    modifier: Modifier = Modifier
) {
    VigorlyOutlineCard(modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(Dimens.Md)) {
            Text(stringResource(R.string.weekly_goal_title), style = HeadlineMd, color = OnSurface)
            Text(
                stringResource(R.string.weekly_goal_sessions, goal.completedSessions, goal.targetSessions),
                style = BodyMd,
                color = OnSurfaceVariant
            )
            LinearProgressIndicator(
                progress = { goal.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimens.Sm),
                color = Primary,
                trackColor = OnSurfaceVariant.copy(alpha = 0.2f)
            )
            Text(
                stringResource(
                    R.string.weekly_goal_remaining,
                    WeeklyProgressCalculator.remainingSessions(goal),
                    WeeklyProgressCalculator.percent(goal)
                ),
                style = BodyMd,
                color = OnSurfaceVariant
            )
        }
    }
}
