package com.example.vigorly.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import com.example.vigorly.R
import com.example.vigorly.data.model.Milestone
import com.example.vigorly.ui.iconForName
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.ButtonText
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer
import androidx.compose.ui.graphics.Color

@Composable
fun ProfileMilestoneShowcase(
    slots: List<String?>,
    milestones: List<Milestone>,
    onSlotClick: (Int) -> Unit,
    onClearSlot: (Int) -> Unit,
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    val milestoneMap = milestones.associateBy { it.id }

    Column(modifier = modifier.fillMaxWidth()) {
        ProfileSectionHeader(
            title = stringResource(R.string.profile_milestones_showcase),
            actionLabel = null,
            onAction = null
        )
        Text(
            stringResource(R.string.profile_milestones_showcase_hint),
            style = BodyMd.copy(fontSize = 16.sp),
            color = OnSurfaceVariant.copy(alpha = 0.75f),
            modifier = Modifier.padding(top = 4.dp, bottom = Dimens.Sm)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            PrimaryAccent.copy(alpha = 0.1f),
                            Primary.copy(alpha = 0.12f),
                            PrimaryContainer.copy(alpha = 0.08f)
                        )
                    )
                )
                .padding(vertical = Dimens.Md, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val slotTints = listOf(PrimaryAccent, PrimaryContainer, Primary, Color(0xFFFF922B))
            slots.forEachIndexed { index, milestoneId ->
                val milestone = milestoneId?.let { milestoneMap[it] }
                MilestoneShowcaseSlot(
                    milestone = milestone,
                    slotTint = slotTints[index % slotTints.size],
                    onClick = { onSlotClick(index) },
                    onClear = if (milestone != null) ({ onClearSlot(index) }) else null,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        TextButton(
            onClick = onViewAll,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = Dimens.Sm)
        ) {
            Text(
                stringResource(R.string.profile_view_milestones),
                style = ButtonText,
                color = PrimaryAccent
            )
        }
    }
}

@Composable
private fun MilestoneShowcaseSlot(
    milestone: Milestone?,
    slotTint: Color,
    onClick: () -> Unit,
    onClear: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (milestone == null) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(slotTint.copy(alpha = 0.12f))
                        .clickable(onClick = onClick),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        drawCircle(
                            color = slotTint.copy(alpha = 0.55f),
                            radius = size.minDimension / 2f,
                            style = Stroke(
                                width = 2.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f))
                            )
                        )
                    }
                    Icon(
                        Icons.Default.Add,
                        contentDescription = stringResource(R.string.profile_milestone_add),
                        tint = slotTint,
                        modifier = Modifier.size(28.dp)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    slotTint.copy(alpha = 0.4f),
                                    slotTint.copy(alpha = 0.12f)
                                )
                            )
                        )
                        .border(2.dp, slotTint.copy(alpha = 0.7f), CircleShape)
                        .clickable(onClick = onClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        iconForName(milestone.iconName),
                        contentDescription = milestone.title,
                        tint = slotTint,
                        modifier = Modifier.size(30.dp)
                    )
                }
                if (onClear != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(OnSurfaceVariant.copy(alpha = 0.85f))
                            .clickable(onClick = onClear),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = stringResource(R.string.profile_milestone_remove),
                            tint = OnSurface,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
        Text(
            milestone?.title ?: stringResource(R.string.profile_milestone_empty_slot),
            style = LabelCaps.copy(fontSize = 12.sp, lineHeight = 14.sp),
            color = if (milestone != null) OnSurface else OnSurfaceVariant.copy(0.65f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp),
            maxLines = 2
        )
    }
}
