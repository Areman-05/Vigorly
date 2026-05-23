package com.example.vigorly.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.vigorly.navigation.VigorlyRoutes
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnPrimary
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.SurfaceContainer

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(VigorlyRoutes.Dashboard, "Dashboard", Icons.Default.Dashboard),
    BottomNavItem(VigorlyRoutes.Workouts, "Workouts", Icons.Default.FitnessCenter),
    BottomNavItem(VigorlyRoutes.History, "History", Icons.Default.History),
    BottomNavItem(VigorlyRoutes.Profile, "Profile", Icons.Default.Person)
)

@Composable
fun VigorlyBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(72.dp)
            .clip(RoundedCornerShape(999.dp)),
        color = SurfaceContainer.copy(alpha = 0.6f),
        tonalElevation = 0.dp
    ) {
        NavigationBar(
            containerColor = SurfaceContainer.copy(alpha = 0f),
            tonalElevation = 0.dp
        ) {
            bottomNavItems.forEach { item ->
                val selected = currentRoute == item.route
                NavigationBarItem(
                    selected = selected,
                    onClick = { onNavigate(item.route) },
                    icon = {
                        Icon(
                            item.icon,
                            contentDescription = item.label,
                            tint = if (selected) OnPrimary else OnSurfaceVariant
                        )
                    },
                    label = {
                        Text(
                            text = item.label.uppercase(),
                            style = LabelCaps,
                            color = if (selected) OnPrimary else OnSurfaceVariant
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OnPrimary,
                        selectedTextColor = OnPrimary,
                        indicatorColor = Primary,
                        unselectedIconColor = OnSurfaceVariant,
                        unselectedTextColor = OnSurfaceVariant
                    )
                )
            }
        }
    }
}
