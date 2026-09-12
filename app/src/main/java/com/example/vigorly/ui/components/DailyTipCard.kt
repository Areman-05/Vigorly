package com.example.vigorly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.data.catalog.WorkoutCoverUrls
import com.example.vigorly.data.model.CoachingTip
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.GlassLabel
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.PrimaryAccent

@Composable
fun DailyTipCard(
    tip: CoachingTip,
    modifier: Modifier = Modifier,
    coverIndex: Int = 0
) {
    val coverUrl = WorkoutCoverUrls.tip(coverIndex)

    GlassSurface(
        modifier = modifier
            .fillMaxWidth()
            .height(188.dp),
        shape = RoundedCornerShape(22.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            RemoteCoverImage(
                url = coverUrl,
                contentDescription = null
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.12f),
                                Color.Black.copy(alpha = 0.5f),
                                Color.Black.copy(alpha = 0.84f)
                            )
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(Dimens.Md)
            ) {
                Text(
                    text = stringResource(R.string.coaching_tip_label),
                    style = BodyMd.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.0.sp
                    ),
                    color = PrimaryAccent
                )
                Text(
                    text = tip.text,
                    style = HeadlineMd.copy(
                        fontSize = 19.sp,
                        lineHeight = 25.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.2).sp
                    ),
                    color = OnSurface,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    text = stringResource(R.string.coaching_tip_personalized_hint),
                    style = BodyMd.copy(fontSize = 14.sp, lineHeight = 18.sp),
                    color = GlassLabel.copy(alpha = 0.9f),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
