package com.example.vigorly

import com.example.vigorly.data.local.AccountsCodec
import com.example.vigorly.util.PasswordHasher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class AccountsCodecTest {
    @Test
    fun encode_decode_roundTrip_keepsPlainPassword() {
        val (salt, hash) = PasswordHasher.hash("Secret1!")
        val account = com.example.vigorly.data.model.UserAccount(
            id = "u1",
            email = "test@vigorly.app",
            password = "Secret1!",
            passwordHash = hash,
            passwordSalt = salt,
            username = "Alex",
            birthDate = "01/01/1990"
        )
        val encoded = AccountsCodec.encode(listOf(account))
        val decoded = AccountsCodec.decode(encoded)
        assertEquals(1, decoded.size)
        assertEquals("Alex", decoded.first().username)
        assertEquals("Secret1!", decoded.first().password)
        assertTrue(PasswordHasher.verify("Secret1!", decoded.first().passwordSalt, decoded.first().passwordHash))
        assertEquals("Secret1!", PasswordHasher.reveal(decoded.first().passwordHash))
    }

    @Test
    fun decode_legacyPasswordField_isRevealable() {
        val raw = """[{"id":"u2","email":"a@b.com","password":"Hola123!","username":"Pat","birthDate":"01/01/2000"}]"""
        val decoded = AccountsCodec.decode(raw)
        assertEquals("Hola123!", decoded.first().password)
        assertEquals("Hola123!", PasswordHasher.reveal(decoded.first().passwordHash))
    }
}
