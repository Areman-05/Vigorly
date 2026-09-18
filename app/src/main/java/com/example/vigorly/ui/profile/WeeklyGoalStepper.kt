package com.example.vigorly.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.core.testing.VigorlyTestTags
import com.example.vigorly.ui.theme.DisplayStat
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.PrimaryAccent

@Composable
fun WeeklyGoalStepper(
    targetSessions: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StepperButton(
            onClick = onDecrease,
            enabled = targetSessions > 1,
            modifier = Modifier.testTag(VigorlyTestTags.WEEKLY_TARGET_DECREASE)
        ) {
            Icon(Icons.Default.Remove, contentDescription = null, tint = OnSurface)
        }
        Text(
            text = targetSessions.toString(),
            style = DisplayStat.copy(fontSize = 32.sp, lineHeight = 34.sp),
            color = PrimaryAccent,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.testTag(VigorlyTestTags.WEEKLY_TARGET_VALUE)
        )
        StepperButton(
            onClick = onIncrease,
            enabled = targetSessions < 14,
            modifier = Modifier.testTag(VigorlyTestTags.WEEKLY_TARGET_INCREASE)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = OnSurface)
        }
    }
}

@Composable
private fun StepperButton(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(
                if (enabled) PrimaryAccent.copy(alpha = 0.2f)
                else OnSurfaceVariant.copy(alpha = 0.12f)
            )
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
