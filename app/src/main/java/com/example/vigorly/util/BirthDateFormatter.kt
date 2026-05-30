package com.example.vigorly.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

object BirthDateFormatter {
    private const val MAX_DIGITS = 8

    fun digitsOnly(raw: String): String = raw.filter { it.isDigit() }.take(MAX_DIGITS)

    fun toFormatted(digits: String): String {
        val d = digitsOnly(digits)
        return when {
            d.isEmpty() -> ""
            d.length <= 2 -> d
            d.length <= 4 -> "${d.substring(0, 2)}/${d.substring(2)}"
            else -> "${d.substring(0, 2)}/${d.substring(2, 4)}/${d.substring(4)}"
        }
    }

    fun formatInput(raw: String): String = toFormatted(digitsOnly(raw))
}

class BirthDateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = BirthDateFormatter.digitsOnly(text.text)
        val formatted = BirthDateFormatter.toFormatted(digits)
        val mapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val clamped = offset.coerceIn(0, digits.length)
                var extra = 0
                if (clamped > 2) extra++
                if (clamped > 4) extra++
                return (clamped + extra).coerceIn(0, formatted.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                val clamped = offset.coerceIn(0, formatted.length)
                var removed = 0
                if (clamped > 2) removed++
                if (clamped > 5) removed++
                return (clamped - removed).coerceIn(0, digits.length)
            }
        }
        return TransformedText(AnnotatedString(formatted), mapping)
    }
}
