package com.example.vigorly.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.vigorly.R
import com.example.vigorly.data.model.CoachingTip
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary

@Composable
fun DailyTipCard(
    tip: CoachingTip,
    modifier: Modifier = Modifier
) {
    VigorlyOutlineCard(modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(Dimens.Md)) {
            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Primary)
            Text(stringResource(R.string.coaching_tip_label), style = LabelCaps, color = OnSurfaceVariant, modifier = Modifier.padding(top = Dimens.Sm))
            Text(tip.text, style = BodyMd, color = OnSurface, modifier = Modifier.padding(top = Dimens.Xs))
        }
    }
}
