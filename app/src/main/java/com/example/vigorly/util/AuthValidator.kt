package com.example.vigorly.util

import com.example.vigorly.data.model.AuthError

object AuthValidator {
    private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val USERNAME_REGEX = Regex("^[\\p{L}0-9][\\p{L}0-9 _.-]{2,29}$")
    private val BIRTH_DATE_REGEX = Regex("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/(19|20)\\d{2}$")
    private val PASSWORD_UPPER = Regex("[A-ZÁÉÍÓÚÑ]")
    private val PASSWORD_LOWER = Regex("[a-záéíóúñ]")
    private val PASSWORD_DIGIT = Regex("\\d")
    private val PASSWORD_SPECIAL = Regex("[!@#\$%^&*()_+\\-=\\[\\]{};':\",./<>?\\\\|`~]")

    fun validateEmail(email: String): AuthError? {
        val trimmed = email.trim()
        if (trimmed.isBlank()) return AuthError.FIELDS_REQUIRED
        if (!EMAIL_REGEX.matches(trimmed)) return AuthError.INVALID_EMAIL
        return null
    }

    fun validateUsername(username: String): AuthError? {
        val trimmed = username.trim()
        if (trimmed.isBlank()) return AuthError.FIELDS_REQUIRED
        if (!USERNAME_REGEX.matches(trimmed)) return AuthError.INVALID_USERNAME
        return null
    }

    fun validatePassword(password: String): AuthError? {
        if (password.isBlank()) return AuthError.FIELDS_REQUIRED
        if (password.length < 8) return AuthError.PASSWORD_WEAK
        if (!PASSWORD_UPPER.containsMatchIn(password)) return AuthError.PASSWORD_WEAK
        if (!PASSWORD_LOWER.containsMatchIn(password)) return AuthError.PASSWORD_WEAK
        if (!PASSWORD_DIGIT.containsMatchIn(password)) return AuthError.PASSWORD_WEAK
        if (!PASSWORD_SPECIAL.containsMatchIn(password)) return AuthError.PASSWORD_WEAK
        return null
    }

    fun validateBirthDate(birthDate: String): AuthError? {
        val trimmed = birthDate.trim()
        if (trimmed.isBlank()) return AuthError.FIELDS_REQUIRED
        if (!BIRTH_DATE_REGEX.matches(trimmed)) return AuthError.INVALID_BIRTH_DATE
        return null
    }

    fun validateRegistration(
        email: String,
        password: String,
        username: String,
        birthDate: String
    ): AuthError? {
        return validateEmail(email)
            ?: validateUsername(username)
            ?: validatePassword(password)
            ?: validateBirthDate(birthDate)
    }

    fun passwordRequirementsMet(password: String): List<Boolean> = listOf(
        password.length >= 8,
        PASSWORD_UPPER.containsMatchIn(password),
        PASSWORD_LOWER.containsMatchIn(password),
        PASSWORD_DIGIT.containsMatchIn(password),
        PASSWORD_SPECIAL.containsMatchIn(password)
    )
}
