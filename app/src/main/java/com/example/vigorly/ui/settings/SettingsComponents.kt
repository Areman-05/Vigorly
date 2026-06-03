package com.example.vigorly.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.DisplayStat
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer

@Composable
fun SettingsSectionTitle(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = LabelCaps.copy(fontSize = 13.sp),
        color = PrimaryAccent.copy(alpha = 0.9f),
        fontWeight = FontWeight.SemiBold,
        modifier = modifier.padding(top = Dimens.Lg, bottom = Dimens.Sm)
    )
}

@Composable
fun SettingsSectionCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Primary.copy(alpha = 0.14f),
                        PrimaryAccent.copy(alpha = 0.06f),
                        PrimaryContainer.copy(alpha = 0.04f)
                    )
                )
            )
            .padding(Dimens.Md),
        content = content
    )
}

@Composable
fun SettingsToggleRow(
    label: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f).padding(end = Dimens.Sm)) {
            Text(
                text = label,
                style = BodyMd.copy(fontSize = 16.sp, fontWeight = FontWeight.Medium),
                color = OnSurface
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = BodyMd.copy(fontSize = 14.sp),
                    color = OnSurfaceVariant.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = OnSurface,
                checkedTrackColor = PrimaryAccent,
                uncheckedThumbColor = OnSurfaceVariant,
                uncheckedTrackColor = OnSurfaceVariant.copy(alpha = 0.25f)
            )
        )
    }
}

@Composable
fun SettingsNavRow(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accent: Color = PrimaryAccent
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        accent.copy(alpha = 0.14f),
                        Primary.copy(alpha = 0.1f)
                    )
                )
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(24.dp))
        }
        Text(
            text = title,
            style = BodyMd.copy(fontSize = 17.sp, fontWeight = FontWeight.SemiBold),
            color = OnSurface,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        )
        Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = accent,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
fun SettingsActionRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    destructive: Boolean = false
) {
    val accent = if (destructive) PrimaryContainer else PrimaryAccent
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(accent.copy(alpha = if (destructive) 0.12f else 0.08f))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                style = BodyMd.copy(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
                color = if (destructive) PrimaryContainer else OnSurface
            )
            Text(
                text = subtitle,
                style = BodyMd.copy(fontSize = 14.sp),
                color = OnSurfaceVariant.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = accent.copy(alpha = 0.85f),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun SettingsWeeklyStepper(
    targetSessions: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingsStepperButton(onClick = onDecrease, enabled = targetSessions > 1) {
            Icon(Icons.Default.Remove, contentDescription = null, tint = OnSurface)
        }
        Text(
            text = targetSessions.toString(),
            style = DisplayStat.copy(fontSize = 32.sp, lineHeight = 34.sp),
            color = PrimaryAccent,
            fontWeight = FontWeight.Bold
        )
        SettingsStepperButton(onClick = onIncrease, enabled = targetSessions < 14) {
            Icon(Icons.Default.Add, contentDescription = null, tint = OnSurface)
        }
    }
}

@Composable
private fun SettingsStepperButton(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(
                if (enabled) PrimaryAccent.copy(alpha = 0.2f)
                else OnSurfaceVariant.copy(alpha = 0.12f)
            )
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
fun SettingsPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    destructive: Boolean = false
) {
    val colors = if (destructive) {
        listOf(PrimaryContainer, PrimaryContainer.copy(alpha = 0.85f))
    } else {
        listOf(PrimaryAccent, PrimaryContainer)
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.horizontalGradient(colors))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = BodyMd.copy(fontSize = 17.sp, fontWeight = FontWeight.Bold),
            color = Color.White
        )
    }
}
