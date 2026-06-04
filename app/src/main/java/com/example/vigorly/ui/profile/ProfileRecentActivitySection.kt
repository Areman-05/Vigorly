package com.example.vigorly.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.data.model.WorkoutHistoryItem
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.PrimaryAccent

private val CardSurface = Color.White.copy(alpha = 0.06f)

@Composable
fun ProfileRecentActivitySection(
    sessions: List<WorkoutHistoryItem>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardSurface)
            .padding(Dimens.Md)
    ) {
        Text(
            stringResource(R.string.profile_recent_history),
            style = HeadlineMd.copy(fontSize = 18.sp),
            color = PrimaryAccent,
            fontWeight = FontWeight.SemiBold
        )
        if (sessions.isEmpty()) {
            Text(
                stringResource(R.string.profile_empty_activity_title),
                style = BodyMd.copy(fontSize = 16.sp, fontWeight = FontWeight.Medium),
                color = OnSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 4.dp)
            )
        } else {
            Column(
                modifier = Modifier.padding(top = Dimens.Sm),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                sessions.forEach { item ->
                    ProfileRecentSessionRow(
                        item = item,
                        accent = PrimaryAccent,
                        onClick = { /* detalle desde historial */ }
                    )
                }
            }
        }
    }
}
