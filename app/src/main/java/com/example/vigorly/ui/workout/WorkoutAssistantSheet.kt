package com.example.vigorly.ui.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.SurfaceContainer
import com.example.vigorly.ui.theme.SurfaceContainerHigh
import com.example.vigorly.util.WorkoutAssistantEngine

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun WorkoutAssistantSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    onApply: (WorkoutAssistantEngine.Result) -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    if (!visible) return

    var prompt by remember(visible) { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceContainer,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.ContainerMargin)
                .padding(bottom = Dimens.Xl)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.Sm)
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = PrimaryAccent,
                        modifier = Modifier.padding(4.dp)
                    )
                    Text(
                        stringResource(R.string.workout_assistant_title),
                        style = HeadlineMd,
                        color = OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = stringResource(R.string.close), tint = OnSurfaceVariant)
                }
            }

            Text(
                stringResource(R.string.workout_assistant_subtitle),
                style = BodyMd.copy(fontSize = 14.sp, lineHeight = 20.sp),
                color = OnSurfaceVariant.copy(alpha = 0.85f),
                modifier = Modifier.padding(top = Dimens.Xs, bottom = Dimens.Md)
            )

            Text(
                stringResource(R.string.workout_assistant_quick_label),
                style = BodyMd.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
                color = OnSurfaceVariant,
                modifier = Modifier.padding(bottom = Dimens.Sm)
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.Sm),
                verticalArrangement = Arrangement.spacedBy(Dimens.Sm)
            ) {
                QuickChip(R.string.workout_assistant_chip_recovery) {
                    onApply(WorkoutAssistantEngine.fromPreset(WorkoutAssistantEngine.QuickPreset.RECOVERY))
                    onDismiss()
                }
                QuickChip(R.string.workout_assistant_chip_hiit) {
                    onApply(WorkoutAssistantEngine.fromPreset(WorkoutAssistantEngine.QuickPreset.HIIT_SHORT))
                    onDismiss()
                }
                QuickChip(R.string.workout_assistant_chip_legs) {
                    onApply(WorkoutAssistantEngine.fromPreset(WorkoutAssistantEngine.QuickPreset.LEGS))
                    onDismiss()
                }
                QuickChip(R.string.workout_assistant_chip_strength) {
                    onApply(WorkoutAssistantEngine.fromPreset(WorkoutAssistantEngine.QuickPreset.STRENGTH))
                    onDismiss()
                }
                QuickChip(R.string.workout_assistant_chip_swim) {
                    onApply(WorkoutAssistantEngine.fromPreset(WorkoutAssistantEngine.QuickPreset.SWIM_LOW_IMPACT))
                    onDismiss()
                }
                QuickChip(R.string.workout_assistant_chip_core) {
                    onApply(WorkoutAssistantEngine.fromPreset(WorkoutAssistantEngine.QuickPreset.CORE))
                    onDismiss()
                }
                QuickChip(R.string.workout_assistant_chip_beginner) {
                    onApply(WorkoutAssistantEngine.fromPreset(WorkoutAssistantEngine.QuickPreset.BEGINNER))
                    onDismiss()
                }
                QuickChip(R.string.workout_assistant_chip_favorites) {
                    onApply(WorkoutAssistantEngine.fromPreset(WorkoutAssistantEngine.QuickPreset.FAVORITES))
                    onDismiss()
                }
            }

            OutlinedTextField(
                value = prompt,
                onValueChange = { prompt = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.Lg),
                placeholder = {
                    Text(
                        stringResource(R.string.workout_assistant_hint),
                        color = OnSurfaceVariant.copy(alpha = 0.55f)
                    )
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryAccent,
                    unfocusedBorderColor = PrimaryAccent.copy(alpha = 0.35f),
                    focusedTextColor = OnSurface,
                    unfocusedTextColor = OnSurface,
                    cursorColor = PrimaryAccent
                ),
                minLines = 2,
                maxLines = 4
            )

            Button(
                onClick = {
                    onApply(WorkoutAssistantEngine.parse(prompt))
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.Md),
                enabled = prompt.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = OnSurface),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.padding(end = Dimens.Sm))
                Text(stringResource(R.string.workout_assistant_apply), fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun QuickChip(
    labelRes: Int,
    onClick: () -> Unit
) {
    Text(
        text = stringResource(labelRes),
        style = BodyMd.copy(fontSize = 13.sp),
        color = PrimaryAccent,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(PrimaryAccent.copy(alpha = 0.12f))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    )
}
