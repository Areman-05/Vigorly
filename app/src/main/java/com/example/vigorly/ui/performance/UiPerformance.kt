package com.example.vigorly.ui.performance

/**
 * Preferencias globales de fluidez. Desactiva animaciones decorativas continuas y fondos pesados
 * en el shell principal; la splash y auth pueden seguir usando gradientes completos.
 */
object UiPerformance {
    const val decorativeMotionEnabled: Boolean = false
    const val useLightMainBackground: Boolean = true
}
