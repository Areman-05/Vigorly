package com.example.vigorly.ui.workout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.vigorly.core.testing.UiTestEnvironment
import com.example.vigorly.ui.performance.UiPerformance

@Composable
fun rememberWorkoutDetailVisible(): Boolean {
    if (!UiPerformance.decorativeMotionEnabled) return true
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
    if (UiTestEnvironment.disableContinuousUiMotion || !UiPerformance.decorativeMotionEnabled) {
        if (visible) {
            Box(modifier) { content() }
        }
    } else {
        val delay = (enterDelayMillis / 3).coerceAtMost(60)
        AnimatedVisibility(
            visible = visible,
            modifier = modifier,
            enter = fadeIn(
                tween(160, delayMillis = delay, easing = FastOutSlowInEasing)
            )
        ) {
            content()
        }
    }
}
