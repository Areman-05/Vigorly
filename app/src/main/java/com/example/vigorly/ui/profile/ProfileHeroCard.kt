package com.example.vigorly.ui.profile

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
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

@Composable
fun ProfileHeroCard(
    displayName: String,
    avatarUrl: String?,
    level: Int,
    levelProgress: Float,
    isProMember: Boolean,
    totalWorkouts: Int,
    modifier: Modifier = Modifier
) {
    val pulseTransition = rememberInfiniteTransition(label = "heroPulse")
    val glowAlpha by pulseTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )
    val ringScale by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ringScale"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        PrimaryAccent.copy(alpha = 0.22f),
                        Primary.copy(alpha = 0.1f),
                        PrimaryContainer.copy(alpha = 0.05f)
                    )
                )
            )
            .padding(vertical = Dimens.Lg, horizontal = Dimens.Md),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(132.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .matchParentSize()
                    .scale(ringScale)
            ) {
                val stroke = 5.dp.toPx()
                val diameter = size.minDimension - stroke
                val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
                val arcSize = Size(diameter, diameter)
                drawArc(
                    color = PrimaryAccent.copy(alpha = 0.15f * glowAlpha),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )
                drawArc(
                    brush = Brush.sweepGradient(
                        listOf(PrimaryAccent, Primary, PrimaryContainer, PrimaryAccent),
                        center = Offset(size.width / 2f, size.height / 2f)
                    ),
                    startAngle = -90f,
                    sweepAngle = 360f * levelProgress.coerceIn(0f, 1f),
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )
            }

            if (!avatarUrl.isNullOrBlank()) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(3.dp, PrimaryAccent.copy(alpha = 0.7f), CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    PrimaryAccent.copy(0.4f),
                                    Primary.copy(0.25f)
                                )
                            )
                        )
                        .border(3.dp, PrimaryAccent.copy(alpha = 0.55f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = OnSurface,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(PrimaryAccent, Primary)
                        )
                    )
                    .border(2.dp, OnSurface.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        stringResource(R.string.profile_level_short),
                        style = LabelCaps.copy(fontSize = 7.sp),
                        color = OnSurface.copy(alpha = 0.85f)
                    )
                    Text(
                        level.toString(),
                        style = DisplayStat.copy(fontSize = 16.sp, lineHeight = 16.sp),
                        color = OnSurface,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        Text(
            displayName,
            style = HeadlineLgMobile.copy(fontSize = 26.sp),
            color = OnSurface,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = Dimens.Md),
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            stringResource(R.string.profile_workouts_completed, totalWorkouts),
            style = BodyMd.copy(fontSize = 13.sp),
            color = OnSurfaceVariant.copy(alpha = 0.8f),
            modifier = Modifier.padding(top = 4.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = Dimens.Sm)
                .clip(RoundedCornerShape(999.dp))
                .background(
                    if (isProMember) PrimaryAccent.copy(alpha = 0.18f)
                    else OnSurfaceVariant.copy(alpha = 0.1f)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Icon(
                Icons.Default.WorkspacePremium,
                contentDescription = null,
                tint = if (isProMember) PrimaryAccent else OnSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Text(
                stringResource(
                    if (isProMember) R.string.profile_member_pro else R.string.profile_member_free
                ),
                style = BodyMd.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
                color = if (isProMember) PrimaryAccent else OnSurfaceVariant.copy(0.85f),
                modifier = Modifier.padding(start = 6.dp)
            )
        }
    }
}
