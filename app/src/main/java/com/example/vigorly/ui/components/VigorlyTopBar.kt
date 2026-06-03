package com.example.vigorly.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.vigorly.R
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VigorlyMainTopBar(
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val todayLabel = remember { formatTodayLabel() }
    val settingsLabel = stringResource(R.string.settings_title)

    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent
        ),
        title = {
            Text(
                text = todayLabel,
                style = BodyMd.copy(fontWeight = FontWeight.Medium),
                color = OnSurface,
                modifier = Modifier.padding(start = Dimens.ContainerMargin)
            )
        },
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = settingsLabel,
                    tint = PrimaryAccent,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityDetailTopBar(
    onBackClick: () -> Unit,
    onCalendarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val calendarLabel = stringResource(R.string.activity_calendar_open)

    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent
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
        title = {},
        actions = {
            IconButton(onClick = onCalendarClick) {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = calendarLabel,
                    tint = Primary
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VigorlyDetailTopBar(
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    showBrandTitle: Boolean = true,
    showSettingsAction: Boolean = true
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent
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
            if (showBrandTitle) {
                Text(
                    text = "VIGORLY",
                    style = HeadlineMd.copy(fontWeight = FontWeight.Black),
                    color = Primary
                )
            }
        },
        actions = {
            if (showSettingsAction) {
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = com.example.vigorly.ui.theme.OnSurfaceVariant
                    )
                }
            }
        }
    )
}

private fun formatTodayLabel(): String {
    val locale = Locale.getDefault()
    val formatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", locale)
    return LocalDate.now()
        .format(formatter)
        .replaceFirstChar { char ->
            if (char.isLowerCase()) char.titlecase(locale) else char.toString()
        }
}
