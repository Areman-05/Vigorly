package com.example.vigorly.ui.insights

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.ui.components.GlassCard
import com.example.vigorly.ui.components.SectionHeader
import com.example.vigorly.ui.components.StatRow
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.util.HistorySummaryCalculator

@Composable
fun InsightsScreen(
    repository: VigorlyRepository,
    modifier: Modifier = Modifier
) {
    val history by repository.history.collectAsState()
    val stats by repository.athleticStats.collectAsState()
    val summary = HistorySummaryCalculator.from(history)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Dimens.ContainerMargin)
    ) {
        SectionHeader("Insights", subtitle = "Training analytics")
        GlassCard(modifier = Modifier.padding(top = Dimens.Md)) {
            Column(Modifier.padding(Dimens.Md)) {
                StatRow("Sessions", "${summary.totalSessions}")
                StatRow("Minutes", "${summary.totalMinutes}")
                StatRow("Calories", "${summary.totalCalories}")
            }
        }
        GlassCard(modifier = Modifier.padding(top = Dimens.Md)) {
            Column(Modifier.padding(Dimens.Md)) {
                stats.forEach { stat ->
                    StatRow(stat.label, "${stat.value}")
                }
            }
        }
    }
}
