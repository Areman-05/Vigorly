package com.example.vigorly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.GlassLabel
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer

@Composable
fun SetupIconBadge(
    icon: ImageVector,
    selected: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 52.dp,
    iconSize: Dp = 28.dp
) {
    Box(
        modifier = modifier
            .size(size)
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
            modifier = Modifier.size(iconSize)
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
    modifier: Modifier = Modifier,
    stacked: Boolean = false,
    @Suppress("UNUSED_PARAMETER") badgeSize: Dp = 52.dp,
    iconSize: Dp = 26.dp,
    contentPadding: Dp = 16.dp,
    @Suppress("UNUSED_PARAMETER") minHeight: Dp? = null
) {
    val shape = RoundedCornerShape(22.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .border(
                width = if (selected) 1.5.dp else 0.dp,
                color = if (selected) PrimaryAccent else Color.Transparent,
                shape = shape
            )
    ) {
        GlassSurface(
            modifier = Modifier.fillMaxSize(),
            shape = shape,
            onClick = onClick
        ) {
            if (stacked) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (selected) PrimaryAccent else Primary,
                        modifier = Modifier.size(iconSize)
                    )
                    Column {
                        Text(
                            title,
                            style = BodyMd.copy(
                                fontSize = 17.sp,
                                lineHeight = 21.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = if (selected) PrimaryAccent else Primary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            subtitle,
                            style = BodyMd.copy(
                                fontSize = 13.sp,
                                lineHeight = 17.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = GlassLabel.copy(alpha = 0.78f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(horizontal = contentPadding, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (selected) PrimaryAccent else Primary,
                        modifier = Modifier.size(iconSize)
                    )
                    Column(
                        modifier = Modifier
                            .padding(start = 14.dp)
                            .weight(1f)
                    ) {
                        Text(
                            title,
                            style = BodyMd.copy(
                                fontSize = 18.sp,
                                lineHeight = 22.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = if (selected) PrimaryAccent else Primary
                        )
                        Text(
                            subtitle,
                            style = BodyMd.copy(
                                fontSize = 14.sp,
                                lineHeight = 19.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = GlassLabel.copy(alpha = 0.78f),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
