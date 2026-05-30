package com.example.vigorly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.vigorly.ui.theme.Background
import com.example.vigorly.ui.theme.Primary
import com.example.vigorly.ui.theme.PrimaryAccent
import com.example.vigorly.ui.theme.PrimaryContainer
import com.example.vigorly.ui.theme.SurfaceContainerLow

@Composable
fun AuthGradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        PrimaryContainer.copy(alpha = 0.35f),
                        Color.Transparent
                    ),
                    center = Offset(0.2f, 0.15f),
                    radius = 900f
                )
            )
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        PrimaryAccent.copy(alpha = 0.18f),
                        Color.Transparent
                    ),
                    center = Offset(0.85f, 0.75f),
                    radius = 700f
                )
            )
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        SurfaceContainerLow.copy(alpha = 0.6f),
                        Background,
                        Background
                    )
                )
            )
    ) {
        content()
    }
}

@Composable
fun SplashGradientBackground(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .background(
                Brush.radialGradient(
                    colors = listOf(PrimaryAccent.copy(0.22f), Color.Transparent),
                    center = Offset(0.5f, 0.38f),
                    radius = 680f
                )
            )
            .background(
                Brush.radialGradient(
                    colors = listOf(PrimaryContainer.copy(0.28f), Color.Transparent),
                    center = Offset(0.15f, 0.85f),
                    radius = 520f
                )
            )
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        Primary.copy(alpha = 0.06f),
                        Color.Transparent
                    ),
                    start = Offset.Zero,
                    end = Offset(0f, 2800f)
                )
            )
    )
}
