package com.example.vigorly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
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
import com.example.vigorly.data.model.CoachingTip
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryContainer

@Composable
fun DailyTipCard(
    tip: CoachingTip,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        PrimaryContainer.copy(alpha = 0.14f),
                        Primary.copy(alpha = 0.05f)
                    )
                )
            )
            .padding(Dimens.Md)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Lightbulb,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Primary.copy(alpha = 0.14f))
                    .padding(7.dp)
            )
            Text(
                text = stringResource(R.string.coaching_tip_label),
                style = LabelCaps,
                color = Primary.copy(alpha = 0.9f),
                modifier = Modifier.padding(start = Dimens.Sm)
            )
        }
        Spacer(Modifier.height(Dimens.Sm))
        Text(
            text = tip.text,
            style = BodyMd.copy(fontSize = 15.sp, lineHeight = 22.sp, fontWeight = FontWeight.Normal),
            color = OnSurface
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.coaching_tip_personalized_hint),
            style = LabelCaps.copy(fontSize = 9.sp),
            color = OnSurfaceVariant.copy(alpha = 0.45f)
        )
    }
}
