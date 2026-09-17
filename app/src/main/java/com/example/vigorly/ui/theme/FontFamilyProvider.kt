package com.example.vigorly.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.vigorly.R

/** Tipografía del sistema para UI general. */
val VigorlyFontFamily: FontFamily = FontFamily.SansSerif

/**
 * Display fitness (Bebas Neue, Google Fonts OFL).
 * Estilo wordmark tipo Nike / apps de entrenamiento: condensada, limpia, legible.
 * https://fonts.google.com/specimen/Bebas+Neue
 */
val VigorlyDisplayFamily: FontFamily = FontFamily(
    Font(R.font.bebas_neue, weight = FontWeight.Normal)
)
