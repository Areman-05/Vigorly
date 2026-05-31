package com.example.vigorly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.vigorly.R
import com.example.vigorly.ui.theme.BodyLg
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.HeadlineLg
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer

@Composable
fun SetupActivityRingsGuide(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        ActivityRings(
            moveProgress = 0.72f,
            exerciseProgress = 0.55f,
            standProgress = 0.68f,
            centerPercent = 0,
            ringSize = 260.dp,
            showCenterPercent = false
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Dimens.Lg)
        ) {
            RingLegendItem(
                color = PrimaryAccent,
                title = stringResource(R.string.setup_ring_move),
                description = stringResource(R.string.setup_ring_move_desc)
            )
            RingLegendItem(
                color = PrimaryContainer,
                title = stringResource(R.string.setup_ring_exercise),
                description = stringResource(R.string.setup_ring_exercise_desc)
            )
            RingLegendItem(
                color = Primary,
                title = stringResource(R.string.setup_ring_stand),
                description = stringResource(R.string.setup_ring_stand_desc)
            )
        }
    }
}

@Composable
private fun RingLegendItem(
    color: Color,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.Xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.Md)
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(color)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
            contentDescription = null,
            tint = color.copy(alpha = 0.85f),
            modifier = Modifier.size(22.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = HeadlineLg.copy(fontWeight = FontWeight.SemiBold),
                color = OnSurface
            )
            Text(
                text = description,
                style = BodyLg,
                color = OnSurfaceVariant,
                modifier = Modifier.padding(top = Dimens.Xs)
            )
        }
    }
}
