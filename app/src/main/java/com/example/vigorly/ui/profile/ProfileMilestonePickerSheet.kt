package com.example.vigorly.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.data.model.Milestone
import com.example.vigorly.ui.iconForName
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.SurfaceContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileMilestonePickerSheet(
    visible: Boolean,
    unlockedMilestones: List<Milestone>,
    onDismiss: () -> Unit,
    onSelect: (Milestone) -> Unit
) {
    if (!visible) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.ContainerMargin, vertical = Dimens.Md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.profile_milestone_pick_title),
                    style = HeadlineMd.copy(fontSize = 22.sp),
                    color = OnSurface,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = stringResource(R.string.close))
                }
            }
            Text(
                stringResource(R.string.profile_milestone_pick_subtitle),
                style = BodyMd.copy(fontSize = 17.sp),
                color = OnSurfaceVariant.copy(alpha = 0.85f),
                modifier = Modifier.padding(bottom = Dimens.Md)
            )

            if (unlockedMilestones.isEmpty()) {
                Text(
                    stringResource(R.string.profile_milestone_pick_empty),
                    style = BodyMd.copy(fontSize = 16.sp),
                    color = OnSurfaceVariant,
                    modifier = Modifier.padding(vertical = Dimens.Lg)
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(Dimens.Sm)) {
                    items(unlockedMilestones, key = { it.id }) { milestone ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onSelect(milestone) }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryAccent.copy(alpha = 0.16f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    iconForName(milestone.iconName),
                                    contentDescription = null,
                                    tint = PrimaryAccent,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column(Modifier.padding(start = Dimens.Md)) {
                                Text(
                                    milestone.title,
                                    style = BodyMd.copy(fontWeight = FontWeight.SemiBold),
                                    color = OnSurface
                                )
                                Text(
                                    milestone.subtitle,
                                    style = BodyMd.copy(fontSize = 16.sp),
                                    color = OnSurfaceVariant.copy(alpha = 0.75f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
