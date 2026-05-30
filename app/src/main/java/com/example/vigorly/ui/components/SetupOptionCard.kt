package com.example.vigorly.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryContainer

@Composable
fun SetupOptionCard(
    title: String,
    subtitle: String,
    pose: FitnessPose,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) Primary else Primary.copy(alpha = 0.15f),
                shape = shape
            )
            .clickable(onClick = onClick)
    ) {
        Row(
            Modifier.padding(Dimens.Md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FitnessSilhouette(pose = pose, size = 72.dp)
            Column(Modifier.padding(start = Dimens.Md).weight(1f)) {
                Text(title, style = HeadlineMd, color = if (selected) PrimaryContainer else OnSurface)
                Text(subtitle, style = BodyMd, color = OnSurfaceVariant, modifier = Modifier.padding(top = Dimens.Xs))
            }
        }
    }
}
