package com.example.vigorly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.data.model.WorkoutType
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.util.WorkoutLabels

@Composable
fun WorkoutFilterChips(
    favoritesOnly: Boolean,
    selectedType: WorkoutType?,
    onFavoritesClick: () -> Unit,
    onSelectAll: () -> Unit,
    onTypeSelected: (WorkoutType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(bottom = Dimens.Md),
        horizontalArrangement = Arrangement.spacedBy(Dimens.Sm)
    ) {
        FilterPill(
            label = stringResource(R.string.favorites_only),
            selected = favoritesOnly,
            onClick = onFavoritesClick
        )
        FilterPill(
            label = stringResource(R.string.filter_all),
            selected = selectedType == null && !favoritesOnly,
            onClick = onSelectAll
        )
        WorkoutType.entries.forEach { type ->
            FilterPill(
                label = WorkoutLabels.typeLabel(type),
                selected = selectedType == type,
                onClick = { onTypeSelected(type) }
            )
        }
    }
}

@Composable
private fun FilterPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = label,
        style = BodyMd.copy(
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        ),
        color = if (selected) OnSurface else OnSurfaceVariant.copy(alpha = 0.75f),
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(
                if (selected) PrimaryAccent.copy(alpha = 0.22f) else Primary.copy(alpha = 0.06f)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    )
}
