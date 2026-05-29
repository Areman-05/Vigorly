package com.example.vigorly.ui.history

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.vigorly.data.model.WorkoutHistoryItem
import com.example.vigorly.ui.components.GlassCard
import com.example.vigorly.ui.components.StatRow
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant

@Composable
fun HistoryDetailScreen(
    item: WorkoutHistoryItem,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.ContainerMargin)
    ) {
        Text("SESSION DETAIL", style = LabelCaps, color = OnSurfaceVariant)
        Text(item.title, style = HeadlineMd, color = OnSurface, modifier = Modifier.padding(top = Dimens.Sm))
        Text(item.timestampLabel, style = BodyMd, color = OnSurfaceVariant, modifier = Modifier.padding(top = Dimens.Xs))
        GlassCard(Modifier.fillMaxWidth().padding(top = Dimens.Lg)) {
            Column(Modifier.padding(Dimens.Md)) {
                StatRow("Duration", "${item.durationMinutes} min", highlight = true)
                StatRow("Calories", "${item.calories} kcal")
                StatRow("Session ID", item.id.take(8).uppercase())
            }
        }
    }
}
