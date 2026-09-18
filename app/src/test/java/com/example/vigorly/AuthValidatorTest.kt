package com.example.vigorly

import com.example.vigorly.data.model.AuthError
import com.example.vigorly.util.AuthValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AuthValidatorTest {
    @Test
    fun validRegistration_passes() {
        assertNull(
            AuthValidator.validateRegistration(
                email = "user@example.com",
                password = "Segura1!",
                username = "Pablo",
                birthDate = "15/03/1995"
            )
        )
    }

    @Test
    fun invalidEmail_rejected() {
        assertEquals(AuthError.INVALID_EMAIL, AuthValidator.validateEmail("userexample.com"))
    }

    @Test
    fun weakPassword_rejected() {
        assertEquals(AuthError.PASSWORD_WEAK, AuthValidator.validatePassword("corta1"))
    }

    @Test
    fun invalidUsername_rejected() {
        assertEquals(AuthError.INVALID_USERNAME, AuthValidator.validateUsername("ab"))
        assertEquals(AuthError.FIELDS_REQUIRED, AuthValidator.validateUsername("  "))
    }

    @Test
    fun invalidBirthDate_rejected() {
        assertEquals(AuthError.INVALID_BIRTH_DATE, AuthValidator.validateBirthDate("1995-03-15"))
        assertNull(AuthValidator.validateBirthDate("15/03/1995"))
    }

    @Test
    fun passwordRequirementsMet_tracksEachRule() {
        val checks = AuthValidator.passwordRequirementsMet("Aa1!")
        assertEquals(listOf(false, true, true, true, true), checks)
        val ok = AuthValidator.passwordRequirementsMet("Segura1!")
        assertEquals(listOf(true, true, true, true, true), ok)
    }
}
