package com.example.vigorly.ui.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.vigorly.data.model.WorkoutDetail
import com.example.vigorly.data.model.WorkoutType
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.ui.components.FavoriteToggle
import com.example.vigorly.ui.components.GlassCard
import com.example.vigorly.ui.components.WorkoutChip
import com.example.vigorly.ui.iconForName
import com.example.vigorly.ui.theme.BodyLg
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.ButtonText
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.DisplayStat
import com.example.vigorly.ui.theme.HeadlineLgMobile
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnPrimaryContainer
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryContainer
import com.example.vigorly.ui.theme.Surface
import com.example.vigorly.ui.theme.SurfaceContainer
import com.example.vigorly.ui.theme.SurfaceContainerHigh

@Composable
fun WorkoutDetailScreen(
    workout: WorkoutDetail,
    repository: VigorlyRepository,
    onStartWorkout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val favorites by repository.favorites.collectAsState()
    Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Box(Modifier.fillMaxWidth().height(400.dp)) {
            AsyncImage(
                model = workout.heroImageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.6f
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(listOf(Color.Transparent, Surface.copy(0.5f), Surface)))
            )
            Box(Modifier.align(Alignment.TopEnd).padding(Dimens.ContainerMargin)) {
                FavoriteToggle(
                    isFavorite = favorites.contains(workout.id),
                    onToggle = { repository.toggleFavorite(workout.id) }
                )
            }
            Column(Modifier.align(Alignment.BottomStart).padding(Dimens.ContainerMargin)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    WorkoutChip(workout.type.name)
                    WorkoutChip("${workout.durationMinutes} MIN", primary = true)
                }
                Text(
                    workout.name.uppercase(),
                    style = DisplayStat.copy(fontWeight = FontWeight.Black),
                    color = Color.White
                )
                Text(workout.description, style = BodyLg, color = OnSurfaceVariant)
            }
        }
        Column(Modifier.padding(Dimens.ContainerMargin)) {
            GlassCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(Dimens.Md), horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                        Column {
                            Text("Target", style = HeadlineMd, color = OnSurface)
                            Text(workout.targetMuscles, style = BodyMd, color = OnSurfaceVariant)
                        }
                    }
                    workout.anatomyImageUrl?.let {
                        AsyncImage(
                            model = it,
                            contentDescription = null,
                            modifier = Modifier.height(200.dp).padding(top = Dimens.Md)
                        )
                    }
                }
            }
            Spacer(Modifier.height(Dimens.Md))
            GlassCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(Dimens.Md)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Intensity", style = BodyMd, color = OnSurfaceVariant)
                        Text(workout.intensity, style = HeadlineMd, color = Primary)
                    }
                    Row(Modifier.fillMaxWidth().padding(vertical = Dimens.Sm), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Est. Cal", style = BodyMd, color = OnSurfaceVariant)
                        Text("${workout.estimatedCalories} kcal", style = HeadlineMd, color = OnSurface)
                    }
                    Button(
                        onClick = onStartWorkout,
                        modifier = Modifier.fillMaxWidth().padding(top = Dimens.Sm),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryContainer,
                            contentColor = OnPrimaryContainer
                        )
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Text("START WORKOUT", style = ButtonText, modifier = Modifier.padding(start = 4.dp))
                    }
                }
            }
            Spacer(Modifier.height(Dimens.Lg))
            workout.blocks.forEach { block ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = Dimens.Md)) {
                    Box(
                        Modifier.size(32.dp).clip(CircleShape).background(SurfaceContainerHigh),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(block.label, style = HeadlineMd, color = Primary)
                    }
                    Text(
                        block.title.uppercase(),
                        style = HeadlineLgMobile,
                        color = OnSurface,
                        modifier = Modifier.padding(start = Dimens.Sm)
                    )
                }
                block.exercises.forEach { exercise ->
                    GlassCard(
                        modifier = Modifier.fillMaxWidth().padding(bottom = Dimens.Sm),
                        onClick = null
                    ) {
                        Row(
                            Modifier.padding(Dimens.Md),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (exercise.imageUrl != null) {
                                AsyncImage(
                                    model = exercise.imageUrl,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    Modifier.size(64.dp).clip(RoundedCornerShape(8.dp)).background(SurfaceContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        iconForName(exercise.iconName ?: "fitness_center"),
                                        null,
                                        tint = OnSurfaceVariant,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                            Column(Modifier.weight(1f).padding(horizontal = Dimens.Md)) {
                                Text(exercise.name, style = HeadlineMd, color = OnSurface)
                                Text(exercise.setsRepsLabel, style = BodyMd, color = OnSurfaceVariant)
                            }
                            IconButton(onClick = {}) {
                                Icon(Icons.Default.MoreVert, null, tint = OnSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}
