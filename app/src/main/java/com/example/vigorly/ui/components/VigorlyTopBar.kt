package com.example.vigorly.ui.components

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VigorlyMainTopBar(
    avatarUrl: String?,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = com.example.vigorly.ui.theme.Surface.copy(alpha = 0.8f)
        ),
        navigationIcon = {
            AsyncImage(
                model = avatarUrl,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = Dimens.ContainerMargin)
                    .size(32.dp)
                    .clip(CircleShape)
            )
        },
        title = {
            Text(
                text = "VIGORLY",
                style = HeadlineMd.copy(fontWeight = FontWeight.Black),
                color = Primary
            )
        },
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Primary)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VigorlyDetailTopBar(
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = com.example.vigorly.ui.theme.Surface.copy(alpha = 0.8f)
        ),
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = com.example.vigorly.ui.theme.OnSurfaceVariant
                )
            }
        },
        title = {
            Text(
                text = "VIGORLY",
                style = HeadlineMd.copy(fontWeight = FontWeight.Black),
                color = Primary
            )
        },
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = com.example.vigorly.ui.theme.OnSurfaceVariant)
            }
        }
    )
}
