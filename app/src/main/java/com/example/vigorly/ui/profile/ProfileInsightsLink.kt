package com.example.vigorly.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer

@Composable
fun ProfileInsightsLink(
    weeklyCompleted: Int,
    weeklyTarget: Int,
    onOpenInsights: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        PrimaryAccent.copy(alpha = 0.16f),
                        Primary.copy(alpha = 0.14f),
                        PrimaryContainer.copy(alpha = 0.1f)
                    )
                )
            )
            .clickable(onClick = onOpenInsights)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(
                            PrimaryAccent.copy(alpha = 0.35f),
                            PrimaryContainer.copy(alpha = 0.25f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Insights,
                contentDescription = null,
                tint = PrimaryAccent,
                modifier = Modifier.size(24.dp)
            )
        }
        Column(
            Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                stringResource(R.string.profile_insights),
                style = BodyMd.copy(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
                color = OnSurface
            )
            Text(
                stringResource(R.string.profile_weekly_progress, weeklyCompleted, weeklyTarget),
                style = BodyMd.copy(fontSize = 14.sp),
                color = PrimaryAccent.copy(alpha = 0.9f),
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = PrimaryAccent,
            modifier = Modifier.size(22.dp)
        )
    }
}
