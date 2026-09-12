package com.example.vigorly.ui.session

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.SportsScore
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.data.model.SessionSummary
import com.example.vigorly.ui.components.RemoteCoverImage
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.DisplayStat
import com.example.vigorly.ui.theme.GlassLabel
import com.example.vigorly.ui.theme.HeadlineLgMobile
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.workout.WorkoutDetailStartCta
import com.example.vigorly.util.TimeFormatter

/**
 * Dirección A — misma estructura que Detalle:
 * eyebrow + título + tiempo + meta rows (sin glass) + CTA.
 */
@Composable
fun SessionSummaryScreen(
    summary: SessionSummary,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    coverUrl: String? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = Dimens.ContainerMargin)
            .padding(top = Dimens.Lg, bottom = Dimens.Xl)
    ) {
        if (coverUrl != null) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.06f))
            ) {
                RemoteCoverImage(url = coverUrl, contentDescription = summary.workoutName)
            }
        }

        Text(
            text = stringResource(R.string.session_complete),
            style = BodyMd.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.0.sp
            ),
            color = PrimaryAccent,
            modifier = Modifier.padding(top = if (coverUrl != null) Dimens.Md else 0.dp)
        )

        Text(
            text = summary.workoutName,
            style = HeadlineLgMobile.copy(
                fontSize = 32.sp,
                lineHeight = 36.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp
            ),
            color = OnSurface,
            modifier = Modifier.padding(top = 10.dp)
        )

        Text(
            text = stringResource(R.string.summary_congrats),
            style = BodyMd.copy(
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.1.sp
            ),
            color = GlassLabel.copy(alpha = 0.9f),
            modifier = Modifier.padding(top = 10.dp)
        )

        Text(
            text = TimeFormatter.formatElapsed(summary.elapsedSeconds),
            style = DisplayStat.copy(
                fontSize = 56.sp,
                lineHeight = 60.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-1.2).sp
            ),
            color = OnSurface,
            modifier = Modifier.padding(top = Dimens.Xl)
        )
        Text(
            text = stringResource(R.string.session_elapsed_label),
            style = BodyMd.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.0.sp
            ),
            color = GlassLabel.copy(alpha = 0.68f),
            modifier = Modifier.padding(top = 8.dp)
        )

        Column(modifier = Modifier.padding(top = Dimens.Lg)) {
            SummaryMetaRow(
                icon = Icons.Outlined.Schedule,
                text = stringResource(
                    R.string.summary_meta_duration,
                    summary.durationMinutes
                )
            )
            SummaryMetaRow(
                icon = Icons.Outlined.LocalFireDepartment,
                text = stringResource(
                    R.string.summary_meta_calories,
                    summary.caloriesBurned
                )
            )
            SummaryMetaRow(
                icon = Icons.Outlined.SportsScore,
                text = stringResource(
                    R.string.summary_meta_exercises,
                    summary.exercisesCompleted,
                    summary.totalExercises
                )
            )
        }

        Text(
            text = stringResource(R.string.session_saved_hint),
            style = BodyMd.copy(
                fontSize = 15.sp,
                lineHeight = 22.sp,
                letterSpacing = 0.1.sp
            ),
            color = OnSurface.copy(alpha = 0.9f),
            modifier = Modifier.padding(top = Dimens.Lg)
        )

        Spacer(Modifier.weight(1f))

        WorkoutDetailStartCta(
            onClick = onDone,
            labelRes = R.string.session_done,
            showPlayIcon = false,
            cornerRadius = 18.dp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SummaryMetaRow(
    icon: ImageVector,
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = OnSurface.copy(alpha = 0.88f),
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = text,
            style = BodyMd.copy(
                fontSize = 16.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.15.sp
            ),
            color = OnSurface.copy(alpha = 0.94f)
        )
    }
}
