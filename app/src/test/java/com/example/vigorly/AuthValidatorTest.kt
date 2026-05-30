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
}
