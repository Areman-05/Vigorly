package com.example.vigorly.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.vigorly.ui.theme.Dimens
import com.example.vigorly.ui.theme.HeadlineMd
import com.example.vigorly.ui.theme.LabelCaps
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null
) {
    androidx.compose.foundation.layout.Column(modifier = modifier.fillMaxWidth()) {
        Text(title, style = HeadlineMd, color = OnSurface)
        subtitle?.let {
            Text(
                it.uppercase(),
                style = LabelCaps,
                color = OnSurfaceVariant,
                modifier = Modifier.padding(top = Dimens.Xs)
            )
        }
    }
}
