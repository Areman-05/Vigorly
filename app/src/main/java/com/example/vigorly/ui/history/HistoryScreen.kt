package com.example.vigorly.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.ui.components.GlassCard
import com.example.vigorly.ui.iconForName
import com.example.vigorly.ui.theme.BodyLg
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.ButtonText
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.SurfaceContainerLow
import com.example.vigorly.ui.theme.SurfaceVariant
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.dp

@Composable
fun HistoryScreen(
    repository: VigorlyRepository,
    modifier: Modifier = Modifier
) {
    val history by repository.history.collectAsState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Dimens.ContainerMargin)
    ) {
        Text("History", style = HeadlineMd, color = OnSurface, modifier = Modifier.padding(bottom = Dimens.Md))
        history.forEach { item ->
            GlassCard(modifier = Modifier.fillMaxWidth().padding(bottom = Dimens.Sm)) {
                Row(
                    Modifier.padding(Dimens.Md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(SurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(iconForName(item.iconName), null, tint = OnSurface)
                    }
                    Column(Modifier.weight(1f).padding(horizontal = Dimens.Md)) {
                        Text(item.title, style = BodyLg, color = OnSurface)
                        Text(item.timestampLabel, style = BodyMd, color = OnSurfaceVariant)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("${item.durationMinutes} MIN", style = ButtonText, color = Primary)
                        Text("${item.calories} KCAL", style = LabelCaps, color = OnSurfaceVariant)
                    }
                }
            }
        }
    }
}
