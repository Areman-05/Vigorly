package com.example.vigorly.ui.profile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.DisplayStat
import com.example.vigorly.ui.theme.HeadlineLgMobile
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer
import com.example.vigorly.util.LevelCalculator

@Composable
fun ProfileHeroCard(
    displayName: String,
    avatarUrl: String?,
    level: Int,
    levelProgress: Float,
    isProMember: Boolean,
    workoutsUntilNext: Int,
    onAvatarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        PrimaryAccent.copy(alpha = 0.2f),
                        Primary.copy(alpha = 0.12f),
                        PrimaryContainer.copy(alpha = 0.06f)
                    )
                )
            )
            .padding(vertical = Dimens.Lg, horizontal = Dimens.Md),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(120.dp)
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val stroke = 5.dp.toPx()
                val diameter = size.minDimension - stroke
                val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
                val arcSize = Size(diameter, diameter)
                val center = Offset(size.width / 2f, size.height / 2f)
                drawArc(
                    color = Primary.copy(alpha = 0.2f),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(PrimaryContainer, PrimaryAccent, Primary, PrimaryContainer),
                        center = center
                    ),
                    startAngle = -90f,
                    sweepAngle = 360f * levelProgress.coerceIn(0f, 1f),
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )
            }

            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onAvatarClick)
                    .semantics { role = Role.Button },
                contentAlignment = Alignment.Center
            ) {
                ProfileAvatarView(
                    avatarUrl = avatarUrl,
                    size = 96.dp,
                    borderColor = Color.White.copy(alpha = 0.55f)
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(PrimaryAccent, PrimaryContainer))
                    )
                    .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape)
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

        Text(
            displayName,
            style = HeadlineLgMobile.copy(fontSize = 28.sp),
            color = OnSurface,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = Dimens.Md),
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 10.dp)
        ) {
            Text(
                stringResource(R.string.profile_level_badge, level),
                style = LabelCaps.copy(fontSize = 14.sp),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(
                        Brush.horizontalGradient(listOf(PrimaryAccent, PrimaryContainer))
                    )
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            )
            if (isProMember) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(Primary.copy(alpha = 0.35f))
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
                        style = BodyMd.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                        color = PrimaryAccent,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }

        val fraction = levelProgress.coerceIn(0f, 1f)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.Md)
                .height(7.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Primary.copy(alpha = 0.25f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(7.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Primary, PrimaryAccent, PrimaryContainer)
                        )
                    )
            )
        }

        Text(
            if (level >= LevelCalculator.MAX_LEVEL) {
                stringResource(R.string.level_max_reached)
            } else {
                stringResource(R.string.level_progress_hint, workoutsUntilNext, level + 1)
            },
            style = BodyMd.copy(fontSize = 16.sp),
            color = OnSurfaceVariant.copy(alpha = 0.8f),
            modifier = Modifier.padding(top = 8.dp),
            textAlign = TextAlign.Center
        )
    }
}
