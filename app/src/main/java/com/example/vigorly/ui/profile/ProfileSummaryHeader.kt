package com.example.vigorly.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.HeadlineLgMobile
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer

@Composable
fun ProfileSummaryHeader(
    displayName: String,
    avatarUrl: String?,
    level: Int,
    streakDays: Int,
    isProMember: Boolean,
    onAvatarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val firstName = displayName.substringBefore(" ").ifBlank { displayName }
    val streakLabel = if (streakDays == 1) {
        stringResource(R.string.profile_metric_streak_one)
    } else {
        stringResource(R.string.profile_metric_streak_many, streakDays)
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(84.dp)
                .clickable(onClick = onAvatarClick)
                .semantics { role = Role.Button }
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .align(Alignment.Center)
                    .clip(CircleShape)
                    .border(2.dp, PrimaryAccent.copy(alpha = 0.45f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                ProfileAvatarView(
                    avatarUrl = avatarUrl,
                    size = 72.dp,
                    borderColor = Color.Transparent
                )
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 2.dp, y = 2.dp)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(PrimaryAccent, PrimaryContainer)))
                    .border(1.5.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                    .clickable(onClick = onAvatarClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = stringResource(R.string.profile_change_avatar),
                    tint = Color.White,
                    modifier = Modifier.size(15.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = Dimens.Md)
        ) {
            Text(
                firstName,
                style = HeadlineLgMobile.copy(fontSize = 26.sp, lineHeight = 30.sp),
                color = OnSurface,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                stringResource(R.string.profile_level_badge, level),
                style = BodyMd.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                color = PrimaryAccent,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                stringResource(R.string.profile_streak_line, streakLabel),
                style = BodyMd.copy(fontSize = 14.sp),
                color = OnSurfaceVariant.copy(alpha = 0.85f),
                modifier = Modifier.padding(top = 2.dp)
            )
            if (isProMember) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(Primary.copy(alpha = 0.28f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        Icons.Default.WorkspacePremium,
                        contentDescription = null,
                        tint = PrimaryAccent,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        stringResource(R.string.profile_member_pro),
                        style = BodyMd.copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                        color = PrimaryAccent,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}
