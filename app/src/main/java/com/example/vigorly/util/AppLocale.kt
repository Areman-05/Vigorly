package com.example.vigorly.util

enum class AppLocale(val code: String, val labelResKey: String) {
    SPANISH("es", "lang_es"),
    CATALAN("ca", "lang_ca"),
    ENGLISH("en", "lang_en"),
    FRENCH("fr", "lang_fr"),
    GERMAN("de", "lang_de");

    companion object {
        fun fromCode(code: String): AppLocale =
            entries.find { it.code == code } ?: SPANISH
    }
}
