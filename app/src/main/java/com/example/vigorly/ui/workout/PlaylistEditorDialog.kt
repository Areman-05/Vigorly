package com.example.vigorly.ui.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.vigorly.R
import com.example.vigorly.data.model.WorkoutDetail
import com.example.vigorly.ui.components.GlassSurface
import com.example.vigorly.ui.components.RemoteCoverImage
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.GlassCrystalBase
import com.example.vigorly.ui.theme.GlassCrystalEdge
import com.example.vigorly.ui.theme.GlassCrystalLift
import com.example.vigorly.ui.theme.GlassCrystalSheen
import com.example.vigorly.ui.theme.GlassLabel
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.VigorlyFontFamily
import com.example.vigorly.util.WorkoutLabels

@Composable
fun PlaylistEditorDialog(
    title: String,
    initialName: String,
    initialSelectedIds: Set<String>,
    candidates: List<WorkoutDetail>,
    showNameField: Boolean,
    confirmLabel: String,
    onDismiss: () -> Unit,
    onConfirm: (name: String, workoutIds: List<String>) -> Unit
) {
    var name by remember(initialName) { mutableStateOf(initialName) }
    var selected by remember(initialSelectedIds) { mutableStateOf(initialSelectedIds) }
    val shape = RoundedCornerShape(24.dp)
    val sheetInteraction = remember { MutableInteractionSource() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.ContainerMargin)
                    .clickable(
                        interactionSource = sheetInteraction,
                        indication = null,
                        onClick = {}
                    )
            ) {
                GlassSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 640.dp),
                    shape = shape
                ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 18.dp)
                ) {
                    Text(
                        text = title,
                        style = HeadlineMd.copy(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.3).sp
                        ),
                        color = OnSurface
                    )

                    if (showNameField) {
                        Spacer(Modifier.height(14.dp))
                        GlassNameField(
                            value = name,
                            onValueChange = { name = it },
                            placeholder = stringResource(R.string.workout_lists_name_hint)
                        )
                    }

                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = stringResource(R.string.workout_lists_pick_hint),
                        style = BodyMd.copy(fontSize = 13.sp, letterSpacing = 0.1.sp),
                        color = GlassLabel.copy(alpha = 0.85f)
                    )
                    Spacer(Modifier.height(10.dp))

                    if (candidates.isEmpty()) {
                        Text(
                            text = stringResource(R.string.workouts_favorites_empty),
                            style = BodyMd.copy(fontSize = 13.sp),
                            color = GlassLabel.copy(alpha = 0.8f),
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f, fill = false)
                                .heightIn(max = 360.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(bottom = 8.dp)
                        ) {
                            items(candidates, key = { it.id }) { workout ->
                                val checked = workout.id in selected
                                SelectableWorkoutRow(
                                    workout = workout,
                                    selected = checked,
                                    onToggle = {
                                        selected = if (checked) selected - workout.id
                                        else selected + workout.id
                                    }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text(
                                text = stringResource(R.string.history_date_cancel),
                                color = GlassLabel
                            )
                        }
                        Spacer(Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            PrimaryAccent.copy(alpha = 0.95f),
                                            PrimaryAccent.copy(alpha = 0.75f)
                                        )
                                    )
                                )
                                .clickable {
                                    onConfirm(name, selected.toList())
                                }
                                .padding(horizontal = 18.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = confirmLabel,
                                style = BodyMd.copy(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = Color.White
                            )
                        }
                    }
                }
            }
            }
        }
    }
}

@Composable
private fun GlassNameField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    val shape = RoundedCornerShape(16.dp)
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        cursorBrush = SolidColor(PrimaryAccent),
        textStyle = TextStyle(
            color = OnSurface,
            fontSize = 15.sp,
            fontFamily = VigorlyFontFamily
        ),
        modifier = Modifier.fillMaxWidth(),
        decorationBox = { inner ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(shape)
                    .background(
                        Brush.verticalGradient(
                            listOf(GlassCrystalLift, GlassCrystalBase)
                        )
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            listOf(GlassCrystalEdge, GlassCrystalSheen)
                        ),
                        shape = shape
                    )
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = GlassLabel.copy(alpha = 0.65f),
                        fontSize = 15.sp,
                        fontFamily = VigorlyFontFamily
                    )
                }
                inner()
            }
        }
    )
}

@Composable
private fun SelectableWorkoutRow(
    workout: WorkoutDetail,
    selected: Boolean,
    onToggle: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color.White.copy(alpha = if (selected) 0.10f else 0.05f))
            .border(
                width = 1.dp,
                color = if (selected) PrimaryAccent.copy(alpha = 0.45f)
                else Color.White.copy(alpha = 0.10f),
                shape = shape
            )
            .clickable(onClick = onToggle)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.06f))
        ) {
            RemoteCoverImage(url = workout.heroImageUrl, contentDescription = workout.name)
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = workout.name,
                style = HeadlineMd.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                color = OnSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = WorkoutLabels.typeLabel(workout.type),
                style = BodyMd.copy(fontSize = 12.sp),
                color = GlassLabel.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(
                    if (selected) PrimaryAccent.copy(alpha = 0.9f)
                    else Color.Transparent
                )
                .border(
                    width = 1.5.dp,
                    color = if (selected) PrimaryAccent else GlassLabel.copy(alpha = 0.45f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
