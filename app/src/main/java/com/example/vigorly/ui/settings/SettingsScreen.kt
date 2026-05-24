package com.example.vigorly.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.ui.components.GlassCard
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.ButtonText
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary

@Composable
fun SettingsScreen(
    repository: VigorlyRepository,
    modifier: Modifier = Modifier
) {
    val profile by repository.profile.collectAsState()
    val notifications by repository.notificationsEnabled.collectAsState()
    val unitsMetric by repository.unitsMetric.collectAsState()
    var nameInput by remember(profile.displayName) { mutableStateOf(profile.displayName) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Dimens.ContainerMargin)
    ) {
        Text("Settings", style = HeadlineMd, color = OnSurface)
        GlassCard(Modifier.fillMaxWidth().padding(top = Dimens.Md)) {
            Column(Modifier.padding(Dimens.Md)) {
                Text("Profile", style = HeadlineMd, color = OnSurface)
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Display name") },
                    modifier = Modifier.fillMaxWidth().padding(top = Dimens.Sm),
                    singleLine = true
                )
                Button(
                    onClick = { if (nameInput.isNotBlank()) repository.updateDisplayName(nameInput) },
                    modifier = Modifier.padding(top = Dimens.Sm)
                ) {
                    Text("Save profile", style = ButtonText)
                }
            }
        }
        GlassCard(Modifier.fillMaxWidth().padding(top = Dimens.Md)) {
            Column(Modifier.padding(Dimens.Md)) {
                SettingToggle("Workout reminders", notifications, repository::setNotificationsEnabled)
                SettingToggle("Metric units", unitsMetric, repository::setUnitsMetric)
            }
        }
        Text(
            "Version 1.0 — Vigorly",
            style = BodyMd,
            color = OnSurfaceVariant,
            modifier = Modifier.padding(top = Dimens.Lg)
        )
    }
}

@Composable
private fun SettingToggle(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = Dimens.Sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = BodyMd, color = OnSurface)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
