package com.example.vigorly

import com.example.vigorly.util.PasswordHasher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordHasherTest {

    @Test
    fun hash_roundTrip_revealsAndVerifies() {
        val (salt, hash) = PasswordHasher.hash("Segura1!")
        assertTrue(PasswordHasher.verify("Segura1!", salt, hash))
        assertFalse(PasswordHasher.verify("otra", salt, hash))
        assertEquals("Segura1!", PasswordHasher.reveal(hash))
        assertTrue(PasswordHasher.isRevealable(hash))
        assertFalse(PasswordHasher.needsRevealUpgrade(hash))
    }

    @Test
    fun legacyHash_stillVerifies() {
        val (salt, hash) = PasswordHasher.legacyHash("Hola123!")
        assertTrue(PasswordHasher.isLegacy(hash))
        assertTrue(PasswordHasher.verify("Hola123!", salt, hash))
        assertEquals("Hola123!", PasswordHasher.reveal(hash))
    }

    @Test
    fun twoHashes_areDeterministicForRevealFormat() {
        val first = PasswordHasher.hash("Misma1!")
        val second = PasswordHasher.hash("Misma1!")
        assertEquals(first.second, second.second)
        assertNotEquals(PasswordHasher.hash("Otra1!").second, first.second)
    }
}
