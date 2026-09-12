package com.example.vigorly.util

object TimeFormatter {
    fun formatElapsed(seconds: Int): String = formatCountdown(seconds)

    /** Cuenta atrás / transcurrido en MM:SS. */
    fun formatCountdown(seconds: Int): String {
        val safe = seconds.coerceAtLeast(0)
        val m = safe / 60
        val s = safe % 60
        return "%02d:%02d".format(m, s)
    }

    fun formatRestCountdown(seconds: Int): String = formatCountdown(seconds)
}
