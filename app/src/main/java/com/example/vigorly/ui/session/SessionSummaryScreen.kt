package com.example.vigorly.ui.session

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.vigorly.R
import com.example.vigorly.data.model.SessionSummary
import com.example.vigorly.ui.components.GlassCard
import com.example.vigorly.ui.components.StatRow
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.ButtonText
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.DisplayStat
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnPrimaryContainer
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryContainer
import com.example.vigorly.util.TimeFormatter

@Composable
fun SessionSummaryScreen(
    summary: SessionSummary,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.ContainerMargin),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(stringResource(R.string.session_complete), style = LabelCaps, color = Primary)
        Text(summary.workoutName, style = HeadlineMd, color = OnSurface, modifier = Modifier.padding(top = Dimens.Sm))
        Text(
            TimeFormatter.formatElapsed(summary.elapsedSeconds),
            style = DisplayStat,
            color = OnSurface,
            modifier = Modifier.padding(top = Dimens.Md)
        )
        GlassCard(Modifier.fillMaxWidth().padding(top = Dimens.Lg)) {
            Column(Modifier.padding(Dimens.Md), verticalArrangement = Arrangement.spacedBy(Dimens.Sm)) {
                StatRow("Duration", "${summary.durationMinutes} min")
                StatRow("Calories", "${summary.caloriesBurned} kcal")
                StatRow(
                    "Exercises",
                    "${summary.exercisesCompleted}/${summary.totalExercises}",
                    highlight = true
                )
            }
        }
        Spacer(Modifier.height(Dimens.Xl))
        Button(
            onClick = onDone,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryContainer,
                contentColor = OnPrimaryContainer
            )
        ) {
            Text(stringResource(R.string.session_done), style = ButtonText)
        }
        Text(
            stringResource(R.string.session_saved_hint),
            style = BodyMd,
            color = OnSurfaceVariant,
            modifier = Modifier.padding(top = Dimens.Md)
        )
    }
}
