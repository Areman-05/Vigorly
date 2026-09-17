package com.example.vigorly.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AuthGradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        MainShellBackground()
        content()
    }
}

@Composable
fun SplashGradientBackground(modifier: Modifier = Modifier) {
    MainShellBackground(modifier = modifier)
}
