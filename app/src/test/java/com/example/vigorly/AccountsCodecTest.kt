package com.example.vigorly

import com.example.vigorly.data.local.AccountsCodec
import org.junit.Assert.assertEquals
import org.junit.Test

class AccountsCodecTest {
    @Test
    fun encode_decode_roundTrip() {
        val account = com.example.vigorly.data.model.UserAccount(
            id = "u1",
            email = "test@vigorly.app",
            password = "secret",
            username = "Alex",
            birthDate = "01/01/1990"
        )
        val encoded = AccountsCodec.encode(listOf(account))
        val decoded = AccountsCodec.decode(encoded)
        assertEquals(1, decoded.size)
        assertEquals("Alex", decoded.first().username)
    }
}
