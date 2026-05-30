package com.example.vigorly.ui.auth

import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import com.example.vigorly.ui.theme.OnSurface
import com.example.vigorly.ui.theme.OnSurfaceVariant
import com.example.vigorly.ui.theme.Outline
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent

val authFieldColors
    @Composable get() = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = PrimaryAccent,
        unfocusedBorderColor = Outline.copy(alpha = 0.28f),
        focusedContainerColor = OnSurface.copy(alpha = 0.04f),
        unfocusedContainerColor = OnSurface.copy(alpha = 0.03f),
        focusedLabelColor = Primary,
        unfocusedLabelColor = OnSurfaceVariant,
        cursorColor = PrimaryAccent,
        focusedTextColor = OnSurface,
        unfocusedTextColor = OnSurface
    )
