package com.example.vigorly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer

@Composable
fun SetupIconBadge(
    icon: ImageVector,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(
                if (selected) {
                    Brush.radialGradient(listOf(PrimaryContainer.copy(0.55f), Primary.copy(0.2f)))
                } else {
                    Brush.radialGradient(listOf(Primary.copy(0.18f), Color.Transparent))
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) PrimaryAccent else Primary.copy(0.85f),
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
fun SetupOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) PrimaryAccent else Primary.copy(alpha = 0.2f),
                shape = shape
            )
            .clickable(onClick = onClick)
            .padding(Dimens.Md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SetupIconBadge(icon = icon, selected = selected)
        Column(Modifier.padding(start = Dimens.Md).weight(1f)) {
            Text(
                title,
                style = HeadlineMd,
                color = if (selected) PrimaryAccent else OnSurface
            )
            Text(
                subtitle,
                style = BodyMd,
                color = OnSurfaceVariant,
                modifier = Modifier.padding(top = Dimens.Xs)
            )
        }
    }
}
