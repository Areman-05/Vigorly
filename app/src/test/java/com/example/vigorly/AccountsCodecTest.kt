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
    fun encode_decode_roundTrip() {
        val (salt, hash) = PasswordHasher.hash("Secret1!")
        val account = com.example.vigorly.data.model.UserAccount(
            id = "u1",
            email = "test@vigorly.app",
            passwordHash = hash,
            passwordSalt = salt,
            username = "Alex",
            birthDate = "01/01/1990"
        )
        val encoded = AccountsCodec.encode(listOf(account))
        val decoded = AccountsCodec.decode(encoded)
        assertEquals(1, decoded.size)
        assertEquals("Alex", decoded.first().username)
        assertTrue(PasswordHasher.verify("Secret1!", decoded.first().passwordSalt, decoded.first().passwordHash))
    }
}
