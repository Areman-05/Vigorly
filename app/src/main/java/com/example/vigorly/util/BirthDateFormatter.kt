package com.example.vigorly.util

object BirthDateFormatter {
    fun formatInput(raw: String): String {
        val digits = raw.filter { it.isDigit() }.take(8)
        return buildString {
            digits.forEachIndexed { index, digit ->
                append(digit)
                if (index == 1 || index == 3) append('/')
            }
        }
    }
}
