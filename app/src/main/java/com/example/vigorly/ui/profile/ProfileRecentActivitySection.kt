package com.example.vigorly.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.data.model.WorkoutHistoryItem
import com.example.vigorly.ui.components.GlassSurface
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.GlassLabel
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.PrimaryAccent

@Composable
fun ProfileRecentActivitySection(
    sessions: List<WorkoutHistoryItem>,
    onItemClick: (String) -> Unit,
    onSeeAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassSurface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.profile_recent_history),
                    style = HeadlineMd.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.3).sp
                    ),
                    color = OnSurface
                )
                Text(
                    text = stringResource(R.string.dashboard_view_all),
                    style = BodyMd.copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                    color = PrimaryAccent,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onSeeAll)
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                )
            }
            if (sessions.isEmpty()) {
                Text(
                    text = stringResource(R.string.profile_empty_activity_title),
                    style = BodyMd.copy(fontSize = 15.sp, fontWeight = FontWeight.Medium),
                    color = OnSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp, bottom = 4.dp)
                )
                Text(
                    text = stringResource(R.string.profile_empty_activity_body),
                    style = BodyMd.copy(fontSize = 13.sp, lineHeight = 18.sp),
                    color = GlassLabel.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
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
                            onClick = { onItemClick(item.id) }
                        )
                    }
                }
            }
        }
    }
}
