package com.example.vigorly.ui.workout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.vigorly.R
import com.example.vigorly.data.model.WorkoutType
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.StatRingMove
import com.example.vigorly.ui.theme.Surface
import com.example.vigorly.util.DurationBucket
import com.example.vigorly.util.WorkoutBrowseFilters
import com.example.vigorly.util.WorkoutLabels
import com.example.vigorly.util.WorkoutZone

@Composable
fun WorkoutFilterPanel(
    visible: Boolean,
    initial: WorkoutBrowseFilters,
    onDismiss: () -> Unit,
    onApply: (WorkoutBrowseFilters) -> Unit
) {
    if (!visible) return

    var draft by remember(visible, initial) { mutableStateOf(initial) }
    var expanded by remember(visible) { mutableStateOf<FilterSection?>(null) }
    val sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.78f))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onDismiss
                )
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .fillMaxHeight(0.78f)
                    .clip(sheetShape)
                    .background(Surface)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {}
                    )
                    .navigationBarsPadding()
                    .padding(horizontal = 22.dp)
                    .padding(top = 20.dp, bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.workout_filter_selected_count, draft.selectedCount),
                        style = BodyMd.copy(fontSize = 14.sp),
                        color = OnSurface.copy(alpha = 0.92f)
                    )
                    Text(
                        text = stringResource(R.string.workout_filter_clear),
                        style = BodyMd.copy(fontSize = 14.sp, fontWeight = FontWeight.Medium),
                        color = OnSurface.copy(alpha = 0.92f),
                        modifier = Modifier.clickable { draft = WorkoutBrowseFilters() }
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    FilterAccordionSection(
                        title = stringResource(R.string.workout_filter_zone),
                        expanded = expanded == FilterSection.ZONE,
                        onToggle = {
                            expanded = if (expanded == FilterSection.ZONE) null else FilterSection.ZONE
                        },
                        showDividerAbove = false
                    ) {
                        WorkoutZone.entries.forEach { zone ->
                            FilterCheckRow(
                                label = zoneLabel(zone),
                                checked = zone in draft.zones,
                                onToggle = { draft = draft.copy(zones = draft.zones.toggle(zone)) }
                            )
                        }
                    }

                    FilterAccordionSection(
                        title = stringResource(R.string.workout_filter_level),
                        expanded = expanded == FilterSection.LEVEL,
                        onToggle = {
                            expanded = if (expanded == FilterSection.LEVEL) null else FilterSection.LEVEL
                        }
                    ) {
                        listOf(
                            "high" to stringResource(R.string.intensity_high),
                            "moderate" to stringResource(R.string.intensity_moderate),
                            "low" to stringResource(R.string.intensity_low)
                        ).forEach { (key, label) ->
                            FilterCheckRow(
                                label = label,
                                checked = key in draft.intensities,
                                onToggle = {
                                    draft = draft.copy(intensities = draft.intensities.toggle(key))
                                }
                            )
                        }
                    }

                    FilterAccordionSection(
                        title = stringResource(R.string.workout_filter_duration),
                        expanded = expanded == FilterSection.DURATION,
                        onToggle = {
                            expanded = if (expanded == FilterSection.DURATION) null else FilterSection.DURATION
                        }
                    ) {
                        DurationBucket.entries.forEach { bucket ->
                            FilterCheckRow(
                                label = durationLabel(bucket),
                                checked = bucket in draft.durations,
                                onToggle = {
                                    draft = draft.copy(durations = draft.durations.toggle(bucket))
                                }
                            )
                        }
                    }

                    FilterAccordionSection(
                        title = stringResource(R.string.workout_filter_type),
                        expanded = expanded == FilterSection.TYPE,
                        onToggle = {
                            expanded = if (expanded == FilterSection.TYPE) null else FilterSection.TYPE
                        }
                    ) {
                        WorkoutType.entries.forEach { type ->
                            FilterCheckRow(
                                label = WorkoutLabels.typeLabel(type),
                                checked = type in draft.types,
                                onToggle = { draft = draft.copy(types = draft.types.toggle(type)) }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = {
                        onApply(draft)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(999.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StatRingMove,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = stringResource(R.string.workout_filter_apply),
                        style = BodyMd.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    )
                }
            }
        }
    }
}

private enum class FilterSection { ZONE, LEVEL, DURATION, TYPE }

@Composable
private fun FilterAccordionSection(
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    showDividerAbove: Boolean = true,
    content: @Composable () -> Unit
) {
    if (showDividerAbove) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.White.copy(alpha = 0.14f))
        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = if (expanded) Icons.Default.Remove else Icons.Default.Add,
            contentDescription = null,
            tint = OnSurface,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = title,
            style = BodyMd.copy(fontSize = 17.sp, fontWeight = FontWeight.Medium),
            color = OnSurface
        )
    }
    AnimatedVisibility(
        visible = expanded,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        Column(modifier = Modifier.padding(bottom = 8.dp)) {
            content()
        }
    }
}

@Composable
private fun FilterCheckRow(
    label: String,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(start = 30.dp, end = 4.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .border(1.5.dp, OnSurface.copy(alpha = 0.9f), RoundedCornerShape(4.dp))
                .background(
                    if (checked) Color.White else Color.Transparent,
                    RoundedCornerShape(4.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color(0xFF121317),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
        Text(
            text = label,
            style = BodyMd.copy(fontSize = 16.sp),
            color = OnSurface
        )
    }
}

@Composable
private fun zoneLabel(zone: WorkoutZone): String = when (zone) {
    WorkoutZone.FULL_BODY -> stringResource(R.string.workout_zone_full_body)
    WorkoutZone.UPPER_BODY -> stringResource(R.string.workout_zone_upper)
    WorkoutZone.LOWER_BODY -> stringResource(R.string.workout_zone_lower)
    WorkoutZone.CORE -> stringResource(R.string.workout_zone_core)
    WorkoutZone.BACK -> stringResource(R.string.workout_zone_back)
    WorkoutZone.ARMS -> stringResource(R.string.workout_zone_arms)
    WorkoutZone.SHOULDERS -> stringResource(R.string.workout_zone_shoulders)
    WorkoutZone.GLUTES -> stringResource(R.string.workout_zone_glutes)
    WorkoutZone.LEGS -> stringResource(R.string.workout_zone_legs)
}

@Composable
private fun durationLabel(bucket: DurationBucket): String = when (bucket) {
    DurationBucket.SHORT -> stringResource(R.string.workout_duration_short)
    DurationBucket.MEDIUM -> stringResource(R.string.workout_duration_medium)
    DurationBucket.LONG -> stringResource(R.string.workout_duration_long)
}

private fun <T> Set<T>.toggle(item: T): Set<T> =
    if (item in this) this - item else this + item
