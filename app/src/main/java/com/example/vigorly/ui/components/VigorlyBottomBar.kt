package com.example.vigorly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.vigorly.R
import com.example.vigorly.core.testing.VigorlyTestTags
import com.example.vigorly.navigation.VigorlyRoutes
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.GlassBorder
import com.example.vigorly.ui.theme.GlassBorderSoft
import com.example.vigorly.ui.theme.GlassHighlight
import com.example.vigorly.ui.theme.GlassLabel
import com.example.vigorly.ui.theme.PrimaryAccent

private data class BottomNavItem(
    val route: String,
    val labelRes: Int,
    val icon: ImageVector,
    val testTag: String
)

/** Solo iconos outline; activo en rosa de identidad (PrimaryAccent). */
@Composable
fun VigorlyBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem(VigorlyRoutes.Dashboard, R.string.nav_dashboard, Icons.Outlined.Home, VigorlyTestTags.NAV_DASHBOARD),
        BottomNavItem(VigorlyRoutes.Workouts, R.string.nav_workouts, Icons.Outlined.FitnessCenter, VigorlyTestTags.NAV_WORKOUTS),
        BottomNavItem(VigorlyRoutes.Analysis, R.string.nav_analysis, Icons.AutoMirrored.Outlined.ShowChart, VigorlyTestTags.NAV_ANALYSIS),
        BottomNavItem(VigorlyRoutes.Profile, R.string.nav_profile, Icons.Outlined.Person, VigorlyTestTags.NAV_PROFILE)
    )
    val pillShape = RoundedCornerShape(32.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = Dimens.ContainerMargin, vertical = Dimens.Sm)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(pillShape)
                .background(Color(0x99141016))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.16f),
                            Color.White.copy(alpha = 0.06f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(GlassBorder, GlassBorderSoft, GlassHighlight)
                    ),
                    shape = pillShape
                )
                .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val selected = currentRoute == item.route
                BottomNavCell(
                    label = stringResource(item.labelRes),
                    icon = item.icon,
                    selected = selected,
                    onClick = { onNavigate(item.route) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag(item.testTag)
                )
            }
        }
    }
}

@Composable
private fun BottomNavCell(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val active = PrimaryAccent
    val inactive = GlassLabel.copy(alpha = 0.72f)

    Box(
        modifier = modifier
            .heightIn(min = 48.dp)
            .clip(RoundedCornerShape(22.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = active.copy(alpha = 0.28f)),
                onClick = onClick
            )
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) active else inactive,
            modifier = Modifier.size(28.dp)
        )
    }
}
