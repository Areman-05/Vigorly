package com.example.vigorly.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(Dimens.Md)) {
            Text("Weekly Goal", style = HeadlineMd, color = OnSurface)
            Text(
                "${goal.completedSessions}/${goal.targetSessions} sessions",
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
                "${WeeklyProgressCalculator.remainingSessions(goal)} remaining · ${WeeklyProgressCalculator.percent(goal)}%",
                style = BodyMd,
                color = OnSurfaceVariant
            )
        }
    }
}
