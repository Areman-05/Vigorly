package com.example.vigorly.ui.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vigorly.R
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.GlassLabel
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.PrimaryAccent

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun CompactPasswordHint(
    checks: List<Boolean>,
    showError: Boolean,
    visible: Boolean
) {
    if (!visible) return
    val labels = listOf(
        stringResource(R.string.auth_password_req_length),
        stringResource(R.string.auth_password_req_upper),
        stringResource(R.string.auth_password_req_lower),
        stringResource(R.string.auth_password_req_digit),
        stringResource(R.string.auth_password_req_symbol)
    )
    val allMet = checks.size >= labels.size && checks.take(labels.size).all { it }

    AnimatedContent(
        targetState = allMet,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "passwordReqs"
    ) { complete ->
        if (complete) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 4.dp, top = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = PrimaryAccent,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = stringResource(R.string.auth_password_strong),
                    style = BodyMd.copy(fontSize = 13.sp),
                    color = OnSurface
                )
            }
        } else {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                labels.forEachIndexed { index, label ->
                    val met = checks.getOrElse(index) { false }
                    PasswordChip(
                        label = label,
                        met = met,
                        highlightMissing = showError && !met
                    )
                }
            }
        }
    }
}

@Composable
private fun PasswordChip(
    label: String,
    met: Boolean,
    highlightMissing: Boolean
) {
    val border = when {
        met -> PrimaryAccent.copy(alpha = 0.7f)
        highlightMissing -> PrimaryAccent.copy(alpha = 0.55f)
        else -> Color.White.copy(alpha = 0.12f)
    }
    val fill = when {
        met -> PrimaryAccent.copy(alpha = 0.18f)
        else -> Color.White.copy(alpha = 0.05f)
    }
    val textColor = when {
        met -> OnSurface
        highlightMissing -> PrimaryAccent
        else -> GlassLabel.copy(alpha = 0.7f)
    }
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(fill)
            .border(1.dp, border, RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (met) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = PrimaryAccent,
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
            )
        }
        Text(
            text = label,
            style = BodyMd.copy(fontSize = 12.sp, lineHeight = 14.sp),
            color = textColor
        )
    }
}
