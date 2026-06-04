package com.example.vigorly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.example.vigorly.ui.theme.Background
import com.example.vigorly.ui.theme.SurfaceContainerLow

/** Fondo ligero para pestañas y pantallas secundarias (un solo gradiente). */
@Composable
fun MainShellBackground(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        SurfaceContainerLow.copy(alpha = 0.35f),
                        Background
                    )
                )
            )
    )
}
