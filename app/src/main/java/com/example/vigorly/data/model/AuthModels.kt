package com.example.vigorly.data.model

data class UserAccount(
    val id: String,
    val email: String,
    /** Contraseña en claro: la app es local y el perfil la muestra con el ojo. */
    val password: String = "",
    val passwordHash: String = "",
    val passwordSalt: String = "",
    val username: String,
    val birthDate: String,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val authProvider: String = "email",
    val googleId: String? = null
)

sealed class AuthResult {
    data class Success(val needsSetup: Boolean = false) : AuthResult()
    data class Error(val messageKey: AuthError) : AuthResult()
}

object AccountUniqueness {
    fun isEmailTaken(
        accounts: List<UserAccount>,
        email: String,
        exceptId: String? = null
    ): Boolean {
        val needle = email.trim()
        if (needle.isEmpty()) return false
        return accounts.any { account ->
            (exceptId == null || account.id != exceptId) &&
                account.email.equals(needle, ignoreCase = true)
        }
    }

    fun isUsernameTaken(
        accounts: List<UserAccount>,
        username: String,
        exceptId: String? = null
    ): Boolean {
        val needle = username.trim()
        if (needle.isEmpty()) return false
        return accounts.any { account ->
            (exceptId == null || account.id != exceptId) &&
                account.username.equals(needle, ignoreCase = true)
        }
    }
}

enum class AuthError {
    INVALID_CREDENTIALS,
    EMAIL_ALREADY_EXISTS,
    USERNAME_ALREADY_EXISTS,
    FIELDS_REQUIRED,
    PASSWORD_TOO_SHORT,
    PASSWORD_WEAK,
    INVALID_EMAIL,
    INVALID_USERNAME,
    INVALID_BIRTH_DATE,
    GOOGLE_SIGN_IN_FAILED,
    GOOGLE_SIGN_IN_CANCELLED,
    GOOGLE_NOT_CONFIGURED,
    GOOGLE_NO_ACCOUNT,
    GOOGLE_CONFIG_ERROR
}
