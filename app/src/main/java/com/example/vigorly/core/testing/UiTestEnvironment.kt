package com.example.vigorly.core.testing

/**
 * Detecta ejecución bajo instrumentación (androidTest) sin depender del módulo de test en compile time.
 */
object UiTestEnvironment {
    @Volatile
    var isInstrumentedTest: Boolean = detectInstrumentation()

    fun refresh() {
        isInstrumentedTest = detectInstrumentation()
    }

    val disableContinuousUiMotion: Boolean
        get() = isInstrumentedTest

    private fun detectInstrumentation(): Boolean = try {
        val registry = Class.forName("androidx.test.platform.app.InstrumentationRegistry")
        registry.getMethod("getInstrumentation").invoke(null) != null
    } catch (_: Throwable) {
        false
    }
}
