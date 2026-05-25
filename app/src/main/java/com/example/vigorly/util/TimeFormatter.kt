package com.example.vigorly.util

object TimeFormatter {
    fun formatElapsed(seconds: Int): String {
        val m = seconds / 60
        val s = seconds % 60
        return "%02d:%02d".format(m, s)
    }

    fun formatRestCountdown(seconds: Int): String = "%02d".format(seconds.coerceAtLeast(0))
}
