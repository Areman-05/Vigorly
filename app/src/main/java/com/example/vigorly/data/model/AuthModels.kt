package com.example.vigorly.data.model

data class UserAccount(
    val id: String,
    val email: String,
    val password: String,
    val username: String,
    val birthDate: String
)

sealed class AuthResult {
    data object Success : AuthResult()
    data class Error(val messageKey: AuthError) : AuthResult()
}

enum class AuthError {
    INVALID_CREDENTIALS,
    EMAIL_ALREADY_EXISTS,
    FIELDS_REQUIRED,
    PASSWORD_TOO_SHORT
}
