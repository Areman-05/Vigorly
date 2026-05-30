package com.example.vigorly.data.model

data class UserAccount(
    val id: String,
    val email: String,
    val passwordHash: String,
    val passwordSalt: String,
    val username: String,
    val birthDate: String,
    val createdAtMillis: Long = System.currentTimeMillis()
)

sealed class AuthResult {
    data object Success : AuthResult()
    data class Error(val messageKey: AuthError) : AuthResult()
}

enum class AuthError {
    INVALID_CREDENTIALS,
    EMAIL_ALREADY_EXISTS,
    FIELDS_REQUIRED,
    PASSWORD_TOO_SHORT,
    PASSWORD_WEAK,
    INVALID_EMAIL,
    INVALID_USERNAME,
    INVALID_BIRTH_DATE
}
