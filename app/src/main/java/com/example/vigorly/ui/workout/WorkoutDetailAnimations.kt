package com.example.vigorly.ui.workout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun rememberWorkoutDetailVisible(): Boolean {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    return visible
}

@Composable
fun WorkoutDetailSectionEnter(
    visible: Boolean,
    enterDelayMillis: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(
            tween(480, delayMillis = enterDelayMillis, easing = FastOutSlowInEasing)
        ) + slideInVertically(
            animationSpec = tween(480, delayMillis = enterDelayMillis, easing = FastOutSlowInEasing),
            initialOffsetY = { it / 5 }
        )
    ) {
        content()
    }
}
