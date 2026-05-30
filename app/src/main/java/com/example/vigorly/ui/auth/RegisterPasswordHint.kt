package com.example.vigorly.ui.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.vigorly.R
import com.example.vigorly.ui.theme.BodyMd
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.PrimaryAccent

@Composable
internal fun CompactPasswordHint(checks: List<Boolean>, showError: Boolean) {
    val labels = listOf(
        stringResource(R.string.auth_password_req_length),
        stringResource(R.string.auth_password_req_upper),
        stringResource(R.string.auth_password_req_lower),
        stringResource(R.string.auth_password_req_digit),
        stringResource(R.string.auth_password_req_symbol)
    )
    Column {
        labels.forEachIndexed { index, label ->
            val met = checks.getOrElse(index) { false }
            Row(modifier = Modifier.padding(top = if (index == 0) 0.dp else 2.dp)) {
                Text(
                    text = if (met) "✓ " else "· ",
                    style = BodyMd,
                    color = when {
                        showError && !met -> PrimaryAccent
                        met -> PrimaryAccent.copy(0.9f)
                        else -> OnSurfaceVariant.copy(0.65f)
                    }
                )
                Text(
                    text = label,
                    style = BodyMd,
                    color = when {
                        showError && !met -> PrimaryAccent
                        met -> PrimaryAccent.copy(0.85f)
                        else -> OnSurfaceVariant.copy(0.75f)
                    }
                )
            }
        }
    }
}
