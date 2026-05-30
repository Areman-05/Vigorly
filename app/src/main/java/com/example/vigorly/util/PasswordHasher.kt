package com.example.vigorly.util

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

object PasswordHasher {
    private const val LEGACY_PREFIX = "legacy:"

    fun hash(password: String): Pair<String, String> {
        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val digest = digest(password, salt)
        return Base64.getEncoder().encodeToString(salt) to Base64.getEncoder().encodeToString(digest)
    }

    fun verify(password: String, saltBase64: String, hashBase64: String): Boolean {
        if (hashBase64.startsWith(LEGACY_PREFIX)) {
            return hashBase64.removePrefix(LEGACY_PREFIX) == password
        }
        if (saltBase64.isBlank() || hashBase64.isBlank()) return false
        val salt = Base64.getDecoder().decode(saltBase64)
        val expected = Base64.getDecoder().decode(hashBase64)
        val actual = digest(password, salt)
        return MessageDigest.isEqual(expected, actual)
    }

    fun legacyHash(plainPassword: String): Pair<String, String> {
        return "" to "$LEGACY_PREFIX$plainPassword"
    }

    fun isLegacy(hashBase64: String): Boolean = hashBase64.startsWith(LEGACY_PREFIX)

    private fun digest(password: String, salt: ByteArray): ByteArray {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(salt)
        md.update(password.toByteArray(Charsets.UTF_8))
        return md.digest()
    }
}
