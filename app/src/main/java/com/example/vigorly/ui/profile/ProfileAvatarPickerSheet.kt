package com.example.vigorly.ui.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.ui.components.GlassSurface
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.GlassLabel
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.Surface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileAvatarPickerSheet(
    visible: Boolean,
    selectedId: String?,
    hasCustomPhoto: Boolean,
    onDismiss: () -> Unit,
    onSelectPreset: (String) -> Unit,
    onPickPhoto: (android.net.Uri) -> Unit
) {
    if (!visible) return

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.Hidden }
    )
    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) onPickPhoto(uri)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Surface,
        tonalElevation = 0.dp,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 4.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color.White.copy(alpha = 0.22f))
            )
        },
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(Surface)
                .padding(horizontal = Dimens.Md)
                .padding(bottom = Dimens.Lg)
        ) {
            Box(Modifier.fillMaxWidth()) {
                Text(
                    stringResource(R.string.profile_avatar_picker_title),
                    style = HeadlineMd.copy(fontSize = 22.sp),
                    color = OnSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                IconButton(onClick = onDismiss, modifier = Modifier.align(Alignment.CenterEnd)) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = OnSurface)
                }
            }
            Text(
                stringResource(R.string.profile_avatar_picker_subtitle),
                style = BodyMd.copy(fontSize = 15.sp),
                color = GlassLabel.copy(alpha = 0.85f),
                modifier = Modifier.padding(top = 4.dp, bottom = Dimens.Md)
            )

            GlassSurface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                onClick = {
                    photoLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.PhotoCamera,
                            contentDescription = null,
                            tint = OnSurface,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            stringResource(R.string.profile_avatar_upload),
                            style = BodyMd.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                            color = OnSurface
                        )
                        Text(
                            stringResource(R.string.profile_avatar_upload_hint),
                            style = BodyMd.copy(fontSize = 13.sp),
                            color = GlassLabel.copy(alpha = 0.8f)
                        )
                    }
                    if (hasCustomPhoto) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = OnSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                stringResource(R.string.profile_avatar_presets),
                style = BodyMd.copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                color = GlassLabel.copy(alpha = 0.85f),
                modifier = Modifier.padding(bottom = 10.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                userScrollEnabled = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                items(ProfileAvatarCatalog.all(), key = { it.id }) { preset ->
                    val selected = !hasCustomPhoto && preset.id == selectedId
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .then(
                                if (selected) {
                                    Modifier.border(2.dp, OnSurface.copy(alpha = 0.85f), CircleShape)
                                } else {
                                    Modifier
                                }
                            )
                            .clickable { onSelectPreset(preset.id) },
                        contentAlignment = Alignment.Center
                    ) {
                        ProfileAvatarView(
                            avatarUrl = ProfileAvatarCatalog.encode(preset.id),
                            size = 64.dp
                        )
                        if (selected) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(2.dp)
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(OnSurface)
                                    .border(1.5.dp, Surface, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Surface,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
