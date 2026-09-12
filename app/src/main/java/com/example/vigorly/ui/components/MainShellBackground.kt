package com.example.vigorly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import com.example.vigorly.ui.theme.AuroraCore
import com.example.vigorly.ui.theme.AuroraDeep
import com.example.vigorly.ui.theme.AuroraMist
import com.example.vigorly.ui.theme.AuroraSoft
import com.example.vigorly.ui.theme.AuroraViolet
import com.example.vigorly.ui.theme.AuroraWarm
import com.example.vigorly.ui.theme.Background

/**
 * Aurora concentrado arriba (magenta/rosa), fade limpio a negro abajo.
 * Sin grano ni textura.
 */
@Composable
fun MainShellBackground(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .drawBehind {
                val w = size.width
                val h = size.height

                // Glow izquierdo (vivo) — solo zona superior
                drawRect(
                    brush = Brush.radialGradient(
                        colorStops = arrayOf(
                            0.0f to AuroraWarm.copy(alpha = 0.55f),
                            0.22f to AuroraCore.copy(alpha = 0.32f),
                            0.48f to AuroraDeep.copy(alpha = 0.18f),
                            1.0f to Color.Transparent
                        ),
                        center = Offset(w * 0.08f, h * 0.02f),
                        radius = maxOf(w, h) * 0.72f,
                        tileMode = TileMode.Clamp
                    )
                )

                // Glow derecho (claro / violeta) — solo arriba
                drawRect(
                    brush = Brush.radialGradient(
                        colorStops = arrayOf(
                            0.0f to AuroraMist.copy(alpha = 0.28f),
                            0.25f to AuroraSoft.copy(alpha = 0.16f),
                            0.45f to AuroraViolet.copy(alpha = 0.22f),
                            1.0f to Color.Transparent
                        ),
                        center = Offset(w * 0.92f, h * 0.06f),
                        radius = maxOf(w, h) * 0.65f,
                        tileMode = TileMode.Clamp
                    )
                )

                // Cierre: a partir de ~mitad el aurora muere a negro sólido
                drawRect(
                    brush = Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color.Transparent,
                            0.38f to Color.Transparent,
                            0.58f to Background.copy(alpha = 0.55f),
                            0.78f to Background.copy(alpha = 0.92f),
                            1.0f to Background
                        )
                    )
                )
            }
    )
}
